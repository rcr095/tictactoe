package com.challenge.tictactoe.api.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameResponse {
    private UUID id;
    private String winner;
    private String nextSymbol;
    private String[][] gameMatrix;
}
