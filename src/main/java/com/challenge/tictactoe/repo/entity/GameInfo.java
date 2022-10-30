package com.challenge.tictactoe.repo.entity;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameInfo {

    private UUID id;
    private String symbol;
    private int x;
    private int y;

    public void setId(String id) {
        this.id = UUID.fromString(id);
    }
}
