package org.wso2.pizza.service.dto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wso2.pizza.service.Pizza;
import org.wso2.pizza.service.dto.ConnectionManager;

public class PizzaDTO {

    private static final String getPizzaQuery = "Select * from pizza where id = ?;";

    /**
     * Retrieves temporary registration email list, which are email records that have not completed the full
     * sign up process.
     *
     * @return email list
     */
    public static Map<String, Map<String, String>> getPizza(String id) {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        Map<String, Map<String, String>> pizzas = new HashMap<>();
        try {

            connection = ConnectionManager.getConnection();
            Statement st = connection.createStatement();
            String query = "SELECT * FROM  pizza where id=" + id + "";
            System.out.println(query);
            resultSet = st.executeQuery(query);
            System.out.println(resultSet);
            int rowId = 0;
            while (resultSet.next()) {
                Map<String, String> pizza = new HashMap<>();
                String pizzaId = resultSet.getString("id");
                String pizzaName = resultSet.getString("pizzaName");
                String pizzaPrice = resultSet.getString("pizzaPrice");
                String pizzaType = resultSet.getString("pizzaType");
                pizza.put("Pizza Code", pizzaId);
                pizza.put("Pizza Name", pizzaName);
                pizza.put("Pizza Price", pizzaPrice);
                pizza.put("Pizza Type", pizzaType);
                pizzas.put(String.valueOf(rowId++), pizza);
            }
        } catch (SQLException e) {
            System.out.println("Error while executing the query");
        } finally {
            ConnectionManager.closeAllConnections(preparedStatement, connection, resultSet);
        }
        return pizzas;
    }

    public static Map<String, String> pizza = new HashMap<>();

    public static Map<String, String> getPizzaName() {
        // Here we are just hard coding pizza menu. Instead you can als call your database and get the pizza menu.
        pizza.put("Margherita", "$21");
        pizza.put("Funghi", "$22");
        pizza.put("Capricciosa", "$19");
        pizza.put("Quattro Stagioni", "$18");
        pizza.put("Kebabpizza", "$24");
        pizza.put("Vegetariana", "$15");

        return pizza;
    }
}

