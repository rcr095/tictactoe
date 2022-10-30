package com.challenge.tictactoe.api.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameRequest {
    private int x;
    private int y;
    private String symbol;
}
