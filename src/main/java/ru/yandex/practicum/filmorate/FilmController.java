package ru.yandex.practicum.filmorate;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor

@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET /film");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable("id") Long id) {
        return filmService.findById(id);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getTop(count);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("POST /film/{}", film.getName());
        return filmService.createFilm(film); //вернуть если ничего не поменяется
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        log.info("PUT /film/{}", film.getName());
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{user-id}")
    public Film addLike(@PathVariable Integer id, @PathVariable("user-id") Long idUser) {
        return filmService.addLike(id, idUser);
    }

    @DeleteMapping("/{id}")
    public Film delete(
            @PathVariable Integer id
    ) {
        return filmService.deleteById(Long.valueOf(id));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(
            @PathVariable("id") Long id,
            @PathVariable("userId") Long userId
    ) {
        return filmService.deleteLike(id, userId);
    }

}
