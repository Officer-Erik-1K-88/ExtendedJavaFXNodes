package com.airent.extendedjavafxnodes.gaxml.javascript;

import com.airent.extendedjavafxnodes.utils.Pair;
import com.airent.extendedjavafxnodes.utils.Storage;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.IdScriptableObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public abstract class NativeObject extends IdScriptableObject {

    private static int noCount = 0;

    @NotNull
    public static NativeObject init(Scriptable scope, boolean sealed, @NotNull Class<? extends NativeObject> nativeClass) {
        return init(scope, sealed, nativeClass, new Object[0]);
    }

    @NotNull
    public static NativeObject init(Scriptable scope, boolean sealed, @NotNull Class<? extends NativeObject> nativeClass, Object[] toStore) {
        if (nativeClass == NativeObject.class) {
            throw new RuntimeException("Cannot initialize the base class of NativeObject.");
        }
        if (toStore == null) {
            throw new NullPointerException("The array of objects to store in this NativeObject must not be null.");
        }
        try {
            return nativeClass.getConstructor(Scriptable.class, Boolean.class, Object[].class).newInstance(scope, sealed, toStore);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final String TAG;

    private final Storage<Object> storage = new Storage<>();

    public Storage<Object> getStorage() {
        return storage;
    }

    private final Storage<Pair<
            Integer,
            BiFunction<
                    Pair<Context, Scriptable>,
                    Pair<Scriptable, Object[]>,
                    Object
                    >
            >> items = new Storage<>(false, 1);
    private int maxArity = 0;

    private void addItem(String name, int arity, BiFunction<
            Pair<Context, Scriptable>,
            Pair<Scriptable, Object[]>,
            Object
            > action) {
        items.put(name, new Pair<>(arity, action));
        if (arity > maxArity) {
            maxArity = arity;
        }
    }
    private int getItemId(String name) {
        Integer id = items.getLiteralIndex(name);
        if (id == null) {
            id = 0;
        }
        return id;
    }
    private Pair<
            Integer,
            BiFunction<
                    Pair<Context, Scriptable>,
                    Pair<Scriptable, Object[]>,
                    Object
                    >
            > getItem(int id) {
        if (id < items.getIndexDifference() || id > items.indexDiff(items.size())) {
            throw new IllegalStateException(String.valueOf(id));
        }
        return items.get(id);
    }
    protected final class ItemConstructor {
        private ItemConstructor() {}

        public boolean hasItem(String name) {
            return NativeObject.this.items.containsKey(name);
        }
        public boolean hasItem(int id) {
            return NativeObject.this.items.indexHasKey(id);
        }

        /**
         * This method will add functions to the NativeObject being initialized.
         * <br><br>
         * The action provides two Pair values,
         * this first Pair has a key value of {@link Context}
         * and the value is of {@link Scriptable} (The scope).
         * And the second Pair has a key value of {@link Scriptable} (The function)
         * and the value is of {@link Object} {@link java.lang.reflect.Array Array}
         * (The properties).
         * The object returned by action must be null for no return.
         *
         * @param name The name of the function.
         * @param arity The number of properties that can be satisfied.
         * @param action The action to preform on call to this function.
         */
        public void addItem(String name, int arity, BiFunction<
                Pair<Context, Scriptable>,
                Pair<Scriptable, Object[]>,
                Object
                > action) {
            NativeObject.this.addItem(name, arity, action);
        }

        public void addID(int id, int arity, BiFunction<
                Pair<Context, Scriptable>,
                Pair<Scriptable, Object[]>,
                Object
                > action) {
            if (hasItem(id)) {
                throw new RuntimeException("Cannot override ids that already exist.");
            }

        }

        /**
         * Gets the id of a function by it's name.
         * The first function has an id of 1.
         *
         * @param name The name of the function to get the id of.
         * @return The id of the function.
         * @see #getItem(int)
         */
        public int getItemId(String name) {
            return NativeObject.this.getItemId(name);
        }

        /**
         * Gets the stored data of a function by it's id
         * where the first function of this NativeObject
         * has an id of 1.
         *
         * @param id The id of the function to get the stored data of.
         * @return The stored data of the function.
         * @see #getItemId(String)
         */
        public Pair<
                Integer,
                BiFunction<
                        Pair<Context, Scriptable>,
                        Pair<Scriptable, Object[]>,
                        Object
                        >
                > getItem(int id) {
            return NativeObject.this.getItem(id);
        }
    }

    public NativeObject() {
        this(null, true);
    }

    public NativeObject(Scriptable scope) {
        this(scope, true);
    }

    public NativeObject(Scriptable scope, Boolean sealed) {
        this(scope, sealed, new Object[0]);
    }

    public NativeObject(Scriptable scope, Boolean sealed, Object[] toStore) {
        this("NativeObject"+noCount, scope, sealed, toStore);
    }

    public NativeObject(String tag, Scriptable scope, Boolean sealed, Object[] toStore) {
        if (toStore == null) {
            throw new NullPointerException("The array of objects to store in this NativeObject must not be null.");
        }
        if (this.getClassName() == null) {
            this.TAG = tag;
        } else {
            this.TAG = this.getClassName();
        }
        noCount++;
        addItem("toSource", 0, (contextScriptablePair, scriptablePair) -> NativeObject.this.TAG);

        storage.addMixed(Arrays.asList(storeObjects(toStore)));
        init(new ItemConstructor());

        if (sealed) {
            sealObject();
        }
        updateScope(scope);
    }

    /**
     * Return the name of the class.
     * This is typically the same name as the constructor.
     * Classes extending ScriptableObject must implement this abstract method.
     * <br><br>
     * This value is used as the value for {@link #thisPropertyName()} by default.
     *
     * @return The name of this class.
     */
    @Override
    public String getClassName() {
        return this.TAG;
    }

    /**
     * Gets the recognized name of this NativeObject.
     * <br><br>
     * By default, this value is the same as {@link #getClassName()}.
     *
     * @return The name that this NativeObject takes as its property name.
     */
    public String thisPropertyName() {
        return getClassName();
    }

    protected void updateScope(Scriptable scope) {
        if (scope == null) return ;
        if (getParentScope() != null) {
            ScriptableObject.deleteProperty(getParentScope(), thisPropertyName());
        }
        setPrototype(getObjectPrototype(scope));
        setParentScope(scope);
        activatePrototypeMap(items.size());
        ScriptableObject.defineProperty(scope, thisPropertyName(), this, this.maxArity);
    }

    protected abstract void init(ItemConstructor itemConstructor);

    /**
     * Handles the storage of objects on construction of a
     * class extending {@code NativeObject}.
     * <br><br>
     * By default, this method loops through all objects provided.
     * If the object is an instance of {@link Pair} and the key value
     * is an instance of a String, then the object will be turned into
     * a {@link Storage.Store} object where the pair's value is set as
     * the element.
     * Otherwise, the object remains unchanged.
     *
     * @param objects The objects that are wanting to be added to storage.
     * @return The objects that will be stored.
     */
    protected Object[] storeObjects(@NotNull Object[] objects) {
        List<Object> objectList = new ArrayList<>();
        for (Object o : objects) {
            if (o instanceof Pair<?, ?> pair) {
                if (pair.getKey() instanceof String) {
                    objectList.add(new Storage.Store<>((String) pair.getKey(), pair.getValue()));
                } else {
                    objectList.add(pair);
                }
            } else {
                objectList.add(o);
            }
        }
        return objectList.toArray();
    }

    @Override
    protected int getMaxInstanceId() {
        return items.size();
    }

    @Override
    protected void initPrototypeId(int id) {
        if (id > getMaxInstanceId()) {
            throw new IllegalStateException(String.valueOf(id));
        } else {
            String name;
            int arity;
            Pair<Integer, BiFunction<Pair<Context, Scriptable>, Pair<Scriptable, Object[]>, Object>> item = getItem(id);
            name = items.getKey(id);
            arity = item.getKey();

            this.initPrototypeMethod(TAG, id, name, arity);
        }
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        } else {
            int methodId = f.methodId();

            Object ret = getItem(methodId).getValue().apply(new Pair<>(cx, scope), new Pair<>(thisObj, args));


            if (ret == null) {
                return Undefined.instance;
            }
            return ret;
        }
    }

    @Override
    protected int findPrototypeId(String s) {
        return getItemId(s);
    }
}
