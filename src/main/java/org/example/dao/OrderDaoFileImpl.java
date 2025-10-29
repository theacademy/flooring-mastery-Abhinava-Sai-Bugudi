package org.example.dao;

import org.example.model.Order;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Repository
public class OrderDaoFileImpl implements OrderDao {

    //Providing File Path for orders
    private static final String ORDERS_FOLDER = "src/main/resources/Orders";
    private static final String DELIMITER = ",";
    private static final DateTimeFormatter FILE_DATE_FORMAT = DateTimeFormatter.ofPattern("MMddyyyy");

    public OrderDaoFileImpl() {
        File folder = new File(ORDERS_FOLDER);
        //making sure file exists
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringException {

        String fileName = buildFileName(date);
        File file = new File(fileName);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        List<Order> orders = new ArrayList<>();

        try (Scanner sc = new Scanner(new BufferedReader(new FileReader(file)))) {
            //skipping the first row due to headings
            if (sc.hasNextLine()) sc.nextLine();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                Order order = unmarshallOrder(line);//Unmarshalling the order
                orders.add(order);
            }
        } catch (IOException e) {
            throw new FlooringException("Could not load orders for " + date, e);
        }

        return orders;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws FlooringException {
        //getting a specific order by date and given order number
        List<Order> orders = loadOrdersForDate(date);
        for (Order o : orders) {
            if (o.getOrderNumber() == orderNumber) {
                return o;
            }
        }
        return null; // order not found
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws FlooringException {
        List<Order> orders = getOrdersByDate(date);

        // Assigning order number automatically (next available)
        int maxOrderNum = orders.stream()
                .mapToInt(Order::getOrderNumber)
                .max()
                .orElse(0);
        order.setOrderNumber(maxOrderNum + 1);

        orders.add(order);
        writeOrders(date, orders);

        return order;
    }

    @Override
    public Order editOrder(LocalDate date, Order updatedOrder) throws FlooringException {
        List<Order> orders = getOrdersByDate(date);
        boolean found = false;

        //Finding the right order using if by the getOrder function
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderNumber() == updatedOrder.getOrderNumber()) {
                orders.set(i, updatedOrder);
                found = true;
                break;
            }
        }

        if (!found) {
            //not found
            throw new FlooringException("Order #" + updatedOrder.getOrderNumber() + " not found for " + date);
        }

        //writing the order to file
        writeOrders(date, orders);
        return updatedOrder;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws FlooringException {
        List<Order> orders = getOrdersByDate(date);
        Order removed = null;

        //matching order and removing it
        Iterator<Order> it = orders.iterator();
        while (it.hasNext()) {
            Order o = it.next();
            if (o.getOrderNumber() == orderNumber) {
                removed = o;
                it.remove();
                break;
            }
        }

        if (removed != null) {
            writeOrders(date, orders);
        } else {
            throw new FlooringException("Order #" + orderNumber + " not found for " + date);
        }

        return removed;
    }

    // ---------- Helper Methods ----------

    private String buildFileName(LocalDate date) {
        //building file name in the orders folder, using the date format
        return ORDERS_FOLDER + "/Orders_" + date.format(FILE_DATE_FORMAT) + ".txt";
    }

    private void writeOrders(LocalDate date, List<Order> orders) throws FlooringException {
        String fileName = buildFileName(date);

        try (PrintWriter out = new PrintWriter(new FileWriter(fileName))) {
            //writing the header first and order next
            out.println("OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total");

            for (Order o : orders) {
                out.println(marshallOrder(o));
            }
        } catch (IOException e) {
            throw new FlooringException("Error writing to file: " + fileName, e);
        }
    }

    private List<Order> loadOrdersForDate(LocalDate date) throws FlooringException {

        //loading all orders for the date
        String fileName = getFileName(date);
        File file = new File(fileName);
        List<Order> orders = new ArrayList<>();

        if (!file.exists()) return orders; // no orders for that date yet

        try (Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // skip header
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Order order = unmarshallOrder(line);
                orders.add(order);
            }
        } catch (IOException e) {
            throw new FlooringException("Could not read orders for " + date, e);
        }
        return orders;
    }

    private String marshallOrder(Order order) {
        //getting the order ready for writing
        return String.join(DELIMITER,
                String.valueOf(order.getOrderNumber()),
                order.getCustomerName(),
                order.getState(),
                order.getTaxRate().toString(),
                order.getProductType(),
                order.getArea().toString(),
                order.getCostPerSquareFoot().toString(),
                order.getLaborCostPerSquareFoot().toString(),
                order.getMaterialCost().toString(),
                order.getLaborCost().toString(),
                order.getTax().toString(),
                order.getTotal().toString()
        );
    }

    private Order unmarshallOrder(String line) {
        //getting the order ready for reading
        String[] tokens = line.split(DELIMITER);
        Order order = new Order();
        order.setOrderNumber(Integer.parseInt(tokens[0]));
        order.setCustomerName(tokens[1]);
        order.setState(tokens[2]);
        order.setTaxRate(new BigDecimal(tokens[3]));
        order.setProductType(tokens[4]);
        order.setArea(new BigDecimal(tokens[5]));
        order.setCostPerSquareFoot(new BigDecimal(tokens[6]));
        order.setLaborCostPerSquareFoot(new BigDecimal(tokens[7]));
        order.setMaterialCost(new BigDecimal(tokens[8]));
        order.setLaborCost(new BigDecimal(tokens[9]));
        order.setTax(new BigDecimal(tokens[10]));
        order.setTotal(new BigDecimal(tokens[11]));
        return order;
    }

    private String getFileName(LocalDate date) {
        //getting file name by date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddyyyy");
        return "src/main/resources/Orders/Orders_" + date.format(formatter) + ".txt";
    }

}
