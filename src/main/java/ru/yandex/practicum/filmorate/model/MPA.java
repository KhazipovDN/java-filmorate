package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
@ToString
public class MPA {
    @NotBlank
    private Integer id;
    @Size(max = 100)
    @NotBlank
    private String name;

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}