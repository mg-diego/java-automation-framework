package Helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonFileCustomHelper {

    public static String findPropertyInsideJson(String response, String keyName) throws Exception {
        try {
            JSONObject jsonResponse = Converter.fromStringToJson(response);
            var subNodes = keyName.split("\\.");
            var jsonNode = jsonResponse.get(subNodes.length > 1 ? subNodes[0] : keyName);

            var counter = 1;
            while (counter < subNodes.length) {
                jsonNode = jsonNode instanceof JSONArray
                        ? ((JSONArray) jsonNode).get(Integer.parseInt(subNodes[counter]))
                        : ((JSONObject) jsonNode).get(subNodes[counter]);
                counter++;
            }
            return jsonNode.toString();
        } catch (Exception e) {
            throw new Exception(String.format("An error occurred during analysis of the error response body, either response body is null or field is not existing inside the response. \n Endpoint response: \n%s", response));
        }
    }


    public static String replaceXthOccurrence(String input, String stringToReplace, String replacement, int x) {
        int index = -1;
        for (int i = 0; i < x; i++) {
            index = input.indexOf(stringToReplace, index + 1);
            if (index == -1) {
                break;
            }
        }
        if (index != -1) {
            return input.substring(0, index) + replacement + input.substring(index + stringToReplace.length());
        }
        return input;
    }

    public static String addJsonInsideJson(String json, String jsonToAdd, String position) {
        return replaceXthOccurrence(json, position, ',' + jsonToAdd + position, 1);
    }

    public static String findPropertyByAnotherPropertySameObject(String jsonString, String searchKey, String searchValue, String returnKey) {
        if (jsonString.trim().startsWith("[")) {
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject arrayObject = jsonArray.getJSONObject(i);
                String result = findInObject(arrayObject, searchKey, searchValue, returnKey);
                if (result != null) {
                    return result;
                }
            }
        } else {
            JSONObject jsonObject = new JSONObject(jsonString);
            return findInObject(jsonObject, searchKey, searchValue, returnKey);
        }
        return null;
    }

    private static String findInObject(JSONObject jsonObject, String searchKey, String searchValue, String returnKey) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            // Check if the value is a JSONArray
            if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject arrayObject = jsonArray.getJSONObject(i);
                    if (arrayObject.has(searchKey)) {
                        Object searchKeyValue = arrayObject.get(searchKey);
                        if (searchKeyValue != null && searchValue.equals(searchKeyValue.toString())) {
                            if (arrayObject.has(returnKey)) {
                                Object returnKeyValue = arrayObject.get(returnKey);
                                return returnKeyValue != null ? returnKeyValue.toString() : null;
                            }
                        }
                    }
                }
            } else if (value instanceof JSONObject) {
                JSONObject obj = (JSONObject) value;
                if (obj.has(searchKey)) {
                    Object searchKeyValue = obj.get(searchKey);
                    if (searchKeyValue != null && searchValue.equals(searchKeyValue.toString())) {
                        if (obj.has(returnKey)) {
                            Object returnKeyValue = obj.get(returnKey);
                            return returnKeyValue != null ? returnKeyValue.toString() : null;
                        }
                    }
                }
            } else {
                if (key.equals(searchKey)) {
                    if (value != null && searchValue.equals(value.toString())) {
                        return jsonObject.has(returnKey) ? jsonObject.get(returnKey).toString() : null;
                    }
                }
            }
        }
        return null;
    }

    public static Object searchJSONObject(JSONObject jsonObject, String keyToSearch) {
        return traverseJSONObject(jsonObject, keyToSearch, Operation.SEARCH, null);
    }

    public static void updateJSONObject(JSONObject jsonObject, String keyToSearch, String newValue) {
        traverseJSONObject(jsonObject, keyToSearch, Operation.UPDATE, newValue);
    }

    public static void removeJSONObject(JSONObject jsonObject, String keyToSearch) {
        traverseJSONObject(jsonObject, keyToSearch, Operation.REMOVE, null);
    }

    private static Object traverseJSONObject(JSONObject jsonObject, String keyToSearch, Operation operation, String newValue) {
        if (jsonObject.has(keyToSearch)) {
            switch (operation) {
                case SEARCH:
                    return jsonObject.get(keyToSearch);
                case UPDATE:
                    updateValue(jsonObject, keyToSearch, newValue);
                    return null;
                case REMOVE:
                    jsonObject.remove(keyToSearch);
                    return null;
            }
        }

        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                Object result = traverseJSONObject((JSONObject) value, keyToSearch, operation, newValue);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                for (Object arrayItem : array) {
                    if (arrayItem instanceof JSONObject) {
                        Object result = traverseJSONObject((JSONObject) arrayItem, keyToSearch, operation, newValue);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static void updateValue(JSONObject jsonObject, String keyToSearch, String newValue) {
        if ((newValue.contains("true") || newValue.contains("false")) && (!newValue.startsWith("[") || !newValue.startsWith("{"))) {
            jsonObject.put(keyToSearch, Boolean.parseBoolean(newValue));
        } else if (!newValue.startsWith("[") && !newValue.startsWith("{")) {
            jsonObject.put(keyToSearch, newValue);
        } else {
            try {
                JSONTokener tokener = new JSONTokener(newValue);
                Object value;
                if (newValue.startsWith("[")) {
                    value = new JSONArray(tokener);
                } else {
                    value = new JSONObject(tokener);
                }
                jsonObject.put(keyToSearch, value);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private enum Operation {
        SEARCH,
        UPDATE,
        REMOVE
    }

}
