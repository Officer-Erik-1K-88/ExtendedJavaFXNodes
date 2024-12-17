package com.airent.extendedjavafxnodes.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

public class ListMap<K, V> extends AbstractMap<K, V> implements SequencedMap<K, V>, Serializable {
    @Serial
    private static final long serialVersionUID = 95356347474635226L;
    private final OptionalMap<K, V> map;
    private final List<K> keys;
    private final List<V> values;
    private final boolean reversed;
    private final boolean subMap;

    public ListMap() {
        this(16);
    }

    public ListMap(int initialCapacity) {
        this(
                new ArrayList<>(initialCapacity),
                new ArrayList<>(initialCapacity),
                new OptionalMap<>(initialCapacity),
                false,
                false
        );
    }

    private ListMap(List<K> keys, List<V> values, OptionalMap<K, V> map, boolean reversed, boolean subMap) {
        this.map = map;
        this.keys = keys;
        this.values = values;
        this.reversed = reversed;
        this.subMap = subMap;
    }

    public boolean isKeyNullable() {
        return map.isKeyNullable();
    }
    public void setKeyNullable(boolean keyNullable) {
        map.setKeyNullable(keyNullable);
    }

    public boolean isValueNullable() {
        return map.isValueNullable();
    }
    public void setValueNullable(boolean valueNullable) {
        map.setValueNullable(valueNullable);
    }

    public final boolean isReversed() {
        return reversed;
    }

    protected final List<K> getKeys() {
        return keys;
    }

    @SuppressWarnings("unchecked")
    public boolean isInstanceOfK(Object obj) {
        try {
            K obj1 = (K) obj;
            if (obj1 != null || isKeyNullable()) {
                return true;
            }
            throw new NullPointerException("Object provided to ListMap must not be null while keyNullable is false.");
        } catch (ClassCastException e) {
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    public boolean isInstanceOfV(Object obj) {
        try {
            V obj1 = (V) obj;
            if (obj1 != null || isValueNullable()) {
                return true;
            }
            throw new NullPointerException("Object provided to ListMap must not be null while valueNullable is false.");
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int indexOf(K key) {
        return keys.indexOf(key);
    }

    public int lastIndexOf(K key) {
        return keys.lastIndexOf(key);
    }

    @Override
    public int size() {
        return keys.size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (subMap) {
            if (!keys.contains(key)) {
                return false;
            }
        }
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (subMap) {
            if (!values.contains(value)) {
                return false;
            }
        }
        return map.containsValue(value);
    }

    public K getKey(int index) {
        return keys.get(index);
    }
    public V getValue(int index) {
        return values.get(index);
    }

    @Override
    public V get(Object key) {
        if (subMap) {
            if (!keys.contains(key)) {
                return null;
            }
        }
        return map.get(key);
    }

    /* ------------------------------------------------------------ */
    // add and insertion

    public boolean add(Pair<K, V> pair) {
        add(size(), pair);
        return true;
    }

    public void add(int index, Pair<K, V> pair) {
        put(index, pair.getKey(), pair.getValue());
    }

    public boolean addAll(Collection<? extends Pair<K, V>> pairCollection) {
        return entrySet().addAll(pairCollection);
    }

    public boolean addAll(int index, Collection<? extends Pair<K, V>> pairCollection) {
        return entrySet().addAll(index, pairCollection);
    }

    public Pair<K, V> set(int index, Pair<K, V> pair) {
        return set(index, pair.getKey(), pair.getValue());
    }

    public Pair<K, V> set(int index, K key, V value) {
        Pair<K, V> pair = new Pair<>();
        if (!keys.contains(key)) {
            if (subMap) {
                if (map.containsKey(key)) {
                    throw new RuntimeException("Cannot override a key that exists, but is outside of this sub mapping of a ListMap.");
                }
            }
            pair.setKey(keys.set(index, key));
            pair.setValue(values.set(index, value));
        } else {
            if (indexOf(key) != index) {
                throw new RuntimeException("Cannot override a key that exists and is not at stated index.");
            }
            pair.setKey(key);
            pair.setValue(values.set(index, value));
        }
        map.remove(pair.getKey());
        map.put(key, value);
        return pair;
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        if (!keys.contains(key)) {
            if (subMap) {
                if (map.containsKey(key)) {
                    throw new RuntimeException("Cannot override a key that exists, but is outside of this sub mapping of a ListMap.");
                }
            }
            keys.add(key);
            values.add(value);
        } else {
            values.set(indexOf(key), value);
        }
        return map.put(key, value);
    }

    public V put(int index, K key, V value) {
        if (!keys.contains(key)) {
            if (subMap) {
                if (map.containsKey(key)) {
                    throw new RuntimeException("Cannot override a key that exists, but is outside of this sub mapping of a ListMap.");
                }
            }
            keys.add(index, key);
            values.add(index, value);
        } else {
            if (indexOf(key) != index) {
                throw new RuntimeException("Cannot override a key that exists.");
            }
            values.set(index, value);
        }
        return map.put(key, value);
    }

    @Override
    public V putFirst(K key, V value) {
        if (!keys.contains(key)) {
            if (subMap) {
                if (map.containsKey(key)) {
                    throw new RuntimeException("Cannot override a key that exists, but is outside of this sub mapping of a ListMap.");
                }
            }
            keys.addFirst(key);
            values.addFirst(value);
        } else {
            values.set(indexOf(key), value);
        }
        return map.put(key, value);
    }

    @Override
    public V putLast(K key, V value) {
        if (!keys.contains(key)) {
            if (subMap) {
                if (map.containsKey(key)) {
                    throw new RuntimeException("Cannot override a key that exists, but is outside of this sub mapping of a ListMap.");
                }
            }
            keys.addLast(key);
            values.addLast(value);
        } else {
            values.set(indexOf(key), value);
        }
        return map.put(key, value);
    }

    /* ------------------------------------------------------------ */
    // removal

    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        if (containsKey(key)) {
            int index = indexOf((K) key);
            keys.remove(index);
            values.remove(index);
            return map.remove(key);
        }
        return null;
    }

    @Override
    public void clear() {
        if (subMap) {
            for (K key : keys) {
                map.remove(key);
            }
        } else {
            map.clear();
        }
        keys.clear();
        values.clear();
    }

    /* ------------------------------------------------------------ */
    // misc

    public void sortByKeys(Comparator<? super K> c) {
        EntrySet es = (EntrySet) entrySet();
        es.sort((o1, o2) -> c.compare(o1.getKey(), o2.getKey()));
    }

    public void sortByValues(Comparator<? super V> c) {
        EntrySet es = (EntrySet) entrySet();
        es.sort((o1, o2) -> c.compare(o1.getValue(), o2.getValue()));
    }

    @Override
    public SequencedMap<K, V> reversed() {
        return new ListMap<>(keys.reversed(), values.reversed(), map, true, subMap);
    }

    private EntrySet entrySet;

    @NotNull
    @Override
    public EntrySet entrySet() {
        EntrySet es = entrySet;
        if (es == null) {
            es = new EntrySet();
            entrySet = es;
        }
        return es;
    }

    /* ------------------------------------------------------------ */
    // collections

    public final class EntrySet extends AbstractSet<Map.Entry<K, V>> implements SequencedSet<Map.Entry<K, V>>, List<Map.Entry<K, V>> {
        EntrySet() {}
        @Override
        public final int size() {
            return ListMap.this.size();
        }
        @Override
        public final void clear() {
            ListMap.this.clear();
        }

        @Override
        public Pair<K, V> get(int index) {
            return new Pair<>(keys.get(index), values.get(index));
        }

        @Override
        public Pair<K, V> set(int index, @NotNull Entry<K, V> element) {
            if (!keys.get(index).equals(element.getKey())) {
                if (containsKey(element.getKey())) throw new RuntimeException("Cannot override a key that already exists in a different index.");
            }
            K key = keys.set(index, element.getKey());
            V value = values.set(index, element.getValue());
            map.put(element.getKey(), element.getValue());
            return new Pair<>(key, value);
        }

        @Override
        public void add(int index, @NotNull Entry<K, V> element) {
            if (containsKey(element.getKey())) throw new RuntimeException("Cannot override a key that already exists in a different index.");
            keys.add(index, element.getKey());
            values.add(index, element.getValue());
            map.put(element.getKey(), element.getValue());
        }

        @NotNull
        @Override
        public Pair<K, V> remove(int index) {
            K key = keys.remove(index);
            V value = values.remove(index);
            map.remove(key);
            return new Pair<>(key, value);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return super.removeAll(c);
        }

        @Override
        @SuppressWarnings("unchecked")
        public int indexOf(Object o) {
            if (o instanceof Entry<?, ?> entry) {
                if (ListMap.this.isInstanceOfK(entry.getKey())) {
                    return ListMap.this.indexOf((K) entry.getKey());
                }
            }
            return -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public int lastIndexOf(Object o) {
            if (o instanceof Entry<?, ?> entry) {
                if (ListMap.this.isInstanceOfK(entry.getKey())) {
                    return ListMap.this.lastIndexOf((K) entry.getKey());
                }
            }
            return -1;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void sort(Comparator<? super Entry<K, V>> c) {
            Object[] a = this.toArray();
            Arrays.sort(a, (Comparator) c);
            EntryListItr i = (EntryListItr) this.listIterator();
            for (Object e : a) {
                i.next();
                i.set(((Entry<K, V>) e), true);
            }
        }

        @NotNull
        @Override
        public ListIterator<Entry<K, V>> listIterator() {
            return listIterator(0);
        }

        @NotNull
        @Override
        public ListIterator<Entry<K, V>> listIterator(int index) {
            return new EntryListItr(index);
        }

        @NotNull
        @Override
        public EntrySet subList(int fromIndex, int toIndex) {
            ListMap<K, V> subMap = new ListMap<>(
                    keys.subList(fromIndex, toIndex),
                    values.subList(fromIndex, toIndex),
                    ListMap.this.map,
                    reversed,
                    true
            );
            return subMap.entrySet();
        }

        @NotNull
        @Contract(" -> new")
        @Override
        public final Iterator<Map.Entry<K,V>> iterator() {
            return new EntryIterator();
        }
        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            if (ListMap.this.containsKey(key)) {
                return ListMap.this.get(key).equals(e.getValue());
            }
            return false;
        }
        @Override
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return ListMap.this.remove(key, value);
            }
            return false;
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends Entry<K, V>> c) {
            boolean changed = false;
            for (Entry<K, V> entry : c) {
                try {
                    add(index, entry);
                    changed = true;
                } catch (RuntimeException ignored) {}
            }
            return changed;
        }

        @NotNull
        @Contract(" -> new")
        @Override
        public final Spliterator<Map.Entry<K,V>> spliterator() {
            return new EntrySpliterator<>(Spliterators.spliterator(this, Spliterator.ORDERED), ListMap.this);
        }

        @NotNull
        @Override
        public EntrySet reversed() {
            return (EntrySet) ListMap.this.reversed().entrySet();
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    private abstract class HashIterator {
        ListIterator<K> actual;
        K next;        // next key to return
        K previous;
        K current;     // current key
        K lastRet;

        HashIterator() {
            actual = ListMap.this.keys.listIterator();
            current = next = previous = lastRet = null;
            if (actual.hasNext()) {
                next = actual.next();
            }
        }

        public final boolean hasNext() {
            return next != null || actual.hasNext();
        }

        public boolean hasPrevious() {
            return previous != null || actual.hasPrevious();
        }

        public int nextIndex() {
            return actual.nextIndex();
        }

        public int previousIndex() {
            return actual.previousIndex();
        }

        @NotNull
        final Entry<K, V> nextNode() {
            if (next == null && actual.hasNext()) next = actual.next();
            if (next == null)
                throw new NoSuchElementException();
            lastRet = current;
            current = next;
            if (actual.hasNext()) {
                next = actual.next();
            } else {
                next = null;
            }
            return currentNode();
        }

        @NotNull
        final Entry<K, V> previousNode() {
            if (previous == null && actual.hasPrevious()) previous = actual.previous();
            if (previous == null)
                throw new NoSuchElementException();
            lastRet = current;
            current = previous;
            if (actual.hasPrevious()) {
                previous = actual.previous();
            } else {
                previous = null;
            }
            return currentNode();
        }

        @NotNull
        final Entry<K, V> currentNode() {
            K key = current;
            V value = ListMap.this.get(current);
            Entry<K, V> e;
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
            ListMap.this.remove(current);
            current = lastRet;
            lastRet = null;
        }

        public void set(@NotNull Map.Entry<K,V> e) {
            this.set(e, false);
        }

        public void set(Map.Entry<K,V> e, boolean replacing) {
            if (!replacing) {
                if (!e.getKey().equals(lastRet)) {
                    if (ListMap.this.containsKey(e.getKey())) throw new RuntimeException("Cannot have duplicate keys.");
                }
            }
            int index = ListMap.this.indexOf(lastRet);
            ListMap.this.keys.set(index, e.getKey());
            ListMap.this.values.set(index, e.getValue());
            ListMap.this.map.remove(lastRet);
            ListMap.this.map.put(e.getKey(), e.getValue());
        }

        public void add(@NotNull Map.Entry<K,V> e) {
            if (!e.getKey().equals(current)) {
                if (ListMap.this.containsKey(e.getKey())) throw new RuntimeException("Cannot have duplicate keys.");
            }
            int index = ListMap.this.indexOf(current);
            ListMap.this.keys.set(index, e.getKey());
            ListMap.this.values.set(index, e.getValue());
            ListMap.this.map.remove(current);
            ListMap.this.map.put(e.getKey(), e.getValue());
            current = next;
            lastRet = null;
        }
    }

    private class EntryIterator extends HashIterator
            implements Iterator<Map.Entry<K,V>> {
        public final Map.Entry<K,V> next() { return nextNode(); }
    }

    private class EntryListItr extends EntryIterator implements ListIterator<Map.Entry<K,V>> {
        EntryListItr(int index) {
            super();
            current = keys.get(index);
        }

        public Map.Entry<K,V> previous() {
            return previousNode();
        }
    }

    /* ------------------------------------------------------------ */
    // spliterators

    private static class MapSpliterator<E, K, V> {
        final Spliterator<E> spliterator;
        final ListMap<K, V> map;

        MapSpliterator(Spliterator<E> spliterator, ListMap<K, V> m) {
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

    private static final class EntrySpliterator<K,V>
            extends MapSpliterator<Entry<K,V>, K, V>
            implements Spliterator<Map.Entry<K,V>> {
        EntrySpliterator(Spliterator<Map.Entry<K,V>> spliterator, ListMap<K, V> m) {
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
