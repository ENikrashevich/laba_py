import java.util.*;

public class Storage {
    private final int storageId;
    private final List<Supplier> suppliers;
    private final List<Integer> location;
    private final List<WarehouseStaff> staff;
    private final List<Courier> couriers;
    private final Queue<Order> pendingOrders;
    public final Queue<Order> readyForDelivery;
    private final Map<Product, Integer> inventory;

    public Storage(Map<Product, Integer> inventory, List<Integer> location) {
        this.storageId = new Random().nextInt(1000);
        this.location = location;
        this.inventory = inventory;
        this.suppliers = new ArrayList<>();
        this.couriers = new ArrayList<>();
        this.staff = new ArrayList<>();
        this.pendingOrders = new LinkedList<>();
        this.readyForDelivery = new LinkedList<>();
    }

    public void addSupplier(Supplier supplier) {
        suppliers.add(supplier);
    }

    public void addStaffMember(WarehouseStaff staffMember) {
        staff.add(staffMember);
    }

    public void addCourier(Courier courier) {
        couriers.add(courier);
        courier.setStorage(this);
    }

    public void addOrder(Order order) {
        pendingOrders.offer(order);
    }

    public int getStorageId() {
        return storageId;
    }

    public List<Integer> getLocation() {
        return location;
    }

    public Map<Product, Integer> getInventory() {
        return inventory;
    }

    public Queue<Order> getPendingOrders() {
        return pendingOrders;
    }

    private void requestRestock(Product product, int quantity) {
        for (Supplier supplier : suppliers) {
            if (supplier.getProduct().equals(product)) {
                supplier.deliver(this, quantity);
                break;
            }
        }
    }

    public void processOrders() {
        Queue<Order> unprocessed = new LinkedList<>();

        while (!pendingOrders.isEmpty()) {
            Order order = pendingOrders.poll();
            boolean canProcess = true;

            for (Product item : order.getItems()) {
                int available = inventory.getOrDefault(item, 0);
                if (available <= 0) {
                    requestRestock(item, 10);
                    available = inventory.getOrDefault(item, 0);
                }
                if (available <= 0) {
                    canProcess = false;
                }
            }

            if (!canProcess) {
                unprocessed.offer(order);
                System.out.println("Заказ " + order.getOrderId() +
                        " отложен (ожидается поставка)");
            } else {
                // Проверяем наличие свободных курьеров перед обработкой
                if (hasAvailableCourier()) {
                    assignOrder(order);
                } else {
                    unprocessed.offer(order);
                    System.out.println("Заказ " + order.getOrderId() +
                            " отложен (нет свободных курьеров)");
                }
            }
        }
        pendingOrders.addAll(unprocessed);
    }

    // Проверка наличия свободных курьеров
    public boolean hasAvailableCourier() {
        for (Courier courier : couriers) {
            if (courier.isWorking() &&
                    courier.getCurrentOrder() == null &&
                    !courier.isReturning()) {
                return true;
            }
        }
        return false;
    }

    // Метод для назначения заказа курьеру
    public void assignToCourier(Order order) {
        for (Courier courier : couriers) {
            if (courier.isWorking() &&
                    courier.getCurrentOrder() == null &&
                    !courier.isReturning()) {
                courier.setCurrentOrder(order);
                courier.processOrder();
                return;
            }
        }
    }


    private void assignOrder(Order order) {
        for (WarehouseStaff staffMember : staff) {
            if (staffMember.isWorking() && staffMember.getCurrentOrder() == null) {
                staffMember.setCurrentOrder(order);
                staffMember.processOrder();
                return;
            }
        }
        pendingOrders.offer(order);
    }

    // Обработка готовых заказов
    public void processReadyOrders() {
        if (readyForDelivery.isEmpty()) return;

        for (Courier courier : couriers) {
            if (courier.isWorking() &&
                    courier.getCurrentOrder() == null &&
                    !courier.isReturning()) {
                Order order = readyForDelivery.poll();
                if (order != null) {
                    courier.setCurrentOrder(order);
                    courier.processOrder();
                    return;
                }
            }
        }
    }

    // Добавление заказа в очередь готовых к доставке
    public void addReadyForDelivery(Order order) {
        readyForDelivery.offer(order);
        System.out.println("Заказ " + order.getOrderId() + " добавлен в очередь доставки");
    }

    public void calculateSalary(){
        for (WarehouseStaff staffMember : staff) {
            staffMember.calculateSalary();
        }
        for (Courier courier : couriers) {
            courier.calculateSalary();
        }
    }
}