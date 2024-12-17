package com.airent.extendedjavafxnodes.utils.json;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class JSON implements ToJSON, Serializable {

    /**
     * Validates that an Object is converted into a JSON object.
     * The Object may not be changed when ran, this is due
     * to the Object being able to be used as is, or
     * because this method cannot parse the Object.
     *
     * @param o The Object to be parsed.
     * @return The parsed Object if parsable, otherwise will return the
     * provided Object.
     */
    public static Object validateToJSON(Object o) {
        if (o instanceof Map<?, ?> map) {
            JSONObject jsonObject = new JSONObject();
            map.forEach((key, value) -> jsonObject.put(String.valueOf(key), validateToJSON(value)));
            return jsonObject;
        } else if (o instanceof Map.Entry<?,?> entry) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(String.valueOf(entry.getKey()), validateToJSON(entry.getValue()));
            return jsonObject;
        } else if (o instanceof Collection<?> collection) {
            JSONArray jsonArray = new JSONArray();
            collection.forEach(object -> jsonArray.put(validateToJSON(object)));
            return jsonArray;
        } else if (o instanceof Iterable<?> iterable) {
            JSONArray jsonArray = new JSONArray();
            iterable.forEach(object -> jsonArray.put(validateToJSON(object)));
            return jsonArray;
        }  else if (o instanceof Iterator<?> iterator) {
            JSONArray jsonArray = new JSONArray();
            iterator.forEachRemaining(object -> jsonArray.put(validateToJSON(object)));
            return jsonArray;
        } else if (o instanceof AutoJSON autoJSON) {
            JSONObject jsonObject = autoJSON.identifierJSON();

            Method[] methods = autoJSON.getClass().getMethods();

            for (Method method : methods) {
                String name = method.getName();
                boolean isGetter = false;
                if (name.startsWith("get")) {
                    name = name.substring(3);
                    isGetter = true;
                } else if (name.startsWith("is")) {
                    name = name.substring(2);
                    isGetter = true;
                }
                if (isGetter) {
                    try {
                        jsonObject.put(name, validateToJSON(method.invoke(autoJSON)));
                    } catch (InvocationTargetException | IllegalAccessException ignored) {
                    }
                }
            }

            return jsonObject;
        } else if (o instanceof ToJSON toJSON) {
            return toJSON.toJSON();
        } else if (o instanceof CharSequence charSequence) {
            try {
                return new JSONObject(charSequence.toString());
            } catch (JSONException ignored) {}
        }
        try {
            Method toJSON = o.getClass().getMethod("toJSON");
            return toJSON.invoke(o);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return o;
        }
    }

    /**
     * Used for data reading with {@code readObject}
     * on serialization action.
     *
     * @param jsonObject The JSON data that makes up the
     *                   class that implements this abstract class.
     */
    protected abstract void serialSync(JSONObject jsonObject);

    @java.io.Serial
    private static final long serialVersionUID = 86433546566887432L;
    @java.io.Serial
    private void writeObject(@NotNull java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden stuff
        s.defaultWriteObject();
        s.writeObject(toJSON().toString());
    }

    @java.io.Serial
    private void readObject(@NotNull java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden stuff
        s.defaultReadObject();
        Object validated = validateToJSON(s.readObject());
        if (validated instanceof JSONObject jsonObject) {
            serialSync(jsonObject);
        }
    }
}
