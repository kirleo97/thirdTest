import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateChecker {
    public static boolean isHoliday(LocalDate date) {
        return PeriodCounter.getHolidays().contains(date);
    }

    public static boolean isWeekendDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return (dayOfWeek == DayOfWeek.SATURDAY) || (dayOfWeek == DayOfWeek.SUNDAY);
    }

    public static boolean isItHourBeforeDinnerOrDinnerHour(LocalDateTime localDateTime) {
        return localDateTime.getHour() <= 14 ? true : false;
    }

    public static boolean isItBeforeHolidayWorkDay(LocalDate localDate) {
        if (isWorkDay(localDate)) {
            return (isHoliday(localDate.plusDays(1)) || (isHoliday(getNextMonday(localDate)) && localDate.getDayOfWeek() == DayOfWeek.FRIDAY));
        }
        return false;
    }

    public static boolean isWorkDay(LocalDate localDate) {
        return !isWeekendDay(localDate) && !isHoliday(localDate);
    }

    public static LocalDate getNextMonday(LocalDate localDate) {
        while (localDate.getDayOfWeek() != DayOfWeek.MONDAY)
            localDate = localDate.plusDays(1);
        return localDate;
    }
}
