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

    public PrattleClient(final Prattle prattle) {
        this.prattle = prattle;
    }

    public PrattleClient(final String token) {
        this.prattle = new Prattle();
        this.prattle.setToken(token);
    }

    public Observable<User> getUser(final Integer id) {
        return Observable.create(new Observable.OnSubscribeFunc<User>() {
            @Override
            public Subscription onSubscribe(final Observer<? super User> Observer) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Observer.onNext(prattle.getUser(id));
                            Observer.onCompleted();
                        } catch (Throwable thr) {
                            Observer.onError(thr);
                        }
                    }
                });
                return Subscriptions.empty();
            }
        });
    }

    public Observable<User> members(final String room) {
        return Observable.create(new Observable.OnSubscribeFunc<User>() {
            @Override
            public Subscription onSubscribe(final Observer<? super User> Observer) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (User user : prattle.allMembersOf(room)) {
                                Observer.onNext(user);
                            }
                            Observer.onCompleted();
                        } catch (Throwable thr) {
                            Observer.onError(thr);
                        }
                    }
                });
                return Subscriptions.empty();
            }
        });
    }

    public Observable<User> participants(final String room) {
        return Observable.create(new Observable.OnSubscribeFunc<User>() {
            @Override
            public Subscription onSubscribe(final Observer<? super User> Observer) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (User user : prattle.allParticipantsIn(room)) {
                                Observer.onNext(user);
                            }
                            Observer.onCompleted();
                        } catch (Throwable thr) {
                            Observer.onError(thr);
                        }
                    }
                });
                return Subscriptions.empty();
            }
        });
    }

    public Observable<Integer> sendMessage(final String room, final String message) {
        return Observable.create(new Observable.OnSubscribeFunc<Integer>() {
            @Override
            public Subscription onSubscribe(final Observer<? super Integer> Observer) {
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Observer.onNext(prattle.sendMessage(room, message));
                            Observer.onCompleted();
                        } catch (Throwable thr) {
                            Observer.onError(thr);
                        }
                    }
                });
                return Subscriptions.empty();
            }
        });
    }
}
