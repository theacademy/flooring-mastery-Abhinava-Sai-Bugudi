package org.example.dao;

import org.example.model.Order;
import org.junit.jupiter.api.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderDaoFileImplTest {

    private OrderDaoFileImpl dao;
    private final LocalDate testDate = LocalDate.of(2025, 11, 29);
    private final String testFile = "Orders/Orders_11292025.txt";

    @BeforeAll
    void setupDir() {
        new File("Orders").mkdirs();
    }

    @BeforeEach
    void setup() {
        dao = new OrderDaoFileImpl();
        new File(testFile).delete(); // ensure clean state before each test
    }

    @Test
    void testAddAndGetOrder() throws FlooringException {
        Order order = new Order();
        order.setCustomerName("Tony Soprano");
        order.setState("NJ");
        order.setTaxRate(new BigDecimal("6.25"));
        order.setProductType("Wood");
        order.setArea(new BigDecimal("200"));
        order.setCostPerSquareFoot(new BigDecimal("5.15"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.75"));
        order.setMaterialCost(new BigDecimal("1030.00"));
        order.setLaborCost(new BigDecimal("950.00"));
        order.setTax(new BigDecimal("123.75"));
        order.setTotal(new BigDecimal("2103.75"));

        dao.addOrder(testDate, order);

        List<Order> orders = dao.getOrdersByDate(testDate);
        assertEquals(1, orders.size(), "Order count should be 1 after adding");

        Order fromFile = dao.getOrder(testDate, order.getOrderNumber());
        assertNotNull(fromFile, "Order should be retrieved successfully");
        assertEquals("Tony Soprano", fromFile.getCustomerName());
        assertEquals(new BigDecimal("2103.75"), fromFile.getTotal());
    }

    @Test
    void testEditOrder() throws FlooringException {
        Order order = new Order();
        order.setCustomerName("Paulie Walnuts");
        order.setState("NJ");
        order.setTaxRate(new BigDecimal("6.25"));
        order.setProductType("Tile");
        order.setArea(new BigDecimal("140"));
        order.setCostPerSquareFoot(new BigDecimal("3.50"));
        order.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        order.setMaterialCost(new BigDecimal("490.00"));
        order.setLaborCost(new BigDecimal("581.00"));
        order.setTax(new BigDecimal("67.06"));
        order.setTotal(new BigDecimal("1138.06"));
        dao.addOrder(testDate, order);

        // Simulate edit
        order.setCustomerName("Silvio Dante");
        dao.editOrder(testDate, order);

        Order edited = dao.getOrder(testDate, order.getOrderNumber());
        assertEquals("Silvio Dante", edited.getCustomerName(), "Customer name should update correctly");
    }

    @Test
    void testRemoveOrder() throws FlooringException {
        Order order = new Order();
        order.setCustomerName("Christopher Moltisanti");
        order.setState("NY");
        order.setTaxRate(new BigDecimal("8.50"));
        order.setProductType("Laminate");
        order.setArea(new BigDecimal("180"));
        order.setCostPerSquareFoot(new BigDecimal("1.75"));
        order.setLaborCostPerSquareFoot(new BigDecimal("2.10"));
        order.setMaterialCost(new BigDecimal("315.00"));
        order.setLaborCost(new BigDecimal("378.00"));
        order.setTax(new BigDecimal("58.97"));
        order.setTotal(new BigDecimal("751.97"));
        dao.addOrder(testDate, order);

        Order removed = dao.removeOrder(testDate, order.getOrderNumber());
        assertNotNull(removed, "Removed order should not be null");
        assertEquals("Christopher Moltisanti", removed.getCustomerName());
    }
}
