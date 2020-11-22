package com.tictactoe.game.service;

import com.tictactoe.game.dto.GameDTO;
import com.tictactoe.game.dto.Index;
import com.tictactoe.game.dto.Move;
import com.tictactoe.game.enums.GameStatus;
import com.tictactoe.game.enums.Mode;
import com.tictactoe.game.enums.Player;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TicTacToeService {

    private static final int TIC_TAC_TOE_SIZE = 3;

    public GameDTO startGame(Mode mode) {
        GameDTO gameDTO = new GameDTO();
        String[][] board = new String[TIC_TAC_TOE_SIZE][TIC_TAC_TOE_SIZE];
        for (String[] row : board) {
            Arrays.fill(row, "");
        }
        Random rand = new Random();
        boolean randomFlag = rand.nextInt(10) < 5;

        if (Mode.PLAYER1vsPLAYER2.equals(mode)) {

            String uniqueID = UUID.randomUUID().toString();
            gameDTO.setId(uniqueID);
            gameDTO.setBoard(board);
            gameDTO.setMode(mode);
            gameDTO.setGameStatus(GameStatus.ONGOING);
            Player startPlayer = randomFlag ? Player.PLAYER1 : Player.PLAYER2;
            gameDTO.setPlayer(startPlayer);
            gameDTO.setSymbol(startPlayer.getValue());
        } else {
            String uniqueID = UUID.randomUUID().toString();
            gameDTO.setId(uniqueID);

            if (randomFlag) {
                Move aIMove = getAIBestMove(board);
                board[aIMove.getX()][aIMove.getY()] = Player.PLAYER2.getValue();
            }

            gameDTO.setBoard(board);
            gameDTO.setMode(mode);
            gameDTO.setGameStatus(GameStatus.ONGOING);
            gameDTO.setPlayer(Player.PLAYER1);
            gameDTO.setSymbol(Player.PLAYER1.getValue());
        }
        return gameDTO;
    }

    public GameDTO registerMove(GameDTO gameDTO) {

        List<Index> winningIndexes = new ArrayList<>();
        if (Objects.nonNull(gameDTO)) {

            //check if there is a winner
            String[][] currentBoard = gameDTO.getBoard();
            for (int i = 0; i < 3; i++) {
                //row
                if (currentBoard[i][0].equals(currentBoard[i][1]) && currentBoard[i][1].equals(currentBoard[i][2]) && (currentBoard[i][0].equals("X") || currentBoard[i][0].equals("O"))) {
                    gameDTO.setWinner(gameDTO.getPlayer());
                    winningIndexes.add(new Index(i, 0));
                    winningIndexes.add(new Index(i, 1));
                    winningIndexes.add(new Index(i, 2));
                    gameDTO.setWinningIndexes(winningIndexes);
                    break;
                }
                //column
                if (currentBoard[0][i].equals(currentBoard[1][i]) && currentBoard[1][i].equals(currentBoard[2][i]) && (currentBoard[0][i].equals("X") || currentBoard[0][i].equals("O"))) {
                    gameDTO.setWinner(gameDTO.getPlayer());
                    winningIndexes.add(new Index(0, i));
                    winningIndexes.add(new Index(1, i));
                    winningIndexes.add(new Index(2, i));
                    gameDTO.setWinningIndexes(winningIndexes);
                    break;
                }
            }
            //diagonal
            if (Objects.isNull(gameDTO.getWinner())) {
                if (currentBoard[0][0].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][2]) && (currentBoard[0][0].equals("X") || currentBoard[0][0].equals("O"))) {
                    gameDTO.setWinner(gameDTO.getPlayer());
                    winningIndexes.add(new Index(0, 0));
                    winningIndexes.add(new Index(1, 1));
                    winningIndexes.add(new Index(2, 2));
                    gameDTO.setWinningIndexes(winningIndexes);
                }
                if (currentBoard[0][2].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][0]) && (currentBoard[0][2].equals("X") || currentBoard[0][2].equals("O"))) {
                    gameDTO.setWinner(gameDTO.getPlayer());
                    winningIndexes.add(new Index(0, 2));
                    winningIndexes.add(new Index(1, 1));
                    winningIndexes.add(new Index(2, 0));
                    gameDTO.setWinningIndexes(winningIndexes);
                }
            }

            if (Objects.isNull(gameDTO.getWinner())) {
                boolean isOnGoing = isOngoing(currentBoard);
                if (isOnGoing) {
                    gameDTO.setGameStatus(GameStatus.ONGOING);
                    gameDTO.setWinner(null);
                }
            }

            //check if tied
            if (Objects.isNull(gameDTO.getWinner())) {
                boolean tiedFlag = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (!currentBoard[i][j].equals("")) {
                            continue;
                        } else {
                            tiedFlag = false;
                        }
                    }
                }

                if (tiedFlag) {
                    gameDTO.setGameStatus(GameStatus.TIED);
                    gameDTO.setWinner(Player.NOBODY);
                }
            }

            if (Objects.isNull(gameDTO.getWinner()) && gameDTO.getMode().equals(Mode.PLAYER1vsPLAYER2)) {
                //toggle player
                if (Player.PLAYER1.equals(gameDTO.getPlayer())) {
                    gameDTO.setPlayer(Player.PLAYER2);
                } else {
                    gameDTO.setPlayer(Player.PLAYER1);
                }

                //toggle symbol
                if (Player.PLAYER1.getValue().equals(gameDTO.getSymbol())) {
                    gameDTO.setSymbol(Player.PLAYER2.getValue());
                } else {
                    gameDTO.setSymbol(Player.PLAYER1.getValue());
                }

            }

            if (Objects.nonNull(gameDTO.getWinner()) && !GameStatus.TIED.equals(gameDTO.getGameStatus())) {
                gameDTO.setGameStatus(GameStatus.COMPLETED);
            }
        }

        return gameDTO;
    }

    public GameDTO registerAIMove(GameDTO gameDTO) {

        if (getAllPossibleMoves(gameDTO.getBoard()).size() > 0) {

            Move aIMove = getAIBestMove(gameDTO.getBoard());
            String[][] currentBoard = gameDTO.getBoard();
            currentBoard[aIMove.getX()][aIMove.getY()] = Player.PLAYER2.getValue();
            gameDTO.setBoard(currentBoard);
        }

        List<Index> winningIndexes = new ArrayList<>();
        if (Objects.nonNull(gameDTO)) {

            //check if there is a winner
            String[][] currentBoard = gameDTO.getBoard();

            for (int i = 0; i < 3; i++) {
                //row
                if (currentBoard[i][0].equals(currentBoard[i][1]) && currentBoard[i][1].equals(currentBoard[i][2]) && (currentBoard[i][0].equals("X") || currentBoard[i][0].equals("O"))) {
                    winningIndexes.add(new Index(i, 0));
                    winningIndexes.add(new Index(i, 1));
                    winningIndexes.add(new Index(i, 2));
                    gameDTO.setWinningIndexes(winningIndexes);

                    if (currentBoard[i][0].equals("X")) {
                        gameDTO.setWinner(Player.PLAYER1);
                    } else {
                        gameDTO.setWinner(Player.PLAYER2);
                    }
                    break;
                }
                //column
                if (currentBoard[0][i].equals(currentBoard[1][i]) && currentBoard[1][i].equals(currentBoard[2][i]) && (currentBoard[0][i].equals("X") || currentBoard[0][i].equals("O"))) {

                    winningIndexes.add(new Index(0, i));
                    winningIndexes.add(new Index(1, i));
                    winningIndexes.add(new Index(2, i));
                    gameDTO.setWinningIndexes(winningIndexes);

                    if (currentBoard[i][0].equals("X")) {
                        gameDTO.setWinner(Player.PLAYER1);
                    } else {
                        gameDTO.setWinner(Player.PLAYER2);
                    }
                    break;
                }
            }
            //diagonal
            if (Objects.isNull(gameDTO.getWinner())) {
                if (currentBoard[0][0].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][2]) && (currentBoard[0][0].equals("X") || currentBoard[0][0].equals("O"))) {

                    winningIndexes.add(new Index(0, 0));
                    winningIndexes.add(new Index(1, 1));
                    winningIndexes.add(new Index(2, 2));
                    gameDTO.setWinningIndexes(winningIndexes);
                    if (currentBoard[0][0].equals("X")) {
                        gameDTO.setWinner(Player.PLAYER1);
                    } else {
                        gameDTO.setWinner(Player.PLAYER2);
                    }
                }
                if (currentBoard[0][2].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][0]) && (currentBoard[0][2].equals("X") || currentBoard[0][2].equals("O"))) {

                    winningIndexes.add(new Index(0, 2));
                    winningIndexes.add(new Index(1, 1));
                    winningIndexes.add(new Index(2, 0));
                    gameDTO.setWinningIndexes(winningIndexes);

                    if (currentBoard[0][2].equals("X")) {
                        gameDTO.setWinner(Player.PLAYER1);
                    } else {
                        gameDTO.setWinner(Player.PLAYER2);
                    }
                }
            }

            if (Objects.isNull(gameDTO.getWinner())) {
                boolean isOnGoing = isOngoing(currentBoard);
                if (isOnGoing) {
                    gameDTO.setGameStatus(GameStatus.ONGOING);
                    gameDTO.setWinner(null);
                }
            }

            //check if tied
            if (Objects.isNull(gameDTO.getWinner())) {
                boolean tiedFlag = true;
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (!currentBoard[i][j].equals("")) {
                            continue;
                        } else {
                            tiedFlag = false;
                        }
                    }
                }

                if (tiedFlag) {
                    gameDTO.setGameStatus(GameStatus.TIED);
                    gameDTO.setWinner(Player.NOBODY);
                }
            }

            if (Objects.nonNull(gameDTO.getWinner()) && !GameStatus.TIED.equals(gameDTO.getGameStatus())) {
                gameDTO.setGameStatus(GameStatus.COMPLETED);
            }
        }

        return gameDTO;
    }

    private boolean isOngoing(String[][] currentBoard) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (currentBoard[i][j].equals("")) {
                    return true;
                }
            }
        }
        return false;
    }

    private Move getAIBestMove(String[][] currentBoard) {
        Move bestMoveForAI = getBestMoveForPlayer(currentBoard, Player.PLAYER2);
        return bestMoveForAI;
    }

    //Using MIN MAX algorithm to find best move for given player
    private Move getBestMoveForPlayer(String[][] currentBoard, Player player) {
        List<Move> possibleMoves = getAllPossibleMoves(currentBoard);

        //Player 2 is always AI
        if (hasWonTheGame(currentBoard, Player.PLAYER2.getValue())) {
            return new Move(-1, -1, +10);
        } else if (hasWonTheGame(currentBoard, Player.PLAYER1.getValue())) {
            //Player 1 is always human
            return new Move(-1, -1, -10);
        } else if (possibleMoves.size() == 0) {
            return new Move(-1, -1, 0);
        }

        List<Move> allMoves = new ArrayList<>();

        for (Move currentMove : possibleMoves) {
            currentBoard[currentMove.getX()][currentMove.getY()] = player.getValue();
            Move nextMoveScore = getBestMoveForPlayer(currentBoard, player == Player.PLAYER1 ? Player.PLAYER2 : Player.PLAYER1);
            currentMove.setScore(nextMoveScore.getScore());
            currentBoard[currentMove.getX()][currentMove.getY()] = "";
            allMoves.add(currentMove);
        }

        //We have to find MAX score for AI and MIN score for human
        Move bestMove = new Move(-1, -1, player == Player.PLAYER1 ? Integer.MAX_VALUE : Integer.MIN_VALUE);

        //Human case for MIN score
        if (player == Player.PLAYER1) {
            for (Move move : allMoves) {
                if (move.getScore() < bestMove.getScore()) {
                    bestMove = move;
                }
            }
        }

        //Computer AI case for MAX score
        if (player == Player.PLAYER2) {
            for (Move move : allMoves) {
                if (move.getScore() > bestMove.getScore()) {
                    bestMove = move;
                }
            }
        }

        return bestMove;
    }

    private boolean hasWonTheGame(String[][] currentBoard, String symbol) {
        for (int i = 0; i < 3; i++) {
            //row wise check
            if (currentBoard[i][0].equals(currentBoard[i][1]) && currentBoard[i][1].equals(currentBoard[i][2]) && currentBoard[i][0].equals(symbol)) {
                return true;
            }
            //column wise check
            if (currentBoard[0][i].equals(currentBoard[1][i]) && currentBoard[1][i].equals(currentBoard[2][i]) && currentBoard[0][i].equals(symbol)) {
                return true;
            }
        }
        //diagonal wise check
        if (currentBoard[0][0].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][2]) && currentBoard[0][0].equals(symbol)) {
            return true;
        }
        if (currentBoard[0][2].equals(currentBoard[1][1]) && currentBoard[1][1].equals(currentBoard[2][0]) && currentBoard[0][2].equals(symbol)) {
            return true;
        }
        return false;
    }

    private List<Move> getAllPossibleMoves(String[][] currentBoard) {
        List<Move> allMoves = new ArrayList<>();

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (currentBoard[x][y] == "") {
                    allMoves.add(new Move(x, y, 0));
                }
            }
        }

        Collections.shuffle(allMoves);
        return allMoves;
    }
}