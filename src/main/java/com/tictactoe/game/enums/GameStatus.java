package com.tictactoe.game.enums;

public enum GameStatus {

    ONGOING("ONGOING"),
    COMPLETED("COMPLETED"),
    TIED("TIED");

    private String value;

    GameStatus(String value) {
        this.value = value;
    }
}
