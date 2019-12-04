import java.time.ZonedDateTime;

public class Task {
    public static void main(String[] args) {
        System.out.println(PeriodCounter.countWorkHoursBeforeNewYear(ZonedDateTime.now()));
        System.out.println(ZonedDateTime.now().getHour());
    }
}