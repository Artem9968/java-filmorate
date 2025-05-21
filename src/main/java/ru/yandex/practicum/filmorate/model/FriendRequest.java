package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class FriendRequest {
    @NotNull
    private Long id;
    private Long userId;
    private Long friendId;
    private boolean accept = false;
}