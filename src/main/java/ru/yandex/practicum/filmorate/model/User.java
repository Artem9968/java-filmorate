package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = {"email"})
@AllArgsConstructor(staticName = "of")
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String login;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Long> friends;
    @JsonIgnore
    private Set<Long> friendRequests;

}
