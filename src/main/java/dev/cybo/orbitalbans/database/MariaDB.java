package dev.cybo.orbitalbans.database;

import com.zaxxer.hikari.HikariDataSource;
import dev.cybo.orbitalbans.enums.PunishmentType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MariaDB {

    private final HikariDataSource hikariDataSource;


    public MariaDB(String host, int port, String username, String password, String database) {

        hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariDataSource.setConnectionTimeout(5000);
        hikariDataSource.setMaxLifetime(20000000);
        hikariDataSource.setMaximumPoolSize(5);
        hikariDataSource.setMinimumIdle(5);
        hikariDataSource.setPoolName("Punishments-MariaDB");
        hikariDataSource.addDataSourceProperty("url", "jdbc:mariadb://" + host + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=UTF-8&useSSL=false&user=" + username + "&password=" + password);

        for (PunishmentType punishmentType : PunishmentType.values()) {
            StringBuilder queryBuilder = buildCreateTableQuery(punishmentType);
            executeQueryAsync(queryBuilder.toString());
        }

    }

    private static StringBuilder buildCreateTableQuery(PunishmentType punishmentType) {
        StringBuilder queryBuilder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        queryBuilder.append(punishmentType.getDatabaseTable());
        queryBuilder.append(" (");

        int columnCount = punishmentType.getDatabaseColumns().length;

        for (int i = 0; i < columnCount; i++) {
            DatabaseColumn databaseColumn = punishmentType.getDatabaseColumns()[i];
            queryBuilder.append(databaseColumn.name());
            queryBuilder.append(" ");
            queryBuilder.append(databaseColumn.type());

            if (i < columnCount - 1) {
                queryBuilder.append(", ");
            }
        }

        queryBuilder.append(");");

        System.out.println(queryBuilder.toString());

        return queryBuilder;
    }


    public CompletableFuture<List<DatabaseRow>> executeQueryAsync(String query, Object... parameters) {
        return CompletableFuture.supplyAsync(() -> {
            List<DatabaseRow> rows = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (int i = 0; i < parameters.length; ++i) {
                    Object variable = parameters[i];
                    int parameterIndex = i + 1;
                    statement.setObject(parameterIndex, variable);
                }


                if (statement.execute()) {
                    try (ResultSet results = statement.getResultSet()) {
                        while (results.next()) {
                            DatabaseRow row = new DatabaseRow();
                            ResultSetMetaData resultSetMetaData = results.getMetaData();
                            for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                                row.addColumn(resultSetMetaData.getColumnName(i), results.getObject(i));
                            }
                            rows.add(row);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return rows;
        }).exceptionallyAsync((e) -> {
            e.printStackTrace();
            return null;
        });
    }


    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }
}