package ru.yandex.practicum.filmorate.storage.genre;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Component

public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> findAll() {
        String sql = "select * from genres order by id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmsGenre(rs));
    }

    @Override
    public Genre findById(Integer id) {
        String sql = "select * from genres where id = ?";

        List<Genre> genreCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmsGenre(rs), id);
        if (genreCollection.size() == 1) {
            return genreCollection.get(0);
        } else {
            throw new NotFoundException(String.format("genre с id-%d не существует.", id));
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sqlQuery = "delete from friendships where user_id = ? or friend_id = ?";
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "delete from genres where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Genre makeFilmsGenre(ResultSet rs) throws SQLException {
        Integer genreId = rs.getInt("id");
        String genreName = rs.getString("name");
        return new Genre(genreId, genreName);
    }
}
