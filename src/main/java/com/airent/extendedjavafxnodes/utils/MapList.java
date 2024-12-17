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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class MapList<E> implements List<E>, Serializable {
    @Serial
    private static final long serialVersionUID = 57467886644356L;

    private final OptionalMap<Integer, E> map;
    private final ArrayList<Integer> indexes;
    private int congruentIndexSize = 0;
    private final boolean indexedAtZero;
    private final int indexDifference;

    public MapList() {
        this(16);
    }

    public MapList(int initialCapacity) {
        this(initialCapacity, true, 0);
    }

    public MapList(boolean indexedAtZero, int indexDifference) {
        this(16, indexedAtZero, indexDifference);
    }

    public MapList(int initialCapacity, boolean indexedAtZero, int indexDifference) {
        this(initialCapacity, indexedAtZero, indexDifference, true);
    }

    public MapList(int initialCapacity, boolean indexedAtZero, int indexDifference, boolean allowNull) {
        this.map = new OptionalMap<>(initialCapacity);
        this.map.setKeyNullable(false);
        this.map.setValueNullable(allowNull);
        this.indexes = new ArrayList<>(initialCapacity);
        this.indexedAtZero = indexedAtZero;
        this.indexDifference = indexDifference;
    }

    public boolean isIndexedAtZero() {
        return indexedAtZero;
    }

    public int getIndexDifference() {
        return indexDifference;
    }

    public boolean isNullable() {
        return this.map.isValueNullable();
    }

    public void setNullable(boolean allowNull) {
        this.map.setValueNullable(allowNull);
    }

    public int getCongruentIndexSize() {
        return congruentIndexSize;
    }

    private void updateCongruentSize(int index) {
        if (index <= indexDiff(congruentIndexSize)) {
            congruentIndexSize++;
        }
    }

    public Integer indexFix(Integer index) {
        if (index == null) return null;
        if (indexedAtZero) {
            return index;
        }
        return index-indexDifference;
    }

    public Integer indexDiff(Integer index) {
        if (index == null) return null;
        if (indexedAtZero) {
            return index;
        }
        return index+indexDifference;
    }

    public int index(int i) {
        return indexes.get(i);
    }

    private int largestIndex() {
        return indexes.getLast();
    }

    private void sortIndexes() {
        indexes.sort(null);
    }

    // list stuff

    @Override
    public int size() {
        return indexes.size();
    }

    @Override
    public boolean isEmpty() {
        return indexes.isEmpty();
    }

    public boolean containsIndex(int index) {
        return map.containsKey(index);
    }

    @Override
    public boolean contains(Object o) {
        return map.containsValue(o);
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

    @Override
    public int indexOf(Object o) {
        for (Integer index : indexes) {
            if (get(index).equals(o)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        for (Integer index : indexes.reversed()) {
            if (get(index).equals(o)) {
                return index;
            }
        }
        return -1;
    }

    @Override
    public E get(int index) {
        return map.get(index);
    }

    @Override
    public E set(int index, E element) {
        if (!indexes.contains(index)) {
            indexes.add(index);
            sortIndexes();
            updateCongruentSize(index);
        }
        return map.put(index, element);
    }



    @Override
    public void add(int index, E element) {
        updateCongruentSize(index);
        int actualIndex = indexFix(index);
        int find = index;
        boolean foundCurr = false;
        E old = null;
        boolean cont = map.containsKey(find);
        while (cont) {
            int indexOf = indexes.indexOf(find);
            if (!foundCurr) {
                actualIndex = indexOf;
                foundCurr = true;
            }
            find = find+1;
            indexes.set(indexOf, find);
            cont = map.containsKey(find);
            if (find-1 != index) {
                old = map.put(find, old);
            } else {
                old = map.put(find, map.get(find-1));
            }
        }
        if (map.containsKey(find-1) && find != index && !map.containsKey(find)) {
            map.put(find, old);
        }
        indexes.add(actualIndex, index);
        map.put(index, element);
        sortIndexes();
    }

    @Override
    public boolean add(E e) {
        add(indexDiff(getCongruentIndexSize()), e);
        return true;
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
        Objects.requireNonNull(c);
        boolean modified = false;
        Iterator<E> it = iterator();
        while (it.hasNext()) {
            if (!c.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
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
        if (index < indexDiff(getCongruentIndexSize())) {
            congruentIndexSize--;
        }
        int find = index;
        E removed = map.remove(index);
        int indexOf = indexes.indexOf(find);
        indexes.remove(indexOf);
        int prevFind = find-1;
        ListIterator<Integer> i = indexes.listIterator(indexOf);
        while (i.hasNext()) {
            if (find-1 == prevFind) {
                i.set(find);
            } else {
                break;
            }
            prevFind = find;
            find = i.next()-1;
        }
        return removed;
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
    public void clear() {
        map.clear();
        indexes.clear();
        congruentIndexSize = 0;
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
        indexes.forEach(integer -> {
            if (fill) {
                while (integer != elements.size()) {
                    elements.add(null);
                }
            }
            elements.add(this.get(integer));
        });
        return elements;
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
        protected MapList<E> parent;
        protected List<E> root;
        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        // prevent creating a synthetic constructor
        Itr(MapList<E> parent) {
            this.parent = parent;
            this.root = parent;
        }

        Itr(MapList<E> parent, List<E> root) {
            this.parent = parent;
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
            return root.get(parent.index(lastRet = i));
        }

        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                root.remove(parent.index(lastRet));
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
                    action.accept(root.get(parent.index(i)));
                // update once at end to reduce heap write traffic
                cursor = i;
                lastRet = i - 1;
            }
        }
    }

    private static class ListItr<E> extends Itr<E> implements ListIterator<E> {
        ListItr(MapList<E> parent, int index) {
            super(parent);
            cursor = index;
        }

        ListItr(MapList<E> parent, List<E> root, int index) {
            super(parent, root);
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
            return root.get(parent.index(lastRet = i));
        }

        @Override
        public void set(E e) {
            if (lastRet < 0)
                throw new IllegalStateException();

            try {
                root.set(parent.index(lastRet), e);
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public void add(E e) {
            try {
                int i = cursor;
                root.add(parent.index(i), e);
                cursor = i + 1;
                lastRet = -1;
            } catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
    }

    private static class SubList<E> extends AbstractList<E> {
        private List<E> root;
        private MapList<E> parent;
        private final int offset;
        private int size;

        public SubList(MapList<E> parent, int fromIndex, int toIndex) {
            this.root = parent;
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
        }
        public SubList(MapList<E> parent, List<E> root, int fromIndex, int toIndex) {
            this.root = root;
            this.parent = parent;
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
