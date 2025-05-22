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
        values.put("mpa_id", film.getMpa().getId());
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");

        Integer filmId = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String sqlQuery = "insert into films_genres(film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(sqlQuery,
                        filmId,
                        genre.getId());
            }
        }
        return findById(Long.valueOf(filmId));
    }


    @Override
    public List<Film> findAll() {
        String sql = "select f.id id, f.name name,f.description description,\n" +
                "f.mpa_id mpa_id, m.name as mpa_name,\n" +
                "f.releaseDate releaseDate, f.duration as duration, g.NAME  as genres_name \n" +
                "from films f\n" +
                "JOIN mpa m ON m.id = f.mpa_id\n" +
                "JOIN FILMS_GENRES fg  ON fg.FILM_ID = f.id\n" +
                "JOIN GENRES g ON g.ID = fg.GENRE_ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public boolean deleteById(Long id) {
        String sqlQuery = "delete from film_likes where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "delete from films_genres where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);

        sqlQuery = "delete from films where id = ?";
        return jdbcTemplate.update(sqlQuery, id) > 0;
    }

    @Override
    public Film update(Film film) {
        findById(film.getId());

        String sqlQuery = "update films set " +
                "name = ?, description = ?, releaseDate = ?, duration = ?, mpa_id = ? " +
                "where id = ?";
        jdbcTemplate.update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            String genreSqlQuery = "delete from films_genres where film_id = ?";
            jdbcTemplate.update(genreSqlQuery, film.getId());
            for (Genre genre : film.getGenres()) {
                genreSqlQuery = "insert into films_genres(film_id, genre_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(genreSqlQuery,
                        film.getId(),
                        genre.getId());
            }
        }

        if (film.getLikes() != null) {
            String likeSqlQuery = "delete from film_likes where film_id = ?";
            jdbcTemplate.update(likeSqlQuery, film.getId());

            for (Long userId : film.getLikes()) {
                likeSqlQuery = "insert into film_likes(film_id, user_id) " +
                        "values (?, ?)";
                jdbcTemplate.update(likeSqlQuery,
                        film.getId(),
                        userId);
            }
        }
        return findById(film.getId());
    }

    @Override
    public Film findById(Long id) {
        String sql = "select f.id id, f.name name,f.description description,\n" +
                "f.mpa_id mpa_id, m.name as mpa_name,\n" +
                "f.releaseDate releaseDate, f.duration as duration \n" +
                "from films f\n" +
                "JOIN mpa m ON m.id = f.mpa_id\n" +
                "WHERE f.id = ?";

        List<Film> filmCollection = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (filmCollection.size() == 1) {
            return filmCollection.getFirst();
        } else {
            throw new NotFoundException(String.format("Фильма с id-%d не существует.", id));
        }
    }

    private Film makeFilm(ResultSet rs) throws SQLException {

        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        Long mpaId = rs.getLong("mpa_id");
        String mpaName = rs.getString("mpa_name");
        LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
        Integer duration = rs.getInt("duration");

        String genreSql = "select DISTINCT g.* \n" +
                "from films_genres f\n" +
                "JOIN genres g ON (f.genre_id = g.id)\n" +
                "where film_id = ?";
        List<Genre> genreCollection = jdbcTemplate.query(genreSql, (rs1, rowNum) -> makeFilmsGenre(rs1), id);

        String likesSql = "select * from film_likes where film_id = ?";
        List<Long> usersCollection = jdbcTemplate.query(likesSql, (rs1, rowNum) -> Long.valueOf(makeFilmsLike(rs1)), id);

        return new Film(id, name, description, releaseDate, duration, new LinkedHashSet<>(genreCollection), new Mpa(mpaId, mpaName), new HashSet<>(usersCollection));
    }

    private Genre makeFilmsGenre(ResultSet rs) throws SQLException {
        Integer genreId = rs.getInt("id");
        String genreName = rs.getString("name");
        return new Genre(genreId, genreName);
    }

    private Integer makeFilmsLike(ResultSet rs) throws SQLException {
        return rs.getInt("user_id");
    }

}
