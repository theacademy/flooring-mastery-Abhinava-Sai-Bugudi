package org.example.service;

import org.example.dao.*;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Tax;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class FlooringServiceImpl implements FlooringService {

    private final OrderDao orderDao;
    private final ProductDao productDao;
    private final TaxDao taxDao;
    @Autowired
    public FlooringServiceImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }


    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringException {
        //getting orders by date
        return orderDao.getOrdersByDate(date);
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws FlooringException {
        //getting a specific order
        return orderDao.getOrder(date, orderNumber);
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws FlooringException {

        //validating order
        validateOrder(order);

        if (date.isBefore(LocalDate.now())) {
            throw new FlooringException("Order date must be in the future.");
        }

        //calculating order costs
        calculateOrderCosts(order);
        //assigning an order number and writing it
        return orderDao.addOrder(date, order);
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws FlooringException {
        //same as adding but for relevant fields
        validateOrder(order);
        calculateOrderCosts(order);
        return orderDao.editOrder(date, order);
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws FlooringException {
        //removing specific order
        return orderDao.removeOrder(date, orderNumber);
    }


    @Override
    public List<Product> getAllProducts() throws FlooringException {
        return productDao.getAllProducts();
    }

    @Override
    public List<Tax> getAllTaxes() throws FlooringException {
        return taxDao.getAllTaxes();
    }

    private static final Set<Character> ALLOWED_CHARS = Set.of(
            ' ', '.', ',', '-', '\'',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '0','1','2','3','4','5','6','7','8','9'
    );//accepted set of characters

    private void validateOrder(Order order) throws FlooringException {
        if (order == null)
            throw new FlooringException("Order cannot be null.");

        String name = order.getCustomerName();
        if (name == null || name.trim().isEmpty())
            throw new FlooringException("Customer name cannot be blank.");
        for (char c : name.trim().toCharArray()) {
            if (!ALLOWED_CHARS.contains(c))
                throw new FlooringException("Invalid character '" + c + "' in name.");
        }

        // Product validation
        Product product = productDao.getProduct(order.getProductType());
        if (product == null)
            throw new FlooringException("Invalid product type: " + order.getProductType());

        // State validation
        Tax tax = taxDao.getTax(order.getState());
        if (tax == null)
            throw new FlooringException("Invalid state abbreviation: " + order.getState());

        // Area validation
        BigDecimal area = order.getArea();
        if (area == null)
            throw new FlooringException("Area is required.");
        if (area.compareTo(new BigDecimal("100")) < 0)
            throw new FlooringException("Area must be at least 100 sq ft.");
    }

    // --------------------- CALCULATIONS ---------------------


    public void calculateOrderCosts(Order order) throws FlooringException {
        Product product = productDao.getProduct(order.getProductType());
        Tax tax = taxDao.getTax(order.getState());

        BigDecimal area = order.getArea();
        BigDecimal costPerSqFt = product.getCostPerSquareFoot();
        BigDecimal laborCostPerSqFt = product.getLaborCostPerSquareFoot();

        BigDecimal materialCost = area.multiply(costPerSqFt);
        BigDecimal laborCost = area.multiply(laborCostPerSqFt);
        BigDecimal subtotal = materialCost.add(laborCost);
        BigDecimal taxAmount = subtotal
                .multiply(tax.getTaxRate().divide(new BigDecimal("100")))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        order.setTaxRate(tax.getTaxRate());
        order.setCostPerSquareFoot(costPerSqFt);
        order.setLaborCostPerSquareFoot(laborCostPerSqFt);
        order.setMaterialCost(materialCost);
        order.setLaborCost(laborCost);
        order.setTax(taxAmount);
        order.setTotal(total);
    }

    @Override
    public void calculateCosts(Order order) throws FlooringException {
        // reuse the same calculation logic
        calculateOrderCosts(order);
    }

}
