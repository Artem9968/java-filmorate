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
        String sql = "SELECT * FROM genre ORDER BY id"; // изменено с genres на genre
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmsGenre(rs));
    }

    @Override
    public Genre findById(Integer id) {
        String sql = "SELECT * FROM genre WHERE id = ?"; // изменено с genres на genre

        List<Genre> genreCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilmsGenre(rs), id);
        if (genreCollection.size() == 1) {
            return genreCollection.get(0);
        } else {
            throw new NotFoundException(String.format("genre с id-%d не существует.", id));
        }
    }

    @Override
    public boolean deleteById(Integer id) {
        String sqlQuery = "DELETE FROM genre WHERE id = ?"; // изменено с genres на genre
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    private Genre makeFilmsGenre(ResultSet rs) throws SQLException {
        Integer genreId = rs.getInt("id");
        String genreName = rs.getString("name");
        return new Genre(genreId, genreName);
    }
}

