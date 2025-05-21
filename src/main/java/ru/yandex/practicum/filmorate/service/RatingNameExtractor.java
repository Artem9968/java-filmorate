package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class RatingNameExtractor implements ResultSetExtractor<Map<Long, String>> {
    @Override
    public Map<Long, String> extractData(ResultSet rs) throws SQLException {
        Map<Long, String> data = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("id");
            String rating = rs.getString("rating");
            data.put(id, rating);
        }
        return data;
    }
}
