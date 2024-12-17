package com.airent.extendedjavafxnodes.gaxml.javascript;

import com.airent.extendedjavafxnodes.utils.Pair;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeConsole;
import org.mozilla.javascript.ScriptStackElement;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.Undefined;

import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Scope extends BasedScope {
    private MainScope mainScope;
    private final Consumer<ScriptableObject> preBase;

    public Scope() {
        this((Script) null);
    }

    public Scope(Script script) {
        this(script, null, null, null);
    }

    public Scope(Consumer<ScriptableObject> initBase) {
        this(null, null, initBase);
    }

    public Scope(Scriptable scope, Scriptable prototype) {
        this(scope, prototype, null);
    }

    public Scope(Scriptable scope, Scriptable prototype, Consumer<ScriptableObject> initBase) {
        this(null, scope, prototype, initBase);
    }

    public Scope(Script script, Scriptable scope, Scriptable prototype, Consumer<ScriptableObject> initBase) {
        super(script);
        if (scope != null) {
            super.setParentScope(scope);
        }
        if (prototype != null) {
            super.setPrototype(prototype);
        }
        preBase = initBase;
    }

    @Override
    public String getClassName() {
        return "Scope";
    }

    @Override
    public int size() {
        return Math.max(super.size(), mainScope.size());
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty() && mainScope.isEmpty();
    }

    @Override
    public Object get(String s, Scriptable scriptable) {
        if (s != null && s.equals("Scope") && isScope(scriptable)) {
            return getScopeActual();
        }
        if (mainScope.has(s, scriptable)) {
            return mainScope.get(s, scriptable);
        }
        return super.get(s, scriptable);
    }

    @Override
    public Object get(int i, Scriptable scriptable) {
        if (mainScope.has(i, scriptable)) {
            return mainScope.get(i, scriptable);
        }
        return super.get(i, scriptable);
    }

    @Override
    public Object get(Symbol symbol, Scriptable scriptable) {
        if (mainScope.has(symbol, scriptable)) {
            return mainScope.get(symbol, scriptable);
        }
        return super.get(symbol, scriptable);
    }

    @Override
    public boolean has(String s, Scriptable scriptable) {
        if (s != null && s.equals("Scope") && isScope(scriptable)) {
            return true;
        }
        if (mainScope.has(s, scriptable)) {
            return true;
        }
        return super.has(s, scriptable);
    }

    @Override
    public boolean has(int i, Scriptable scriptable) {
        if (mainScope.has(i, scriptable)) {
            return true;
        }
        return super.has(i, scriptable);
    }

    @Override
    public boolean has(Symbol symbol, Scriptable scriptable) {
        if (mainScope.has(symbol, scriptable)) {
            return true;
        }
        return super.has(symbol, scriptable);
    }

    @Override
    public boolean hasInstance(Scriptable scriptable) {
        if (isScope(scriptable)) {
            return true;
        }
        if (mainScope.hasInstance(scriptable)) {
            return true;
        }
        return super.hasInstance(scriptable);
    }

    @Override
    public void putConst(String s, Scriptable scriptable, Object o) {
        cannotOverride(s);
        if (mainScope.isSealed()) {
            super.putConst(s, scriptable, o);
        } else {
            mainScope.putConst(s, scriptable, o);
        }
    }

    @Override
    public void defineConst(String s, Scriptable scriptable) {
        cannotOverride(s);
        if (mainScope.isSealed()) {
            super.defineConst(s, scriptable);
        } else {
            mainScope.defineConst(s, scriptable);
        }
    }

    @Override
    public boolean isConst(String s) {
        if (mainScope.isConst(s)) {
            return true;
        }
        return super.isConst(s);
    }

    @Override
    public void put(String s, Scriptable scriptable, Object o) {
        cannotOverride(s);
        if (mainScope.isSealed()) {
            super.put(s, scriptable, o);
        } else {
            mainScope.put(s, scriptable, o);
        }
    }

    @Override
    public void put(int i, Scriptable scriptable, Object o) {
        if (mainScope.isSealed()) {
            super.put(i, scriptable, o);
        } else {
            mainScope.put(i, scriptable, o);
        }
    }

    @Override
    public void put(Symbol symbol, Scriptable scriptable, Object o) {
        if (mainScope.isSealed()) {
            super.put(symbol, scriptable, o);
        } else {
            mainScope.put(symbol, scriptable, o);
        }
    }

    @Override
    public void delete(String s) {
        cannotOverride(s, "Cannot delete %1$s.");
        if (mainScope.isSealed()) {
            super.delete(s);
        } else {
            mainScope.delete(s);
        }
    }

    @Override
    public void delete(int i) {
        if (mainScope.isSealed()) {
            super.delete(i);
        } else {
            mainScope.delete(i);
        }
    }

    @Override
    public void delete(Symbol symbol) {
        if (mainScope.isSealed()) {
            super.delete(symbol);
        } else {
            mainScope.delete(symbol);
        }
    }

    @Override
    public Object[] getIds() {
        return getListed(mainScope.getIds(), super.getIds());
    }

    @Override
    public Object[] getAllIds() {
        return getListed(mainScope.getAllIds(), super.getAllIds());
    }

    @Override
    public void defineProperty(String propertyName, Object value, int attributes) {
        cannotOverride(propertyName);
        if (mainScope.isSealed()) {
            super.defineProperty(propertyName, value, attributes);
        } else {
            mainScope.defineProperty(propertyName, value, attributes);
        }
    }

    @Override
    public void defineProperty(Symbol key, Object value, int attributes) {
        if (mainScope.isSealed()) {
            super.defineProperty(key, value, attributes);
        } else {
            mainScope.defineProperty(key, value, attributes);
        }
    }

    @Override
    public void defineProperty(String propertyName, Class<?> clazz, int attributes) {
        cannotOverride(propertyName);
        if (mainScope.isSealed()) {
            super.defineProperty(propertyName, clazz, attributes);
        } else {
            mainScope.defineProperty(propertyName, clazz, attributes);
        }
    }

    @Override
    public void defineProperty(String propertyName, Object delegateTo, Method getter, Method setter, int attributes) {
        cannotOverride(propertyName);
        if (mainScope.isSealed()) {
            super.defineProperty(propertyName, delegateTo, getter, setter, attributes);
        } else {
            mainScope.defineProperty(propertyName, delegateTo, getter, setter, attributes);
        }
    }

    @Override
    public void defineProperty(String name, Supplier<Object> getter, Consumer<Object> setter, int attributes) {
        cannotOverride(name);
        if (mainScope.isSealed()) {
            super.defineProperty(name, getter, setter, attributes);
        } else {
            mainScope.defineProperty(name, getter, setter, attributes);
        }
    }

    @Override
    public void defineOwnProperties(Context cx, ScriptableObject props) {
        if (mainScope.isSealed()) {
            super.defineOwnProperties(cx, props);
        } else {
            mainScope.defineOwnProperties(cx, props);
        }
    }

    @Override
    public void defineOwnProperty(Context cx, Object id, ScriptableObject desc) {
        if (mainScope.isSealed()) {
            super.defineOwnProperty(cx, id, desc);
        } else {
            mainScope.defineOwnProperty(cx, id, desc);
        }
    }

    @Override
    public void defineFunctionProperties(String[] names, Class<?> clazz, int attributes) {
        if (mainScope.isSealed()) {
            super.defineFunctionProperties(names, clazz, attributes);
        } else {
            mainScope.defineFunctionProperties(names, clazz, attributes);
        }
    }

    @Override
    public int getAttributes(String name) {
        try {
            return mainScope.getAttributes(name);
        } catch (EvaluatorException e) {
            return super.getAttributes(name);
        }
    }

    @Override
    public int getAttributes(int index) {
        try {
            return mainScope.getAttributes(index);
        } catch (EvaluatorException e) {
            return super.getAttributes(index);
        }
    }

    @Override
    public int getAttributes(Symbol sym) {
        try {
            return mainScope.getAttributes(sym);
        } catch (EvaluatorException e) {
            return super.getAttributes(sym);
        }
    }

    @Override
    public void setAttributes(String name, int attributes) {
        cannotOverride(name, "Cannot set attributes directly tied to %1$s, only it's properties.");
        if (mainScope.isSealed()) {
            super.setAttributes(name, attributes);
        } else {
            mainScope.setAttributes(name, attributes);
        }
    }

    @Override
    public void setAttributes(int index, int attributes) {
        if (mainScope.isSealed()) {
            super.setAttributes(index, attributes);
        } else {
            mainScope.setAttributes(index, attributes);
        }
    }

    @Override
    public void setAttributes(Symbol key, int attributes) {
        if (mainScope.isSealed()) {
            super.setAttributes(key, attributes);
        } else {
            mainScope.setAttributes(key, attributes);
        }
    }

    @Override
    public void setGetterOrSetter(String name, int index, Callable getterOrSetter, boolean isSetter) {
        cannotOverride(name);
        if (mainScope.isSealed()) {
            super.setGetterOrSetter(name, index, getterOrSetter, isSetter);
        } else {
            mainScope.setGetterOrSetter(name, index, getterOrSetter, isSetter);
        }
    }

    @Override
    public Object getGetterOrSetter(String name, int index, Scriptable scope, boolean isSetter) {
        Object value = mainScope.getGetterOrSetter(name, index, scope, isSetter);
        if (value == null || value == Undefined.instance) {
            value = super.getGetterOrSetter(name, index, scope, isSetter);
        }
        return value;
    }

    @Override
    protected void init(ScriptableObject baseScope) {
        mainScope = new MainScope(getScopeActual(), null);
        super.put("MainScope", getScopeActual(), this.mainScope);
        if (preBase != null) {
            preBase.accept(baseScope);
        }
        NativeConsole.init(baseScope, true, (NativeConsole.ConsolePrinter) (context, scriptable, level, objects, scriptStackElements) -> {
            boolean isError = level.compareTo(NativeConsole.Level.ERROR)==0;
            if (isError) {
                System.err.println("ERROR: JavaScript native error was declared.");
            }
            if (level.compareTo(NativeConsole.Level.WARN)==0) {
                System.err.println("WARNING: JavaScript native warn was declared.");
            }
            if (level.compareTo(NativeConsole.Level.TRACE)==0) {
                System.err.println("TRACING: JavaScript native trace was declared.");
            }
            if (level.compareTo(NativeConsole.Level.DEBUG)==0) {
                System.err.println("DEBUGGING: JavaScript native debug was declared.");
            }
            if (objects != null) {
                for (Object o : objects) {
                    if (isError) {
                        System.err.println(o);
                    } else {
                        System.out.println(o);
                    }
                }
            }
            if (scriptStackElements != null) {
                for (ScriptStackElement stackElement : scriptStackElements) {
                    StringBuilder sb = new StringBuilder();
                    stackElement.renderJavaStyle(sb);
                    if (isError) {
                        System.err.println(sb);
                    } else {
                        System.out.println(sb);
                    }
                }
            }
        });
        NativeObject ns = NativeSystem.init(getScopeActual(), true, NativeSystem.class, new Object[]{
                new Pair<>("script", getScript())
        });
        addScopeOnly(ns.thisPropertyName());
    }

    private static final String[] noOverride = new String[] {
            "Scope", "BaseScope", "MainScope", "ScopeActual"
    };

    private static void cannotOverride(String s) {
        cannotOverride(s, "Cannot override %1$s.");
    }

    private static void cannotOverride(String s, String message) {
        if (s != null) {
            for (String noOver : noOverride) {
                if (s.equals(noOver)) {
                    throw new RuntimeException(String.format(message, noOver));
                }
            }
        }
    }

    private static class MainScope extends ScriptableObject {

        public MainScope(Scriptable scope, Scriptable prototype) {
            super(scope, prototype);
        }

        @Override
        public String getClassName() {
            return "MainScope";
        }

        @Override
        public void setParentScope(Scriptable m) {
            throw new RuntimeException("Cannot set the parent scope of a MainScope.");
        }
    }
}
