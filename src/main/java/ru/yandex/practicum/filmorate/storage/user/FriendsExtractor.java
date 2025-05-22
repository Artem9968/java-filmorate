package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class FriendsExtractor implements ResultSetExtractor<Map<Long, Set<Long>>> {

    @Override
    public Map<Long, Set<Long>> extractData(ResultSet rs) throws SQLException {
        Map<Long, Set<Long>> data = new LinkedHashMap<>();
        while (rs.next()) {
            Long userId = rs.getLong("userId");
            data.putIfAbsent(userId, new HashSet<>());
            Long friendId = rs.getLong("friendId");
            data.get(userId).add(friendId);
        }
        return data;
    }
}
