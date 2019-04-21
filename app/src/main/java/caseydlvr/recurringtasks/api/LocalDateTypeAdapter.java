package caseydlvr.recurringtasks.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.lang.reflect.Type;

public class LocalDateTypeAdapter implements JsonDeserializer, JsonSerializer {
    private static final DateTimeFormatter RX_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final DateTimeFormatter TX_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String dateString = json.getAsString();

        return dateString == null ? null: LocalDate.parse(dateString, RX_FORMAT);
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        LocalDate localDate = (LocalDate) src;
        String dateString = localDate == null ? "" : localDate.format(TX_FORMAT);

        return new JsonPrimitive(dateString);
    }
}
