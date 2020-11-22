package com.tictactoe.game.dto;

import lombok.Data;

@Data
public class Index {
    int row;
    int column;

    public Index(int row, int column){
        this.row = row;
        this.column = column;
    }
}
