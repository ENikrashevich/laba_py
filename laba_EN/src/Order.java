// Order.java
import java.util.List;

public class Order {
    public enum Status {
        PENDING,
        PROCESSING,
        DELIVERED
    }

    private final Product[] items;
    private final List<Integer> storageLocation;
    private final List<Integer> customerLocation;
    private final Storage storage;
    private Status status;
    private final int orderId;
    private static int nextId = 1;

    public Order(Product[] items,
                 List<Integer> storageLocation,
                 List<Integer> customerLocation,
                 Storage storage) {
        this.items = items;
        this.storageLocation = storageLocation;
        this.customerLocation = customerLocation;
        this.storage = storage;
        this.status = Status.PENDING;
        this.orderId = nextId++;
    }

    // Геттеры и сеттеры
    public Product[] getItems() { return items; }
    public int getOrderId() { return orderId; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Storage getStorage() { return storage; }
    public List<Integer> getStorageLocation() { return storageLocation; }
    public List<Integer> getCustomerLocation() { return customerLocation; }
}