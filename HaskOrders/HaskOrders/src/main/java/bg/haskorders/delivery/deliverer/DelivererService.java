    package bg.haskorders.delivery.deliverer;
    import bg.haskorders.delivery.model.order.Order;
    import bg.haskorders.delivery.model.order.OrderStatus;
    import bg.haskorders.delivery.repository.OrderRepository;

    import java.time.LocalDateTime;
    import java.util.List;

    public class DelivererService {

        private final OrderRepository orderRepository;

        public DelivererService(OrderRepository orderRepository) {
            this.orderRepository = orderRepository;
        }

        public List<Order> getAvailableOrders() {
            return orderRepository.getOrdersByStatus(OrderStatus.READY_FOR_DELIVERY);
        }

        public List<Order> getAssignedOrders(int deliveryPersonId) {
            return orderRepository.getOrdersByStatusAndDeliveryPerson(OrderStatus.OUT_FOR_DELIVERY, deliveryPersonId);
        }

        public boolean takeOrder(int orderId, int deliveryPersonId) {
            return orderRepository.assignDeliveryPerson(orderId, deliveryPersonId);
        }

        public double getEarnings(int deliveryPersonId, LocalDateTime start, LocalDateTime end) {
            return orderRepository.calculateDeliveryEarnings(deliveryPersonId, start, end);
        }

        public boolean hasBonus(int deliveryPersonId, LocalDateTime start, LocalDateTime end, double threshold) {
            return orderRepository.isEligibleForBonus(deliveryPersonId, threshold, start, end);
        }
        public boolean completeDelivery(int orderId) {
            return orderRepository.updateStatus(orderId, OrderStatus.DELIVERED);
        }

    }

