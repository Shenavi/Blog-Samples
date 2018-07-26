package org.wso2.pizza.service;

public class Pizza {

    public Pizza(String pizzaId, String pizzaName,String pizzaPrice, String pizzaType) {
        this.pizzaName = pizzaName;
        this.pizzaId = pizzaId;
        this.pizzaPrice = pizzaPrice;
        this.pizzaType = pizzaType;
    }

    public String getPizzaId() {
        return pizzaId;
    }

    public void setPizzaId(String pizzaId) {
        this.pizzaId = pizzaId;
    }

    public String getPizzaName() {
        return pizzaName;
    }

    public void setPizzaName(String pizzaName) {
        this.pizzaName = pizzaName;
    }

    public String getPizzaType() {
        return pizzaType;
    }

    public void setPizzaType(String pizzaType) {
        this.pizzaType = pizzaType;
    }

    public String getPizzaPrice() {
        return pizzaPrice;
    }

    public void setPrice(String pizzaPrice) {
        this.pizzaPrice = pizzaPrice;
    }

    String pizzaId;
    String pizzaName;
    String pizzaType;
    String pizzaPrice;
}
