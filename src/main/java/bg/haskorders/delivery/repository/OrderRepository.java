package bg.haskorders.delivery.repository;

import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

public class OrderRepository {
    private static OrderRepository instance;
    private final List<Order> orders;

    public OrderRepository() {
        this.orders = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepository();
        }
        return instance;
    }

    public synchronized List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public synchronized void addOrder(Order order){
        order.setOrder_date(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        orders.add(order);
    }

    public synchronized boolean removeOrder(int orderID){
        return orders.removeIf(order -> order.getOrder_id() == orderID);
    }

    public synchronized Optional<Order> findById(int orderId){
        return orders.stream().filter(o -> o.getOrder_id() == orderId).findFirst();
    }

    public synchronized boolean updateStatus(int orderId, OrderStatus newStatus) {
        return orders.stream()
                .filter(o -> o.getOrder_id() == orderId)
                .findFirst()
                .map(o -> {
                    o.setStatus(newStatus);
                    return true;
                })
                .orElse(false);
    }

    public synchronized boolean assignDeliveryPerson(int orderId, int deliveryPersonId){
        Order order = findById(orderId).orElse(null);
        if(order != null && order.getStatus() == OrderStatus.READY_FOR_DELIVERY ){
            order.setDelivery_person_id(deliveryPersonId);
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            return true;
        }
        return false;
    }

    public synchronized double calculateCompanyRevenue(LocalDateTime start, LocalDateTime end){
        return orders.stream().filter(o -> !o.getOrder_date().isBefore(start) &&
                        !o.getOrder_date().isAfter(end))
                .mapToDouble(Order::getTotal_amount)
                .sum();
    }

    public synchronized double calculateDeliveryEarnings(int deliveryPersonId, LocalDateTime start, LocalDateTime end){
        List<Order> orders = getAllOrders();
        return orders.stream()
                .filter(order -> order.getDelivery_person_id() != null) // <- this avoids the null crash
                .filter(order -> order.getDelivery_person_id().intValue() == deliveryPersonId)
                .filter(order -> !order.getOrder_date().isBefore(start) && !order.getOrder_date().isAfter(end))
                .mapToDouble(Order::getTotal_amount)
                .sum();
    }

    public synchronized List<Order> getOrdersByStatus(OrderStatus status) {
        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

    public synchronized List<Order> getOrdersByStatusAndDeliveryPerson(OrderStatus status, int deliveryPersonId) {
        return orders.stream()
                .filter(order -> order.getStatus() == status &&
                        order.getDelivery_person_id() != null &&
                        order.getDelivery_person_id() == deliveryPersonId)
                .toList();
    }
    public synchronized boolean isEligibleForBonus(int deliveryPersonId, double threshold, LocalDateTime start, LocalDateTime end) {
        double earnings = calculateDeliveryEarnings(deliveryPersonId, start, end);
        return earnings >= threshold;
    }
}
