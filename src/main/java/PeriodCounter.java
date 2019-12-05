import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

public class PeriodCounter {
    private static Set<LocalDate> holidays = new HashSet<>();

    public static Set<LocalDate> getHolidays() {
        return holidays;
    }

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
            if (DateChecker.isHoliday(localDateForCount))
                numberOfHolidays++;
            if (DateChecker.isWeekendDay(localDateForCount))
                numberOfWeekends++;
            if (DateChecker.isItBeforeHolidayWorkDay(localDateForCount))
                numberOfBeforeHolidayWorkDays++;
            localDateForCount = localDateForCount.plusDays(1);
        }

        int numberOfWorkDaysAfterCurrentDay = numberOfFullDays - numberOfWeekends - numberOfHolidays;
        int numberOfWorkHours = numberOfWorkHoursForCurrentDay + numberOfWorkDaysAfterCurrentDay * 8 - numberOfBeforeHolidayWorkDays;

        return numberOfWorkHours;
    }

    public static void countWorkWeeksAndDaysBeforeNewYear(ZonedDateTime zonedDateTime) {
        ZonedDateTime ourZonedDataTime = zonedDateTime.withZoneSameInstant(ZoneId.of("Europe/Moscow"));
        LocalDate startLocalDate = ourZonedDataTime.toLocalDate();
        LocalDate newYearLocalDate = LocalDate.of(startLocalDate.plusYears(1).getYear(), 1, 1);

        Period periodBetweenCurrentDayAndNewYear = Period.between(startLocalDate, newYearLocalDate);

        // для начала посчитаем общее количество месяцев, недель и дней до НГ, включая праздники и выходные дни
        int numberOfDays = (int) ChronoUnit.DAYS.between(startLocalDate, newYearLocalDate);
        System.out.println("В общем, до НГ осталось " + periodBetweenCurrentDayAndNewYear.getMonths() + " месяцев и " + periodBetweenCurrentDayAndNewYear.getDays() + " дней.");
        System.out.println("В общем, до НГ осталось " + numberOfDays/7 + " недель и " + numberOfDays % 7 + " дней.");
        System.out.println("В общем, до НГ осталось " + numberOfDays + " дней всего.");

        // теперь посчитаем фактическое количество недель и дней, которое необходимо отработать, если исключить выходные дни и праздники
        int numberOfHolidaysInPeriod = 0;
        for (LocalDate date : holidays) {
            if (DateChecker.isDataInPeriod(date, startLocalDate, newYearLocalDate))
                numberOfHolidaysInPeriod++;
        }
        int numberOfWeekendDaysInPeriod = getNumberOfWeekendDaysInPeriod(startLocalDate, newYearLocalDate);
        int numberOfWorkDays = numberOfDays - numberOfWeekendDaysInPeriod - numberOfHolidaysInPeriod;
        System.out.println("Количество рабочих дней до НГ составляет " + numberOfWorkDays);
        System.out.println("До НГ осталось отработать " + numberOfWorkDays/7 + " недель и " + numberOfWorkDays % 7 + " дней");
        LocalDate nextMondayAfterStartDate = getNextMonday(startLocalDate);
        LocalDate lastMondayBeforeEndDay = getLastMondayBeforeDate(newYearLocalDate);
        System.out.println("Количество полных рабочих недель составляет " + ChronoUnit.DAYS.between(nextMondayAfterStartDate, lastMondayBeforeEndDay)/7);
    }

    public static int getWorkHoursForCurrentDay(LocalDateTime localDateTime) {
        int currentHour = localDateTime.getHour();
        LocalDate localDate = localDateTime.toLocalDate();
        if (DateChecker.isHoliday(localDate) || DateChecker.isWeekendDay(localDate))
            return 0;
        if (DateChecker.isItBeforeHolidayWorkDay(localDate)) {
            if (DateChecker.isItHourBeforeDinnerOrDinnerHour(localDateTime))
                return 18 - currentHour - 1;
            else
                return 18 - currentHour;
        } else {
            if (DateChecker.isItHourBeforeDinnerOrDinnerHour(localDateTime))
                return 19 - currentHour - 1;
            else
                return 19 - currentHour;
        }
    }

    public static LocalDate getNextMonday(LocalDate localDate) {
        while (localDate.getDayOfWeek() != DayOfWeek.MONDAY)
            localDate = localDate.plusDays(1);
        return localDate;
    }

    public static LocalDate getLastMondayBeforeDate(LocalDate localDate) {
        while (localDate.getDayOfWeek() != DayOfWeek.MONDAY)
            localDate = localDate.minusDays(1);
        return localDate;
    }

    public static int getNumberOfWeekendDaysInPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDate nextMonday = getNextMonday(startDate);
        LocalDate lastMonday = getLastMondayBeforeDate(endDate);
        int numberOfWeekendDays = (int) (ChronoUnit.DAYS.between(nextMonday, lastMonday))/7 * 2;

        if (startDate.getDayOfWeek() != DayOfWeek.SUNDAY)
            numberOfWeekendDays += 2;
        if (startDate.getDayOfWeek() == DayOfWeek.SUNDAY)
            numberOfWeekendDays += 1;

        if (endDate.getDayOfWeek() == DayOfWeek.SUNDAY)
            numberOfWeekendDays += 1;
        return numberOfWeekendDays;
    }

    public static int getQuarter(LocalDate localDate) {
        int month = localDate.getMonthValue();
        if (month >= 1 && month <= 3)
            return 1;
        if (month > 3 && month <= 6)
            return 2;
        if (month > 6 && month <= 9)
            return 3;
        else
            return 4;
    }
}
