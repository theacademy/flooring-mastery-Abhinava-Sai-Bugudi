package org.example.dao;

import org.example.model.Product;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
@Repository
public class ProductDaoFileImpl implements ProductDao {
    private final String PRODUCT_FILE = "src/main/resources/Data/Products.txt";
    private final String DELIMITER = ",";
    private Map<String, Product> products = new HashMap<>();


    @Override
    public List<Product> getAllProducts() throws FlooringException {
        loadProducts(); // Ensuring we have fresh data
        return new ArrayList<>(products.values()); // Return all products from the hashmap
    }


    @Override
    public Product getProduct(String productType) throws FlooringException {
        if (productType == null || productType.isBlank()) return null;
        loadProducts();
        return products.get(productType.trim().toLowerCase()); // case insensitive and whitespace resistant
    }

    private void loadProducts() throws FlooringException {
        products.clear();
        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(PRODUCT_FILE)))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // skipping header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                //splitting using the delimiter
                String[] tokens = line.split(DELIMITER);
                if (tokens.length == 3) {
                    Product product = new Product();
                    product.setProductType(tokens[0].trim());
                    product.setCostPerSquareFoot(new BigDecimal(tokens[1].trim()));
                    product.setLaborCostPerSquareFoot(new BigDecimal(tokens[2].trim()));
                    products.put(product.getProductType().toLowerCase(), product); // storing lowercase
                }
            }
        } catch (IOException e) {
            throw new FlooringException("Could not load products file.", e);
        }
    }


}
