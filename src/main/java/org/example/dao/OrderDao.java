package org.example.dao;

import org.example.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {
    List<Order> getOrdersByDate(LocalDate date) throws FlooringException;
    Order addOrder(LocalDate date, Order order) throws FlooringException;
    Order editOrder(LocalDate date, Order order) throws FlooringException;
    Order removeOrder(LocalDate date, int orderNumber) throws FlooringException;
    Order getOrder(LocalDate date, int orderNumber) throws FlooringException;
}