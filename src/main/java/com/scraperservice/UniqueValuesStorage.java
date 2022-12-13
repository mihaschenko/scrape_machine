package com.scraperservice;

import com.scraperservice.helper.LogHelper;
import com.scraperservice.manager.StatisticManager;
import com.scraperservice.utils.RandomStringHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UniqueValuesStorage implements AutoCloseable {
    private final String tableIndex;
    private final Connection connection;
    private final Statement statement;
    private volatile int index = 1;
    private final StatisticManager statisticManager;

    private static final String NAME_DATA_REGEX = "([0-9]+)_([0-9]+)_([0-9]+)$";

    @PostConstruct
    private void init() throws SQLException {
        // remove all old tables
        ResultSet tableNames =
                statement.executeQuery("SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';");
        Date currentData = new Date();
        List<String> tableDeleteList = new ArrayList<>();
        final long monthTime = 1000L*60*60*24*31;
        while (tableNames.next()) {
            String name = tableNames.getString("name");
            Pattern pattern = Pattern.compile(NAME_DATA_REGEX);
            Matcher matcher = pattern.matcher(name);
            if(matcher.find()) {
                GregorianCalendar tableData = new GregorianCalendar();
                tableData.set(Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(2))-1,
                        Integer.parseInt(matcher.group(1)));
                if(currentData.getTime() - tableData.getTime().getTime() >= monthTime)
                    tableDeleteList.add(name);
            }
            else
                tableDeleteList.add(name);
        }

        for(String tableName : tableDeleteList)
            statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
    }

    public UniqueValuesStorage(ScraperSetting scraperSetting, StatisticManager statisticManager) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/uniqueLinkValues.s3db");

        tableIndex = "_" + RandomStringHelper.getRandomStringOnlyLetters( 10) + "_"
                + new SimpleDateFormat("hh_mm_ss_SSS_dd_MM_yyyy").format(new Date());
        statement = connection.createStatement();

        this.statisticManager = statisticManager;
        createTableAndIndex();

        if(scraperSetting.getStartLinks() != null)
            addAll(scraperSetting.getStartLinks());
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
        catch (SQLException e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return true;
        }
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
            boolean result = statement.executeUpdate(sql) != 0;

            if(result)
                statisticManager.addTotalUniqueLinks(1);
            else
                statisticManager.addDuplicateLinksCounter(1);

            return result;
        }
        catch (SQLException e) {
            //LogHelper.getLogger().log(Level.WARNING, e.getMessage(), e);
            return false;
        }
    }

    public synchronized String get(int id) {
        try{
            String sql = String.format("SELECT link FROM UV_%s WHERE id=%d", tableIndex, id);
            ResultSet resultSet = statement.executeQuery(sql);
            if(resultSet.next())
                return resultSet.getString("link");
            return null;
        }
        catch (SQLException e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
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
        catch (SQLException e) {
            LogHelper.getLogger().log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
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