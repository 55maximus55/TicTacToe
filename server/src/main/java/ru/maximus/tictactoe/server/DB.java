package ru.maximus.tictactoe.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {

    public static Connection connection;
    public static Statement statement;
    public static ResultSet resultSet;

    public static void connect() throws ClassNotFoundException, SQLException {
        connection = null;
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:TEST1.sqlite");
    }

    public static void createDB() throws SQLException {
        statement = connection.createStatement();
        statement.execute("CREATE TABLE if not exists 'users' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'name' text, 'pass' text);");
    }

    public static void writeDB() throws SQLException
    {
        statement.execute("INSERT INTO 'users' ('name', 'pass') VALUES ('root1', '123'); ");
    }

    // -------- Вывод таблицы--------
    public static void readDB() throws SQLException {
        resultSet = statement.executeQuery("SELECT * FROM users");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String pass = resultSet.getString("pass");
            System.out.println("ID = " + id);
            System.out.println("name = " + name);
            System.out.println("pass = " + pass);
            System.out.println();
        }
    }

    public static void closeDB() throws SQLException {
        connection.close();
        statement.close();
        resultSet.close();
    }

    public static int auth(String user, String pass) throws SQLException{
        resultSet = statement.executeQuery("SELECT * FROM users WHERE (name=\"" + user + "\" AND pass=\"" + pass + "\")");

        int id = -1;

        while (resultSet.next()) {
            id = resultSet.getInt("id");
        }
        return id;
    }

}