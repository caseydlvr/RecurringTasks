package caseydlvr.recurringtasks.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class LocalDateTypeAdapter implements JsonDeserializer {
    private static final DateTimeFormatter SERVER_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();

        return dateString == null ? null: LocalDate.parse(dateString, SERVER_FORMAT);
    }
}
