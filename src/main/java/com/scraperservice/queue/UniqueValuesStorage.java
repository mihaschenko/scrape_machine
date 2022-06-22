package com.scraperservice.queue;

import com.scraperservice.helper.LogHelper;
import com.scraperservice.utils.RandomStringHelper;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

//FIXME I thing, this method to check unique values doesn't fit
public class UniqueValuesStorage implements AutoCloseable {
    private final String tableIndex;
    private final Connection connection;
    private Statement statement;

    public UniqueValuesStorage() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/uniqueLinkValues.s3db");

        tableIndex = "_" + RandomStringHelper.getRandomStringOnlyLetters( 10) + "_"
                + new SimpleDateFormat("hh_mm_dd_MM_yyyy").format(new Date());
        statement = connection.createStatement();
        createTableAndIndex();
    }

    private void createTableAndIndex() throws SQLException {
        String sql = "CREATE TABLE 'UV_" + tableIndex + "' ('id' INT NOT NULL UNIQUE, 'link' TEXT NOT NULL UNIQUE)";
        statement.executeUpdate(sql);

        sql = "CREATE INDEX id_" + tableIndex + " ON UV_" + tableIndex + "(id)";
        statement.executeUpdate(sql);
    }

    public boolean add(int id, String str) throws SQLException {
        String sql = String.format("INSERT INTO UV_%s (id, link) VALUES (%d, '%s')", tableIndex, id, str);
        return statement.executeUpdate(sql) != 0;
    }

    public String get(int id) throws SQLException {
        String sql = String.format("SELECT link FROM UV_%s WHERE id=%d", tableIndex, id);
        ResultSet resultSet = statement.executeQuery(sql);
        if(resultSet.next())
            return resultSet.getString("link");
        return null;
    }

    // Only for test
    private void showAllUniqueValues() throws Exception {
        ResultSet resultSet = statement.executeQuery("SELECT * FROM UV_" + tableIndex);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String link = resultSet.getString("link");

            System.out.println("ID = " + id);
            System.out.println("LINK = " + link);
            System.out.println();
        }
    }

    @Override
    public void close() {
        if(connection != null) {
            removeTable();
            try{
                connection.close();
            }
            catch (SQLException e) {
                LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    private void removeTable() {
        try {
            String sql = "DROP TABLE UV_" + tableIndex;
            statement.execute(sql);
            statement.close();
        }
        catch (SQLException e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
