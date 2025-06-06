import java.util.List;
import java.util.Random;

public class Customer {
    private final String email;
    private final List<Integer> location;
    private final int customerId;

    public Customer(String email, List<Integer> location) {
        this.customerId = new Random().nextInt(1000);
        this.email = email;
        this.location = location;
    }

    public void placeOrder(Product[] items, Storage storage) {
        Order order = new Order(items, storage.getLocation(), this.location, storage);
        storage.addOrder(order);
        System.out.print("Покупатель " + customerId + " сделал заказ " + order.getOrderId() + ": ");
        for (Product item : items){
            System.out.print(item.getName() + " ");
        }
        System.out.println();
    }
}