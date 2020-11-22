package com.tictactoe.game.dto;

import lombok.Data;

@Data
public class Move {
    private int x;
    private int y;
    private int score;


    public Move(int x, int y, int score) {
        this.x=x;
        this.y=y;
        this.score=score;
    }
}
