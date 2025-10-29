package org.example.service;

import org.example.dao.*;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Tax;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlooringServiceImplTest {

    private FlooringService service;

    @BeforeAll
    void setup() {
        OrderDao orderDao = new OrderDaoFileImpl();
        ProductDao productDao = new ProductDaoFileImpl();
        TaxDao taxDao = new TaxDaoFileImpl();
        service = new FlooringServiceImpl(orderDao, productDao, taxDao);
    }

    @Test
    void testAddAndRetrieveOrder() throws FlooringException {
        Order order = new Order();
        order.setCustomerName("Tony Soprano");
        order.setState("KY");
        order.setProductType("Tile");
        order.setArea(new BigDecimal("160"));

        LocalDate testDate = LocalDate.now().plusDays(3);

        Order added = service.addOrder(testDate, order);
        assertNotNull(added, "Order should be successfully added");
        assertEquals("Tony Soprano", added.getCustomerName());
        assertTrue(added.getTotal().compareTo(BigDecimal.ZERO) > 0, "Total should be positive");

        List<Order> retrieved = service.getOrdersByDate(testDate);
        assertTrue(retrieved.size() > 0, "Orders list should not be empty");
    }

    @Test
    void testInvalidCustomerNameThrowsException() {
        Order order = new Order();
        order.setCustomerName("!!!");
        order.setState("KY");
        order.setProductType("Tile");
        order.setArea(new BigDecimal("120"));

        assertThrows(FlooringException.class, () ->
                        service.addOrder(LocalDate.now().plusDays(2), order),
                "Invalid name should throw an exception");
    }

    @Test
    void testCalculationAccuracy() throws FlooringException {
        Product tile = new Product();
        tile.setProductType("Tile");
        tile.setCostPerSquareFoot(new BigDecimal("3.50"));
        tile.setLaborCostPerSquareFoot(new BigDecimal("4.15"));

        Tax tx = new Tax();
        tx.setStateAbbreviation("TX");
        tx.setTaxRate(new BigDecimal("4.45"));

        Order order = new Order();
        order.setCustomerName("Silvio Dante");
        order.setState("TX");
        order.setProductType("Tile");
        order.setArea(new BigDecimal("100"));

        service.calculateCosts(order);

        BigDecimal expectedMaterial = new BigDecimal("350.00");
        BigDecimal expectedLabor = new BigDecimal("415.00");
        BigDecimal expectedTax = new BigDecimal("34.04"); // corrected from 34.09
        BigDecimal expectedTotal = new BigDecimal("799.04"); // corrected from 799.09

        assertEquals(0, expectedMaterial.compareTo(order.getMaterialCost()));
        assertEquals(0, expectedLabor.compareTo(order.getLaborCost()));
        assertEquals(0, expectedTax.compareTo(order.getTax()));
        assertEquals(0, expectedTotal.compareTo(order.getTotal()));
    }

    @Test
    void testAreaValidationThrowsException() {
        Order order = new Order();
        order.setCustomerName("Paulie Walnuts");
        order.setState("TX");
        order.setProductType("Wood");
        order.setArea(new BigDecimal("50")); // too small

        assertThrows(FlooringException.class, () ->
                        service.addOrder(LocalDate.now().plusDays(1), order),
                "Area below 100 sq ft should throw exception");
    }
}
