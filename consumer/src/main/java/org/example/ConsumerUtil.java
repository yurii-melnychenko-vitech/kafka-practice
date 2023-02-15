package org.example;

import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.util.List;

public class ConsumerUtil {
    public static java.sql.Array createSqlArray(JdbcTemplate jdbcTemplate, String typeOfArray, List<String> list){
        java.sql.Array intArray = null;
        try {
            intArray = jdbcTemplate.getDataSource().getConnection().createArrayOf(typeOfArray, list.toArray());
        } catch (SQLException ignore) {
        }
        return intArray;
    }
}
