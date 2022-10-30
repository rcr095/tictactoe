package com.challenge.tictactoe.repo.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class IDMapper implements RowMapper<UUID> {

    @Override
    public UUID mapRow(ResultSet rs, int rowNum) throws SQLException {
        return UUID.fromString(rs.getString("id"));
    }
}
