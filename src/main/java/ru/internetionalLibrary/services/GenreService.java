package ru.internetionalLibrary.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.internetionalLibrary.models.Book;
import ru.internetionalLibrary.models.Genre;
import ru.internetionalLibrary.storage.daoImpl.DaoGenreStorage;

import java.util.List;
@Service
@Slf4j
@RequiredArgsConstructor
public class GenreService {

    private final DaoGenreStorage daoGenreStorage;
    public List<Genre> getAllGenre() {
        return daoGenreStorage.getAllGenre();
    }

    public Genre getGenreById(Integer id) {
        return daoGenreStorage.getGenreById(id);
    }
    public List<Genre> getGenresByIdBook(Integer id) {
        return daoGenreStorage.getGenresByIdBook(id);
    }
    public void addOrUpdateFilmGenres(Book film) {
        daoGenreStorage.addOrUpdateBookGenres(film);
    }
}
