package org.wso2.pizza.service.dto;

import com.mysql.jdbc.Driver;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionManager {

    static Connection con;
    static String url;

    public static Connection getConnection() {

        url = "jdbc:mysql://localhost:3306/pizzadb";

        try {

            Driver dr = new com.mysql.jdbc.Driver();
            Properties properties = new Properties();
            properties.put("user","root");
            properties.put("password","root");

            con = dr.connect(url,properties);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Utility method to close the connection streams.
     *
     * @param preparedStatement PreparedStatement
     * @param connection        Connection
     * @param resultSet         ResultSet
     */
    public static void closeAllConnections(PreparedStatement preparedStatement, Connection connection,
                                           ResultSet resultSet) {
        closeResultSet(resultSet);
        closeStatement(preparedStatement);
        closeConnection(connection);
    }

    /**
     * Method used to close connection stream and prepared statement in database upadate scenario
     *
     * @param preparedStatement PreparedStatement
     * @param connection        Connection
     */
    public static void closePSAndConnection(PreparedStatement preparedStatement, Connection connection) {
        closeStatement(preparedStatement);
        closeConnection(connection);
    }

    /**
     * Close Connection
     *
     * @param dbConnection Connection
     */
    private static void closeConnection(Connection dbConnection) {
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException e) {
               System.out.println("A database error occurred. Unable to close the database connections. Continuing with " +
                        "others. - " + e.getMessage());
            }
        }
    }

    /**
     * Close ResultSet
     *
     * @param resultSet ResultSet
     */
    private static void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("A database error occurred. Unable to close the ResultSet  - " + e.getMessage());
            }
        }
    }

    /**
     * Close PreparedStatement
     *
     * @param preparedStatement PreparedStatement
     */
    private static void closeStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                System.out.println("A database error occurred. Unable to close the PreparedStatement. Continuing with" +
                        " others. - " + e.getMessage());
            }
        }
    }
}
