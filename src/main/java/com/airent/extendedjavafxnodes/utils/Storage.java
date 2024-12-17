package com.airent.extendedjavafxnodes.utils;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class Storage<E> implements List<E>, RandomAccess, Serializable {
    @Serial
    private static final long serialVersionUID = 5346454553342L;
    public static class Store<V> implements Serializable {
        @Serial
        private static final long serialVersionUID = 53464543342L;
        private final Storage<V> storage;
        private final ArrayList<String> keys = new ArrayList<>();
        private V element;
        private Integer index;

        public Store(V element) {
            this(null, element);
        }

        public Store(String key, V element) {
            this(key, element, (Integer) null);
        }

        public Store(String key, V element, Integer index) {
            this(key, element, index, null);
        }

        public Store(V element, Storage<V> storage) {
            this(null, element, storage);
        }

        public Store(String key, V element, Storage<V> storage) {
            this(key, element, null, storage);
        }

        public Store(String key, V element, Integer index, Storage<V> storage) {
            if (key != null && !key.isBlank()) {
                this.keys.add(key);
            }
            this.element = element;
            this.index = index;
            this.storage = storage;
        }

        public String getKey() {
            if (this.keys.isEmpty()) return null;
            return this.keys.getFirst();
        }

        public List<String> getKeys() {
            if (storage != null) {
                return storage.wrapStoreKeys(this.keys);
            }
            return this.keys;
        }

        public V getElement() {
            return element;
        }

        public V setElement(V element) {
            V old = this.element;
            this.element = element;
            return old;
        }

        public Integer getIndex() {
            return index;
        }

        protected void setIndex(Integer index) {
            this.index = index;
        }
    }

    private List<String> wrapStoreKeys(ArrayList<String> keys) {
        return new AbstractList<String>() {
            final ArrayList<String> k = keys;
            @Override
            public int size() {
                return k.size();
            }

            @Override
            public boolean isEmpty() {
                return k.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                return k.contains(o);
            }

            @Override
            public int indexOf(Object o) {
                return k.indexOf(o);
            }

            @Override
            public int lastIndexOf(Object o) {
                return k.lastIndexOf(o);
            }

            @Override
            public String get(int index) {
                return k.get(index);
            }

            @Override
            public String set(int index, String element) {
                String old = remove(index);
                add(index, element);
                return old;
            }

            @Override
            public void add(int index, String element) {
                if (containsKey(element)) {
                    throw new RuntimeException("Cannot add key to this Store as it already exists for another store in this Storage.");
                }
                k.add(index, element);
            }

            @Override
            public boolean remove(Object o) {
                return k.remove(o);
            }

            @Override
            public String remove(int index) {
                return k.remove(index);
            }

            @Override
            public void clear() {
                k.clear();
            }

            @NotNull
            @Override
            public Object[] toArray() {
                return k.toArray();
            }

            @NotNull
            @Override
            public <T> T[] toArray(@NotNull T[] a) {
                return k.toArray(a);
            }
        };
    }

    private final MapList<Store<E>> stores;

    public Storage() {
        this(16);
    }
    public Storage(boolean indexedAtZero, int indexDifference) {
        this(16, indexedAtZero, indexDifference);
    }
    public Storage(int initialCapacity) {
        this(initialCapacity, true, 0);
    }
    public Storage(int initialCapacity, boolean indexedAtZero, int indexDifference) {
        this.stores = new MapList<>(initialCapacity, indexedAtZero, indexDifference);
    }

    public boolean isAllowNull() {
        return this.stores.isNullable();
    }

    public void setAllowNull(boolean allowNull) {
        this.stores.setNullable(allowNull);
    }

    public boolean isIndexedAtZero() {
        return this.stores.isIndexedAtZero();
    }

    public int getIndexDifference() {
        return this.stores.getIndexDifference();
    }

    @Override
    public int size() {
        return stores.size();
    }

    @Override
    public boolean isEmpty() {
        return stores.isEmpty();
    }

    // store stuff

    private Store<E> getStore(int index) {
        return stores.get(index);
    }

    private Store<E> getStore(String key) {
        for (Store<E> store : stores) {
            if (store.getKeys().contains(key)) {
                return store;
            }
        }
        throw new RuntimeException("No store with the key of `"+key+"` exist.");
    }

    // checking stuff

    public boolean containsKey(String key) {
        for (Store<E> store : stores) {
            if (store.getKeys().contains(key)) {
                return true;
            }
        }
        return false;
    }

    public boolean indexHasKey(int index) {
        Store<E> store = stores.get(index);
        return store != null && !store.getKeys().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        for (Store<E> store : stores) {
            if (store.getElement().equals(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public boolean isInstanceOfE(Object obj) {
        try {
            E obj1 = (E) obj;
            if (obj1 != null || isAllowNull()) {
                return true;
            }
            throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
        } catch (ClassCastException e) {
            return false;
        }
    }

    // index stuff

    @Override
    public int indexOf(Object o) {
        for (Store<E> store : stores) {
            if (store.getElement().equals(o)) {
                return store.getIndex();
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (Store<E> store : stores.reversed()) {
            if (store.getElement().equals(o)) {
                return store.getIndex();
            }
        }
        return -1;
    }

    public int getIndex(String key) {
        Integer index = getLiteralIndex(key);
        if (index == null) return -1;
        return index;
    }

    public Integer getLiteralIndex(String key) {
        try {
            return getStore(key).getIndex();
        } catch (RuntimeException e) {
            return null;
        }
    }

    public Integer indexFix(Integer index) {
        return stores.indexFix(index);
    }

    public Integer indexDiff(Integer index) {
        return stores.indexDiff(index);
    }

    // key stuff

    /**
     * Gets the primary reference key of the element at the provided index.
     *
     * @param index The index of the element to get the primary reference key of.
     * @return The primary reference key of the element at the provided index.
     * Will be null if there are no keys that reference that element.
     */
    public String getKey(int index) {
        Store<E> store = getStore(index);
        return store.getKey();
    }

    /**
     * Gets the primary reference key of the element at the provided reference key.
     *
     * @param key A reference key of the element to get the primary reference key of.
     * @return The primary reference key of the element at the provided reference key.
     */
    public String getKey(String key) {
        Store<E> store = getStore(key);
        return store.getKey();
    }

    /**
     * Gets the reference keys of the element at the provided index.
     *
     * @param index The index of the element to get the reference keys of.
     * @return A list of Strings that are the reference keys of that element.
     */
    public List<String> getKeys(int index) {
        Store<E> store = getStore(index);
        return store.getKeys();
    }

    public void setKey(String key, Integer index) {
        if (key == null) {
            throw new NullPointerException("No key can be null in Storage.");
        }
        if (containsKey(key)) {
            if (stores.containsIndex(index)) {
                getStore(key).keys.remove(key);
            } else {
                throw new RuntimeException("Cannot move key.");
            }
        }
        if (!stores.containsIndex(index)) throw new RuntimeException("Cannot add a key to an element that doesn't exist.");
        getStore(index).keys.add(key);
    }

    public boolean removeKey(String key) {
        Store<E> store = getStore(key);
        return store.keys.remove(key);
    }

    public boolean removeKey(String key, Integer index) {
        Store<E> store = getStore(key);
        if (Objects.equals(store.getIndex(), index)) {
            return store.keys.remove(key);
        }
        return false;
    }

    public E removeByKey(String key) {
        return remove((int) getStore(key).getIndex());
    }

    // list stuff

    @Override
    public E get(int index) {
        return getStore(index).getElement();
    }

    public E get(String key) {
        return getStore(key).getElement();
    }

    /**
     * This method will add a new element to this Storage with
     * the provided key as it's primary reference key.
     * However, if the key is already tied to an element,
     * then that element will be replaced with the new element,
     * also the primary reference key will not be changed during replacement.
     *
     * @param key The primary key to reference the new element to.
     * @param element The new element to add.
     * @return The old element that was referenced by the provided key
     * if the provided key was tied to an element.
     * Otherwise, will return null.
     */
    public E put(String key, E element) {
        if (!isAllowNull() && element == null) {
            throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
        }
        if (key == null) {
            throw new NullPointerException("No key can be null in Storage.");
        }
        if (containsKey(key)) {
            return set(getIndex(key), element);
        }
        add(element);
        setKey(key, indexDiff(stores.getCongruentIndexSize())-1);
        return null;
    }

    /**
     * Unlike {@link #put(String, Object)}, this method can only add
     * (not replace) a new element to this Storage at the provided
     * index.
     *
     * @param key The primary key to reference the new element to.
     * @param index The index to add the new element at.
     * @param element The new element to add.
     */
    public void put(String key, int index, E element) {
        if (!isAllowNull() && element == null) {
            throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
        }
        if (key == null) {
            throw new NullPointerException("No key can be null in Storage.");
        }
        if (containsKey(key)) {
            throw new RuntimeException("Cannot change the element of a key when trying to insert new element at a certain index.");
        }
        add(index, element);
        setKey(key, index);
    }

    @Override
    public E set(int index, E element) {
        if (element != null || isAllowNull()) {
            if (stores.containsIndex(index)) {
                return getStore(index).setElement(element);
            } else {
                Store<E> newStore = new Store<>(element, this);
                newStore.setIndex(index);
                stores.set(index, newStore);
                return null;
            }
        }
        throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
    }

    private void addStore(int index, E element) {
        int actualIndex = index;
        int find = index;
        boolean foundCurr = false;
        for (Store<E> store : stores) {
            if (store.getIndex() == find) {
                if (!foundCurr) {
                    actualIndex = stores.indexOf(store);
                }
                find = find+1;
                store.setIndex(find);
                foundCurr = true;
            } else {
                if (foundCurr) break;
            }
        }
        if (actualIndex > indexDiff(stores.size())) {
            actualIndex = indexDiff(stores.size());
        }
        Store<E> newStore = new Store<>(element, this);
        newStore.setIndex(index);
        stores.add(actualIndex, newStore);
    }

    @Override
    public void add(int index, E element) {
        if (!isAllowNull() && element == null) {
            throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
        }
        addStore(index, element);
    }

    @Override
    public boolean add(E e) {
        if (e != null || isAllowNull()) {
            int oldSize = size();
            addStore(indexDiff(stores.getCongruentIndexSize()), e);
            return oldSize != size();
        }
        throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
    }

    @SuppressWarnings("unchecked")
    public void addMixed(@NotNull Collection<Object> c) {
        for (Object o : c) {
            if (o instanceof Store<?> store) {
                if (isInstanceOfE(store.getElement())) {
                    E e = (E) store.getElement();
                    if (store.getKeys().isEmpty()) {
                        if (store.getIndex() == null) {
                            add(e);
                        } else {
                            add(store.getIndex(), e);
                        }
                    } else {
                        if (store.getIndex() == null) {
                            put(store.getKey(), e);
                        } else {
                            put(store.getKey(), store.getIndex(), e);
                        }
                    }
                }
            } else {
                if (isInstanceOfE(o)) {
                    add((E) o);
                }
            }
        }
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        if (c.isEmpty()) return false;
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        if (c.isEmpty()) return false;
        AtomicInteger index2 = new AtomicInteger(index);
        c.forEach(e -> {
            add(index2.getAndIncrement(), e);
        });
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        AtomicBoolean changed = new AtomicBoolean(false);
        this.forEach(e -> {
            if (!c.contains(e)) {
                remove(e);
                changed.set(true);
            }
        });
        return changed.get();
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        for (int i=0; i<this.size(); i++) {
            E replacement = operator.apply(this.get(stores.index(i)));
            if (!isAllowNull() && replacement == null) {
                throw new NullPointerException("Object provided to Storage must not be null while allowNull is false.");
            }
            this.set(i, replacement);
        }
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        AtomicReference<Boolean> modified = new AtomicReference<>(false);
        c.forEach(object -> {
            if (remove(object)) {
                modified.set(true);
            }
        });
        return modified.get();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        AtomicReference<Boolean> modified = new AtomicReference<>(false);
        this.forEach(e -> {
            if (filter.test(e)) {
                if (remove(e)) {
                    modified.set(true);
                }
            }
        });
        return modified.get();
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) return false;
        this.remove(index);
        return true;
    }

    @Override
    public E remove(int index) {
        Store<E> ret = stores.remove(index);
        if (ret != null) {
            return ret.getElement();
        }
        return null;
    }

    @Override
    public void clear() {
        stores.clear();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return toList(true).toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return toList(true).toArray(a);
    }

    public List<E> toList(boolean fill) {
        ArrayList<E> elements = new ArrayList<>();
        stores.toList(fill).forEach(store -> {
            if (fill && (store == null || store.getElement() == null)) {
                elements.add(null);
            } else {
                elements.add(store.getElement());
            }
        });
        return elements;
    }

    /**
     * Performs the given action for each key-value pair of the stores
     * until all key-value pairs have been processed or the action throws an
     * exception.
     *
     * @param action The action to be performed for each key-value pair.
     * @throws NullPointerException if the specified action is null
     */
    public void forEach(BiConsumer<String, E> action) {
        Objects.requireNonNull(action);
        stores.forEach((value) -> {
            action.accept(value.getKey(), value.getElement());
        });
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Itr<>(this);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<E> listIterator(int index) {
        return new ListItr<>(this, index);
    }

    @NotNull
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        return new SubList<>(this, fromIndex, toIndex);
    }

    private static class Itr<E> implements Iterator<E> {
        protected Storage<E> parent;
        protected List<E> root;
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        // prevent creating a synthetic constructor
        Itr(Storage<E> storage) {
            this.parent = storage;
            this.root = storage;
        }

        Itr(Storage<E> storage, List<E> root) {
            this.parent = storage;
            this.root = root;
        }

        public boolean hasNext() {
            return cursor != root.size();
        }

        public E next() {
            int i = cursor;
            if (i >= root.size())
                throw new NoSuchElementException();
            cursor = i + 1;
            return root.get(parent.stores.index(lastRet = i));
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                root.remove(parent.stores.index(lastRet));
                cursor = lastRet;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            final int size = root.size();
            int i = cursor;
            if (i < size) {
                for (; i < size; i++)
                    action.accept(root.get(parent.stores.index(i)));
                // update once at end to reduce heap write traffic
                cursor = i;
                lastRet = i - 1;
            }
        }
    }

    private static class ListItr<E> extends Itr<E> implements ListIterator<E> {
        ListItr(Storage<E> storage, int index) {
            super(storage);
            cursor = index;
        }

        ListItr(Storage<E> storage, List<E> root, int index) {
            super(storage, root);
            cursor = index;
        }

        @Override
        public boolean hasPrevious() {
            return cursor != 0;
        }

        @Override
        public int nextIndex() {
            return cursor;
        }

        @Override
        public int previousIndex() {
            return cursor - 1;
        }

        @Override
        public E previous() {
            int i = cursor - 1;
            if (i < 0)
                throw new NoSuchElementException();
            if (i >= root.size())
                throw new ConcurrentModificationException();
            cursor = i;
            return root.get(parent.stores.index(lastRet = i));
        }

        @Override
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                root.set(parent.stores.index(lastRet), e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            try {
                int i = cursor;
                root.add(parent.stores.index(i), e);
                cursor = i + 1;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static class SubList<E> extends AbstractList<E> {
        private List<E> root;
        private Storage<E> parent;
        private final int offset;
        private int size;

        public SubList(Storage<E> storage, int fromIndex, int toIndex) {
            this.root = storage;
            this.parent = storage;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }
        public SubList(Storage<E> storage, List<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = storage;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }

        @Override
        public E get(int index) {
            return root.get(index+offset);
        }

        @Override
        public int size() {
            return this.size;
        }

        @Override
        public E set(int index, E element) {
            int oldSize = root.size();
            E ret = root.set(index+offset, element);
            if (oldSize < root.size()) {
                this.size++;
            }
            return ret;
        }

        @Override
        public void add(int index, E element) {
            int oldSize = root.size();
            root.add(index+offset, element);
            if (oldSize < root.size()) {
                this.size++;
            }
        }

        @Override
        public E remove(int index) {
            int oldSize = root.size();
            E ret = super.remove(index);
            if (oldSize > root.size()) {
                this.size--;
            }
            return ret;
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return new Itr<>(this.parent, this);
        }

        @NotNull
        @Override
        public ListIterator<E> listIterator() {
            return this.listIterator(0);
        }

        @NotNull
        @Override
        public ListIterator<E> listIterator(int index) {
            return new ListItr<>(this.parent, this, index);
        }

        @NotNull
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return new SubList<>(this.parent, this, fromIndex, toIndex);
        }
    }
}
