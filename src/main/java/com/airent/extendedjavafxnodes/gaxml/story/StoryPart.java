package com.airent.extendedjavafxnodes.gaxml.story;

import com.airent.extendedjavafxnodes.utils.Named;
import com.airent.extendedjavafxnodes.utils.Pair;
import com.airent.extendedjavafxnodes.utils.json.JSON;
import com.airent.extendedjavafxnodes.utils.json.ToJSON;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class StoryPart<T extends Named> extends AbstractMap<String, T> implements SequencedMap<String, T>, Named, java.io.Serializable, ToJSON {
    @java.io.Serial
    private static final long serialVersionUID = 1539469808L;

    protected final HashMap<String, T> children = new HashMap<>();
    protected final ArrayList<String> childrenNames = new ArrayList<>();

    @Override
    public abstract String getName();

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean containsKey(Object name) {
        if (name instanceof String) {
            return childrenNames.contains(name);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return children.containsValue(value);
    }

    public boolean containsAll(Collection<?> c) {
        if (c.isEmpty()) return false;
        if (isEmpty()) return false;
        for (Object o : c) {
            if (o instanceof String s) {
                if (!containsKey(s)) {
                    return false;
                }
            } else {
                if (!containsValue(o)) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<String> childNames() {
        return Collections.unmodifiableList(childrenNames);
    }

    @Override
    public T put(String key, T value) {
        T old = children.put(key, value);
        if (!containsKey(key)) {
            childrenNames.add(key);
        }
        return old;
    }

    @Override
    public T putFirst(String key, T value) {
        T old = children.put(key, value);
        if (containsKey(key)) {
            childrenNames.remove(key);
        }
        childrenNames.addFirst(key);
        return old;
    }

    @Override
    public T putLast(String key, T value) {
        T old = children.put(key, value);
        if (containsKey(key)) {
            childrenNames.remove(key);
        }
        childrenNames.addLast(key);
        return old;
    }

    public boolean add(@NotNull T part) {
        return add(size(), part);
    }

    public boolean add(int index, @NotNull T part) {
        String identifier;
        if (part.isNameUnique()) {
            identifier = part.getName();
            if (identifier == null) throw new NullPointerException("The name of the part must not be null.");
        } else {
            identifier = part.getId();
            if (identifier == null) throw new NullPointerException("The id of the part must not be null.");
        }
        if (!children.containsKey(identifier)) {
            children.put(identifier, part);
            childrenNames.add(index, identifier);
            return true;
        }
        return false;
    }

    public T set(int index, @NotNull T part) {
        String identifier;
        if (part.isNameUnique()) {
            identifier = part.getName();
            if (identifier == null) throw new NullPointerException("The name of the part must not be null.");
        } else {
            identifier = part.getId();
            if (identifier == null) throw new NullPointerException("The id of the part must not be null.");
        }
        if (!children.containsKey(identifier)) {
            children.put(identifier, part);
            String old = childrenNames.set(index, identifier);
            return children.remove(old);
        } else {
            if (childrenNames.get(index).equals(identifier)) {
                return children.put(identifier, part);
            }
        }
        throw new RuntimeException("Cannot set, the identifier of the provided part isn't at the provided index.");
    }

    @Override
    public T get(Object key) {
        if (key instanceof String string) {
            return this.get(string);
        } else if (key instanceof Integer integer) {
            return this.get((int) integer);
        }
        return null;
    }

    public T get(String name) {
        return children.get(name);
    }

    public T get(int index) {
        return get(childrenNames.get(index));
    }

    public T getFirst() {
        return get(0);
    }

    public T getLast() {
        return get(size()-1);
    }

    @Override
    public T remove(Object key) {
        if (key instanceof String string) {
            return this.remove(string);
        } else if (key instanceof Integer integer) {
            return this.remove((int) integer);
        }
        return null;
    }

    @Override
    public boolean remove(Object key, Object value) {
        T checking = this.get(key);
        if (checking != null && checking.equals(value)) {
            return this.remove(key) != null;
        }
        return false;
    }

    public T remove(String name) {
        T ret = children.remove(name);
        childrenNames.remove(name);
        return ret;
    }

    public T remove(int index) {
        return remove(childrenNames.get(index));
    }

    public void forEach(Consumer<T> action) {
        childrenNames.forEach(name -> {
            action.accept(get(name));
        });
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super T> action) {
        childrenNames.forEach(name -> {
            action.accept(name, get(name));
        });
    }

    private ReversedMap reversedMap;

    @Override
    public SequencedMap<String, T> reversed() {
        ReversedMap rm = reversedMap;
        if (rm == null) {
            rm = new ReversedMap((EntrySet) entrySet().reversed());
            reversedMap = rm;
        }
        return rm;
    }

    @Override
    public JSONObject toJSON() {
        return (JSONObject) JSON.validateToJSON(children);
    }

    public JSONArray namesToJSON() {
        return new JSONArray(childrenNames);
    }

    private transient AsList asList;

    public List<T> toList() {
        AsList asList1 = asList;
        if (asList1 == null) {
            asList1 = new AsList();
            asList = asList1;
        }
        return asList1;
    }

    public class AsList extends AbstractList<T> implements List<T> {
        @Override
        public int size() {
            return StoryPart.this.size();
        }

        @Override
        public T get(int index) {
            return StoryPart.this.get(index);
        }

        @Override
        public T set(int index, T element) {
            return StoryPart.this.set(index, element);
        }

        @Override
        public void add(int index, T element) {
            StoryPart.this.add(index, element);
        }

        @Override
        public T remove(int index) {
            return StoryPart.this.remove(index);
        }
    }

    private EntrySet entrySet;

    @NotNull
    @Override
    public SequencedSet<Map.Entry<String, T>> entrySet() {
        EntrySet es = entrySet;
        if (es == null) {
            es = new EntrySet(childrenNames);
            entrySet = es;
        }
        return es;
    }

    /* ------------------------------------------------------------ */
    // collections

    private final class EntrySet extends AbstractSet<Map.Entry<String, T>> implements SequencedSet<Map.Entry<String, T>> {
        List<String> names;
        EntrySet(List<String> names) {
            this.names = names;
        }

        @NotNull
        @Override
        public Iterator<Entry<String, T>> iterator() {
            return new EntryIterator(names);
        }

        @Override
        public void addFirst(@NotNull Entry<String, T> stringTEntry) {
            String key = stringTEntry.getKey();
            T value = stringTEntry.getValue();
            children.put(key, value);
            if (containsKey(key)) {
                names.remove(key);
            }
            names.addFirst(key);
        }

        @Override
        public void addLast(@NotNull Entry<String, T> stringTEntry) {
            String key = stringTEntry.getKey();
            T value = stringTEntry.getValue();
            children.put(key, value);
            if (containsKey(key)) {
                names.remove(key);
            }
            names.addLast(key);
        }

        @Override
        public Entry<String, T> getFirst() {
            return new Pair<>(names.getFirst(), StoryPart.this.get(names.getFirst()));
        }

        @Override
        public Entry<String, T> getLast() {
            return new Pair<>(names.getLast(), StoryPart.this.get(names.getLast()));
        }

        @Override
        public Entry<String, T> removeFirst() {
            String key = names.removeFirst();
            Pair<String, T> ret = new Pair<>(key, StoryPart.this.get(key));
            children.remove(key);
            return ret;
        }

        @Override
        public Entry<String, T> removeLast() {
            String key = names.removeLast();
            Pair<String, T> ret = new Pair<>(key, StoryPart.this.get(key));
            children.remove(key);
            return ret;
        }

        @Override
        public final int size() {
            return StoryPart.this.size();
        }
        @Override
        public final void clear() {
            StoryPart.this.clear();
        }

        @Override
        public final boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> e))
                return false;
            Object key = e.getKey();
            if (key instanceof String string) {
                if (StoryPart.this.containsKey(string)) {
                    return StoryPart.this.get(string).equals(e.getValue());
                }
            }
            return false;
        }
        @Override
        public final boolean remove(Object o) {
            if (o instanceof Map.Entry<?, ?> e) {
                Object key = e.getKey();
                Object value = e.getValue();
                return StoryPart.this.remove(key, value);
            }
            return false;
        }

        private EntrySet revEntrySet;

        @Override
        public EntrySet reversed() {
            EntrySet res = revEntrySet;
            if (res == null) {
                res = new EntrySet(names.reversed());
                revEntrySet = res;
            }
            return res;
        }
    }

    /* ------------------------------------------------------------ */
    // iterators

    private final class EntryIterator implements Iterator<Entry<String, T>> {
        Iterator<String> nameItr;
        String current = null;

        EntryIterator(@NotNull List<String> names) {
            nameItr = names.iterator();
        }

        @Override
        public boolean hasNext() {
            return nameItr.hasNext();
        }

        @Override
        public Entry<String, T> next() {
            current = nameItr.next();
            return new Pair<>(current, StoryPart.this.get(current));
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            StoryPart.this.remove(current);
            current = null;
        }
    }

    private final class ReversedMap extends AbstractMap<String, T> implements SequencedMap<String, T> {
        private final EntrySet entrySet;

        ReversedMap(EntrySet entrySet) {
            this.entrySet = entrySet;
        }

        @NotNull
        @Override
        public EntrySet entrySet() {
            return entrySet;
        }

        @Override
        public ReversedMap reversed() {
            return new ReversedMap(entrySet.reversed());
        }
    }
}
