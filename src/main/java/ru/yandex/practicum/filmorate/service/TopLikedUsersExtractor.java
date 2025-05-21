package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class TopLikedUsersExtractor implements ResultSetExtractor<LinkedHashMap<Long, Long>> {
    @Override
    public LinkedHashMap<Long, Long> extractData(ResultSet rs) throws SQLException {
        LinkedHashMap<Long, Long> data = new LinkedHashMap<>();
        while (rs.next()) {
            Long filmId = rs.getLong("name");
            Long likes = rs.getLong("coun");
            data.putIfAbsent(filmId, likes);
        }
        return data;
    }
}
