package com.scraperservice;

import com.scraperservice.helper.LogHelper;
import com.scraperservice.utils.RandomStringHelper;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;

public class UniqueValuesStorage implements AutoCloseable {
    private final String tableIndex;
    private final Connection connection;
    private final Statement statement;
    private volatile int index = 1;

    public UniqueValuesStorage() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/uniqueLinkValues.s3db");

        tableIndex = "_" + RandomStringHelper.getRandomStringOnlyLetters( 10) + "_"
                + new SimpleDateFormat("hh_mm_ss_SSS_dd_MM_yyyy").format(new Date());
        statement = connection.createStatement();
        createTableAndIndex();
    }

    private void createTableAndIndex() throws SQLException {
        String sql = "CREATE TABLE 'UV_" + tableIndex + "' ('id' NOT NULL UNIQUE, " +
                "'link' TEXT NOT NULL UNIQUE, 'is_taken' BOOLEAN NOT NULL)";
        statement.executeUpdate(sql);

        sql = "CREATE INDEX id_" + tableIndex + " ON UV_" + tableIndex + "(id)";
        statement.executeUpdate(sql);
        sql = "CREATE INDEX is_taken_" + tableIndex + " ON UV_" + tableIndex + "(is_taken)";
        statement.executeUpdate(sql);
    }

    public synchronized boolean isEmpty() {
        try{
            String sql = String.format("SELECT id FROM UV_%s WHERE is_taken=false", tableIndex);
            ResultSet resultSet = statement.executeQuery(sql);
            return !resultSet.next();
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public boolean addAll(Collection<String> collection) {
        int result = 0;
        for(String str : collection) {
            if(add(str))
                result++;
        }
        return result > 0;
    }

    public synchronized boolean add(String str) {
        try{
            String sql = String.format("INSERT INTO UV_%s (id, link, is_taken) VALUES (%d, '%s', false)", tableIndex, index, str);
            index++;
            return statement.executeUpdate(sql) != 0;
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public synchronized String get(int id) {
        try{
            String sql = String.format("SELECT link FROM UV_%s WHERE id=%d", tableIndex, id);
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return resultSet.getString("link");
            return null;
        }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public synchronized String poll() {
        try{
            String sql = String.format("SELECT * FROM UV_%s WHERE is_taken=false", tableIndex);
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next()) {
                String result = resultSet.getString("link");
                sql = String.format("UPDATE UV_%s SET is_taken=true WHERE id=%d", tableIndex, resultSet.getInt("id"));
                statement.executeUpdate(sql);
                return result;
            }
            return null;
        }
        catch (SQLException e) { throw new RuntimeException(e); }
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