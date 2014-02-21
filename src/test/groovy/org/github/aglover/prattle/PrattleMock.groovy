package org.github.aglover.prattle

class PrattleMock extends Prattle {

    @Override
    Integer sendMessage(String address, String message) {
        return 204
    }

    @Override
    List<User> allParticipantsIn(final String address) {
        [new User(id: 568915, name: "Andrew Glover", mention: "AndrewGlover"),
                new User(id: 334670, name: "Cameron Fieber", mention: "cfieber")]
    }

    @Override
    User getUser(Integer id) {
        new User(id: 568915, name: "Andrew Glover", mention: "AndrewGlover")
    }

    @Override
    List<User> allMembersOf(String room) {
        [new User(id: 568915, name: "Andrew Glover", mention: "AndrewGlover"),
                new User(id: 167037, name: "Joe Sondow", mention: "JoeSondow"),
                new User(id: 664234, name: "Daniel Woods", mention: "DanielWoods"),
                new User(id: 334670, name: "Cameron Fieber", mention: "cfieber")]
    }
}
