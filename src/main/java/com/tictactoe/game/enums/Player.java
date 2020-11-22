package com.tictactoe.game.enums;

public enum Player {

    PLAYER1("X"),
    PLAYER2("O"),
    NOBODY("NOBODY");

    private String value;

    Player(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
