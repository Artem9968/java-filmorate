package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Buffer;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmResponse;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String DEFAULT_GENRE = "нет жанра";

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(
            FilmStorage filmStorage,
            UserStorage userStorage,
            FilmService filmInterface
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmService = filmInterface;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public FilmResponse findById(@PathVariable("id") Long id) {
        return filmStorage.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmResponse create(@Valid @RequestBody ObjectNode objectNode) {
        Buffer buffer = parseObjectNodeToBuffer(objectNode);
        return filmStorage.create(buffer);
    }

    @PutMapping
    public FilmResponse update(@Valid @RequestBody ObjectNode objectNode) {
        Buffer buffer = parseObjectNodeToBuffer(objectNode);
        return filmStorage.update(buffer);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmResponse addLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public FilmResponse delLike(@Valid @PathVariable("id") Long id, @PathVariable("userId") Long userId) {
        return filmService.delLike(userId, id);
    }

    @GetMapping("/popular")
    public LinkedHashSet<FilmResponse> viewRating(@RequestParam(defaultValue = "10") Long count) {
        return filmService.viewRating(count);
    }

    /**
     * преобразует json объект в объект Buffer
     *
     * @param objectNode json объект
     * @return объект Buffer
     */
    private Buffer parseObjectNodeToBuffer(ObjectNode objectNode) {
        Long id = objectNode.has("id") ? objectNode.get("id").asLong() : 0L;
        String name = objectNode.get("name").asText();
        String description = objectNode.get("description").asText();
        String releaseDate = objectNode.get("releaseDate").asText();
        Integer duration = objectNode.get("duration").asInt();
        List<String> mpa = objectNode.get("mpa").findValuesAsText("id");
        List<String> genres = extractGenresFromObjectNode(objectNode);

        return Buffer.of(
                id,
                name,
                description,
                LocalDate.parse(releaseDate, DATE_FORMATTER),
                duration,
                genres,
                Long.valueOf(mpa.get(0))
        );
    }

    /**
     * извлекает список жанров из json объекта
     *
     * @param objectNode json объект
     * @return список жанров
     */
    private List<String> extractGenresFromObjectNode(ObjectNode objectNode) {
        try {
            return objectNode.get("genres").findValuesAsText("id");
        } catch (NullPointerException e) {
            return List.of(DEFAULT_GENRE);
        }
    }
}
