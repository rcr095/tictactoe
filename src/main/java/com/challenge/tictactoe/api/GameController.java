package com.challenge.tictactoe.api;

import java.util.UUID;

import javax.management.InvalidAttributeValueException;
import javax.naming.NameNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.challenge.tictactoe.api.request.GameRequest;
import com.challenge.tictactoe.api.response.GameResponse;
import com.challenge.tictactoe.service.GameService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<UUID[]> getGames() {
        return ResponseEntity.ok(gameService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGameById(@PathVariable UUID id) throws NameNotFoundException {
        return ResponseEntity.ok(gameService.getById(id));
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest body)
            throws InvalidAttributeValueException, NameNotFoundException {
        return new ResponseEntity<>(gameService.createGame(body), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GameResponse> playTurn(@PathVariable UUID id, @RequestBody GameRequest body)
            throws InvalidAttributeValueException, NameNotFoundException {
        return ResponseEntity.ok(gameService.playTurn(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws NameNotFoundException {
        gameService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
