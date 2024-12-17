package com.airent.extendedjavafxnodes.utils;

import java.util.Map;

public class Pair<K, V>  implements Map.Entry<K, V> {
    private K key;
    private V value;

    public Pair() {
        key = null;
        value = null;
    }

    /**
     * Creates a new key-value pair.
     *
     * @param key The {@code Object} to set as the key.
     * @param value The {@code Object} to set as the value.
     */
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * The copy constructor.
     * @param pair The key-value pair to copy.
     */
    public Pair(Pair<K, V> pair) {
        this(pair.key, pair.value);
    }

    /**
     * Get the stored key of this pair.
     * @return The value of the {@link #key}.
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * Get the stored value of this pair.
     * @return The value of the {@link #value}.
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * Replaces the key corresponding to this pair with the specified value.
     *
     * @param key new key to be stored in this pair
     * @return old key corresponding to the pair
     */
    public K setKey(K key) {
        K old = this.key;
        this.key = key;
        return old;
    }

    /**
     * Replaces the value corresponding to this pair with the specified value.
     *
     * @param value new value to be stored in this pair
     * @return old value corresponding to the pair
     */
    @Override
    public V setValue(V value) {
        V old = this.value;
        this.value = value;
        return old;
    }

    /**
     * Updates both the {@link #key} and {@link #value} values of this pair.
     * @param key The new {@link #key} value.
     * @param value the new {@link #value} value.
     */
    public final void update(K key, V value) {
        setKey(key);
        setValue(value);
    }
}
