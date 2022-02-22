package com.example.startuplogin;

public class Order {

    String orderName;
    String meat;
    String fries;
    String drink;
    String orderImage;

    public Order() {
    }

    public Order(String orderName, String meat, String fries, String drink, String orderImage) {
        this.orderName = orderName;
        this.meat = meat;
        this.fries = fries;
        this.drink = drink;
        this.orderImage = orderImage;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getMeat() {
        return meat;
    }

    public void setMeat(String meat) {
        this.meat = meat;
    }

    public String getFries() {
        return fries;
    }

    public void setFries(String fries) {
        this.fries = fries;
    }

    public String getDrink() {
        return drink;
    }

    public void setDrink(String drink) {
        this.drink = drink;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }
}
