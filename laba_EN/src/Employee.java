import java.time.Duration;
import java.time.LocalTime;
import java.util.Random;

public abstract class Employee {
    protected int employeeId;
    protected LocalTime shiftStart;
    protected LocalTime shiftEnd;
    protected String status;
    protected Order currentOrder;
    protected float earnings;
    protected Storage storage;

    public Employee(LocalTime shiftStart, LocalTime shiftEnd) {
        this.employeeId = new Random().nextInt(1000);
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.status = "Свободен";
        this.earnings = 0;
    }

    public boolean isWorking() {
        LocalTime now = SimulationTime.currentTime();
        return !now.isBefore(shiftStart) && !now.isAfter(shiftEnd);
    }

    public void calculateSalary() {
        int hours = (int) Duration.between(shiftStart, shiftEnd).toHours();
        earnings = hours * 300;
        setStatus("Смена закончилась");
        System.out.println("Работник " + employeeId + " заработал: " + earnings + " руб.");
    }

    public int getEmployeeId() { return employeeId; }
    public Order getCurrentOrder() { return currentOrder; }
    public void setCurrentOrder(Order order) { this.currentOrder = order; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public float getEarnings() { return earnings; }

    public abstract void processOrder();
}