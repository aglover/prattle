package org.github.aglover.prattle

import com.jayway.awaitility.groovy.AwaitilitySupport
import groovyx.net.http.RESTClient
import org.github.aglover.prattle.exception.TokenNotProvidedException
import rx.util.functions.Action1
import rx.util.functions.Func1
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON
import static java.util.concurrent.TimeUnit.SECONDS

@Mixin(AwaitilitySupport)
class PrattleClientSpec extends Specification {

    def "an exception should be thrown if no token is provided"() {
        setup:
        def client = new Prattle()

        when:
        client.allMembersOf("some room")

        then:
        thrown TokenNotProvidedException
    }

    def "it should get a 202 for auth test"() {
        def token = new URLEncoder().encode(System.properties["hipchat.token"], "UTF-8")
        def url = "https://api.hipchat.com/v2/room?auth_token=${token}&auth_test=true"

        when:
        def response = new RESTClient(url).get(contentType: JSON, requestContentType: JSON)

        then:
        response.status == 202
    }

    def "it should return a user"() {
        def prattle = new PrattleMock()

        expect:
        def res = prattle.getUser(568915)
        res.name == "Andrew Glover"
    }

    def "it should return a user Rx"() {
        def prattle = new PrattleClient(new PrattleMock())
        def name = ""

        expect:
        prattle.getUser(568915).subscribe(new Action1<User>() {
            @Override
            void call(User user) {
                name = user.name
            }
        })

        await().atMost(2, SECONDS).until { name == "Andrew Glover" }
    }

    def "Sending a message should result in a 204 status from hipchat"() {
        def prattle = new PrattleClient(new PrattleMock())

        def answer = 0
        expect:
        prattle.sendMessage("Cloud Interface Tools", "Hello from Async RxJava").subscribe(new Action1<Integer>() {
            @Override
            void call(Integer integer) {
                answer = integer
            }
        })

        await().atMost(2, SECONDS).until { answer == 204 }
    }

    def "it should return a list of users for a room non-Rx"() {
        def prattle = new PrattleMock()
        def list = prattle.allParticipantsIn("Cloud Interface Tools")

        expect:
        list != null
        list.size() == 2
        list.each {
            it.id != null
            it.name != null
            it.mention != null
        }
    }

    def "it should return a list of users for a room rx"() {
        def prattle = new PrattleClient(new PrattleMock())
        def users = []

        expect:
        prattle.participants("Cloud Interface Tools").subscribe(new Action1<User>() {
            @Override
            void call(User iUsers) {
                users << iUsers
            }
        })
        await().atMost(2, SECONDS).until { users.size() == 2 }
    }

    def "it should return a list of users for a room rx count"() {
        def prattle = new PrattleClient(new PrattleMock())
        def value = 0

        expect:
        prattle.participants("Cloud Interface Tools").count().subscribe(new Action1<Integer>() {
            @Override
            void call(Integer integer) {
                value = integer
            }
        })
        await().atMost(2, SECONDS).until { value == 2 }
    }

    def "it should return a list of users for a room rx limit to 1"() {
        def prattle = new PrattleClient(new PrattleMock())
        def expect = null

        expect:
        prattle.participants("Cloud Interface Tools").filter(new Func1<User, Boolean>() {
            @Override
            Boolean call(User user) {
                return user.name.equalsIgnoreCase("Andrew Glover")
            }
        }).subscribe(new Action1<User>() {
            @Override
            void call(User iUser) {
                expect = iUser
            }
        })
        await().atMost(2, SECONDS).until { expect.id == 568915 }
    }

    def "it should get all members of a room rx"() {
        def prattle = new PrattleClient(new PrattleMock())
        def value = 0

        expect:
        prattle.members("Cloud Interface Tools").count().subscribe(new Action1<Integer>() {
            @Override
            void call(Integer integer) {
                value = integer
            }
        })
        await().atMost(2, SECONDS).until { value == 4 }
    }
}
