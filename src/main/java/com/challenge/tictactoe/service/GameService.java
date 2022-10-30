package com.challenge.tictactoe.service;

import java.util.UUID;

import javax.management.InvalidAttributeValueException;
import javax.naming.NameNotFoundException;

import org.springframework.stereotype.Service;

import com.challenge.tictactoe.api.request.GameRequest;
import com.challenge.tictactoe.api.response.GameResponse;
import com.challenge.tictactoe.repo.GameInfoRepository;
import com.challenge.tictactoe.repo.entity.GameInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameInfoRepository gameInfoRepository;

    public UUID[] getAll() {
        return gameInfoRepository.getAll();
    }

    public GameResponse getById(UUID id) throws NameNotFoundException {
        final GameInfo[] gameInfoArray = gameInfoRepository.getById(id);
        if (gameInfoArray.length == 0)
            throw new NameNotFoundException();

        return buildGameResponse(gameInfoArray, id);
    }

    public GameResponse createGame(GameRequest request) throws InvalidAttributeValueException {
        if (!"X".equals(request.getSymbol()))
            throw new InvalidAttributeValueException("Invalid symbol");

        final UUID id = UUID.randomUUID();
        final GameInfo[] gameInfo = gameInfoRepository.playTurn(id, request);

        return buildGameResponse(gameInfo, id);
    }

    public GameResponse playTurn(UUID id, GameRequest request)
            throws NameNotFoundException, InvalidAttributeValueException {
        final GameInfo[] gameInfoArray = gameInfoRepository.getById(id);
        if (gameInfoArray.length == 0)
            throw new NameNotFoundException();

        final GameResponse oldGameResponse = buildGameResponse(gameInfoArray, id);
        if (oldGameResponse.getWinner() != null)
            throw new InvalidAttributeValueException("Game winner has already been decided");

        if (!oldGameResponse.getNextSymbol().equals(request.getSymbol()))
            throw new InvalidAttributeValueException("Invalid symbol");

        return buildGameResponse(gameInfoRepository.playTurn(id, request), id);
    }

    public void deleteById(UUID id) throws NameNotFoundException {
        if (gameInfoRepository.getById(id).length == 0)
            throw new NameNotFoundException();
        gameInfoRepository.deleteById(id);
    }

    private String checkWinner(String[][] matrix) {
        for (int i = 0; i < 3; i++) {
            final String xSymbol = matrix[i][0]; // Check horizontal lines for winner
            if (isNotNullAndEquals(xSymbol, matrix[i][1]) && isNotNullAndEquals(xSymbol, matrix[i][2])) {

                return xSymbol;
            }

            final String ySymbol = matrix[0][i]; // Check vertical lines for winner
            if (isNotNullAndEquals(ySymbol, matrix[1][i]) && isNotNullAndEquals(ySymbol, matrix[2][i])) {

                return ySymbol;
            }
        }

        // Check diagonal lines for winner
        final String topLeftSymbol = matrix[0][0];
        if (isNotNullAndEquals(topLeftSymbol, matrix[1][1]) && isNotNullAndEquals(topLeftSymbol, matrix[2][2]))
            return topLeftSymbol;

        final String topRightSymbol = matrix[0][2];
        if (isNotNullAndEquals(topRightSymbol, matrix[1][1]) && isNotNullAndEquals(topRightSymbol, matrix[2][0]))
            return topRightSymbol;

        return null;
    }

    private String nextSymbol(String[][] matrix) {
        int x = 0;
        int o = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final String symbol = matrix[i][j];
                if (isNotNullAndEquals(symbol, "X"))
                    x++;
                else if (isNotNullAndEquals(symbol, "O"))
                    o++;
            }
        }

        if (x > o)
            return "O";

        return "X";
    }

    private boolean isNotNullAndEquals(String v1, String v2) {
        return v1 != null && v2 != null && v1.equals(v2);
    }

    private GameResponse buildGameResponse(GameInfo[] gameInfoArray, UUID id) {
        String[][] matrix = new String[3][3];

        for (GameInfo info : gameInfoArray) {
            matrix[info.getX()][info.getY()] = info.getSymbol();
        }

        final String winner = checkWinner(matrix);

        if (winner != null)
            return new GameResponse(id, winner, null, matrix);
        else
            return new GameResponse(id, null, nextSymbol(matrix), matrix);
    }
}
