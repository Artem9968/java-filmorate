package ru.yandex.practicum.filmorate.service;

import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GenreExtractor implements ResultSetExtractor<Map<Long, String>> {
    @Override
    public Map<Long, String> extractData(ResultSet rs) throws SQLException {
        Map<Long, String> data = new LinkedHashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("id");
            String name = rs.getString("name");
            data.put(id, name);
        }
        return data;
    }
}
