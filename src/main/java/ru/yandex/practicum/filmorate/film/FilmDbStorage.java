package ru.yandex.practicum.filmorate.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.baserepository.BaseRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String create = "INSERT INTO films (id, name, description, releaseDate, genreId, ratingId, duration) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String update = "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ? WHERE id = ?";
    private static final String getall = "SELECT * FROM films";
    private static final String getFilms = "SELECT * FROM films WHERE id = ?";
    private static final String findId = "SELECT COUNT(*) FROM films WHERE id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    int id;

    private Integer countColomn() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM films", Integer.class);
        if (count == null)
            throw new ResourceNotFoundException("Ошибка подключения");
        return count;
    }

    @Override
    public void createFilm(Film film) {
        id = countColomn();
        jdbc.update(create, id, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()), film.getGenre(), film.getRating(), film.getDuration());
    }

    @Override
    public void updateFilm(Film film) {
        Integer id = film.getId();
        if (!findById(film)) {
            throw new ResourceNotFoundException("Фильм не найден");
        }
        update(update, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getId());
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        List<Film> films = findMany(getall);
        Map<Integer, Film> filmMap = new HashMap<>();
        for (Film film : films) {
            filmMap.put(film.getId(), film);
        }
        return filmMap;
    }

    @Override
    public Film getFilmsById(Integer id) {
        return findOne(getFilms, id).orElseThrow(() -> new ResourceNotFoundException("Фильм не найден"));
    }

    @Override
    public Boolean findById(Film film) {
        int count = jdbc.queryForObject(findId, Integer.class, film.getId());
        return count > 0;
    }

    protected long insert(String query, Object... params) {
        return super.insert(query, params);
    }

}
