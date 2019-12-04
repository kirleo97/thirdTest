import java.time.*;
import java.util.HashSet;
import java.util.Set;

public class PeriodCounter {
    private static Set<LocalDate> holidays = new HashSet<>();

    public static void addHoliday(LocalDate date) {
        holidays.add(date);
    }

    public static int countWorkHoursBeforeNewYear(ZonedDateTime zonedDateTime) {
        ZonedDateTime ourZonedDataTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Moscow"));
        LocalDateTime localDateTime = ourZonedDataTime.toLocalDateTime();
        LocalDate localDate = ourZonedDataTime.toLocalDate();

        // получим оставшиеся рабочие часы для текущего дня
        int numberOfWorkHoursForCurrentDay = getWorkHoursForCurrentDay(localDateTime);

        // получим оставшиеся рабочие часы до конца года, начиная со следующего дня...
        LocalDate newYearLocalDate = LocalDate.of(localDate.plusYears(1).getYear(), 1, 1);
        int numberOfFullDays = 0;
        int numberOfWeekends = 0;
        int numberOfHolidays = 0;
        int numberOfBeforeHolidayWorkDays = 0;

        LocalDate localDateForCount = localDate.plusDays(1);
        while (!localDateForCount.isEqual(newYearLocalDate)) {
            numberOfFullDays++;
            if (isHoliday(localDateForCount))
                numberOfHolidays++;
            if (isWeekendDay(localDateForCount))
                numberOfWeekends++;
            if (isItBeforeHolidayWorkDay(localDateForCount))
                numberOfBeforeHolidayWorkDays++;
            localDateForCount = localDateForCount.plusDays(1);
        }

        int numberOfWorkDaysAfterCurrentDay = numberOfFullDays - numberOfWeekends - numberOfHolidays;
        int numberOfWorkHours = numberOfWorkHoursForCurrentDay + numberOfWorkDaysAfterCurrentDay * 8 - numberOfBeforeHolidayWorkDays;

        return numberOfWorkHours;
    }

    public static int getWorkHoursForCurrentDay(LocalDateTime localDateTime) {
        int currentHour = localDateTime.getHour();
        LocalDate localDate = localDateTime.toLocalDate();
        if (isHoliday(localDate) || isWeekendDay(localDate))
            return 0;
        if (isItBeforeHolidayWorkDay(localDate)) {
            if (isItHourBeforeDinnerOrDinnerHour(localDateTime))
                return 18 - currentHour - 1;
            else
                return 18 - currentHour;
        } else {
            if (isItHourBeforeDinnerOrDinnerHour(localDateTime))
                return 19 - currentHour - 1;
            else
                return 19 - currentHour;
        }
    }


    public static boolean isHoliday(LocalDate date) {
        return holidays.contains(date);
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
