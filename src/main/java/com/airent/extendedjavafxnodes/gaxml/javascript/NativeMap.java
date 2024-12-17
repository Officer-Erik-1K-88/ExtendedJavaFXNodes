package com.airent.extendedjavafxnodes.gaxml.javascript;

import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Delegator;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Symbol;
import org.mozilla.javascript.SymbolKey;
import org.mozilla.javascript.Undefined;

import java.util.HashMap;
import java.util.Map;

public class NativeMap extends NativeObject {
    public static final SymbolKey GETSIZE = new SymbolKey("[Symbol.getSize]");
    private final Map<Object, Object> map;

    public NativeMap(Map<Object, Object> map) {
        super();
        this.map = map;
    }

    @Override
    public String getClassName() {
        return "Map";
    }

    @Override
    protected void init(ItemConstructor itemConstructor) {
        itemConstructor.addItem("constructor", 0, (contextScriptablePair, scriptablePair) -> {
            if (scriptablePair.getKey() == null) {
                NativeMap nm = new NativeMap(new HashMap<>());
                Object[] args = scriptablePair.getValue();
                if (args.length > 0) {
                    loadFromIterable(contextScriptablePair.getKey(),
                            contextScriptablePair.getValue(), nm, key(args));
                }

                return nm;
            }
            throw new RuntimeException("Cannot create a new Map.");
        });
        itemConstructor.addItem("set", 2, (contextScriptablePair, scriptablePair) -> {
            return this.map.put(scriptablePair.getValue()[0], scriptablePair.getValue()[1]);
        });
        itemConstructor.addItem("get", 1, (contextScriptablePair, scriptablePair) -> {
            return this.map.get(scriptablePair.getValue()[0]);
        });
        itemConstructor.addItem("delete", 1, (contextScriptablePair, scriptablePair) -> {
            return this.map.remove(scriptablePair.getValue()[0]);
        });
        itemConstructor.addItem("has", 1, (contextScriptablePair, scriptablePair) -> {
            return this.map.containsKey(scriptablePair.getValue()[0]);
        });
        itemConstructor.addItem("clear", 0, (contextScriptablePair, scriptablePair) -> {
            this.map.clear();
            return Undefined.instance;
        });
        itemConstructor.addItem("keys", 0, (contextScriptablePair, scriptablePair) -> {
            return new NativeList(this.map.keySet());
        });
        itemConstructor.addItem("values", 0, (contextScriptablePair, scriptablePair) -> {
            return new NativeList(this.map.values());
        });
        itemConstructor.addItem("entries", 0, (contextScriptablePair, scriptablePair) -> {
            return new NativeList(this.map.entrySet());
        });
        itemConstructor.addItem("forEach", 1, (contextScriptablePair, scriptablePair) -> {
            Object arg = scriptablePair.getValue()[0];
            if (!(arg instanceof Callable)) {
                throw new RuntimeException("Cannot call provided argument.");
            }
            Callable f = (Callable) arg;
            this.map.forEach((key, value) -> {
                f.call(contextScriptablePair.getKey(), contextScriptablePair.getValue(), scriptablePair.getKey(), new Object[]{value, key, this});
            });
            return Undefined.instance;
        });
    }

    protected int findPrototypeId(Symbol k) {
        if (GETSIZE.equals(k)) {
            return 11;
        } else if (SymbolKey.ITERATOR.equals(k)) {
            return 9;
        } else {
            return SymbolKey.TO_STRING_TAG.equals(k) ? 12 : 0;
        }
    }

    static void loadFromIterable(Context cx, Scriptable scope, ScriptableObject map, Object arg1) {
        if (arg1 != null && !Undefined.instance.equals(arg1)) {
            Object ito = ScriptRuntime.callIterator(arg1, cx, scope);
            if (!Undefined.instance.equals(ito)) {
                Scriptable proto = ScriptableObject.getClassPrototype(scope, map.getClassName());
                Callable set = ScriptRuntime.getPropFunctionAndThis(proto, "set", cx, scope);
                ScriptRuntime.lastStoredScriptable(cx);
                ScriptRuntime.loadFromIterable(cx, scope, arg1, (key, value) -> {
                    set.call(cx, scope, map, new Object[]{key, value});
                });
            }
        }
    }

    public static Object key(Object[] args) {
        if (args.length > 0) {
            Object key = args[0];
            return key instanceof Delegator ? ((Delegator)key).getDelegee() : key;
        } else {
            return Undefined.instance;
        }
    }
}
