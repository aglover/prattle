package org.github.aglover.prattle

class PrattleMock extends Prattle {

    @Override
    Integer sendMessage(String address, String message) {
        return 204
    }

    @Override
    Integer sendMessage(Integer userId, String message) {
        return 204
    }

    @Override
    List<User> allParticipantsIn(final String address) {
        [new User(id: 568999, name: "Andrew Smith", mention: "AndrewSmith"),
                new User(id: 334699, name: "Cameron Bloch", mention: "cbloch")]
    }

    @Override
    User getUser(Integer id) {
        new User(id: 568999, name: "Andrew Smith", mention: "AndrewSmith")
    }

    @Override
    List<User> allMembersOf(String room) {
        [new User(id: 568999, name: "Andrew Smith", mention: "AndrewSmith"),
                new User(id: 167022, name: "Joe Sunny", mention: "JoeSunny"),
                new User(id: 664211, name: "Daniel Hughs", mention: "DanielHughs"),
                new User(id: 334699, name: "Cameron Bloch", mention: "cbloch")]
    }
}
