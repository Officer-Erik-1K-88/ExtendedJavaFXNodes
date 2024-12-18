package com.airent.extendedjavafxnodes.utils.math;

import ch.obermuhlner.math.big.BigDecimalMath;
import com.airent.extendedjavafxnodes.utils.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class NumberConvert {
    public static final MathContext context = new MathContext(2448);

    // Key Numeric values and their corresponding English words
    private static final List<Pair<String, BigDecimal>> numberValues;
    static {
        numberValues = new ArrayList<>();
        numberValues.add(new Pair<>("Million", new BigDecimal("1E6")));
        numberValues.add(new Pair<>("Thousand", new BigDecimal("1E3")));
        numberValues.add(new Pair<>("Hundred", new BigDecimal("100")));
        numberValues.add(new Pair<>("Ninety", new BigDecimal("90")));
        numberValues.add(new Pair<>("Eighty", new BigDecimal("80")));
        numberValues.add(new Pair<>("Seventy", new BigDecimal("70")));
        numberValues.add(new Pair<>("Sixty", new BigDecimal("60")));
        numberValues.add(new Pair<>("Fifty", new BigDecimal("50")));
        numberValues.add(new Pair<>("Forty", new BigDecimal("40")));
        numberValues.add(new Pair<>("Thirty", new BigDecimal("30")));
        numberValues.add(new Pair<>("Twenty", new BigDecimal("20")));
        numberValues.add(new Pair<>("Nineteen", new BigDecimal("19")));
        numberValues.add(new Pair<>("Eighteen", new BigDecimal("18")));
        numberValues.add(new Pair<>("Seventeen", new BigDecimal("17")));
        numberValues.add(new Pair<>("Sixteen", new BigDecimal("16")));
        numberValues.add(new Pair<>("Fifteen", new BigDecimal("15")));
        numberValues.add(new Pair<>("Fourteen", new BigDecimal("14")));
        numberValues.add(new Pair<>("Thirteen", new BigDecimal("13")));
        numberValues.add(new Pair<>("Twelve", new BigDecimal("12")));
        numberValues.add(new Pair<>("Eleven", new BigDecimal("11")));
        numberValues.add(new Pair<>("Ten", new BigDecimal("10")));
        numberValues.add(new Pair<>("Nine", new BigDecimal("9")));
        numberValues.add(new Pair<>("Eight", new BigDecimal("8")));
        numberValues.add(new Pair<>("Seven", new BigDecimal("7")));
        numberValues.add(new Pair<>("Six", new BigDecimal("6")));
        numberValues.add(new Pair<>("Five", new BigDecimal("5")));
        numberValues.add(new Pair<>("Four", new BigDecimal("4")));
        numberValues.add(new Pair<>("Three", new BigDecimal("3")));
        numberValues.add(new Pair<>("Two", new BigDecimal("2")));
        numberValues.add(new Pair<>("One", new BigDecimal("1")));
    }

    @NotNull
    private static String intListToString(@NotNull List<Integer[]> integers) {
        List<String> stag = new ArrayList<>();
        for (Integer[] uth : integers) {
            stag.add(Arrays.toString(uth));
        }
        return Arrays.toString(stag.toArray());
    }

    /**
     * The storage of numbers that have been constructed.
     */
    private static final HashMap<Long, String> foundNumbers = new HashMap<>();
    private static final HashMap<String, BigDecimal> foundExponents = new HashMap<>();
    static {
        foundNumbers.put(0L, "Thousand");
        foundNumbers.put(-1L, "Hundred");
        foundExponents.put("Thousand", new BigDecimal("1E3"));
        foundExponents.put("Hundred", new BigDecimal("1E2"));
    }

    private static final List<String> units = List.of(
            "",
            "Un",
            "Duo",
            "Tre",
            "Quattuor",
            "Quin",
            "Se",
            "Septe",
            "Octo",
            "Nove"
    );
    private static final List<String> tens = List.of(
            "",
            "Deci",
            "Viginti",
            "Triginta",
            "Quadraginta",
            "Quinquaginta",
            "Sexaginta",
            "Septuaginta",
            "Octoginta",
            "Nonaginta"
    );
    private static final List<String> hundreds = List.of(
            "",
            "Centi",
            "Ducenti",
            "Trecenti",
            "Quadringenti",
            "Quingenti",
            "Sescenti",
            "Septingenti",
            "Octingenti",
            "Nongenti"
    );

    /**
     * Constructs/finds the name of a number where the number
     * is expected to be of 1 with {@code exponent} of zeros following
     * where {@code exponent} is supposedly dividable by 3.
     *
     * @param exponent The exponent of the number to get the name of.
     * @return The constructed name of the number.
     */
    @NotNull
    private static String getNumbersName(long exponent) {
        long group = (exponent / 3) - 1;
        //System.out.println(group);

        if (foundNumbers.containsKey(group)) {
            return foundNumbers.get(group);
        }

        // Determine Latin prefixes for larger numbers

        List<String> s = List.of("Viginti", "Triginta", "Quadraginta", "Quinquaginta", "Trecenti", "Quadringenti", "Quingenti");
        List<String> n = List.of("Deci", "Triginta", "Quadraginta", "Quinquaginta", "Sexaginta", "Septuaginta", "Centi", "Ducenti", "Trecenti", "Quadringenti", "Quingenti", "Sescenti", "Septingenti");
        List<String> m = List.of("Viginti", "Octoginta", "Octingenti");
        List<String> x = List.of("Octoginta", "Centi", "Octingenti");

        StringBuilder name = new StringBuilder();

        long buildTop = group;

        int hundredIndex = 0; // Hundreds digit
        int tenIndex = 0; // Tens digit
        int unitIndex = 0; // Units digit

        List<Integer[]> indexes = new ArrayList<>();
        while (buildTop != 0L) {
            hundredIndex = 0;
            tenIndex = 0;
            if (buildTop < 10L) {
                unitIndex = (int) buildTop;
                buildTop = 0L;
            } else {
                unitIndex = (int) (buildTop / 10L);
                unitIndex = (int) (buildTop - (unitIndex * 10L));
                buildTop = buildTop / 10L;
                if (buildTop < 10L) {
                    tenIndex = (int) buildTop;
                    buildTop = 0L;
                } else {
                    tenIndex = (int) (buildTop / 10L);
                    tenIndex = (int) (buildTop - (tenIndex * 10L));
                    buildTop = buildTop / 10L;
                    if (buildTop < 10L) {
                        hundredIndex = (int) buildTop;
                        buildTop = 0L;
                    } else {
                        hundredIndex = (int) (buildTop / 10L);
                        hundredIndex = (int) (buildTop - (hundredIndex * 10L));
                        buildTop = buildTop / 10L;
                    }
                }
            }
            indexes.add(new Integer[]{unitIndex, tenIndex, hundredIndex});
        }

        //System.out.println(intListToString(indexes));

        int counter = 0;
        for (Integer[] uth : indexes.reversed()) {
            String unit = "";
            String ten = "";
            String hundred = "";
            if (uth[0] > 0) {
                unit = units.get(uth[0]);
            }
            if (uth[1] > 0) {
                ten = tens.get(uth[1]);
            }
            if (uth[2] > 0) {
                hundred = hundreds.get(uth[2]);
            }

            if (ten.isEmpty() && hundred.isEmpty()) {
                unit = switch (unit) {
                    case "Un" -> "Milli";
                    case "Duo" -> "Billi";
                    case "Quattuor" -> "Quadri";
                    case "Quin" -> "Quinti";
                    case "Se" -> "Sexti";
                    default -> unit;
                };
                appendToNumberName(name, unit);
            } else {
                String sx = (ten.isEmpty() ? hundred : ten);
                if (unit.equals("Tre") || unit.equals("Se")) {
                    if (s.contains(sx)) {
                        unit = unit + "s";
                    } else if (x.contains(sx)) {
                        if (unit.equals("Tre")) {
                            unit = unit + "s";
                        } else {
                            unit = unit + "x";
                        }
                    }
                }
                String mn = (ten.isEmpty() ? hundred : ten);
                if (unit.equals("Septe") || unit.equals("Nove")) {
                    if (m.contains(mn)) {
                        unit = unit + "m";
                    } else if (n.contains(mn)) {
                        unit = unit + "n";
                    }
                }

                appendToNumberName(name, unit);
                appendToNumberName(name, ten);
                appendToNumberName(name, hundred);
            }
            if (!name.isEmpty() && (counter >= 1 || !unit.isEmpty() || !ten.isEmpty() || !hundred.isEmpty())) {
                if (indexes.size() == 1) {
                    if (!name.toString().endsWith("illi")) {
                        appendToNumberName(name, "illi");
                    }
                } else {
                    if (checkATNNRem(name.toString(), true)) {
                        name.append("nilli");
                    } else {
                        name.append("illi");
                    }
                }
            } else {
                if (!name.isEmpty()) {
                    counter++;
                }
            }
        }

        // ending of number to word calculation (Is constant will all numbers)
        name.append("on");

        String finalName = name.toString().toLowerCase();
        finalName = finalName.substring(0, 1).toUpperCase()+finalName.substring(1);
        foundNumbers.put(group, finalName);
        foundExponents.put(finalName, new BigDecimal("1E"+exponent));
        return finalName;
    }

    private static void appendToNumberName(@NotNull StringBuilder name, String toAdd) {
        if (checkATNNRem(name.toString(), true) && checkATNNRem(toAdd, false)) {
            name.deleteCharAt(name.length()-1);
        }
        name.append(toAdd);
    }

    private static boolean checkATNNRem(@NotNull String value, boolean ending) {
        String[] toCheckFor = new String[] {
                "a", "i", "o", "u", "e"
        };
        String val = value.toLowerCase();
        for (String check : toCheckFor) {
            if (ending) {
                if (val.endsWith(check)) {
                    return true;
                }
            } else {
                if (val.startsWith(check)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Constructs/finds the name of a number using its exponent
     * rounded down to the nearest factor of 3.
     *
     * @param number The number to find the name of.
     * @return A {@link Pair} where the key is the number's name
     * and the value is the exponent of the number as a {@link BigDecimal}
     * formatted as follows: "1E"+exponent
     * @see #findNumberName(long)
     */
    @NotNull
    @Contract("_ -> new")
    public static Pair<String, BigDecimal> findNumberName(BigDecimal number) {
        int exponent = BigDecimalMath.exponent(number);
        if (exponent % 3 != 0) {
            if ((exponent-1) % 3 != 0) {
                if ((exponent-2) % 3 != 0) {
                    throw new RuntimeException("Unexpected outcome, couldn't find instance of 3 (going down) from "+exponent);
                } else {
                    exponent -= 2;
                }
            } else {
                exponent -= 1;
            }
        }
        BigDecimal val = new BigDecimal("1E"+exponent);
        return new Pair<>(findNumberName(exponent), val);
    }

    /**
     * Constructs/finds the name of a number where the number
     * is expected to be of 1 with {@code exponent} of zeros following
     * where {@code exponent} is required to be dividable by 3.
     *
     * @param exponent The exponent of the number to get the name of.
     * @return The constructed name of the number.
     * @throws RuntimeException If the {@code exponent} isn't dividable by 3,
     * or if the number's name couldn't be found or created.
     */
    @NotNull
    public static String findNumberName(long exponent) {
        if (exponent % 3 != 0) {
            throw new RuntimeException("The exponent provided isn't dividable by 3.");
        }
        String word = getNumbersName(exponent);
        if (word.isEmpty() || word.equals("On")) {
            throw new RuntimeException("Couldn't find number's name from exponent.");
        }
        return word;
    }

    @NotNull
    private static String findDecimalName(BigDecimal number, String placement) {
        for (Pair<String, BigDecimal> numberValue : numberValues) {
            if (number.compareTo(numberValue.getValue()) == 0) {
                if (placement != null) {
                    return placement+"-"+numberValue.getKey()+"th";
                }
                return numberValue.getKey()+"th";
            }
        }
        try {
            int exponent = BigDecimalMath.exponent(number);
            if (exponent % 3 != 0) {
                if ((exponent-1) % 3 != 0) {
                    if ((exponent-2) % 3 != 0) {
                        throw new RuntimeException("Unexpected outcome, couldn't find instance of 3 (going down) from "+exponent);
                    } else {
                        placement = "hundred";
                    }
                } else {
                    placement = "ten";
                }
            }
            String val = findNumberName(exponent);
            if (placement != null) {
                return placement+"-"+val+"th";
            }
            return val+"th";
        } catch (RuntimeException ignored) {}
        if (placement == null) {
            return findDecimalName(number.divide(BigDecimal.TEN, context), "ten");
        } else if (placement.equals("ten")) {
            return findDecimalName(number.divide(BigDecimal.TEN, context), "hundred");
        }
        return "";
    }

    // Method to convert number into words recursively
    private static String convertToWordsRec(@NotNull BigDecimal n, @NotNull List<Pair<String, BigDecimal>> values) {
        String res = "";

        // checks to see if n is less than the largest value in the key Numeric values
        // if n is less than then iterate over the key Numeric values
        // otherwise must have the Numeric value's word form constructed
        if (n.compareTo(values.getFirst().getValue()) < 0) {
            // Iterating over all key Numeric values
            for (int i = 0; i < values.size(); i++) {
                BigDecimal value = values.get(i).getValue();
                String word = values.get(i).getKey();

                // If the number is greater than or equal to the current numeric value
                if (n.compareTo(value) >= 0) {

                    // Append the quotient part
                    // If the number is greater than or equal to 100
                    // then only we need to handle that
                    if (n.compareTo(BigDecimal.valueOf(100)) >= 0)
                        res += convertToWordsRec(n.divide(value, context), values) + " ";

                    // Append the word for numeric value
                    res += word;

                    // Append the remainder part
                    if (n.remainder(value).compareTo(BigDecimal.ZERO) > 0)
                        res += " " + convertToWordsRec(n.remainder(value), values);

                    return res;
                }
            }
        } else {
            // build the Numeric value's word form
            Pair<String, BigDecimal> val = findNumberName(n);
            String word = val.getKey();
            BigDecimal value = val.getValue();

            // If the number is greater than or equal to the current numeric value
            if (n.compareTo(value) >= 0) {

                // Append the quotient part
                // If the number is greater than or equal to 100
                // then only we need to handle that
                //System.out.println(n);
                if (n.compareTo(BigDecimal.valueOf(100)) >= 0)
                    res += convertToWordsRec(n.divide(value, context), values) + " ";

                // Append the word for numeric value
                res += word;

                // Append the remainder part
                if (n.remainder(value).compareTo(BigDecimal.ZERO) > 0)
                    res += " " + convertToWordsRec(n.remainder(value), values);

                return res;
            }
        }


        return res;
    }

    private static String convertToWords(@NotNull BigDecimal n) {
        if (n.compareTo(BigDecimal.ZERO) == 0)
            return "Zero";

        boolean isNegative = n.abs().compareTo(n) != 0;
        n = n.abs();

        BigDecimal top = BigDecimalMath.integralPart(n);
        BigDecimal bottom = BigDecimalMath.fractionalPart(n);
        String ret;

        if (bottom.compareTo(BigDecimal.ZERO) == 0) {
            ret = convertToWordsRec(n, numberValues);
        } else {
            BigDecimal size = BigDecimal.ONE;

            while (BigDecimalMath.fractionalPart(bottom).compareTo(BigDecimal.ZERO) != 0) {
                bottom = bottom.multiply(BigDecimal.TEN);
                size = size.multiply(BigDecimal.TEN);
            }

            String end = findDecimalName(size, null);

            String firstPart = convertToWordsRec(top, numberValues);
            String secondPart = convertToWordsRec(bottom, numberValues);

            ret = firstPart + " and " + secondPart + " " + end;
        }
        if (isNegative) {
            ret = "Negative " + ret;
        }
        return ret;
    }

    /**
     * Converts a number to words.
     * <BR>
     * This method can handle any type of number value
     * and any object that can be converted into a
     * string version of a number by call from {@link String#valueOf(Object)}.
     *
     * @param number The object that represents a number.
     *               This value must-have a string value
     *               that can be parsed by {@link BigDecimal}.
     * @return The String of words that represent a number.
     */
    @NotNull
    public static String convertToWords(Object number) {
        String ret = convertToWords(convertToBig(number));
        ret = ret.replaceAll(" +", " ");
        return ret;
    }

    /**
     * Converts the given number to a numeric value
     * followed by a word that represents the number
     * of digit places to move the decimal place.
     *
     * @param number The object that represents a number.
     *               This value must-have a string value
     *               that can be parsed by {@link BigDecimal}.
     * @param placesBeforeWord The number of digits to leave in the
     *                         integral part of the numeric value part.
     *                         This value is set to 0 if it is less than
     *                         or equal to 1, and in all other cases,
     *                         this value is multiplied by 2 then
     *                         subtracted by 1.
     *                         This calculation is done
     *                         so that this value is correlated to
     *                         what the word is that proceeds the
     *                         numeric value.
     * @param roundTo The number of decimal places to round to.
     * @return The String representation of the given number.
     */
    @NotNull
    public static String convertPartialWord(Object number, int placesBeforeWord, int roundTo) {
        if (placesBeforeWord <= 1) {
            placesBeforeWord = 0;
        } else {
            placesBeforeWord *= 2;
            placesBeforeWord -= 1;
        }
        BigDecimal n = convertToBig(number);
        int exponent = BigDecimalMath.exponent(n);
        String end = "";
        if (placesBeforeWord < exponent) {
            Pair<String, BigDecimal> pair = findNumberName(new BigDecimal("1E"+(exponent-placesBeforeWord)));
            if (pair.getValue().compareTo(new BigDecimal("100")) > 0) {
                n = n.divide(pair.getValue(), context);
                end = " "+pair.getKey();
            }
        }
        if (roundTo < 0) {
            String string = n.toPlainString();
            if (string.contains(".")) {
                int endIndex = string.length();
                while (string.charAt(endIndex-1) == '0') {
                    endIndex--;
                }
                if (BigDecimalMath.fractionalPart(n).compareTo(BigDecimal.ZERO) == 0) {
                    int count = string.length()-endIndex;
                    string = string.substring(0, endIndex-1);
                    return string+"0".repeat(count);
                }
                return string.substring(0, endIndex)+end;
            }
            return string+end;
        }
        if (roundTo != Integer.MAX_VALUE) {
            roundTo += BigDecimalMath.integralPart(n).toPlainString().length();
        }
        return n.round(new MathContext(roundTo)).toPlainString()+end;
    }

    @NotNull
    public static String convertPartialWord(Object number, int placesBeforeWord) {
        return convertPartialWord(number, placesBeforeWord, -1);
    }

    @NotNull
    public static String convertPartialWord(Object number) {
        return convertPartialWord(number, 2);
    }

    /**
     * Converts a number in a String format
     * into a {@link BigDecimal}.
     *
     * @param numberWords The String formatted number to convert.
     * @return The number that was represented as a String.
     */
    public static BigDecimal convertToNumber(@NotNull String numberWords) {
        try {
            return new BigDecimal(numberWords);
        } catch (NumberFormatException ignored) {}

        String[] words = numberWords.split(" ");
        boolean isNegative = words[0].equals("Negative");

        if (!isNegative && words.length == 2) {
            try {
                BigDecimal numeric = new BigDecimal(words[0]);
                if (foundExponents.containsKey(words[1])) {
                    return numeric.multiply(foundExponents.get(words[1]));
                } else {
                    throw new RuntimeException("Cannot process the provided String to a number.");
                }
            } catch (NumberFormatException ignored) {}
        }

        List<Pair<Integer, String>> place = new ArrayList<>();
        List<Integer> added = new ArrayList<>();
        int startI = 0;
        if (isNegative) {
            place.add(new Pair<>(0, "-"));
            added.add(0);
            startI = 1;
        }
        boolean hasDecimal = false;
        String[] decimalWords = new String[0];
        for (Pair<String, BigDecimal> pair : numberValues) {
            for (int i=startI; i<words.length; i++) {
                String word = words[i];
                if (word.equals("and")) {
                    if (!hasDecimal) {
                        hasDecimal = true;
                        decimalWords = Arrays.copyOfRange(words, i+1, words.length-1);
                    }
                    break;
                }
                if (pair.getKey().equals(word)) {
                    place.add(new Pair<>(i, pair.getValue().toPlainString()));
                    added.add(i);
                }
            }
        }
        int integralPartLength = words.length;
        if (hasDecimal) {
            integralPartLength -= decimalWords.length+2;
        }
        for (int i=0; i<integralPartLength; i++) {
            String word = words[i];
            if (!added.contains(i)) {
                if (foundExponents.containsKey(word)) {
                    place.add(new Pair<>(i, foundExponents.get(word).toPlainString()));
                    added.add(i);
                }
            }
        }
        if (place.size() != integralPartLength) {
            throw new RuntimeException("Cannot process the provided String to a number.");
        }
        BigDecimal ret = new BigDecimal("0");
        place.sort(Map.Entry.comparingByKey());
        if (place.getFirst().getValue().equals("-")) {
            place.removeFirst();
        }
        BigDecimal prev = new BigDecimal("0");
        boolean isStart = true;
        for (Pair<Integer, String> pair : place) {
            BigDecimal value = new BigDecimal(pair.getValue());
            int exponent = BigDecimalMath.exponent(value);
            if (!isStart && exponent >= 2) {
                prev = prev.multiply(value);
                if (exponent >= 3) {
                    ret = ret.add(prev);
                    prev = new BigDecimal("0");
                    isStart = true;
                }
            } else {
                prev = prev.add(value);
                isStart = false;
            }
        }
        if (!isStart) {
            ret = ret.add(prev);
        }
        if (hasDecimal && decimalWords.length != 0) {
            BigDecimal decimal = convertToNumber(String.join(" ", decimalWords));
            String placement = words[words.length-1];
            String zero = "";
            placement = placement.substring(0, placement.length()-2);
            String[] sepered = placement.split("-");
            if (sepered.length == 2) {
                zero = sepered[0];
                placement = sepered[1];
            }
            BigDecimal bd;
            if (foundExponents.containsKey(placement) || placement.equals("Ten")) {
                if (placement.equals("Ten")) {
                    bd = new BigDecimal("10");
                } else {
                    bd = foundExponents.get(placement);
                }
                if (!zero.isEmpty()) {
                    if (zero.equals("ten")) {
                        bd = bd.multiply(BigDecimal.TEN);
                    } else if (zero.equals("hundred")) {
                        bd = bd.multiply(new BigDecimal("100"));
                    }
                }
            } else {
                throw new RuntimeException("Cannot find placement of decimal places.");
            }
            decimal = decimal.divide(bd, context);
            ret = ret.add(decimal);
        }
        if (isNegative) {
            ret = ret.negate();
        }
        return ret;
    }

    /**
     * This method converts an Object into a number.
     * The allowed values are primarily objects that
     * will provide a {@link Object#toString() String value}
     * that is in a valid format for {@link BigDecimal} to parse.
     * However, there are a few exceptions to this clause,
     * those exceptions are as follows:
     * <BR>
     * {@code null} - Returns zero.
     * It Can be a String literal (case-insensitive)
     * or actual null.
     * <HR>
     * {@code true} - Returns one.
     * It Can be a String literal (case-insensitive)
     * or the boolean value of true.
     * <HR>
     * {@code false} - Returns zero.
     * It Can be a String literal (case-insensitive)
     * or the boolean value of false.
     * <HR>
     * {@code CharSequence} - Firstly the CharSequence
     * is converted into a string and then is checked to
     * see if it can be converted to {@code BigDecimal} and if it can't
     * then the string's length and the unicode number of each char in the string
     * will be added together then divided by the string's length to get the return.
     * <HR>
     * {@code iterables, iterator, and arrays} - With these, we first convert all
     * stored objects into numbers using this method,
     * those numbers are then added up and divided by the length of the iterable,
     * iterator, or array.
     * However, if the value's length is zero, then the
     * object's hashcode will be returned.
     * <HR>
     * {@code maps} - If it is a map, then the same thing as
     * {@code iterables, iterator, and arrays} will occur,
     * but instead of just one value, it'll be doing two values.
     * Both the key and value are converted into their respective
     * number values; they are added together, then divided by two.
     * <HR><BR>
     * Any other values that don't meet any of the listed out criteria,
     * then that object's hashcode will be returned.
     *
     * @param number The object that represents a number
     *               or should be parsed into a number.
     * @return A {@link BigDecimal} number value of the provided Object.
     */
    @NotNull
    @Contract("null -> new")
    public static BigDecimal convertToBig(Object number) {
        if (number == null) {
            return BigDecimal.ZERO;
        }
        if (number instanceof Boolean bool) {
            if (bool) {
                return BigDecimal.ONE;
            }
            return BigDecimal.ZERO;
        }
        if (number instanceof Number num) {
            if (num instanceof BigDecimal) {
                return (BigDecimal) num;
            }
            return new BigDecimal(String.valueOf(num));
        }

        // String checking
        if (number instanceof String string) {
            if (string.equalsIgnoreCase("null") ||
                    string.equalsIgnoreCase("false")) {
                return BigDecimal.ZERO;
            } else if (string.equalsIgnoreCase("true")) {
                return BigDecimal.ONE;
            }
        }

        try {
            return new BigDecimal(String.valueOf(number));
        } catch (NumberFormatException e) {
            if (number instanceof CharSequence charSequence) {
                String s = charSequence.toString();
                BigDecimal bigDecimal = new BigDecimal(s.length());
                for (char c : s.toCharArray()) {
                    bigDecimal = bigDecimal.add(new BigDecimal(c));
                }
                bigDecimal = bigDecimal.divide(new BigDecimal(s.length()), context);
                return bigDecimal;
            }
        }

        // massive conversions
        if (number instanceof Object[] objects) {
            BigDecimal bigDecimal = new BigDecimal("0");
            for (Object o : objects) {
                bigDecimal = bigDecimal.add(convertToBig(o));
            }
            if (objects.length == 0) return new BigDecimal(number.hashCode());
            bigDecimal = bigDecimal.divide(new BigDecimal(objects.length), context);
            return bigDecimal;
        }
        if (number instanceof Iterable<?> iterable) {
            AtomicInteger atomicInteger = new AtomicInteger(0);
            AtomicReference<BigDecimal> bigDecimalAtomicReference = new AtomicReference<>(BigDecimal.ZERO);
            iterable.forEach(o -> {
                bigDecimalAtomicReference.set(bigDecimalAtomicReference.get().add(convertToBig(o)));
                atomicInteger.getAndIncrement();
            });
            if (atomicInteger.get() == 0) return new BigDecimal(number.hashCode());
            return bigDecimalAtomicReference.get().divide(convertToBig(atomicInteger.get()), context);
        }
        if (number instanceof Iterator<?> iterator) {
            BigDecimal bigDecimal = new BigDecimal("0");
            int length = 0;
            while (iterator.hasNext()) {
                Object o = iterator.next();
                bigDecimal = bigDecimal.add(convertToBig(o));
                length++;
            }
            if (length == 0) return new BigDecimal(number.hashCode());
            bigDecimal = bigDecimal.divide(new BigDecimal(length), context);
            return bigDecimal;
        }

        if (number instanceof Map.Entry<?,?> entry) {
            BigDecimal n1 = convertToBig(entry.getKey());
            BigDecimal n2 = convertToBig(entry.getValue());
            return n1.add(n2).divide(BigDecimal.TWO, context);
        }
        if (number instanceof Map<?,?> map) {
            AtomicReference<BigDecimal> bigDecimal = new AtomicReference<>(new BigDecimal("0"));
            map.forEach((key, value) -> {
                BigDecimal n1 = convertToBig(key);
                BigDecimal n2 = convertToBig(value);
                bigDecimal.set(bigDecimal.get().add(n1.add(n2).divide(BigDecimal.TWO, context)));
            });
            return bigDecimal.get().divide(new BigDecimal(map.size()), context);
        }

        return new BigDecimal(number.hashCode());
    }
}
