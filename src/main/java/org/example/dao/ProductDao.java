package org.example.dao;

import org.example.model.Product;

import java.util.List;

public interface ProductDao {
    List<Product> getAllProducts() throws FlooringException;
    Product getProduct(String productType) throws FlooringException;
}
