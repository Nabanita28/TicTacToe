package com.tictactoe.game.controller;

import com.tictactoe.game.dto.GameDTO;
import com.tictactoe.game.enums.Mode;
import com.tictactoe.game.service.TicTacToeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TicTacToeController {

    private final TicTacToeService ticTacToeService;

    @Autowired
    public TicTacToeController(TicTacToeService ticTacToeService) {
        this.ticTacToeService = ticTacToeService;
    }

    @GetMapping(path = "/tictactoe/api/v1/startGame")
    public ResponseEntity<GameDTO> startGame(@RequestParam("mode") Mode mode) {
        return ResponseEntity.ok().body(ticTacToeService.startGame(mode));
    }

    @PostMapping(path = "/tictactoe/api/v1/registerMove")
    public ResponseEntity<GameDTO> registerMove(@RequestBody GameDTO gameDTO) {
        if (Mode.PLAYER1vsPLAYER2.equals(gameDTO.getMode())) {
            return ResponseEntity.ok().body(ticTacToeService.registerMove(gameDTO));
        } else {
            return ResponseEntity.ok().body(ticTacToeService.registerAIMove(gameDTO));
        }
    }
}
