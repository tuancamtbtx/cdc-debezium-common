package vn.dataplatform.core.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author tuan.nguyen3
 */
public class JsonPathUtils {
  public static Gson gson = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

  public static GsonJsonProvider gsonJsonProvider = new GsonJsonProvider(gson);

  public static final Configuration jConf =
      Configuration.builder().jsonProvider(gsonJsonProvider).options(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS).build();

  public static final Configuration jPathConf = Configuration.builder().jsonProvider(gsonJsonProvider)
      .options(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS, Option.AS_PATH_LIST, Option.ALWAYS_RETURN_LIST).build();

  public static Map<String, String> pathToValueMap(String json, List<String> paths) {
    if (json == null || json.trim().isEmpty() || paths.isEmpty()) {
      return Collections.emptyMap();
    }

    DocumentContext jVals = JsonPath.using(jConf).parse(json);
    DocumentContext jPaths = JsonPath.using(jPathConf).parse(json);

    return new HashSet<>(paths).stream() // Make sure that keys are unique
        .flatMap(p -> StreamSupport.stream(jPaths.read(p, JsonArray.class).spliterator(), false).map(JsonElement::getAsString))
        .collect(Collectors.toMap(p -> p, p -> {
          JsonElement e = jVals.read(p, JsonElement.class);
          if (e.isJsonArray()) {
            return gson.toJson(e.getAsJsonArray());
          }
          return e.isJsonNull() ? "" : e.getAsString();
        })).entrySet().stream().filter(kv -> !kv.getValue().trim().isEmpty()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public static <T> String setJsonValues(String json, Map<String, T> updatedPaths) {
    if (json == null || updatedPaths == null || json.trim().isEmpty() || updatedPaths.isEmpty()) {
      return json;
    }

    DocumentContext doc = JsonPath.using(jConf).parse(json);
    updatedPaths.forEach((path, newVal) -> doc.map(path, (val, conf) -> newVal));
    return doc.jsonString();
  }
}
