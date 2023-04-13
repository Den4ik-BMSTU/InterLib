package ru.internetionalLibrary.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Author {
    int id;

    @NotBlank
    @NotNull(message = "Неверные данные: ошибка в записи автора")
    String name;
}