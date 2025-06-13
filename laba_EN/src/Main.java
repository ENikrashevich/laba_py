import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SimulationTime.setTime(LocalTime.of(9, 0));

        Product chair = new Product("Стул", 1, 1, 3332.0, 1);
        Product sofa = new Product("Диван", 2, 2, 31111.0, 2);
        Product table = new Product("Стол", 3, 3, 3222.0, 3);

        Map<Product, Integer> stock = new HashMap<>();
        stock.put(chair, 1);
        stock.put(sofa, 2);
        stock.put(table, 0);
        Storage storage = new Storage(stock, List.of(0, 0));

        Courier courier = new Courier(LocalTime.of(9, 0), LocalTime.of(18, 0));
        WarehouseStaff keeper = new WarehouseStaff(LocalTime.of(8, 0), LocalTime.of(17, 0));

        storage.addCourier(courier);
        storage.addStaffMember(keeper);

        Supplier supplierLaptop = new Supplier(1, chair, 1);
        Supplier supplierSofa = new Supplier(2, sofa,4);
        Supplier supplierTable = new Supplier(3, table,3);

        storage.addSupplier(supplierLaptop);
        storage.addSupplier(supplierSofa);
        storage.addSupplier(supplierTable);

        Customer user1 = new Customer("user1@mail.ru", List.of(5, 5));
        Customer user2 = new Customer("user2@mail.ru", List.of(10, 10));
        Customer user3 = new Customer("user3@mail.ru", List.of(15, 15));
        Customer user4 = new Customer("user4@mail.ru", List.of(20, 20));

        System.out.println("\nЗаказ 1 в " + SimulationTime.currentTime());
        user1.placeOrder(new Product[]{chair}, storage);
        System.out.println("\nЗаказ 2 в " + SimulationTime.currentTime());
        user2.placeOrder(new Product[]{sofa, sofa}, storage);
        storage.processOrders();

        System.out.println("\nЗаказ 3 в " + SimulationTime.currentTime());
        user3.placeOrder(new Product[]{table}, storage);
        System.out.println("\nЗаказ 4 в " + SimulationTime.currentTime());
        user4.placeOrder(new Product[]{sofa, chair, table}, storage);
        storage.processOrders();

        System.out.println("\nОстаток на складе:");
        storage.getInventory().forEach((product, count) ->
                System.out.println(product.getName() + ": " + count)
        );
        storage.calculateSalary();
    }
}