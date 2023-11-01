package vn.dataplatform.cdc.utils;

import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * @author tuan.nguyen3
 */
public final class TransformUtils {
    public TransformUtils() {

    }

    public static ArrayList<String> jsonStringToArray(String jsonString) throws JSONException {

        ArrayList<String> stringArray = new ArrayList<String>();

        JSONArray jsonArray = new JSONArray(jsonString);

        for (int i = 0; i < jsonArray.length(); i++) {
            Object valId = jsonArray.get(i);
            stringArray.add(valId.toString());
        }
        return stringArray;
    }

    public static Optional<Object> getValueFromJsonObject(Object json, String pathField) {
        if (json == null) {
            return Optional.empty();
        }
        try {
            if (json instanceof String) {
                return Optional.of(JsonPath.parse(json.toString()).read(pathField));
            }
            return Optional.of(JsonPath.parse(json).read(pathField));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<String> getRootField(String pathField) {
        if (pathField.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(pathField.split("\\.")[0]);
    }

    public static Optional<String> getPathChildField(String pathField) {
        if (pathField.isEmpty()) {
            return Optional.empty();
        }
        String[] fields = pathField.split("\\.");
        if (fields.length == 1) {
            return Optional.empty();
        }
        return Optional.of(String.format("$.%s", pathField.substring(fields[0].length() + 1)));
    }

    public static String getTypeOfValue(Object value) {
        if (value instanceof Map) {
            return "JSONObject";
        } else if (value instanceof List) {
            return "JSONArray";
        } else if (value instanceof String) {
            return "String";
        }
        return "nono";
    }

    public static List<String> getFields(String fields) {
        if (fields != null) {
            return Arrays.asList(fields.split(","));
        }
        return Collections.emptyList();
    }


    public static boolean isNestedField(String field) {
        if (field.isEmpty()) {
            return false;
        }
        return field.contains(".");
    }


    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

}
