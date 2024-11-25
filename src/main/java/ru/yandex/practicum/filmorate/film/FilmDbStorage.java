package ru.yandex.practicum.filmorate.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.myException.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.myException.ValidationException;
import ru.yandex.practicum.filmorate.user.UserDbStorage;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private int id;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Integer countColomn(String countColomn) {
        Integer count = jdbcTemplate.queryForObject(countColomn, Integer.class);
        if (count == null)
            throw new ResourceNotFoundException("Ошибка подключения");
        return count + 1;
    }

    @Override
    public void createFilm(Film film) {
        if (checkMPA(film.getMpa().getId())) {
            String countColomn = "SELECT COUNT(*) FROM FILM";
            id = countColomn(countColomn);
            film.setId(id);
            String create = "INSERT INTO FILM (FILM_ID, FIlM_NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(create, id, film.getName(), film.getDescription(), java.sql.Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId());
            insertFilmGenre(id, getGenreIdList(film.getGenres()));
            log.info("Создан фильм с идентефикатором {}", id);
        }
    }

    @Override
    public void deleteFilm(Film film) {
        deleteFromFilmUsersLikes(film);
        deleteFromFilmGenre(film);
        deleteFromFilm(film);
        log.info("Удалён фильм с идентефикатором {}", film.getId());
    }

    @Override
    public void updateFilm(Film film) {
        SqlRowSet userRS = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", film.getId());
        if (userRS.next()) {
            String sqlQuery = "UPDATE FILM set FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, " +
                    "RATING_ID = ? where FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getMpa().getId(), film.getId());
            updateFilmGenre(film);
            updateFilmUsersLikes(film);
            log.info("Изменён фильм {}", film.getId());
        } else throw new ResourceNotFoundException("Нет фильма с таким id");
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        Map<Integer, Film> films = new HashMap<>();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM");
        Map<Integer, Set<Integer>> likesMap = getTableLikes();
        Map<Integer, Set<Genre>> genresMap = getTableGenres();
        Map<Integer, MPA> ratingsMap = getTableRatings();
        while (filmRows.next()) {
            Film film = new Film();
            int filmId = filmRows.getInt("FILM_ID");
            film.setId(filmId);
            film.setName(filmRows.getString("FILM_NAME"));
            film.setDescription(filmRows.getString("DESCRIPTION"));
            film.setReleaseDate(Objects.requireNonNull(filmRows.getDate("RELEASE_DATE")).toLocalDate());
            film.setDuration(filmRows.getInt("DURATION"));

            film.setLikes(likesMap.getOrDefault(filmId, new HashSet<>()));
            film.setGenres(new ArrayList<>(genresMap.getOrDefault(filmId, new HashSet<>())));
            film.setMpa(ratingsMap.get(filmRows.getInt("RATING_ID")));
            films.put(film.getId(), film);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
        }
        return films;
    }

    @Override
    public Boolean findById(Film film) {
        String sql = "SELECT COUNT(*) FROM FILM WHERE FILM_ID = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, film.getId());
        return count != null && count > 0;
    }

    @Override
    public Film getFilmsById(Integer filmId) {
        String getFilm = "SELECT * FROM FILM WHERE FILM_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(getFilm, filmId);
        if (filmRows.next()) {
            return makeFilm(filmRows);
        } else {
            return null;
        }
    }

    @Override
    public List<Genre> getAllGenres() {
        List<Genre> filmGenres = new ArrayList<>();
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE ORDER BY GENRE_ID");
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            filmGenres.add(genre);
        }
        return filmGenres;
    }

    @Override
    public Genre getGenreById(Integer id) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT *  FROM GENRE WHERE GENRE_ID = ? ORDER BY GENRE_ID", id);
        if (genreRows.next()) {
            return makeGenre(genreRows);
        } else
            throw new ResourceNotFoundException("Не найден жанр с таким номером: " + id);
    }

    @Override
    public List<MPA> getAllRatings() {
        List<MPA> filmRatings = new ArrayList<>();
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING ORDER BY RATING_ID");
        while (ratingRows.next()) {
            MPA mpa = makeMPA(ratingRows);
            filmRatings.add(mpa);
        }
        return filmRatings;
    }

    @Override
    public MPA getRatingById(Integer id) {
        SqlRowSet ratingRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE RATING_ID = ?", id);
        if (ratingRows.next()) {
            return makeMPA(ratingRows);
        } else
            throw new ResourceNotFoundException("Не найден рейтинг с таким номером: " + id);
    }

    private void updateFilmUsersLikes(Film film) {
        deleteFromFilmUsersLikes(film);
        String sqlQuery = "INSERT INTO FILM_USERS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        for (Integer userId : film.getLikes()) {
            jdbcTemplate.update(sqlQuery, film.getId(), userId);
        }
    }

    private void updateFilmGenre(Film film) {
        deleteFromFilmGenre(film);
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        Set<Genre> genres = new HashSet<>(film.getGenres());
        for (Genre genre : genres) {
            jdbcTemplate.update(sqlQuery, film.getId(), genre.getId());
        }
    }

    private void deleteFromFilm(Film film) {
        String sqlQuery = "DELETE FROM FILM WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmGenre(Film film) {
        String sqlQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private void deleteFromFilmUsersLikes(Film film) {
        String sqlQuery = "DELETE FROM FILM_USERS_LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlQuery, film.getId());
    }

    private Film makeFilm(SqlRowSet rs) {
        Film film = new Film();
        film.setId(rs.getInt("FILM_ID"));
        film.setName(rs.getString("FILM_NAME"));
        film.setDescription(rs.getString("DESCRIPTION"));
        film.setReleaseDate(Objects.requireNonNull(rs.getDate("RELEASE_DATE")).toLocalDate());
        film.setDuration(rs.getInt("DURATION"));
        film.setLikes(getLikes(film.getId()));
        film.setGenres(getGenres(film.getId()));
        MPA mpa = getRatingById(rs.getInt("RATING_ID"));
        film.setMpa(mpa);
        return film;
    }

    private MPA makeMPA(SqlRowSet rs) {
        Integer ratingId = rs.getInt("RATING_ID");
        String ratingName = rs.getString("RATING_NAME");
        return new MPA(ratingId, ratingName);
    }

    private Genre makeGenre(SqlRowSet rs) {
        Integer genreId = rs.getInt("GENRE_ID");
        String genreName = rs.getString("GENRE_NAME");
        return new Genre(genreId, genreName);
    }

    private void insertFilmGenre(Integer filmId, Set<Integer> genreIdList) {
        String sqlQuery = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        for (Integer id : genreIdList) {
            if (id > 6)
                throw new ValidationException("Такого жанра не существует");
            jdbcTemplate.update(sqlQuery, filmId, id);
        }
    }

    private Set<Integer> getLikes(Integer filmId) {
        String sql = "SELECT USER_ID FROM FILM_USERS_LIKES WHERE FILM_ID = ?";
        List<Integer> likes = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(likes);
    }

    private List<Genre> getGenres(Integer filmId) {
        SqlRowSet genreRows =
                jdbcTemplate.queryForRowSet("SELECT g.GENRE_ID, g.GENRE_NAME FROM FILM_GENRE AS f " +
                        "LEFT JOIN GENRE AS g ON g.GENRE_ID = f.GENRE_ID" +
                        " WHERE FILM_ID = ?" +
                        "ORDER BY g.GENRE_ID", filmId);
        List<Genre> genres = new ArrayList<>();
        while (genreRows.next()) {
            Genre genre = makeGenre(genreRows);
            genres.add(genre);
        }
        return genres;
    }

    private Set<Integer> getGenreIdList(List<Genre> genreList) {
        Set<Integer> genreIdList = new HashSet<>();
        if (Objects.isNull(genreList)) {
            return genreIdList;
        } else if (genreList.size() != 0) {
            for (Genre genre : genreList) {
                genreIdList.add(genre.getId());
            }
        }
        return genreIdList;
    }

    private Boolean checkMPA(Integer count) {
        if (count > 5)
            throw new ValidationException("Такого рейтинга не существует");
        return true;
    }

    private Map<Integer, Set<Integer>> getTableLikes() {
        Map<Integer, Set<Integer>> likesMap = new HashMap<>();
        SqlRowSet likesRows = jdbcTemplate.queryForRowSet("SELECT FILM_ID, USER_ID FROM FILM_USERS_LIKES");
        while (likesRows.next()) {
            int filmId = likesRows.getInt("FILM_ID");
            if (!likesMap.containsKey(filmId)) {
                likesMap.put(filmId, new HashSet<>());
            }
            likesMap.get(filmId).add(likesRows.getInt("USER_ID"));
        }
        return likesMap;
    }

    private Map<Integer, Set<Genre>> getTableGenres() {
        Map<Integer, Set<Genre>> genresMap = new HashMap<>();
        SqlRowSet genresRows = jdbcTemplate.queryForRowSet(
                "SELECT FILM_GENRE.FILM_ID, GENRE.GENRE_ID, GENRE.GENRE_NAME " +
                        "FROM FILM_GENRE " +
                        "JOIN GENRE ON FILM_GENRE.GENRE_ID = GENRE.GENRE_ID"
        );
        while (genresRows.next()) {
            int filmId = genresRows.getInt("FILM_ID");
            genresMap.putIfAbsent(filmId, new HashSet<>());
            Set<Genre> genres = genresMap.get(filmId);
            genres.add(new Genre(
                    genresRows.getInt("GENRE_ID"),
                    genresRows.getString("GENRE_NAME")
            ));
        }
        return genresMap;
    }

    private Map<Integer, MPA> getTableRatings() {
        Map<Integer, MPA> ratingsMap = new HashMap<>();
        SqlRowSet ratingsRows = jdbcTemplate.queryForRowSet("SELECT RATING_ID, RATING_NAME FROM RATING");
        while (ratingsRows.next()) {
            ratingsMap.put(
                    ratingsRows.getInt("RATING_ID"),
                    new MPA(ratingsRows.getInt("RATING_ID"), ratingsRows.getString("RATING_NAME"))
            );
        }
        return ratingsMap;
    }
}
