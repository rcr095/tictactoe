package com.challenge.tictactoe.repo;

import java.util.UUID;

import javax.management.InvalidAttributeValueException;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.challenge.tictactoe.api.request.GameRequest;
import com.challenge.tictactoe.repo.entity.GameInfo;
import com.challenge.tictactoe.repo.mapper.GameInfoMapper;
import com.challenge.tictactoe.repo.mapper.IDMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class GameInfoRepository {

    private final IDMapper idMapper;
    private final GameInfoMapper rowMapper;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public UUID[] getAll() {
        var sql = "SELECT id FROM gameinfo GROUP BY id;";

        try {
            return namedJdbcTemplate.query(sql, idMapper).toArray(new UUID[0]);
        } catch (DataAccessException e) {
            return new UUID[0];
        }
    }

    public GameInfo[] getById(UUID id) {
        var sql = "SELECT * FROM gameinfo WHERE id = :id";

        var map = new MapSqlParameterSource();
        map.addValue("id", id.toString());

        return namedJdbcTemplate.query(sql, map, rowMapper).toArray(new GameInfo[0]);
    }

    public GameInfo[] playTurn(UUID id, GameRequest request)
            throws InvalidAttributeValueException {
        var sql = "INSERT INTO gameinfo (id, symbol, x, y) VALUES (:id, :symbol, :x, :y);";

        var map = new MapSqlParameterSource();
        map.addValue("id", id);
        map.addValue("symbol", request.getSymbol());
        map.addValue("x", request.getX());
        map.addValue("y", request.getY());

        try {
            namedJdbcTemplate.update(sql, map);
            return getById(id);
        } catch (DataAccessException e) {
            throw new InvalidAttributeValueException("Invalid request");
        }
    }

    public void deleteById(UUID id) {
        var sql = "DELETE FROM gameinfo WHERE id = :id";

        var map = new MapSqlParameterSource();
        map.addValue("id", id.toString());

        namedJdbcTemplate.update(sql, map);
    }
}