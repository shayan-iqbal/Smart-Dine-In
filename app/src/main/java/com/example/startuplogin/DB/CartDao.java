package com.example.startuplogin.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CartDao {

    @Query("select * from Cart")
    List<Cart> getAllItems();

    @Query("select * from Cart where user_id =:uid")
    List<Cart> getAllItemsOfCurrentUser(String uid);

    @Query("UPDATE Cart SET item_quantity =:quantity WHERE itemId =:itemId")
    void updateCartItem(String quantity, int itemId);

    @Insert
    void insertCartItem(Cart... cart);

    @Query("DELETE FROM Cart WHERE itemId=:itemId")
    void deleteCartItem(int itemId);

    @Query("select sum(item_price*item_quantity) from cart where user_id=:uid")
    int countPrice(String uid);
}
