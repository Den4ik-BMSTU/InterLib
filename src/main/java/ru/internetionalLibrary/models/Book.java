package ru.internetionalLibrary.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    private int id;

    @NotBlank
    @NotNull(message = "Неверные данные: имя пустое или содержит только пробелы")
    private String name;

    @Size(max=200,
            message = "Неверные данные: Описание больше 200 символов")
    private String description;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Min(value = 0
            , message = "Неверные данные: Количество страниц должно быть больше 0")
    private int volume;

    private Set<Integer> likes = new HashSet<>();

    private int rate;
    private List<Genre> genres;
    private List<Author> authors;
}
