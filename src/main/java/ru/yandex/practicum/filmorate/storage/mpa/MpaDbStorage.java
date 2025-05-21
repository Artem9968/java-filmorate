package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaById(Long id) {
        String sql = "SELECT id, rating FROM filmrating WHERE id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> Mpa.of(rs.getLong("id"), rs.getString("rating")), id);
        } catch (DataAccessException e) {
            throw new NotFoundException("MPA with id " + id + " not found");
        }
    }

    public List<Mpa> getAllMpa() {
        String sql = "SELECT id, rating FROM filmrating";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Mpa.of(rs.getLong("id"), rs.getString("rating")));
    }
}
