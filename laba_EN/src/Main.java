import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SimulationTime.setTime(LocalTime.of(9, 0));

        Product laptop = new Product("Ноутбук", 1, 101, 50000.0, 1001);
        Product phone = new Product("Телефон", 2, 102, 30000.0, 1002);
        Product tablet = new Product("Планшет", 3, 103, 35000.0, 1003);

        Map<Product, Integer> stock = new HashMap<>();
        stock.put(laptop, 3);
        stock.put(phone, 4);
        stock.put(tablet, 2);
        Storage storage = new Storage(stock, List.of(0, 0));

        Courier courier = new Courier(LocalTime.of(9, 0), LocalTime.of(18, 0));
        WarehouseStaff keeper = new WarehouseStaff(LocalTime.of(8, 0), LocalTime.of(17, 0));

        storage.addCourier(courier);
        storage.addStaffMember(keeper);

        Customer user1 = new Customer("user1@mail.ru", List.of(5, 5));
        Customer user2 = new Customer("user2@mail.ru", List.of(10, 10));
        Customer user3 = new Customer("user3@mail.ru", List.of(15, 15));
        Customer user4 = new Customer("user4@mail.ru", List.of(20, 20));

        System.out.println("\n=== Заказ 1 в " + SimulationTime.currentTime() + " ===");
        user1.placeOrder(new Product[]{laptop}, storage);
        System.out.println("\n=== Заказ 2 в " + SimulationTime.currentTime() + " ===");
        user2.placeOrder(new Product[]{phone, phone}, storage);
        storage.processOrders();

        System.out.println("\n=== Заказ 3 в " + SimulationTime.currentTime() + " ===");
        user3.placeOrder(new Product[]{tablet}, storage);
        System.out.println("\n=== Заказ 4 в " + SimulationTime.currentTime() + " ===");
        user4.placeOrder(new Product[]{laptop, tablet}, storage);
        storage.processOrders();

        System.out.println("\nОстаток на складе:");
        storage.getInventory().forEach((product, count) ->
                System.out.println(product.getName() + ": " + count)
        );
        storage.calculateSalary();
    }
}