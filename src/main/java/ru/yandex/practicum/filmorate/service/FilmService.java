package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmResponse;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
@Slf4j(topic = "TRACE")
@RequiredArgsConstructor
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    // SQL-запросы
    private final String selectLikedUsersQuery = "select filmId, userId from likedUsers";
    private final String insertLikeQuery = "insert into likedUsers(filmId, userId) values (?, ?)";
    private final String selectFilmGenresQuery = "select filmId, genreId from filmGenre where filmId = ?";
    private final String deleteLikeQuery = "delete from likedUsers where filmId = ? and userId = ?";
    private final String selectTopFilmsQuery = "select f.id as name, COUNT(l.userId) as coun from likedUsers as l LEFT OUTER JOIN film AS f ON l.filmId = f.id GROUP BY f.name ORDER BY COUNT(l.userId) DESC LIMIT 10";


    public FilmResponse addLike(Long idUser, Long idFilm) {
        log.info("Обработка Post-запроса...");
        if (userStorage.findById(idUser) != null && filmStorage.findById(idFilm) != null) {
            Map<Long, Set<Long>> likedUsers = jdbcTemplate.query(selectLikedUsersQuery, new FilmDbStorage.LikedUsersExtractor());
            if (likedUsers.get(idFilm) != null && likedUsers.get(idFilm).contains(idUser)) {
                log.error("Пользователь с ID {} уже поставил лайк фильму с ID {}", idUser, idFilm);
                throw new ConditionsNotMetException("Пользователь с ID " + idUser + " уже поставил лайк фильму с ID " + idFilm);
            } else {
                jdbcTemplate.update(insertLikeQuery, idFilm, idUser);
            }
        }
        FilmResponse film = filmStorage.findById(idFilm);
        LinkedHashSet genres = new LinkedHashSet<>();
        Map<Long, LinkedHashSet<Long>> filmGenre = jdbcTemplate.query(selectFilmGenresQuery, new FilmDbStorage.FilmGenreExtractor(), film.getId());
        if (!filmGenre.isEmpty()) {
            for (Long g : filmGenre.get(film.getId()))
                genres.add(g);
        }
        return FilmResponse.of(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), new HashSet<>(), film.getMpa(), genres);
    }


    public FilmResponse delLike(Long idUser, Long idFilm) {
        log.info("Обработка Del-запроса...");
        if (userStorage.findById(idUser) != null && filmStorage.findById(idFilm) != null) {
            Map<Long, Set<Long>> likedUsers = jdbcTemplate.query(selectLikedUsersQuery, new FilmDbStorage.LikedUsersExtractor());
            if (likedUsers.get(idFilm) != null && !likedUsers.get(idFilm).contains(idUser)) {
                log.error("Пользователь с ID {} не ставил лайк фильму с ID {}", idUser, idFilm);
                throw new ConditionsNotMetException("Пользователь с ID " + idUser + " не ставил лайк фильму с ID " + idFilm);
            } else {
                jdbcTemplate.update(deleteLikeQuery, idFilm, idUser);
            }
        }
        FilmResponse film = filmStorage.findById(idFilm);
        LinkedHashSet genres = new LinkedHashSet<>();
        Map<Long, LinkedHashSet<Long>> filmGenre = jdbcTemplate.query(selectFilmGenresQuery, new FilmDbStorage.FilmGenreExtractor(), film.getId());
        if (!filmGenre.isEmpty()) {
            for (Long g : filmGenre.get(film.getId()))
                genres.add(g);
        }
        return FilmResponse.of(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), new HashSet<>(), film.getMpa(), genres);
    }

    public LinkedHashSet<FilmResponse> viewRating(Long count) {
        log.info("Обработка Get-запроса...");
        LinkedHashMap<Long, Long> likedUsers = jdbcTemplate.query(selectTopFilmsQuery, new TopLikedUsersExtractor());
        LinkedHashSet<FilmResponse> films = new LinkedHashSet<>();
        if (likedUsers == null) {
            log.error("Список фильмов с рейтингом пуст.");
            throw new NotFoundException("Список фильмов с рейтингом пуст.");
        } else {
            LinkedHashSet genres = new LinkedHashSet<>();
            for (Long l : likedUsers.keySet()) {
                Map<Long, LinkedHashSet<Long>> filmGenre = jdbcTemplate.query(selectFilmGenresQuery, new FilmDbStorage.FilmGenreExtractor(), filmStorage.findById(l).getId());
                if (!filmGenre.isEmpty()) {
                    for (Long g : filmGenre.get(filmStorage.findById(l).getId()))
                        genres.add(g);
                }
                films.add(FilmResponse.of(filmStorage.findById(l).getId(), filmStorage.findById(l).getName(), filmStorage.findById(l).getDescription(), filmStorage.findById(l).getReleaseDate(), filmStorage.findById(l).getDuration(), new HashSet<>(), filmStorage.findById(l).getMpa(), genres));
            }
        }
        return films;
    }
}