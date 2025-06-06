import java.time.LocalTime;

public class SimulationTime {
    private static LocalTime currentTime;

    public static void setTime(LocalTime time) {currentTime = time;}

    public static LocalTime currentTime() {return currentTime;}

    public static void advanceTime(long seconds) {currentTime = currentTime.plusSeconds(seconds);}
}