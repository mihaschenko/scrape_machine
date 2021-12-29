import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteTest {
    private Connection connection = null;
    private Statement statement = null;

    public void connection() throws SQLException
    {
        connection = DriverManager.getConnection("jdbc:sqlite:src/main/resources/uniqueLinkValues.s3db");
    }

    @Test
    public void createTable() throws SQLException {
        try{
            connection();
            statement = connection.createStatement();
            String sql = "CREATE TABLE 'test' ('hash' INT NOT NULL, 'link' TEXT NOT NULL)";
            statement.executeUpdate(sql);

            sql = "CREATE INDEX hash ON test(hash)";
            statement.executeUpdate(sql);
        }
        finally {
            if(statement != null)
                statement.close();
            if(connection != null)
                connection.close();
        }
    }
}
