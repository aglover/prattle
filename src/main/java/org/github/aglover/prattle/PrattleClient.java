package org.github.aglover.prattle;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.Subscriptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PrattleClient {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private Prattle prattle;

    private PrattleClient() {
    }

    public PrattleClient(final Prattle prattle) {
        this.prattle = prattle;
    }

    public PrattleClient(final String token) {
        this.prattle = new Prattle();
        this.prattle.setToken(token);
    }

    public Observable<User> getUser(final Integer id) {
        return Observable.create(new AsyncOnSubscribeFunction<User>() {
            @Override
            void handleOnNext(final Observer<? super User> observer) {
                observer.onNext(prattle.getUser(id));
            }
        });
    }

    public Observable<User> members(final String room) {
        return Observable.create(new AsyncOnSubscribeFunction<User>() {
            @Override
            void handleOnNext(final Observer<? super User> observer) {
                for (final User user : prattle.allMembersOf(room)) {
                    observer.onNext(user);
                }
            }
        });
    }

    public Observable<User> participants(final String room) {
        return Observable.create(new AsyncOnSubscribeFunction<User>() {
            @Override
            void handleOnNext(final Observer<? super User> observer) {
                for (final User user : prattle.allParticipantsIn(room)) {
                    observer.onNext(user);
                }
            }
        });
    }

    public Observable<Integer> sendMessage(final String room, final String message) {
        return Observable.create(new AsyncOnSubscribeFunction<Integer>() {
            @Override
            void handleOnNext(final Observer<? super Integer> observer) {
                observer.onNext(prattle.sendMessage(room, message));
            }
        });
    }

    public Observable<Integer> sendMessage(final Integer userId, final String message) {
        return Observable.create(new AsyncOnSubscribeFunction<Integer>() {
            @Override
            void handleOnNext(final Observer<? super Integer> observer) {
                observer.onNext(prattle.sendMessage(userId, message));
            }
        });
    }

    abstract class AsyncOnSubscribeFunction<T> implements Observable.OnSubscribeFunc<T> {

        abstract void handleOnNext(final Observer<? super T> observer);

        @Override
        public Subscription onSubscribe(final Observer<? super T> observer) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        handleOnNext(observer);
                        observer.onCompleted();
                    } catch (final Throwable thr) {
                        observer.onError(thr);
                    }
                }
            });
            return Subscriptions.empty();
        }
    }
}
