package caseydlvr.recurringtasks.db;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

public class Converters {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : FORMATTER.parse(value, LocalDate.FROM);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate localDate) {
        return localDate == null ? null : localDate.format(FORMATTER);
    }
}
