package com.airent.extendedjavafxnodes.gaxml.javascript;

import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.ExternalArrayData;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.Undefined;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class BasedScope extends ScriptableObject implements Serializable {
    private final BaseScope baseScope;
    private final ScopeActual scopeActual;

    public BasedScope() {
        this(null);
    }

    public BasedScope(Script script) {
        setScript(script);
        baseScope = new BaseScope();
        scopeActual = new ScopeActual(baseScope, null);
        initBase();
    }

    @Override
    public String getClassName() {
        return "BasedScope";
    }

    @Override
    public int size() {
        return Math.max(scopeActual.size(), baseScope.size());
    }

    @Override
    public boolean isEmpty() {
        return scopeActual.isEmpty() && baseScope.isEmpty();
    }

    @Override
    public String getTypeOf() {
        return scopeActual.getTypeOf();
    }

    @Override
    public Object get(String s, Scriptable scriptable) {
        if (baseScope.has(s, scriptable)) {
            return baseScope.get(s, scriptable);
        }
        checkIsScopeOnly(s);
        return scopeActual.get(s, scriptable);
    }

    @Override
    public Object get(int i, Scriptable scriptable) {
        if (baseScope.has(i, scriptable)) {
            return baseScope.get(i, scriptable);
        }
        checkIsScopeOnly(i);
        return scopeActual.get(i, scriptable);
    }

    @Override
    public Object get(Symbol symbol, Scriptable scriptable) {
        if (baseScope.has(symbol, scriptable)) {
            return baseScope.get(symbol, scriptable);
        }
        checkIsScopeOnly(symbol);
        return scopeActual.get(symbol, scriptable);
    }

    @Override
    public boolean has(String s, Scriptable scriptable) {
        if (baseScope.has(s, scriptable)) {
            return true;
        }
        checkIsScopeOnly(s);
        return scopeActual.has(s, scriptable);
    }

    @Override
    public boolean has(int i, Scriptable scriptable) {
        if (baseScope.has(i, scriptable)) {
            return true;
        }
        checkIsScopeOnly(i);
        return scopeActual.has(i, scriptable);
    }

    @Override
    public boolean has(Symbol symbol, Scriptable scriptable) {
        if (baseScope.has(symbol, scriptable)) {
            return true;
        }
        checkIsScopeOnly(symbol);
        return scopeActual.has(symbol, scriptable);
    }

    @Override
    public boolean hasInstance(Scriptable scriptable) {
        if (baseScope.hasInstance(scriptable)) {
            return true;
        }
        return scopeActual.hasInstance(scriptable);
    }

    @Override
    public boolean isConst(String s) {
        if (baseScope.isConst(s)) {
            return true;
        }
        checkIsScopeOnly(s);
        return scopeActual.isConst(s);
    }

    public static Object[] getListed(Object[]... objects) {
        if (objects == null || objects.length == 0) {
            return new Object[0];
        }
        if (objects.length == 1) {
            return objects[0];
        }

        List<Object> combined = new ArrayList<>();
        for (Object[] oa : objects) {
            for (Object o : oa) {
                if (!combined.contains(o)) {
                    combined.add(o);
                }
            }
        }
        return combined.toArray();
    }

    @Override
    public Object[] getIds() {
        return getListed(baseScope.getIds(), scopeActual.getIds());
    }

    @Override
    public Object[] getAllIds() {
        return getListed(baseScope.getAllIds(), scopeActual.getAllIds());
    }

    @Override
    public int getAttributes(String name) {
        try {
            return baseScope.getAttributes(name);
        } catch (EvaluatorException e2) {
            checkIsScopeOnly(name);
            return scopeActual.getAttributes(name);
        }
    }

    @Override
    public int getAttributes(int index) {
        try {
            return baseScope.getAttributes(index);
        } catch (EvaluatorException e2) {
            checkIsScopeOnly(index);
            return scopeActual.getAttributes(index);
        }
    }

    @Override
    public int getAttributes(Symbol sym) {
        try {
            return baseScope.getAttributes(sym);
        } catch (EvaluatorException e2) {
            checkIsScopeOnly(sym);
            return scopeActual.getAttributes(sym);
        }
    }

    @Override
    public Object getGetterOrSetter(String name, int index, Scriptable scope, boolean isSetter) {
        Object value = baseScope.getGetterOrSetter(name, index, scope, isSetter);
        if (value == null || value == Undefined.instance) {
            checkIsScopeOnly(name);
            value = scopeActual.getGetterOrSetter(name, index, scope, isSetter);
        }
        return value;
    }

    // overriding so to not set directly to BasedScope.

    @Override
    public void put(String name, Scriptable start, Object value) {
        scopeActual.put(name, start, value);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        scopeActual.put(index, start, value);
    }

    @Override
    public void put(Symbol key, Scriptable start, Object value) {
        scopeActual.put(key, start, value);
    }

    @Override
    public void delete(String name) {
        scopeActual.delete(name);
    }

    @Override
    public void delete(int index) {
        scopeActual.delete(index);
    }

    @Override
    public void delete(Symbol key) {
        scopeActual.delete(key);
    }

    @Override
    public void putConst(String name, Scriptable start, Object value) {
        scopeActual.putConst(name, start, value);
    }

    @Override
    public void defineConst(String name, Scriptable start) {
        scopeActual.defineConst(name, start);
    }

    @Override
    @Deprecated
    public void setAttributes(int index, Scriptable start, int attributes) {
        scopeActual.setAttributes(index, start, attributes);
    }

    @Override
    public void setAttributes(String name, int attributes) {
        scopeActual.setAttributes(name, attributes);
    }

    @Override
    public void setAttributes(int index, int attributes) {
        scopeActual.setAttributes(index, attributes);
    }

    @Override
    public void setAttributes(Symbol key, int attributes) {
        scopeActual.setAttributes(key, attributes);
    }

    @Override
    public void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter) {
        scopeActual.setGetterOrSetter(name, index, getterOrSetter, isSetter);
    }

    @Override
    @Deprecated
    public Object getGetterOrSetter(String name, int index, boolean isSetter) {
        return scopeActual.getGetterOrSetter(name, index, isSetter);
    }

    @Override
    public void setExternalArrayData(ExternalArrayData array) {
        scopeActual.setExternalArrayData(array);
    }

    @Override
    public ExternalArrayData getExternalArrayData() {
        return scopeActual.getExternalArrayData();
    }

    @Override
    public Object getExternalArrayLength() {
        return scopeActual.getExternalArrayLength();
    }

    @Override
    public Scriptable getPrototype() {
        return scopeActual.getPrototype();
    }

    @Override
    public void setPrototype(Scriptable m) {
        scopeActual.setPrototype(m);
    }

    @Override
    public Scriptable getParentScope() {
        return scopeActual.getParentScope();
    }

    @Override
    public void setParentScope(Scriptable m) {
        scopeActual.setParentScope(m);
    }

    @Override
    public Object getDefaultValue(Class<?> typeHint) {
        return scopeActual.getDefaultValue(typeHint);
    }

    @Override
    public boolean avoidObjectDetection() {
        return scopeActual.avoidObjectDetection();
    }

    @Override
    public void defineProperty(String propertyName, Object value, int attributes) {
        scopeActual.defineProperty(propertyName, value, attributes);
    }

    @Override
    public void defineProperty(Symbol key, Object value, int attributes) {
        scopeActual.defineProperty(key, value, attributes);
    }

    @Override
    public void defineProperty(String propertyName, Class<?> clazz, int attributes) {
        scopeActual.defineProperty(propertyName, clazz, attributes);
    }

    @Override
    public void defineProperty(String propertyName, Object delegateTo, Method getter, Method setter, int attributes) {
        scopeActual.defineProperty(propertyName, delegateTo, getter, setter, attributes);
    }

    @Override
    public void defineOwnProperties(Context cx, ScriptableObject props) {
        scopeActual.defineOwnProperties(cx, props);
    }

    @Override
    public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
        scopeActual.defineOwnProperty(cx, id, desc);
    }

    @Override
    public void defineProperty(String name, Supplier<Object> getter, Consumer<Object> setter, int attributes) {
        scopeActual.defineProperty(name, getter, setter, attributes);
    }

    @Override
    public void defineFunctionProperties(String[] names, Class<?> clazz, int attributes) {
        scopeActual.defineFunctionProperties(names, clazz, attributes);
    }

    @Override
    public boolean isExtensible() {
        return scopeActual.isExtensible();
    }

    @Override
    public void preventExtensions() {
        scopeActual.preventExtensions();
    }

    @Override
    public void sealObject() {
        scopeActual.sealObject();
    }

    // initialization
    private void initBase() {
        scopeActual.put("BaseScope", scopeActual, this.baseScope);

        Scriptable par = this.baseScope.getParentScope();
        this.baseScope.sps(null);

        init(this.baseScope);

        this.baseScope.sps(par);

        //TODO: Add in the following line when Rhino JS allows for sealing objects.
        //this.baseScope.sealObject();
    }

    protected abstract void init(ScriptableObject baseScope);

    // the BaseScope holder, holds the true, unchangeable, scope information.
    protected static class BaseScope extends ScriptableObject {
        public BaseScope() {
            this(null, null);
        }
        public BaseScope(Scriptable scope, Scriptable prototype) {
            super();
            try(Context cx = Context.enter()) {
                cx.initStandardObjects(this, false);
            }
            if (scope != null) {
                super.setParentScope(scope);
            }
            if (prototype != null) {
                super.setPrototype(prototype);
            }
        }

        @Override
        public String getClassName() {
            return "BaseScope";
        }

        @Override
        public void setParentScope(Scriptable m) {
            throw new RuntimeException("Cannot set the parent scope of a BaseScope.");
        }

        void sps(Scriptable m) {
            super.setParentScope(m);
        }
    }

    // ScopeOnly (Makes to so that we can only get objects through the call of Scope.)
    private final List<Object> ScopeOnly = new ArrayList<>();

    private void checkIsScopeOnly(Object o) {
        if (ScopeOnly.contains(o)) {
            throw new RuntimeException("The object tied to `"+o.toString()+"` is only accessible via call to Scope");
        }
    }

    public void addScopeOnly(Object o) {
        if (!ScopeOnly.contains(o)) {
            ScopeOnly.add(o);
        }
    }

    public void addScopeOnly(String name) {
        if (!ScopeOnly.contains(name)) {
            ScopeOnly.add(name);
        }
    }
    public void addScopeOnly(int index) {
        if (!ScopeOnly.contains(index)) {
            ScopeOnly.add(index);
        }
    }
    public void addScopeOnly(Symbol key) {
        if (!ScopeOnly.contains(key)) {
            ScopeOnly.add(key);
        }
    }

    public void putScopeOnly(String name, Scriptable start, Object value) {
        scopeActual.put(name, start, value);
        addScopeOnly(name);
    }

    public void putScopeOnly(int index, Scriptable start, Object value) {
        scopeActual.put(index, start, value);
        addScopeOnly(index);
    }

    public void putScopeOnly(Symbol key, Scriptable start, Object value) {
        scopeActual.put(key, start, value);
        addScopeOnly(key);
    }

    public ScriptableObject getScopeActual() {
        return scopeActual;
    }

    public boolean isScope(Scriptable scriptable) {
        return (scriptable == this) || (scriptable == scopeActual);
    }

    private static class ScopeActual extends ScriptableObject {

        public ScopeActual() {
            super();
        }

        public ScopeActual(Scriptable scope, Scriptable prototype) {
            super(scope, prototype);
        }

        @Override
        public String getClassName() {
            return "ScopeActual";
        }
    }

    // Script
    private Script script;

    public final Script getScript() {
        return script;
    }

    void setScript(Script script) {
        this.script = script;
    }

    // serialization
    @Serial
    private static final long serialVersionUID = 44323454323332L;

    @Serial
    private void writeObject(@NotNull java.io.ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();

        stream.writeObject(baseScope);
        stream.writeObject(scopeActual);
        stream.writeObject(ScopeOnly);
    }

    @Serial
    private void readObject(@NotNull java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();

        stream.readObject(); // baseScope
        stream.readObject(); // scopeActual
        Object so = stream.readObject(); // ScopeOnly
        if (so instanceof List<?> sol) {
            sol.forEach(this::addScopeOnly);
        }
    }
}
