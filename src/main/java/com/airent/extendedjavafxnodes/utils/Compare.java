package com.airent.extendedjavafxnodes.utils;

import com.airent.extendedjavafxnodes.utils.math.NumberConvert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Compare implements Comparator<Object> {
    /**
     * The default construct of {@code Compare}.
     */
    public static final Compare natural = new Compare(true, 2000000);
    private final boolean arrayAllowRiving;
    /**
     * The default, predefined maximum loop times allowed.
     */
    private final int loopCap;
    public Compare(boolean arrayAllowRiving, int loopCap) {
        this.arrayAllowRiving = arrayAllowRiving;
        this.loopCap = loopCap;
    }

    /**
     * Compares its two arguments for order.
     * Returns a negative integer, zero,
     * or a positive integer as the first argument
     * is less than, equal to, or greater than the second.
     * <br>
     * The two objects don't need to be of the same type for any of the conversion
     * methods to work, only one of them needs to be of the appropriate class.
     * <br><br>
     * If one of the values is null, then the null is considered the smallest.
     * <hr>
     * When comparing booleans, then the {@link #illogicalBoolCompare(Object, Object)}
     * is used to compare the objects.
     * <hr>
     * When comparing CharSequences then the {@link #charCompare(Object, Object)}
     * is used to compare the objects.
     * <hr>
     * When comparing Map.Entry's then the keys are compared
     * and values are compared separately.
     * This in turn makes the comparing to prioritize the comparison
     * of the keys.
     * This means that if the key's comparison gives a
     * one, then a one is returned, if the keys would return zero,
     * then the comparison of values is returned.
     * And if the keys
     * would return negative one, then negative one is returned
     * unless values would return one, then one shall be returned.
     * <hr>
     * When comparing arrays, iterators, and iterables, then it'll use
     * {@link #arrayCompare(Object, Object)}.
     * <hr>
     * Anything that doesn't have a specific compare function
     * will be compared with {@link #numberCompare(Object, Object)}.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater
     * than the second.
     */
    @Override
    public int compare(Object o1, Object o2) {
        if (equals(o1, o2)) return 0;
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1 instanceof Boolean || o2 instanceof Boolean) {
            return illogicalBoolCompare(o1, o2);
        }
        if (o1 instanceof CharSequence || o2 instanceof CharSequence) {
            return charCompare(o1, o2);
        }
        if (o1 instanceof Map.Entry<?,?> || o2 instanceof Map.Entry<?,?>) {
            int c1;
            int c2;
            if (o1 instanceof Map.Entry<?,?> entry) {
                if (o2 instanceof Map.Entry<?,?> entry1) {
                    c1 = compare(entry.getKey(), entry1.getKey());
                    c2 = compare(entry.getValue(), entry1.getValue());
                } else {
                    c1 = compare(entry.getKey(), o2);
                    c2 = compare(entry.getValue(), o2);
                }
            } else {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o2;
                c1 = compare(o1, entry.getKey());
                c2 = compare(o1, entry.getValue());
            }
            if (c1 > 0) {
                return 1;
            } else if (c1 == 0) {
                return c2;
            } else {
                if (c2 > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
        if (isArray(o1) || isArray(o2)) {
            return arrayCompare(o1, o2);
        }
        return numberCompare(o1, o2);
    }

    /**
     * Compares two objects after they both have been converted
     * into boolean values with {@link Convert#toBoolean(Object)}.
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @return zero if the first object represents the same boolean
     * value as the second object;
     * a positive value if the first object represents true and the
     * second represents false;
     * and a negative value if the first object represents false and
     * the second represents true
     */
    public int boolCompare(Object o1, Object o2) {
        Boolean bool = Convert.toBoolean(o1);
        Boolean bool2 = Convert.toBoolean(o2);
        return bool.compareTo(bool2);
    }

    /**
     * Compares two objects after they both have been converted
     * into boolean values with {@link Convert#toBoolean(Object)}.
     * <br>
     * This method may seem similar to {@link #boolCompare(Object, Object)},
     * however, this method is illogical and doesn't give zero when both
     * boolean values are truly the same
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @return If both booleans are true, then one is returned
     * if both booleans are false then negative one is returned;
     * otherwise zero is returned.
     */
    public int illogicalBoolCompare(Object o1, Object o2) {
        boolean bool = Convert.toBoolean(o1);
        boolean bool2 = Convert.toBoolean(o2);
        if (bool2 && bool) {
            return 1;
        } else if (!bool2 && !bool) {
            return -1;
        }
        return 0;
    }

    /**
     * Compares two objects after they both have been converted
     * into {@link BigDecimal} values with {@link NumberConvert#convertToBig(Object)}.
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @return a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater
     * than the second.
     */
    public int numberCompare(Object o1, Object o2) {
        BigDecimal bigDecimal = NumberConvert.convertToBig(o1);
        BigDecimal bigDecimal2 = NumberConvert.convertToBig(o2);
        return bigDecimal.compareTo(bigDecimal2);
    }

    /**
     * Compares two objects after they both have been converted
     *  into {@link String} values with {@link String#valueOf(Object)}.
     *  And are therefor converted with {@link String#compareTo(String)}.
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @return Value 0 if the argument string is equal to this string;
     * a value less than 0 if this string is lexicographically
     * less than the string argument;
     * and a value greater than 0 if this string is lexicographically
     * greater than the string argument.
     */
    public int charCompare(Object o1, Object o2) {
        String s = String.valueOf(o1);
        String s2 = String.valueOf(o2);
        return s.compareTo(s2);
    }

    /**
     * Converts two objects into lists and compares the lists.
     * <br>
     * This method is meant mainly for comparing of
     * arrays, iterables, iterators, and maps.
     * With maps, it is its entrySet that will be used.
     * <br><br>
     * Firstly, this method converts the two objects into
     * lists if possible, otherwise, will put
     * the object into a list by its self.
     * After that, this method will then loop threw all the
     * items in the list made from the first object.
     * And for each of those items, it'll loop through all the
     * items from the list made from the second object comparing
     * each item using {@link #compare(Object, Object)}.
     * <br>
     * Meaning that for each item of the first list,
     * all the items of the second list will be compared to that
     * one item.
     * <br><br>
     * Due to this method looping the second list per item of the
     * first list, there is a loop cap. The loop cap limits the
     * looping to {@link #loopCap} loops.
     * <br>
     * If the looping exceeds the loop cap, then the looping of
     * the second list ends, only comparing the first item of
     * the second list to the remaining items of the first list
     * until either the first list ends or twice the loop cap
     * is exceeded.
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @return a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater
     * than the second.
     */
    public int arrayCompare(Object o1, Object o2) {
        return arrayCompare(o1, o2, loopCap);
    }

    /**
     * Converts two objects into lists and compares the lists.
     * <br>
     * Refer to {@link #arrayCompare(Object, Object)} for more
     * information on how arrays, iterables, iterators, and maps
     * are compared.
     *
     * @param o1 The first Object to convert and compare.
     * @param o2 The second Object to convert and compare.
     * @param loopCap The amount of times the second list is allowed to loop.
     *                This value times two is the number of times the
     *                first list is allowed to loop.
     * @return a negative integer, zero, or a positive integer
     * as the first argument is less than, equal to, or greater
     * than the second.
     * @see #arrayCompare(Object, Object)
     */
    public int arrayCompare(Object o1, Object o2, int loopCap) {
        return arrayCompare(o1, o2, loopCap, !arrayAllowRiving);
    }

    private int arrayCompare(Object o1, Object o2, int loopCap, boolean reversed) {
        List<?> list1 = convertToList(o1);
        List<?> list2 = convertToList(o2);
        if (equals(list1, list2)) return 0;

        int compare = 0;
        int belowZero = 0;
        int aboveZero = 0;
        int looped = 0;
        for (Object o : list1) {
            int compared = 0;
            if (looped < loopCap) {
                for (Object object : list2) {
                    compared = compare(o, object);
                    looped++;
                    if (looped >= loopCap) break;
                }
            } else {
                compared = compare(o, list2.getFirst());
                looped++;
            }
            compare += compared;
            if (compared < 0) {
                belowZero += 1;
            } else if (compared > 0) {
                aboveZero += 1;
            }
            if (looped >= (loopCap*2)) break;
        }
        if (belowZero < aboveZero) {
            return 1;
        } else if (belowZero > aboveZero) {
            return -1;
        }
        if (compare > 0) {
            return 1;
        } else if (compare < 0) {
            return -1;
        }
        compare = Integer.compare(list1.size(), list2.size());
        if (compare == 0) {
            if (looped >= loopCap) {
                if (reversed) return 0;
                compare = arrayCompare(o2, o1, loopCap, true);
                if (compare > 0) {
                    return -1;
                } else if (compare < 0) {
                    return 1;
                }
            }
        }
        return compare;
    }

    private List<?> convertToList(Object o) {
        if (o instanceof List<?>) {
            return (List<?>) o;
        }
        if (o instanceof Map<?,?> map) o = map.entrySet();
        if (o instanceof Iterable<?> iterable) {
            ArrayList<Object> arrayList = new ArrayList<>();
            iterable.forEach(arrayList::add);
            return arrayList;
        }
        if (o instanceof Iterator<?> iterator) {
            ArrayList<Object> arrayList = new ArrayList<>();
            while (iterator.hasNext()) {
                arrayList.add(iterator.next());
            }
            return arrayList;
        }
        if (o instanceof Object[] objects) {
            return Arrays.asList(objects);
        }
        return List.of(o);
    }

    private boolean isArray(Object o) {
        return o instanceof Object[] ||
                o instanceof Iterable<?> ||
                o instanceof Iterator<?> ||
                o instanceof Map<?,?>;
    }

    /**
     * Checks to see if the two values are equal.
     *
     * @param o1 The first Object to check if is equal to the second.
     * @param o2 The second Object to check if is equal to the first.
     * @return {@code true} if the two objects are equal through call
     * to {@link Objects#equals(Object, Object)}.
     */
    public boolean equals(Object o1, Object o2) {
        return Objects.equals(o1, o2);
    }
}
