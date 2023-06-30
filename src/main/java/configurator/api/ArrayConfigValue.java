package configurator.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.ArrayList;
import java.util.List;

public class ArrayConfigValue extends ConfigValue<JsonArray> {
    public ArrayConfigValue(String name, ConfigCategory parentCategory) {
        super(name, parentCategory);
    }

    public List<JsonElement> getJsonList() {
        List<JsonElement> list = new ArrayList<>();
        JsonArray array = get();
        if (array != null) {
            array.forEach(list::add);
        }
        return list;
    }

    public List<JsonObject> getJsonObjectList() {
        List<JsonObject> list = new ArrayList<>();
        JsonArray array = get();
        if (array != null) {
            for (JsonElement element : array) {
                if (element.isJsonObject()) {
                    list.add((JsonObject)element);
                }
            }
        }
        return list;
    }

    public <T extends Number> List<T> getNumberList() {
        List<T> list = new ArrayList<>();
        JsonArray array = get();
        if (array != null) {
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isNumber()) {
                        list.add((T)primitive.getAsNumber());
                    }
                }
            }
        }
        return list;
    }

    public List<String> getStringList() {
        List<String> list = new ArrayList<>();
        JsonArray array = get();
        if (array != null) {
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isString()) {
                        list.add(primitive.getAsString());
                    }
                }
            }
        }
        return list;
    }

    public List<Boolean> getBooleanList() {
        List<Boolean> list = new ArrayList<>();
        JsonArray array = get();
        if (array != null) {
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    JsonPrimitive primitive = (JsonPrimitive) element;
                    if (primitive.isBoolean()) {
                        list.add(primitive.getAsBoolean());
                    }
                }
            }
        }
        return list;
    }

    public static JsonArray convertListToJson(List<?> list) {
        JsonArray array = new JsonArray();
        for (Object val : list) {
            if (val instanceof Boolean) array.add((Boolean) val);
            else if (val instanceof String) array.add((String) val);
            else if (val instanceof Number) array.add((Number) val);
            else if (val instanceof Character) array.add((Character) val);
            else if (val instanceof JsonElement) array.add((JsonElement) val);
        }
        return array;
    }
}
