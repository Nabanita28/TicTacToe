package com.tictactoe.game.dto;

import com.tictactoe.game.enums.GameStatus;
import com.tictactoe.game.enums.Mode;
import com.tictactoe.game.enums.Player;
import lombok.Data;

import java.util.List;

@Data
public class GameDTO {

    private String id;
    private GameStatus gameStatus;
    private Mode mode;
    private Player player;
    private String symbol;
    String[][] board;
    private List<Index> winningIndexes;
    private Player winner;
}
