package org.example.dao;

import org.example.model.Product;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductDaoFileImplTest {

    private ProductDao dao;

    @BeforeAll
    void setup() {
        dao = new ProductDaoFileImpl();
    }

    @Test
    void testGetAllProducts() throws FlooringException {
        List<Product> products = dao.getAllProducts();

        assertNotNull(products, "Product list should not be null");
        assertTrue(products.size() > 0, "Product list should contain at least one product");

        // Check expected product types exist
        assertTrue(products.stream().anyMatch(p -> p.getProductType().equalsIgnoreCase("Tile")));
        assertTrue(products.stream().anyMatch(p -> p.getProductType().equalsIgnoreCase("Wood")));
    }

    @Test
    void testGetProductByType() throws FlooringException {
        Product wood = dao.getProduct("Wood");
        assertNotNull(wood, "Wood product should exist");
        assertEquals("Wood", wood.getProductType());
        assertTrue(wood.getCostPerSquareFoot().doubleValue() > 0);
        assertTrue(wood.getLaborCostPerSquareFoot().doubleValue() > 0);
    }

    @Test
    void testInvalidProductTypeReturnsNull() throws FlooringException {
        Product invalid = dao.getProduct("Marble");
        assertNull(invalid, "Nonexistent product should return null");
    }
}
