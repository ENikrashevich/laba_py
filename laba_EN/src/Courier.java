import java.time.LocalTime;
import java.util.List;

public class Courier extends Employee {

    private boolean isReturning; // Флаг для отслеживания состояния возврата

    public Courier(LocalTime shiftStart, LocalTime shiftEnd) {
        super(shiftStart, shiftEnd);
        this.isReturning = false;
    }

    @Override
    public void processOrder() {
        if (currentOrder == null || !isWorking() || isReturning) {
            System.out.println("Курьер " + employeeId + " занят или не на смене");
            return;
        }

        setStatus("Доставка");
        // Расчет расстояния и времени до клиента
        List<Integer> storageLoc = currentOrder.getStorageLocation();
        List<Integer> customerLoc = currentOrder.getCustomerLocation();
        double distanceToCustomer = calculateDistance(storageLoc, customerLoc);

        System.out.println("Курьер " + employeeId + " начал доставку заказа " +
                currentOrder.getOrderId() + " в " + SimulationTime.currentTime());
        System.out.println("От склада " + storageLoc + " до клиента " + customerLoc);
        System.out.println("Расстояние: " + String.format("%.2f", distanceToCustomer) + " км");

        int deliveryTime = (int) (distanceToCustomer * 30 + 120);  // 30 сек/км + 2 мин
        System.out.println("Ожидаемое время доставки: " + deliveryTime / 60 + " мин " + deliveryTime % 60 + " сек");

        // Доставка заказа
        SimulationTime.advanceTime(deliveryTime);
        currentOrder.setStatus(Order.Status.DELIVERED);
        System.out.println("✓ Курьер " + employeeId + " доставил заказ " +
                currentOrder.getOrderId() + " в " + SimulationTime.currentTime());

        // Возврат на склад
        isReturning = true;
        setStatus("Возвращение");
        returnToStorage();
    }

    // Метод для возврата на склад
    private void returnToStorage() {
        List<Integer> storageLoc = currentOrder.getStorageLocation();
        List<Integer> currentLoc = currentOrder.getCustomerLocation();
        double returnDistance = calculateDistance(currentLoc, storageLoc);

        int returnTime = (int) (returnDistance * 30);
        System.out.println("Курьер " + employeeId + " возвращается на склад");
        System.out.println("Расстояние: " + String.format("%.2f", returnDistance) + " км");
        System.out.println("Ожидаемое время: " + returnTime/60 + " мин " + returnTime%60 + " сек");

        SimulationTime.advanceTime(returnTime);
        System.out.println("✓ Курьер " + employeeId + " вернулся на склад в " + SimulationTime.currentTime());

        // После возврата освобождаем курьера
        isReturning = false;
        currentOrder = null;
        setStatus("Available");

        // Проверяем новые заказы
        storage.processReadyOrders();
    }

    private double calculateDistance(List<Integer> loc1, List<Integer> loc2) {
        int dx = loc2.get(0) - loc1.get(0);
        int dy = loc2.get(1) - loc1.get(1);
        return Math.sqrt(dx * dx + dy * dy);
    }

    public void setStorage(Storage storage){this.storage = storage;}
    public boolean isReturning() {return isReturning;}
}