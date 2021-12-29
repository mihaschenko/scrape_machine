package com.scraperservice.queue;

import com.scraperservice.helper.LogHelper;
import com.scraperservice.utils.RandomStringHelper;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class UniqueValuesStorage implements AutoCloseable {
    private final String tableIndex;
    private final Connection connection;

    public UniqueValuesStorage() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/uniqueLinkValues.s3db");

        tableIndex = "_" + RandomStringHelper.getRandomStringOnlyLetters( 10) + "_"
                + new SimpleDateFormat("hh_mm_dd_MM_yyyy").format(new Date());
        createTableAndIndex();
    }

    private void createTableAndIndex() throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "CREATE TABLE 'UV_" + tableIndex + "' ('hash' INT NOT NULL, 'link' TEXT NOT NULL)";
        statement.executeUpdate(sql);

        sql = "CREATE INDEX hash_" + tableIndex + " ON UV_" + tableIndex + "(hash)";
        statement.executeUpdate(sql);
        statement.close();
    }

    public boolean checkUniqueValue(String value) {
        int hash = value.hashCode();
        boolean result = false;
        synchronized(this) {
            Statement statement;
            try{
                statement = connection.createStatement();
                String sql = "SELECT link FROM UV_" + tableIndex + " WHERE hash=" + hash;
                ResultSet resultSet = statement.executeQuery(sql);

                boolean isLinkUnique = true;
                while (resultSet.next()) {
                    String link = resultSet.getString("link");
                    if(link.equals(value)) {
                        isLinkUnique = false;
                        break;
                    }
                }
                if(isLinkUnique) {
                    sql = "INSERT INTO UV_" + tableIndex + " (hash, link) " +
                            "VALUES (" + hash + ", '" + value + "');";
                    statement.execute(sql);
                }

                result = isLinkUnique;
                statement.close();
            }
            catch (SQLException e) {
                LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return result;
    }

    // Only for test
    private void showAllUniqueValues() throws Exception {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM UV_" + tableIndex);

        while (resultSet.next()) {
            int hash = resultSet.getInt("hash");
            String link = resultSet.getString("link");

            System.out.println("HASH = " + hash);
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
            Statement statement = connection.createStatement();
            String sql = "DROP TABLE UV_" + tableIndex;
            statement.execute(sql);
            statement.close();
        }
        catch (SQLException e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
