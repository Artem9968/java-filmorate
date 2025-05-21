package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class EmailExtractor implements ResultSetExtractor<Set<String>> {

    @Override
    public Set<String> extractData(ResultSet rs) throws SQLException {
        Set<String> data = new HashSet<>();
        while (rs.next()) {
            String email = rs.getString("email");
            data.add(email);
        }
        return data;
    }
}