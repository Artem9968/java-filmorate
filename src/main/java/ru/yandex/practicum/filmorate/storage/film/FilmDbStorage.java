package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Primary
@Component
@Repository

@Slf4j(topic = "TRACE")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film create(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("ratingId", film.getMpa().getId()); // изменено с mpa_id на ratingId
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film") // изменено с films на film
                .usingGeneratedKeyColumns("id");

        Integer filmId = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "INSERT INTO filmGenre(filmId, genreId) " + // изменено с films_genres на filmGenre, поля filmId, genreId
                        "VALUES (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        filmId,
                        genre.getId());
            }
        }
        return findById(Long.valueOf(filmId));
    }


    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.id id, f.name name, f.description description, " +
                "f.ratingId ratingId, r.rating AS rating_name, " + // изменено с mpa_id и m.name на ratingId и r.rating
                "f.releaseDate releaseDate, f.duration duration, g.name AS genres_name " + // изменено с g.NAME
                "FROM film f " + // изменено с films
                "JOIN filmrating r ON r.id = f.ratingId " + // изменено с mpa на filmrating
                "JOIN filmGenre fg ON fg.filmId = f.id " + // изменено с films_genres на filmGenre, поля filmId
                "JOIN genre g ON g.id = fg.genreId"; // изменено с genres и поля id

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public boolean deleteById(Long id) {
        String sqlQuery = "DELETE FROM likedUsers WHERE filmId = ?"; // изменено с film_likes на likedUsers, поле filmId
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "DELETE FROM filmGenre WHERE filmId = ?"; // изменено с films_genres на filmGenre, поле filmId
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "DELETE FROM film WHERE id = ?"; // изменено с films на film
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Film update(Film film) {
        findById(film.getId());

        String sqlQuery = "UPDATE film SET " + // изменено с films на film
                "name = ?, description = ?, releaseDate = ?, duration = ?, ratingId = ? " + // изменено с mpa_id на ratingId
                "WHERE id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            String genreSqlQuery = "DELETE FROM filmGenre WHERE filmId = ?"; // изменено с films_genres на filmGenre, поле filmId
            jdbcTemplate.update(genreSqlQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                genreSqlQuery = "INSERT INTO filmGenre(filmId, genreId) " + // изменено с films_genres на filmGenre
                        "VALUES (?, ?)";
                jdbcTemplate.update(genreSqlQuery,
                        film.getId(),
                        genre.getId());
            }
        }

        if (film.getLikes() != null) {
            String likeSqlQuery = "DELETE FROM likedUsers WHERE filmId = ?"; // изменено с film_likes на likedUsers, поле filmId
            jdbcTemplate.update(likeSqlQuery, film.getId());

            for (Long userId : film.getLikes()) {
                likeSqlQuery = "INSERT INTO likedUsers(filmId, userId) " + // изменено с film_likes на likedUsers
                        "VALUES (?, ?)";
                jdbcTemplate.update(likeSqlQuery,
                        film.getId(),
                        userId);
            }
        }
        return findById(film.getId());
    }

    @Override
    public Film findById(Long id) {
        String sql = "SELECT f.id id, f.name name, f.description description, " +
                "f.ratingId ratingId, r.rating AS rating_name, " + // изменено с mpa_id и m.name на ratingId и r.rating
                "f.releaseDate releaseDate, f.duration duration " +
                "FROM film f " + // изменено с films
                "JOIN filmrating r ON r.id = f.ratingId " + // изменено с mpa на filmrating
                "WHERE f.id = ?";

        List<Film> filmCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (filmCollection.size() == 1) {
            return filmCollection.get(0); // исправлено с getFirst() на get(0)
        } else {
            throw new NotFoundException(String.format("Фильма с id-%d не существует.", id));
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Long ratingId = rs.getLong("ratingId");
        String ratingName = rs.getString("rating_name");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");

        String genreSql = "SELECT DISTINCT g.* " +
                "FROM filmGenre fg " + // изменено с films_genres на filmGenre
                "JOIN genre g ON fg.genreId = g.id " + // изменено с genres и поля genre_id
                "WHERE fg.filmId = ?"; // изменено с film_id на filmId
        List<Genre> genreCollection = jdbcTemplate.query(genreSql, (rs1, rowNum) -> makeFilmsGenre(rs1), id);

        String likesSql = "SELECT * FROM likedUsers WHERE filmId = ?"; // изменено с film_likes на likedUsers
        List<Long> usersCollection = jdbcTemplate.query(likesSql, (rs1, rowNum) -> Long.valueOf(makeFilmsLike(rs1)), id);

        return new Film(id, name, description, releaseDate, duration,
                new LinkedHashSet<>(genreCollection),
                new Mpa(ratingId, ratingName),
                new HashSet<>(usersCollection));
    }

    private Genre makeFilmsGenre(ResultSet rs) throws SQLException {
        Integer genreId = rs.getInt("id");
        String genreName = rs.getString("name");
        return new Genre(genreId, genreName);
    }

    private Integer makeFilmsLike(ResultSet rs) throws SQLException {
        return rs.getInt("userId"); // изменено с user_id на userId
    }

}


