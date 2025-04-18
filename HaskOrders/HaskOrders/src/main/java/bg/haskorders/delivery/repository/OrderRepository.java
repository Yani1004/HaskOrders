package bg.haskorders.delivery.repository;

import bg.haskorders.delivery.model.order.Order;
import bg.haskorders.delivery.model.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Collections;

public class OrderRepository {
    private final List<Order> orders;

    public OrderRepository(List<Order> initialOrders) {
        this.orders = Collections.synchronizedList(new ArrayList<>(initialOrders));
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
        return orders.stream().filter(o -> !o.getOrder_date().isBefore(start)&&
                        !o.getOrder_date().isAfter(end) &&
                        o.getDelivery_person_id() == deliveryPersonId &&
                        o.getStatus() == OrderStatus.DELIVERED &&
                        o.getDelivery_person_id() != null)
                .mapToDouble(Order::getTotal_amount)
                .sum();
    }
}
