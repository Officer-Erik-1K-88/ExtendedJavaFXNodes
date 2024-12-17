package com.airent.extendedjavafxnodes.utils;

import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValueBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SequencedCollection;
import java.util.SequencedSet;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class ObservableCollection<E> extends ObjectPropertyBase<Collection<E>> implements Iterable<E> {
    private final Observe observable;

    private final Object bean;
    private final String name;
    private final boolean set;
    private ObserveSet observeSet;

    public ObservableCollection(Object bean, String name) {
        this(bean, name, new ArrayList<>());
    }

    public ObservableCollection(Object bean, String name, Collection<E> actual) {
        super();
        this.bean = bean;
        this.name = name;
        this.observable = new Observe(actual);
        this.set = actual instanceof Set;

        super.bind(this.observable);
    }

    @SuppressWarnings("unchecked")
    public boolean isInstanceOfE(Object obj) {
        try {
            E obj1 = (E) obj;
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean isSet() {
        return set;
    }

    /**
     * Gets the Collection that is the reference to the collection that is being listened to.
     * Changes to this list will be reflected in the Collection to be observed.
     * <br><br>
     * It is recommended to only use this method if {@link #isSet()} is false.
     * Otherwise, (if {@link #isSet()} is true) this method will be a SequencedSet.
     *
     * @return The Collection that is the reference to a Collection.
     */
    public Set<E> getSet() {
        if (observeSet == null) {
            if (isSet()) {
                observeSet = new ObserveSet(this.observable);
            } else {
                observeSet = new ObserveSequencedSet(this.observable);
            }
        }
        return observeSet;
    }

    public Collection<E> getObservable() {
        return observable;
    }

    /**
     * Get the list that is the reference to the collection that is being listened to.
     * Changes to this list will be reflected in the Collection to be observed.
     *
     * @return The List that is the reference to a Collection.
     */
    public List<E> getList() {
        return observable.list;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    public int size() {
        return observable.size();
    }

    public boolean isEmpty() {
        return observable.isEmpty();
    }

    public boolean contains(Object o) {
        return observable.contains(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return observable.list.list.getSet().iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        observable.list.list.getSet().forEach(action);
    }

    @NotNull
    public Object[] toArray() {
        return observable.list.list.getSet().toArray();
    }

    @NotNull
    public <T> T[] toArray(@NotNull T[] a) {
        return observable.list.list.getSet().toArray(a);
    }

    public <T> T[] toArray(IntFunction<T[]> generator) {
        return observable.list.list.getSet().toArray(generator);
    }

    @SuppressWarnings("unchecked")
    public boolean add(Object e) {
        if (isInstanceOfE(e)) {
            return observable.add((E) e);
        }
        return false;
    }

    public boolean remove(Object o) {
        return observable.remove(o);
    }

    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(observable).containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends E> c) {
        return observable.addAll(c);
    }

    public boolean removeAll(@NotNull Collection<?> c) {
        return observable.removeAll(c);
    }

    public boolean removeIf(Predicate<? super E> filter) {
        return observable.removeIf(filter);
    }

    public boolean retainAll(@NotNull Collection<?> c) {
        return observable.retainAll(c);
    }

    public void clear() {
        observable.clear();
    }

    @SuppressWarnings("unchecked")
    public Object set(int index, Object value) {
        if (isInstanceOfE(value)) {
            return observable.list.set(index, (E) value);
        }
        throw new ClassCastException("Not allowed type.");
    }

    @SuppressWarnings("unchecked")
    public void add(int index, Object element) {
        if (isInstanceOfE(element)) {
            observable.list.add(index, (E) element);
        }
        throw new ClassCastException("Not allowed type.");
    }

    private class Observe extends ObserveValue<Collection<E>> implements ObservableList<E> {

        private final ObserveList list;

        public Observe(Collection<E> c) {
            list = new ObserveList(new ToList<>(c));
        }

        // Observation
        @Override
        public Collection<E> get() {
            return list;
        }

        @Override
        public Collection<E> getValue() {
            return get();
        }

        @Override
        public void addListener(ListChangeListener<? super E> listener) {
            list.addListener(listener);
        }

        @Override
        public void removeListener(ListChangeListener<? super E> listener) {
            list.removeListener(listener);
        }

        @Override
        public int size() {
            return list.size();
        }

        // checking
        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return list.containsAll(c);
        }

        // getting
        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        // changing
        @Override
        public E set(int index, E element) {
            return list.set(index, element);
        }

        @SafeVarargs
        @Override
        public final boolean setAll(E... elements) {
            return list.setAll(elements);
        }

        @Override
        public boolean setAll(Collection<? extends E> col) {
            return list.setAll(col);
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            list.replaceAll(operator);
        }

        // adding
        @Override
        public void add(int index, E element) {
            list.add(index, element);
        }

        @Override
        public boolean add(E e) {
            return list.add(e);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends E> c) {
            return list.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NotNull Collection<? extends E> c) {
            return list.addAll(index, c);
        }

        @SafeVarargs
        @Override
        public final boolean addAll(E... elements) {
            return list.addAll(elements);
        }

        // removal
        @SafeVarargs
        @Override
        public final boolean retainAll(E... elements) {
            return list.retainAll(elements);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return list.retainAll(c);
        }

        @Override
        public E remove(int index) {
            return list.remove(index);
        }

        @Override
        public boolean remove(Object o) {
            return list.remove(o);
        }

        @Override
        public void remove(int from, int to) {
            list.remove(from, to);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return list.removeAll(c);
        }

        @SafeVarargs
        @Override
        public final boolean removeAll(E... elements) {
            return list.removeAll(elements);
        }

        @Override
        public E removeFirst() {
            return list.removeFirst();
        }

        @Override
        public E removeLast() {
            return list.removeLast();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            return list.removeIf(filter);
        }

        @Override
        public void clear() {
            list.clear();
        }

        // order
        @Override
        public void sort(Comparator<? super E> c) {
            list.sort(c);
        }

        @Override
        public List<E> reversed() {
            return list.reversed();
        }

        @Override
        public FilteredList<E> filtered(Predicate<E> predicate) {
            return list.filtered(predicate);
        }

        @Override
        public SortedList<E> sorted(Comparator<E> comparator) {
            return list.sorted(comparator);
        }

        @Override
        public SortedList<E> sorted() {
            return list.sorted();
        }

        // iteration
        @NotNull
        @Override
        public Object[] toArray() {
            return list.toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return list.toArray(a);
        }

        @Override
        public <T> T[] toArray(IntFunction<T[]> generator) {
            return list.toArray(generator);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            list.forEach(action);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return list.iterator();
        }

        @NotNull
        @Override
        public ListIterator<E> listIterator() {
            return list.listIterator();
        }

        @NotNull
        @Override
        public ListIterator<E> listIterator(int index) {
            return list.listIterator(index);
        }

        @NotNull
        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return list.subList(fromIndex, toIndex);
        }

        @Override
        public Spliterator<E> spliterator() {
            return list.spliterator();
        }

        @Override
        public Stream<E> stream() {
            return list.stream();
        }

        @Override
        public Stream<E> parallelStream() {
            return list.parallelStream();
        }
    }

    private class ObserveList extends ModifiableObservableListBase<E> implements List<E> {

        private final ToList<E> list;

        public ObserveList(ToList<E> list) {
            this.list = list;
        }

        @Override
        public E get(int index) {
            return list.get(index);
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        protected void doAdd(int index, E element) {
            list.add(index, element);
        }

        @Override
        protected E doSet(int index, E element) {
            return list.set(index, element);
        }

        @Override
        protected E doRemove(int index) {
            return list.remove(index);
        }
    }

    public static class ToList<E> extends AbstractList<E> implements List<E> {
        private final Collection<E> set;
        private final List<E> list;

        public ToList(Collection<E> set) {
            this.set = set;
            if (this.set instanceof List) {
                this.list = (List<E>) this.set;
            } else {
                this.list = new ArrayList<>(this.set);
            }
        }

        public Collection<E> getSet() {
            return set;
        }

        public List<E> getList() {
            return list;
        }

        @Override
        public E get(int index) {
            if (list.size() < index && index < set.size()) {
                throw new RuntimeException("The operation of adding what list doesn't have but set does is not implemented.");
            }
            if (index > set.size()) {
                throw new IndexOutOfBoundsException("The index of "+index+" is out of range of "+ (set.size()-1));
            }
            E ret = list.get(index);
            while (!set.contains(ret)) {
                list.remove(index);
                ret = list.get(index);
            }
            return ret;
        }

        @Override
        public int size() {
            return this.list.size();
        }

        @Nullable
        private E change(int index, E element, boolean set) {
            if (!(this.set instanceof List)) {
                if (this.set instanceof SequencedCollection<E> sc) {
                    if (index == 0) {
                        if (set) {
                            sc.removeFirst();
                        }
                        sc.addFirst(element);
                    } else if (index == size()-1) {
                        if (set) {
                            sc.removeLast();
                        }
                        sc.addLast(element);
                    } else {
                        ArrayList<E> removed = new ArrayList<>();
                        for (int i=0; i<=index; i++) {
                            removed.add(sc.removeFirst());
                        }
                        sc.addFirst(element);
                        int rSize = removed.size();
                        if (set) rSize -= 1;
                        for (int i=rSize-1; i >= 0; i--) {
                            sc.addFirst(removed.get(i));
                        }
                    }
                } else {
                    if (set) {
                        this.set.remove(get(index));
                    }
                    this.set.add(element);
                }
            }
            if (set) {
                return list.set(index, element);
            } else {
                list.add(index, element);
                return null;
            }
        }

        @Override
        public E set(int index, E element) {
            return change(index, element, true);
        }

        @Override
        public void add(int index, E element) {
            change(index, element, false);
        }

        @Override
        public E remove(int index) {
            E ret = list.remove(index);
            set.remove(ret);
            return ret;
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }
    }

    private class ObserveSequencedSet extends ObserveSet implements SequencedSet<E> {
        private final List<E> observable;

        public ObserveSequencedSet(Observe observable) {
            super(observable);
            this.observable = observable;
        }

        private ObserveSequencedSet(List<E> observable, Observe observe) {
            super(observe);
            this.observable = observable;
        }

        @Override
        public SequencedSet<E> reversed() {
            return new ObserveSequencedSet(observable.reversed(), observe);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return observable.iterator();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return observable.toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return observable.toArray(a);
        }
    }

    private class ObserveSet implements Set<E> {
        protected final Observe observe;

        public ObserveSet(Observe observe) {
            this.observe = observe;
        }

        @Override
        public int size() {
            return observe.size();
        }

        @Override
        public boolean isEmpty() {
            return observe.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return observe.contains(o);
        }

        @NotNull
        @Override
        public Iterator<E> iterator() {
            return observe.list.list.getSet().iterator();
        }

        @NotNull
        @Override
        public Object[] toArray() {
            return observe.list.list.getSet().toArray();
        }

        @NotNull
        @Override
        public <T> T[] toArray(@NotNull T[] a) {
            return observe.list.list.getSet().toArray(a);
        }

        @Override
        public boolean add(E e) {
            return observe.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return observe.remove(o);
        }

        @Override
        public boolean containsAll(@NotNull Collection<?> c) {
            return new HashSet<>(observe).containsAll(c);
        }

        @Override
        public boolean addAll(@NotNull Collection<? extends E> c) {
            return observe.addAll(c);
        }

        @Override
        public boolean retainAll(@NotNull Collection<?> c) {
            return observe.retainAll(c);
        }

        @Override
        public boolean removeAll(@NotNull Collection<?> c) {
            return observe.removeAll(c);
        }

        @Override
        public void clear() {
            observe.clear();
        }
    }

    public static abstract class ObserveValue<V> extends ObservableValueBase<V> implements ObservableObjectValue<V> {}
}
