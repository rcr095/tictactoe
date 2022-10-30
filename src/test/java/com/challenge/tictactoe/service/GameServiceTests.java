package com.challenge.tictactoe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.management.InvalidAttributeValueException;
import javax.naming.NameNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.challenge.tictactoe.api.request.GameRequest;
import com.challenge.tictactoe.api.response.GameResponse;
import com.challenge.tictactoe.repo.GameInfoRepository;
import com.challenge.tictactoe.repo.entity.GameInfo;

@ExtendWith(MockitoExtension.class)
public class GameServiceTests {

    @InjectMocks
    private GameService gameService;

    private GameInfoRepository gameInfoRepository = mock(GameInfoRepository.class);

    private final UUID id = UUID.randomUUID();

    @Test
    public void canCreateGame() throws InvalidAttributeValueException, NameNotFoundException {
        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("X");
        gameRequest.setX(0);
        gameRequest.setY(0);

        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo });

        GameResponse created = gameService.createGame(gameRequest);

        assertNull(created.getWinner());
        assertNotNull(created.getId());
        assertEquals(3, created.getGameMatrix().length);
        assertEquals(3, created.getGameMatrix()[0].length);
        assertEquals("O", created.getNextSymbol());
    }

    @Test
    public void canGetAllWhenEmpty() {
        when(gameInfoRepository.getAll()).thenReturn(new UUID[0]);
        assertEquals(0, gameService.getAll().length);
    }

    @Test
    public void canGetAll_whenPresent() throws InvalidAttributeValueException, NameNotFoundException {
        when(gameInfoRepository.getAll()).thenReturn(new UUID[] { id });

        UUID[] gameList = gameService.getAll();

        assertEquals(1, gameList.length);
        assertEquals(id, gameList[0]);
    }

    @Test
    public void getByIdThrowException_whenInvalidId() throws NameNotFoundException {
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[0]);

        assertThrows(NameNotFoundException.class, () -> gameService.getById(UUID.randomUUID()));
    }

    @Test
    public void canGetById_whenNoWinnerIsPresent() throws NameNotFoundException {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(0);

        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[] { gameInfo });

        GameResponse game = gameService.getById(id);

        assertEquals(id, game.getId());
        assertEquals("O", game.getNextSymbol());
        assertNull(game.getWinner());
    }

    @Test
    public void canGetById_whenWinnerIsPresent() throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setId(id.toString());
        gameInfo1.setSymbol("X");
        gameInfo1.setX(0);
        gameInfo1.setY(0);

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setId(id.toString());
        gameInfo2.setSymbol("X");
        gameInfo2.setX(1);
        gameInfo2.setY(1);

        GameInfo gameInfo3 = new GameInfo();
        gameInfo3.setId(id.toString());
        gameInfo3.setSymbol("X");
        gameInfo3.setX(2);
        gameInfo3.setY(2);

        when(gameInfoRepository.getById(any(UUID.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2, gameInfo3 });

        GameResponse game = gameService.getById(id);

        assertEquals(id, game.getId());
        assertEquals("X", game.getWinner());
        assertNull(game.getNextSymbol());
    }

    @Test
    public void playTurnThrowException_whenInvalidId() throws NameNotFoundException {
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[0]);

        assertThrows(NameNotFoundException.class, () -> gameService.playTurn(UUID.randomUUID(), new GameRequest()));
    }

    @Test
    public void playTurnThrowException_whenInvalidSymbol()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(0);
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[] { gameInfo });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("Y");
        gameRequest.setX(0);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenThrow(new InvalidAttributeValueException(
                        "Invalid request"));

        InvalidAttributeValueException thrown = assertThrows(
                InvalidAttributeValueException.class,
                () -> gameService.playTurn(id, gameRequest));

        assertTrue(thrown.getMessage().contains("Invalid symbol"));
    }

    @Test
    public void playTurnThrowException_whenRepeatedPlayer()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(2);
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[] { gameInfo });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("X");
        gameRequest.setX(0);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenThrow(new InvalidAttributeValueException(
                        "Invalid request"));

        InvalidAttributeValueException thrown = assertThrows(
                InvalidAttributeValueException.class,
                () -> gameService.playTurn(id, gameRequest));

        assertTrue(thrown.getMessage().contains("Invalid symbol"));
    }

    @Test
    public void playTurnThrowException_whenRepeatedCoordinates()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(0);
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[] { gameInfo });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(0);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenThrow(new InvalidAttributeValueException(
                        "Invalid request"));

        InvalidAttributeValueException thrown = assertThrows(
                InvalidAttributeValueException.class,
                () -> gameService.playTurn(id, gameRequest));

        assertTrue(thrown.getMessage().contains("Invalid request"));
    }

    @Test
    public void canPlayTurn_whenNoWinnerIsPresent() throws InvalidAttributeValueException, NameNotFoundException {

        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(id.toString());
        gameInfo.setSymbol("X");
        gameInfo.setX(0);
        gameInfo.setY(0);
        when(gameInfoRepository.getById(any(UUID.class))).thenReturn(new GameInfo[] { gameInfo });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(1);
        gameRequest.setY(1);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo });

        GameResponse game = gameService.playTurn(id, gameRequest);

        assertEquals(id, game.getId());
        assertNull(game.getWinner());
        assertNotNull(game.getNextSymbol());
    }

    @Test
    public void canPlayTurn_whenHorizontalWinnerIsPresent()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setId(id.toString());
        gameInfo1.setSymbol("X");
        gameInfo1.setX(0);
        gameInfo1.setY(0);

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setId(id.toString());
        gameInfo2.setSymbol("X");
        gameInfo2.setX(1);
        gameInfo2.setY(0);

        GameInfo gameInfo3 = new GameInfo();
        gameInfo3.setId(id.toString());
        gameInfo3.setSymbol("X");
        gameInfo3.setX(2);
        gameInfo3.setY(0);

        when(gameInfoRepository.getById(any(UUID.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2 });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(2);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2, gameInfo3 });

        GameResponse game = gameService.playTurn(id, gameRequest);

        assertEquals(id, game.getId());
        assertNotNull(game.getWinner());
        assertNull(game.getNextSymbol());
    }

    @Test
    public void canPlayTurn_whenVerticalWinnerIsPresent()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setId(id.toString());
        gameInfo1.setSymbol("X");
        gameInfo1.setX(0);
        gameInfo1.setY(0);

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setId(id.toString());
        gameInfo2.setSymbol("X");
        gameInfo2.setX(0);
        gameInfo2.setY(1);

        GameInfo gameInfo3 = new GameInfo();
        gameInfo3.setId(id.toString());
        gameInfo3.setSymbol("X");
        gameInfo3.setX(0);
        gameInfo3.setY(2);

        when(gameInfoRepository.getById(any(UUID.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2 });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(2);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2, gameInfo3 });

        GameResponse game = gameService.playTurn(id, gameRequest);

        assertEquals(id, game.getId());
        assertNotNull(game.getWinner());
        assertNull(game.getNextSymbol());
    }

    @Test
    public void canPlayTurn_whenDiagonalTopLeftWinnerIsPresent()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setId(id.toString());
        gameInfo1.setSymbol("X");
        gameInfo1.setX(0);
        gameInfo1.setY(0);

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setId(id.toString());
        gameInfo2.setSymbol("X");
        gameInfo2.setX(1);
        gameInfo2.setY(1);

        GameInfo gameInfo3 = new GameInfo();
        gameInfo3.setId(id.toString());
        gameInfo3.setSymbol("X");
        gameInfo3.setX(2);
        gameInfo3.setY(2);

        when(gameInfoRepository.getById(any(UUID.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2 });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(2);
        gameRequest.setY(0);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2, gameInfo3 });

        GameResponse game = gameService.playTurn(id, gameRequest);

        assertEquals(id, game.getId());
        assertNotNull(game.getWinner());
        assertNull(game.getNextSymbol());
    }

    @Test
    public void canPlayTurn_whenDiagonalTopRightWinnerIsPresent()
            throws InvalidAttributeValueException, NameNotFoundException {
        GameInfo gameInfo1 = new GameInfo();
        gameInfo1.setId(id.toString());
        gameInfo1.setSymbol("X");
        gameInfo1.setX(2);
        gameInfo1.setY(0);

        GameInfo gameInfo2 = new GameInfo();
        gameInfo2.setId(id.toString());
        gameInfo2.setSymbol("X");
        gameInfo2.setX(1);
        gameInfo2.setY(1);

        GameInfo gameInfo3 = new GameInfo();
        gameInfo3.setId(id.toString());
        gameInfo3.setSymbol("X");
        gameInfo3.setX(0);
        gameInfo3.setY(2);

        when(gameInfoRepository.getById(any(UUID.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2 });

        GameRequest gameRequest = new GameRequest();
        gameRequest.setSymbol("O");
        gameRequest.setX(1);
        gameRequest.setY(2);
        when(gameInfoRepository.playTurn(any(UUID.class), any(GameRequest.class)))
                .thenReturn(new GameInfo[] { gameInfo1, gameInfo2, gameInfo3 });

        GameResponse game = gameService.playTurn(id, gameRequest);

        assertEquals(id, game.getId());
        assertNotNull(game.getWinner());
        assertNull(game.getNextSymbol());
    }
}
