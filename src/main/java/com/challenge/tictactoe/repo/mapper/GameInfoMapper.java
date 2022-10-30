package com.challenge.tictactoe.repo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.challenge.tictactoe.repo.entity.GameInfo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GameInfoMapper implements RowMapper<GameInfo> {

    @Override
    public GameInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        GameInfo gameInfo = new GameInfo();
        gameInfo.setId(rs.getString("id"));
        gameInfo.setSymbol(rs.getString("symbol"));
        gameInfo.setX(rs.getInt("x"));
        gameInfo.setY(rs.getInt("y"));

        return gameInfo;
    }
}
