package com.example.startuplogin;

import java.io.Serializable;

public class Table implements Serializable {
    String tableName;
    int tableSeat;
    String tableStatus;
    String tableId;
    String orderId;
    String tableTimeOut;

    public Table(String tableName, int tableSeat, String tableStatus, String tableId, String orderId, String tableTimeOut) {
        this.tableName = tableName;
        this.tableSeat = tableSeat;
        this.tableStatus = tableStatus;
        this.tableId = tableId;
        this.orderId = orderId;
        this.tableTimeOut = tableTimeOut;
    }

    public Table() {
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getTableSeat() {
        return tableSeat;
    }

    public void setTableSeat(int tableSeat) {
        this.tableSeat = tableSeat;
    }

    public String getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(String tableStatus) {
        this.tableStatus = tableStatus;
    }

    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTableTimeOut() {
        return tableTimeOut;
    }

    public void setTableTimeOut(String tableTimeOut) {
        this.tableTimeOut = tableTimeOut;
    }
}
