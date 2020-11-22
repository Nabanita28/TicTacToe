package com.tictactoe.game.enums;

public enum Mode {
    PLAYER1vsPLAYER2("PLAYER1vsPLAYER2"),
    PLAYERvsAI("PLAYERvsAI");

    private String value;

    private Mode(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

    public void setValue(String value){
        this.value = value;
    }
}
