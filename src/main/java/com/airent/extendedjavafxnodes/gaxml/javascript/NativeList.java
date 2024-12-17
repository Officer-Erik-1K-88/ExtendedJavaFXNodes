package com.airent.extendedjavafxnodes.gaxml.javascript;

import com.airent.extendedjavafxnodes.utils.ObservableCollection;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class NativeList extends NativeArray {
    private final ObservableCollection<?> actual;

    public NativeList(@NotNull Collection<?> collection) {
        super(collection.toArray());
        this.actual = new ObservableCollection<>(this, "actual", collection);
    }

    public NativeList(Object[] objects) {
        super(objects);
        ArrayList<Object> list = new ArrayList<>(Arrays.asList(objects));
        this.actual = new ObservableCollection<>(this, "actual", list);
    }

    public NativeList(int initialCapacity) {
        super(initialCapacity);
        ArrayList<Object> list = new ArrayList<>(initialCapacity);
        this.actual = new ObservableCollection<>(this, "actual", list);
    }

    public List<?> getList() {
        return actual.getList();
    }

    public Collection<?> getCollection() {
        return this.actual.getSet();
    }

    @Override
    public int size() {
        if (actual.isEmpty()) return super.size();
        return actual.size();
    }

    @Override
    public boolean isEmpty() {
        return actual.isEmpty() && super.isEmpty();
    }

    @Override
    public boolean has(int index, Scriptable start) {
        if (super.has(index, start)) {
            return true;
        }
        return index >= 0 && index <= this.size()-1;
    }

    @Override
    public boolean contains(Object o) {
        if (actual.isEmpty()) return super.contains(o);
        return actual.contains(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection c) {
        for (Object o : c) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object get(int index, Scriptable start) {
        Object ret = super.get(index, start);
        if (ret == Scriptable.NOT_FOUND) {
            return NativeSystem.ensureSafe(this.get(index));
        }
        return ret;
    }

    @Override
    public Object get(int index) {
        if (index < actual.size()) {
            return getList().get(index);
        } else {
            return super.get(index);
        }
    }

    @Override
    public Object get(long index) {
        if (index < actual.size()) {
            return getList().get((int) index);
        } else {
            return super.get(index);
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        int started = super.size();
        super.put(index, start, value);
        if (super.size() == started+1) {
            actual.set(index, value);
        }
    }

    @Override
    public Object set(int index, Object element) {
        super.put(index, this, element);
        return actual.set(index, element);
    }

    private void addInDenseAt(int index, Scriptable start, Object element) {
        Object old = element;
        Object nuw = Scriptable.NOT_FOUND;

        int currIndex = index;
        while (super.has(currIndex, start)) {
            nuw = old;
            old = super.get(currIndex, start);
            super.put(currIndex, start, nuw);

            currIndex++;
        }
        if (old != Scriptable.NOT_FOUND) {
            super.put(currIndex, start, old);
        }
    }

    @Override
    public void add(int index, Object element) {
        actual.add(index, element);
        addInDenseAt(index, this, element);
    }

    @Override
    public boolean add(Object object) {
        actual.add(object);
        super.put(actual.size(), this, object);
        return true;
    }

    @Override
    public boolean addAll(@NotNull Collection c) {
        boolean modified = false;
        for (Object o : c) {
            add(o);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean addAll(int index, @NotNull Collection c) {
        boolean modified = false;
        for (Object o : c) {
            add(index++, o);
            modified = true;
        }
        return modified;
    }

    @Override
    public boolean retainAll(@NotNull Collection c) {
        boolean changed = false;
        for (Object o : this) {
            if (!c.contains(o)) {
                if (remove(o)) {
                    changed = true;
                }
            }
        }
        return changed;
    }

    @Override
    public void delete(int index) {
        Object ret = this.get(index);
        super.delete(index);
        if (index < actual.size()) {
            actual.remove(index);
        }
    }

    @Override
    public Object remove(int index) {
        Object ret = this.get(index);
        delete(index);
        return ret;
    }

    @Override
    public boolean remove(Object o) {
        int index = indexOf(o);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection c) {
        boolean removed = false;
        for (Object o : c) {
            if (remove(o)) {
                removed = true;
            }
        }
        return removed;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        int ret = getList().indexOf(o);
        if (ret == -1) {
            return super.indexOf(o);
        }
        return ret;
    }

    @Override
    public int lastIndexOf(Object o) {
        int ret = getList().lastIndexOf(o);
        if (ret == -1) {
            return super.lastIndexOf(o);
        }
        return ret;
    }

    @NotNull
    @Override
    public Object[] toArray() {
        if (actual.isEmpty()) return super.toArray();
        return getList().toArray();
    }

    @NotNull
    @Override
    public Object[] toArray(Object[] a) {
        if (actual.isEmpty()) return super.toArray(a);
        return getList().toArray(a);
    }

    @NotNull
    @Override
    public Iterator iterator() {
        return this.listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator listIterator() {
        return this.listIterator(0);
    }

    @NotNull
    @Override
    public ListIterator listIterator(final int start) {
        final int len = this.size();
        if (start >= 0 && start <= len) {
            return new ListIterator() {
                int cursor = start;

                public boolean hasNext() {
                    return this.cursor < len;
                }

                public Object next() {
                    if (this.cursor == len) {
                        throw new NoSuchElementException();
                    } else {
                        return NativeList.this.get(this.cursor++);
                    }
                }

                public boolean hasPrevious() {
                    return this.cursor > 0;
                }

                public Object previous() {
                    if (this.cursor == 0) {
                        throw new NoSuchElementException();
                    } else {
                        return NativeList.this.get(--this.cursor);
                    }
                }

                public int nextIndex() {
                    return this.cursor;
                }

                public int previousIndex() {
                    return this.cursor - 1;
                }

                public void remove() {
                    NativeList.this.remove(this.cursor);
                }

                public void add(Object o) {
                    throw new UnsupportedOperationException();
                }

                public void set(Object o) {
                    NativeList.this.set(this.cursor, o);
                }
            };
        } else {
            throw new IndexOutOfBoundsException("Index: " + start);
        }
    }

    @NotNull
    @Override
    public List subList(final int fromIndex, final int toIndex) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        } else if (toIndex > this.size()) {
            throw new IndexOutOfBoundsException("toIndex = " + toIndex);
        } else if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        } else {
            return new AbstractList() {

                private int size = toIndex - fromIndex;

                private int diffIndex(int index) {
                    return index + fromIndex;
                }

                public Object get(int index) {
                    return NativeList.this.get(diffIndex(index));
                }

                @Override
                public Object set(int index, Object element) {
                    return NativeList.this.set(diffIndex(index), element);
                }

                @Override
                public void add(int index, Object element) {
                    NativeList.this.add(diffIndex(index), element);
                    size++;
                }

                @Override
                public Object remove(int index) {
                    size--;
                    return NativeList.this.remove(diffIndex(index));
                }

                @Override
                public boolean addAll(int index, Collection c) {
                    return NativeList.this.addAll(diffIndex(index), c);
                }

                public int size() {
                    return size;
                }
            };
        }
    }
}
