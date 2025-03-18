package org.example.db;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionClass {
    private static Connection connection;
    private static String url;
    private static String user;
    private static String password;
    private static Database database;

    private ConnectionClass() {}

    public static void setConfig() throws IOException {
        try (InputStream inputStream = ConnectionClass.class.getResourceAsStream("/db/db.properties")) {
            Properties properties = new Properties();

            properties.load(inputStream);

            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");
        }
    }

    public static void setConfig(String newUrl, String newUser, String newPassword) {
        url = newUrl;
        user = newUser;
        password = newPassword;

    }

    public static Connection getConnection() throws SQLException, LiquibaseException {
        if (url == null) {
            try {
                setConfig();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        connection = DriverManager.getConnection(url, user, password);

        if (database == null) {
            database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("/db/changelog/changelog.xml", new ClassLoaderResourceAccessor(), database);
            liquibase.update();
        }

        return connection;
    }

    public static void nullConnection() {
        database = null;
        connection = null;
    }
}