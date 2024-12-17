package com.airent.extendedjavafxnodes.utils;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.airent.extendedjavafxnodes.utils.math.NumberConvert;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Convert {
    /**
     * Converts an Object to a different Object based on a given function
     * (Declared as the {@code type} parameter).
     * <br>
     * Any Object that is a CharSequence will be converted into a String.
     * <br>
     * <h2>Types</h2>
     * Some types may have settings.
     * Settings can always be omitted.
     * <br>
     * To add settings to a type, the given type string must be declared
     * as function name, followed by {@code (} (left parentheses),
     * then comma separated values (The settings), and final it must end with
     * {@code )} (right parentheses).
     * <br>
     * The number of settings for each type may differ,
     * you do not need to declare all settings for the type, any setting
     * not declared will have a default value.
     * <br><br>
     * Entry Title Format: type (Object/data types that can use this type)
     * <br>
     * The types are as follows:
     * <br>
     * <h4>capitalize (String)</h4>
     * Given that the object is a String,
     * the returned String will have all its letters capitalized.
     * <br>
     * However, this type has two settings, the first setting is
     * the start index of letters to capitalize (inclusive),
     * and the second setting is the end index of letters to
     * capitalize (exclusive).
     * <hr>
     * <h4>firstCapital (String)</h4>
     * Capitalizes the first letter in a String.
     * <hr>
     * <h4>title (String)</h4>
     * Gives title casing to the provided string,
     * this type has one setting; this setting is the
     * style rule of the casing.
     * <br>
     * Refer to {@link StringConvert#toTitleCase(String, String)}
     * for more details of how title casing works.
     * <hr>
     * <h4>lowerCase (String)</h4>
     * This is exactly the same as capitalize,
     * but instead of making it upper-casing, it is put into lower casing.
     * <hr>
     * <h4>string (Any)</h4>
     * Converts the object to a string,
     * this type has three settings, the first one is a boolean
     * that refers whether to do a partial conversion (if possible).
     * <br>
     * The second one is the integer value that refers to a placement
     * of the value, the placement is the number of times to repeat
     * the converted String, however, if the value being converted
     * is a number and is set to a partial conversion, then
     * the placement is reference to the number of places to move
     * the end word by.
     * <br>
     * The third one is mainly for a partial numeric conversion,
     * this is used to round the number, if this is set to -1 then
     * no rounding is done, however, if this value is less than
     * -1, then the conversion is just a call of {@link String#valueOf(Object)}.
     * <br>
     * For more information on this type, refer to {@link #toString(Object, boolean, int, int)}.
     * <hr>
     * <h4>big (Any)</h4>
     * Converts an object into a numeric value.
     * <br>
     * Refer to {@link NumberConvert#convertToBig(Object)} for more
     * information on how this type works.
     * <hr>
     * <h4>random (String, List, Number)</h4>
     * If the provided value is a number,
     * then this type generates a random number where
     * the provided number is the maximum number that can be
     * generated (exclusive).
     * <br>
     * If the provided value is a string, then the string will be
     * randomly jumbled up.
     * <br>
     * If the provided value is a list, then the list will be
     * randomly jumbled up, just like if it was a string.
     * However, the list provided will be left unchanged
     * and a new {@link ArrayList} is created and is where the
     * objects are placed while being jumbled up.
     * <hr>
     * <h4>round (Number)</h4>
     * Rounds a number to the nearest whole
     * number, however, if provided a setting, then the setting is
     * the number of decimal places to leave in.
     * <hr>
     * <h4>What if the value isn't stated?</h4>
     * Then it isn't used and falls to no change,
     * or the value is deemed as a call to a Map, List, or JSON objects.
     *
     * @param type The functional conversion declaration to apply to object.
     * @param object The object to convert.
     * @return The Object that object was converted to.
     */
    public static Object convert(@NotNull String type, Object object) {
        Objects.requireNonNull(type);
        Random random = new Random();
        Object content = object;
        String[] typeParameters = new String[0];
        if (type.contains("(") && type.endsWith(")")) {
            String[] parts = type.split("\\(");
            type = parts[0];
            typeParameters = parts[1].substring(0, parts[1].length()-1).replaceAll(" +", "").split(",");
        }
        if (!(content instanceof String) && content instanceof CharSequence charSequence) {
            content = charSequence.toString();
        }
        int startIndex = 0;
        int endIndex = -1;
        if (type.equals("capitalize") || type.equals("lowerCase")) {
            if (typeParameters.length != 0) {
                try {
                    startIndex = Integer.parseInt(typeParameters[0]);
                    endIndex = Integer.parseInt(typeParameters[1]);
                } catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
            }
        }
        switch (type) {
            case "capitalize" -> {
                if (content instanceof String string) {
                    content = StringConvert.capitalize(string, startIndex, endIndex);
                }
            }
            case "firstCapital" -> {
                if (content instanceof String string) {
                    content = string.substring(0, 1).toUpperCase()+
                            string.substring(1);
                }
            }
            case "title" -> {
                String style;
                if (typeParameters.length != 0) {
                    style = typeParameters[0];
                } else {
                    style = "all";
                }
                if (content instanceof String string) {
                    content = StringConvert.toTitleCase(string, style);
                }
            }
            case "lowerCase" -> {
                if (content instanceof String string) {
                    content = StringConvert.lowerCase(string, startIndex, endIndex);
                }
            }
            case "string" -> {
                boolean partial = false;
                int place = 0;
                int roundTo = -1;
                if (typeParameters.length != 0) {
                    if (Character.isDigit(typeParameters[0].charAt(0))) {
                        try {
                            place = Integer.parseInt(typeParameters[0]);
                            roundTo = Integer.parseInt(typeParameters[1]);
                        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
                    } else {
                        partial = Boolean.getBoolean(typeParameters[0]);
                        try {
                            place = Integer.parseInt(typeParameters[1]);
                            roundTo = Integer.parseInt(typeParameters[2]);
                        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {}
                    }
                }
                content = toString(content, partial, place, roundTo);
            }
            case "big" -> {
                content = NumberConvert.convertToBig(content);
            }
            case "random" -> {
                if (content instanceof String string) {
                    try {
                        content = convert("random", new BigDecimal(string));
                    } catch (NumberFormatException e) {
                        StringBuilder sb = new StringBuilder();
                        List<Integer> used = new ArrayList<>();
                        while (used.size() != string.length()) {
                            int index = random.nextInt(string.length());
                            if (used.contains(index)) {
                                continue;
                            }
                            used.add(index);
                            sb.append(string.charAt(index));
                        }
                        content = sb.toString();
                    }
                } else if (content instanceof List<?> list) {
                    List<Object> arrayList = new ArrayList<>();
                    List<Integer> used = new ArrayList<>();
                    while (used.size() != list.size()) {
                        int index = random.nextInt(list.size());
                        if (used.contains(index)) {
                            continue;
                        }
                        used.add(index);
                        arrayList.add(list.get(index));
                    }
                    content = arrayList;
                } else if (content instanceof Number number) {
                    switch (number) {
                        case Long l -> content = random.nextLong(l);
                        case Double d -> content = random.nextDouble(d);
                        case Integer i -> content = random.nextInt(i);
                        case Float f -> content = random.nextFloat(f);
                        case Short s -> content = Short.parseShort(String.valueOf(random.nextInt(s)));
                        case BigDecimal bigDecimal -> {
                            if (BigDecimalMath.isIntValue(bigDecimal)) {
                                content = convert("random", bigDecimal.intValueExact());
                            } else if (BigDecimalMath.isLongValue(bigDecimal)) {
                                content = convert("random", bigDecimal.longValueExact());
                            } else if (BigDecimalMath.isDoubleValue(bigDecimal)) {
                                content = convert("random", bigDecimal.doubleValue());
                            } else {
                                BigDecimal randomNumber = new BigDecimal("1");
                                double nextDoubleMax = Double.MAX_VALUE;
                                int castTime = Math.abs(bigDecimal.divide(BigDecimal.valueOf(nextDoubleMax), NumberConvert.context).intValue());
                                if (castTime < 10) {
                                    castTime = 10;
                                }
                                for (int i = 1; i <= castTime; i++) {
                                    randomNumber = randomNumber.multiply(BigDecimal.valueOf(random.nextDouble(nextDoubleMax)));
                                    while (randomNumber.compareTo(bigDecimal) >= 0) {
                                        randomNumber = randomNumber.subtract(bigDecimal);
                                    }
                                }
                                content = randomNumber;
                            }
                            if (!(content instanceof BigDecimal)) {
                                content = new BigDecimal(String.valueOf(content));
                            }
                        }
                        default -> {
                            content = convert("random", new BigDecimal(String.valueOf(number)));
                        }
                    }
                }
            }
            case "round" -> {
                int roundTo = 0;
                if (typeParameters.length != 0) {
                    try {
                        roundTo = Integer.parseInt(typeParameters[0]);
                    } catch (NumberFormatException ignored) {}
                }
                if (content instanceof Number) {
                    BigDecimal bigDecimal;
                    if (content instanceof BigDecimal) {
                        bigDecimal = (BigDecimal) content;
                    } else {
                        bigDecimal = new BigDecimal(String.valueOf(content));
                    }
                    bigDecimal = BigDecimalMath.round(bigDecimal, new MathContext(bigDecimal.toPlainString().split("\\.")[0].length()+roundTo));
                    content = bigDecimal;
                }
            }
            default -> {
                if (content instanceof Map<?, ?> map) {
                    if (!map.isEmpty()) {
                        if (map.keySet().iterator().next() instanceof String) {
                            content = map.get(type);
                        }
                    }
                } else if (content instanceof List<?> list) {
                    content = list.get(Integer.parseInt(type));
                } else if (content instanceof JSONObject jsonObject) {
                    if (!jsonObject.isEmpty()) {
                        content = jsonObject.get(type);
                    }
                } else if (content instanceof JSONArray jsonArray) {
                    content = jsonArray.get(Integer.parseInt(type));
                }
            }
        }
        return content;
    }

    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     * <br><br>
     * Partial factor is false.
     * <br>
     * Placement is negated.
     * <br>
     * Rounding is off.
     *
     * @param content The Object to convert into a string.
     * @return The String value that was constructed.
     * @see #toString(Object, boolean, int, int)
     */
    public static String toString(Object content) {
        return toString(content, 0);
    }
    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     * <br><br>
     * Placement is negated.
     * <br>
     * Rounding is off.
     *
     * @param content The Object to convert into a string.
     * @return The String value that was constructed.
     * @see #toString(Object, boolean, int, int)
     */
    public static String toString(Object content, boolean partial) {
        return toString(content, partial, 0, -1);
    }
    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     * <br><br>
     * Partial factor is false.
     * <br>
     * Rounding is off.
     *
     * @param content The Object to convert into a string.
     * @return The String value that was constructed.
     * @see #toString(Object, boolean, int, int)
     */
    public static String toString(Object content, int place) {
        return toString(content, false, place, -1);
    }
    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     * <br><br>
     * Rounding is off.
     *
     * @param content The Object to convert into a string.
     * @return The String value that was constructed.
     * @see #toString(Object, boolean, int, int)
     */
    public static String toString(Object content, boolean partial, int place) {
        return toString(content, partial, place, -1);
    }

    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     * <br><br>
     * Partial factor is false.
     *
     * @param content The Object to convert into a string.
     * @return The String value that was constructed.
     * @see #toString(Object, boolean, int, int)
     */
    public static String toString(Object content, int place, int roundTo) {
        return toString(content, false, place, roundTo);
    }

    /**
     * Converts an Object to a String value based on three settings
     * that are optimized for numerical conversion factors.
     *
     * @param content The Object to convert into a string.
     * @param partial Whether to do a partial conversion to string,
     *                this boolean value only applies to the following
     *                types: numerical.
     *                <br>
     *                For numerical values, if this is {@code true}
     *                then part of the return will remain numerical.
     *                Otherwise, the numerical value will be fully turned
     *                into a wordy version of the number.
     * @param place The amount of time to repeat the converted String,
     *              or is {@code placesBeforeWord} for {@link NumberConvert#convertPartialWord(Object, int, int)}
     *              given partial is true and content is numerical.
     * @param roundTo If this value is less than -1, then will just call {@link String#valueOf(Object)}
     *                on content.
     *                However, similar to place, if partial is true and content is numerical,
     *                then this value is used to round the numeric value to that many decimal places,
     *                set to -1 for no rounding.
     * @return The String value that was constructed.
     */
    public static String toString(Object content, boolean partial, int place, int roundTo) {
        if (roundTo < -1) {
            content = String.valueOf(content);
        } else {
            if (content == null) {
                content = "null";
            } else {
                try {
                    if (partial) {
                        return NumberConvert.convertPartialWord(content, place, roundTo);
                    } else {
                        content = NumberConvert.convertToWords(content);
                    }
                } catch (NumberFormatException e) {
                    content = String.valueOf(content);
                }
            }
        }
        if (content instanceof String string) {
            if (place != 0) {
                content = string.repeat(place);
            }
        }
        return (String) content;
    }

    public static Boolean toBoolean(Object bool) {
        if (bool instanceof Boolean) {
            return (Boolean) bool;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(bool.toString());
            return bigDecimal.compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException ignored) {}
        return false;
    }
}
