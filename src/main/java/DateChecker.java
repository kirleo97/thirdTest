import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

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
            return (isHoliday(localDate.plusDays(1)) || (isHoliday(PeriodCounter.getNextMonday(localDate)) && localDate.getDayOfWeek() == DayOfWeek.FRIDAY));
        }
        return false;
    }

    public static boolean isWorkDay(LocalDate localDate) {
        return !isWeekendDay(localDate) && !isHoliday(localDate);
    }

    public static boolean isDataInPeriod(LocalDate checkDate, LocalDate startDate, LocalDate endDate) {
        Period betweenStartAndCheckDate = Period.between(startDate, checkDate);
        Period betweenCheckAndEndDate = Period.between(checkDate, endDate);
        return !betweenStartAndCheckDate.isNegative() && !betweenCheckAndEndDate.isNegative();
    }
}
