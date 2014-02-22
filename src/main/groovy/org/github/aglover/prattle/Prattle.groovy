package org.github.aglover.prattle

import groovyx.net.http.RESTClient
import org.github.aglover.prattle.exception.TokenNotProvidedException

import static groovyx.net.http.ContentType.JSON

class Prattle {

    String token

    protected String encode(final String value) {
        new URLEncoder().encode(value, "UTF-8").replace('+', '%20')
    }

    protected String secureURL(String url) {
        if(this.token == null || this.token.equals("")){
            throw new TokenNotProvidedException("You must provide a HipChat token!")
        }
        "${url}?auth_token=${encode(this.token)}"
    }

    private def doGet(String url, Closure closure) {
        def response = new RESTClient(secureURL(url))
                .get(contentType: JSON, requestContentType: JSON)
        return closure.call(response)
    }

    Integer sendMessage(final String room, final String message) {
        new RESTClient(secureURL("https://api.hipchat.com/v2/room/${encode(room)}/notification"))
                .post(contentType: JSON, requestContentType: JSON, body: [message: message]).status
    }

    User getUser(final Integer id) {
        return doGet("https://api.hipchat.com/v2/user/${id}") { response ->
            if (response.status == 200) {
                new User(id: response.data.id, name: response.data.name, mention: response.data.mention_name)
            } else {
                throw new Exception("response wasn't 200")
            }
        }
    }

    List<User> allMembersOf(final String room) {
        return doGet("https://api.hipchat.com/v2/room/${encode(room)}/member") { response ->
            if (response.status == 200) {
                response.data.items.collect {
                    new User(id: it.id, name: it.name, mention: it.mention_name)
                }
            } else {
                throw new Exception("response wasn't 200")
            }
        }
    }

    List<User> allParticipantsIn(final String room) {
        return doGet("https://api.hipchat.com/v2/room/${encode(room)}/member") { response ->
            if (response.status == 200) {
                response.data.participants.collect {
                    new User(id: it.id, name: it.name, mention: it.mention_name)
                }
            } else {
                throw new Exception("response wasn't 200")
            }
        }
    }
}
