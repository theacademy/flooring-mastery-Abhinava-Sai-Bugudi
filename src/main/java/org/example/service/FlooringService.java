package org.example.service;

import org.example.dao.FlooringException;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Tax;

import java.time.LocalDate;
import java.util.List;

public interface FlooringService {

    // Orders
    List<Order> getOrdersByDate(LocalDate date) throws FlooringException;
    Order getOrder(LocalDate date, int orderNumber) throws FlooringException;
    Order addOrder(LocalDate date, Order order) throws FlooringException;
    Order editOrder(LocalDate date, Order order) throws FlooringException;
    Order removeOrder(LocalDate date, int orderNumber) throws FlooringException;

    // Reference Data
    List<Product> getAllProducts() throws FlooringException;
    List<Tax> getAllTaxes() throws FlooringException;
    void calculateCosts(Order order) throws FlooringException;

}
