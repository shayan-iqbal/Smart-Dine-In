package com.example.startuplogin.DB;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Cart {

    @PrimaryKey(autoGenerate = true)
    int itemId;
    @ColumnInfo(name = "user_id")
    String userId;
    @ColumnInfo(name = "item_name")
    String itemName;
    @ColumnInfo(name = "item_image")
    String itemImage;
    @ColumnInfo(name = "item_price")
    String itemPrice;
    @ColumnInfo(name = "item_meat")
    String meat;
    @ColumnInfo(name = "item_fries")
    String fries;
    @ColumnInfo(name = "item_drink")
    String drink;
    @ColumnInfo(name = "item_quantity")
    String quantity;

    public Cart(String userId, String itemName, String itemImage, String itemPrice, String meat, String fries, String drink, String quantity) {
        this.userId = userId;
        this.itemName = itemName;
        this.itemImage = itemImage;
        this.itemPrice = itemPrice;
        this.meat = meat;
        this.fries = fries;
        this.drink = drink;
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}

