package com.airent.extendedjavafxnodes.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

public class OptionalMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Serializable {

    @Serial
    private static final long serialVersionUID = 59876345874654L;

    private final HashMap<K, V> map;
    private boolean keyNullable = true;
    private boolean valueNullable = true;

    public OptionalMap() {
        this.map = new HashMap<>();
    }
    public OptionalMap(int initialCapacity) {
        this.map = new HashMap<>(initialCapacity);
    }
    public OptionalMap(int initialCapacity, float loadFactor) {
        this.map = new HashMap<>(initialCapacity, loadFactor);
    }
    public OptionalMap(Map<? extends K, ? extends V> m) {
        this.map = new HashMap<>();
        this.putAll(m);
    }

    public boolean isKeyNullable() {
        return keyNullable;
    }
    public void setKeyNullable(boolean keyNullable) {
        this.keyNullable = keyNullable;
    }

    public boolean isValueNullable() {
        return valueNullable;
    }
    public void setValueNullable(boolean valueNullable) {
        this.valueNullable = valueNullable;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (!isKeyNullable() && key == null) return false;
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (!isValueNullable() && value == null) return false;
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        if (key == null && !isKeyNullable()) throw new NullPointerException("The provided key was unacceptably null.");
        if (value == null && !isValueNullable()) throw new NullPointerException("The provided value was unacceptably null.");
        return map.put(key, value);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        if (Objects.equals(oldValue, newValue)) return false;
        if (newValue == null && !isValueNullable()) {
            throw new NullPointerException("The provided value to replace with was unacceptably null.");
        }
        return map.replace(key, oldValue, newValue);
    }

    @Nullable
    @Override
    public V replace(K key, V value) {
        if (value == null && !isValueNullable()) {
            throw new NullPointerException("The provided value to replace with was unacceptably null.");
        }
        return map.replace(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public void clear() {
        map.clear();
    }

    private Set<K> keySet;
    private Collection<V> values;
    private Set<Map.Entry<K,V>> entrySet;

    @NotNull
    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet(map.keySet());
            keySet = ks;
        }
        return ks;
    }

    @NotNull
    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values(map.values());
            values = vs;
        }
        return vs;
    }

    @NotNull
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
        Set<Map.Entry<K, V>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet(map.entrySet())) : es;
    }

    /* ------------------------------------------------------------ */
    // collections

    final class KeySet extends AbstractSet<K> {
        final Set<K> actual;
        KeySet(Set<K> actual) {
            this.actual = actual;
        }
        public final int size() {
            return OptionalMap.this.size();
        }
        public final void clear() {
            OptionalMap.this.clear();
        }
        @NotNull
        @Contract(" -> new")
        public final Iterator<K> iterator() {
            return new KeyIterator();
        }
        public final boolean contains(Object o) {
            return containsKey(o);
        }
        public final boolean remove(Object key) {
            return OptionalMap.this.remove(key) != null;
        }
        @NotNull
        @Contract(" -> new")
        public final Spliterator<K> spliterator() {
            return new KeySpliterator<>(actual.spliterator(), OptionalMap.this);
        }
    }

    final class Values extends AbstractCollection<V> {
        final Collection<V> actual;
        Values(Collection<V> actual) {
            this.actual = actual;
        }
        public final int size() {
            return OptionalMap.this.size();
        }
        public final void clear() {
            OptionalMap.this.clear();
        }
        @NotNull
        @Contract(" -> new")
        public final Iterator<V> iterator() {
            return new ValueIterator();
        }
        public final boolean contains(Object o) {
            return containsValue(o);
        }
        @NotNull
        @Contract(" -> new")
        public final Spliterator<V> spliterator() {
            return new ValueSpliterator<>(actual.spliterator(), OptionalMap.this);
        }
    }

    final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        final Set<Entry<K, V>> actual;
        EntrySet(Set<Entry<K, V>> actual) {
            this.actual = actual;
        }
        public final int size() {
            return OptionalMap.this.size();
        }
        public final void clear() {
            OptionalMap.this.clear();
        }

        @NotNull
        @Contract(" -> new")
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            if (OptionalMap.this.containsKey(key)) {
                return OptionalMap.this.get(key).equals(e.getValue());
            }
            return false;
        }
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return OptionalMap.this.remove(key, value);
            }
            return false;
        }

        @NotNull
        @Contract(" -> new")
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<>(actual.spliterator(), OptionalMap.this);
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    abstract class HashIterator {
        Iterator<Entry<K, V>> actual;
        Entry<K, V> next;        // next entry to return
        Entry<K, V> current;     // current entry

        HashIterator() {
            actual = OptionalMap.this.map.entrySet().iterator();
            current = next = null;
            if (actual.hasNext()) {
                next = actual.next();
            }
        }

        public final boolean hasNext() {
            return next != null || actual.hasNext();
        }

        final Entry<K, V> nextNode() {
            if (next == null && actual.hasNext()) next = actual.next();
            Entry<K, V> e;
            if (next == null)
                throw new NoSuchElementException();
            current = next;
            next = actual.next();
            K key = current.getKey();
            V value = current.getValue();
            if ((key != null || isKeyNullable()) && (value != null || isValueNullable())) {
                e = new Pair<>(key, value);
            } else {
                if (key != null && isValueNullable()) {
                    e = new Pair<>(key, null);
                } else if (value != null && isKeyNullable()) {
                    e = new Pair<>(null, value);
                } else {
                    throw new NullPointerException("Null values cannot be returned.");
                }
            }
            return e;
        }

        public final void remove() {
            Entry<K, V> p = current;
            if (p == null)
                throw new IllegalStateException();
            current = null;
            OptionalMap.this.remove(p.getKey());
        }
    }

    final class KeyIterator extends HashIterator
            implements Iterator<K> {
        public final K next() { return nextNode().getKey(); }
    }

    final class ValueIterator extends HashIterator
            implements Iterator<V> {
        public final V next() { return nextNode().getValue(); }
    }

    final class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    static class OptionalMapSpliterator<E, K, V> {
        final Spliterator<E> spliterator;
        final OptionalMap<K, V> map;

        OptionalMapSpliterator(Spliterator<E> spliterator, OptionalMap<K, V> m) {
            this.spliterator = spliterator;
            this.map = m;
        }

        public final long estimateSize() {
            return spliterator.estimateSize();
        }

        public int characteristics() {
            return spliterator.characteristics();
        }
    }

    static final class KeySpliterator<K, V>
            extends OptionalMapSpliterator<K, K, V>
            implements Spliterator<K> {
        KeySpliterator(Spliterator<K> spliterator, OptionalMap<K, V> m) {
            super(spliterator, m);
        }

        public KeySpliterator<K, V> trySplit() {
            return new KeySpliterator<>(spliterator.trySplit(), map);
        }

        public void forEachRemaining(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            spliterator.forEachRemaining(key -> {
                if (key != null || map.isKeyNullable()) {
                    action.accept(key);
                }
            });
        }

        public boolean tryAdvance(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            return spliterator.tryAdvance(key -> {
                if (key != null || map.isKeyNullable()) {
                    action.accept(key);
                }
            });
        }
    }

    static final class ValueSpliterator<K, V>
            extends OptionalMapSpliterator<V, K, V>
            implements Spliterator<V> {
        ValueSpliterator(Spliterator<V> spliterator, OptionalMap<K,V> m) {
            super(spliterator, m);
        }

        public ValueSpliterator<K, V> trySplit() {
            return new ValueSpliterator<>(spliterator.trySplit(), map);
        }

        public void forEachRemaining(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            spliterator.forEachRemaining(value -> {
                if (value != null || map.isValueNullable()) {
                    action.accept(value);
                }
            });
        }

        public boolean tryAdvance(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            return spliterator.tryAdvance(value -> {
                if (value != null || map.isValueNullable()) {
                    action.accept(value);
                }
            });
        }
    }

    static final class EntrySpliterator<K,V>
            extends OptionalMapSpliterator<Map.Entry<K,V>, K, V>
            implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(Spliterator<Map.Entry<K,V>> spliterator, OptionalMap<K, V> m) {
            super(spliterator, m);
        }

        public EntrySpliterator<K, V> trySplit() {
            return new EntrySpliterator<>(spliterator.trySplit(), map);
        }

        public void forEachRemaining(Consumer<? super Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            spliterator.forEachRemaining(entry -> {
                if ((entry.getKey() != null || map.isKeyNullable()) &&
                        (entry.getValue() != null || map.isValueNullable())) {
                    action.accept(entry);
                }
            });
        }

        public boolean tryAdvance(Consumer<? super Entry<K, V>> action) {
            if (action == null)
                throw new NullPointerException();
            return spliterator.tryAdvance(entry -> {
                if ((entry.getKey() != null || map.isKeyNullable()) &&
                        (entry.getValue() != null || map.isValueNullable())) {
                    action.accept(entry);
                }
            });
        }
    }
}
