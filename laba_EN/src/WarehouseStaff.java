// WarehouseStaff.java
import java.time.LocalTime;
import java.util.Map;

public class WarehouseStaff extends Employee {
    public WarehouseStaff(LocalTime shiftStart, LocalTime shiftEnd) {
        super(shiftStart, shiftEnd);
    }

    @Override
    public void processOrder() {
        if (currentOrder == null || !isWorking()) {
            return;
        }

        Map<Product, Integer> inventory = currentOrder.getStorage().getInventory();
        boolean itemsAvailable = true;

        for (Product item : currentOrder.getItems()) {
            if (inventory.getOrDefault(item, 0) < 1) {
                itemsAvailable = false;
                break;
            }
        }

        if (!itemsAvailable) {
            System.out.println("[Ошибка] Недостаточно товаров для заказа " +
                    currentOrder.getOrderId());
            currentOrder.getStorage().getPendingOrders().offer(currentOrder);
            currentOrder = null;
            return;
        }

        int processingTime = currentOrder.getItems().length * 45;
        SimulationTime.advanceTime(processingTime);
        LocalTime completionTime = SimulationTime.currentTime();

        for (Product item : currentOrder.getItems()) {
            inventory.put(item, inventory.get(item) - 1);
        }

        currentOrder.setStatus(Order.Status.PROCESSING);
        System.out.println("Кладовщик " + employeeId +
                " собрал заказ " + currentOrder.getOrderId() +
                " в " + completionTime);

        if (currentOrder.getStorage().hasAvailableCourier()) {
            currentOrder.getStorage().assignToCourier(currentOrder);
        } else {
            // Если нет свободных курьеров, добавляем в очередь готовых заказов
            currentOrder.getStorage().addReadyForDelivery(currentOrder);
            System.out.println("Заказ " + currentOrder.getOrderId() +
                    " готов к доставке, ожидает курьера");
        }

        currentOrder = null;
        setStatus("Available");
    }
}