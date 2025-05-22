package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;


import java.util.Collection;


@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MpaController {
    private final MpaService mpaService;


    @GetMapping
    public Collection<Mpa> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable Long id) {
        return mpaService.findById(id);
    }


}
