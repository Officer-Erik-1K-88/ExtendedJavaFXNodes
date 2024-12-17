package com.airent.extendedjavafxnodes.gaxml.javascript;

import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NativeSystem extends NativeObject {
    // static
    public static <T> T cast(Object value, Class<? extends T> cast) {
        if (value == null || value instanceof Undefined) {
            return null;
        }
        return cast.cast(value);
    }

    public static Object ensureSafe(Object value) {
        if (value == null) {
            return Undefined.instance;
        }
        if (value instanceof Scriptable) {
            return value;
        }
        if (value.getClass().isArray()) {
            return new NativeArray((Object[]) value);
        }
        if (value instanceof List) {
            return new NativeList((List<Object>) value);
        }
        if (value instanceof Set) {
            return new NativeList((Set<Object>) value);
        }
        if (value instanceof Collection<?> c) {
            return new NativeList((Collection<Object>) c);
        }
        if (value instanceof Map) {
            return new NativeMap((Map<Object, Object>) value);
        }
        return value;
    }

    // NativeSystem
    public NativeSystem(Scriptable scope, Boolean sealed, Object[] toStore) {
        super(scope, sealed, toStore);
    }

    @Override
    public String getClassName() {
        return "System";
    }

    private void loopStr(List<String> strings, Object[] objects, boolean includeNumber) {
        if (objects == null) return ;
        for (Object o : objects) {
            switch (o) {
                case String s -> strings.add(s);
                case Object[] objects1 -> loopStr(strings, objects1, includeNumber);
                case Collection<?> c -> loopStr(strings, c.toArray(), includeNumber);
                case Number n -> {
                    if (includeNumber) {
                        strings.add(n.toString());
                    }
                }
                case null, default -> {}
            }
        }
    }

    @Override
    protected void init(@NotNull ItemConstructor itemConstructor) {
        Script script = ((Script) getStorage().get("script"));
        itemConstructor.addItem("variables", 1, (contextScriptablePair, scriptablePair) -> {
            List<String> strings = new ArrayList<>();
            loopStr(strings, scriptablePair.getValue(), false);
            if (strings.isEmpty()) {
                return Undefined.instance;
            }
            String[] props = script.getProcessor().variableParser(false, strings.toArray(new String[0]));
            Object[] args = new Object[props.length];
            for (int i=0; i<props.length; i++) {
                String prop = props[i];
                try {
                    args[i] = new BigDecimal(prop);
                } catch (NumberFormatException e) {
                    args[i] = prop;
                }
            }

            return new NativeArray(args);
        });
        itemConstructor.addItem("variable", 1, (contextScriptablePair, scriptablePair) -> {
            String prop = script.getProcessor().variableParser(false, new String[]{scriptablePair.getValue()[0].toString()})[0];
            Object arg;
            try {
                arg = new BigDecimal(prop);
            } catch (NumberFormatException e) {
                arg = prop;
            }
            return ensureSafe(arg);
        });
        itemConstructor.addItem("math", 2, (contextScriptablePair, scriptablePair) -> {
            List<String> vars = new ArrayList<>();
            String eqauteMsg = (String) scriptablePair.getValue()[0];
            Object[] heldVars = new Object[scriptablePair.getValue().length-1];
            if (heldVars.length != 0) {
                System.arraycopy(scriptablePair.getValue(), 1, heldVars, 0, scriptablePair.getValue().length - 1);
                loopStr(vars, heldVars, true);
            }
            return ensureSafe(script.getProcessor().mathCheck(eqauteMsg, vars.toArray(new String[0])));
        });
        itemConstructor.addItem("script", 1, (contextScriptablePair, scriptablePair) -> {
            Object object = scriptablePair.getValue()[0];
            if (!(object instanceof String) && !(object instanceof File)) {
                throw new RuntimeException("Cannot process Scope.System.script("+object.toString()+"). Must only provide a String or File.");
            }
            if (object instanceof String) {
                File f = script.getProcessor().findPath((String) object, false).toFile();
                if (f.exists()) {
                    return script.parse(f);
                } else {
                    return script.parse((String) object);
                }
            }
            return ensureSafe(script.parse((File) object));
        });
        itemConstructor.addItem("return", 1, (contextScriptablePair, scriptablePair) -> {
            Object ret;
            if (scriptablePair.getValue() == null) {
                ret = null;
            } else {
                ret = scriptablePair.getValue()[0];
            }
            if (ret == null) {
                ret = Undefined.instance;
            }
            script.parseReturn = ret;
            return ensureSafe(ret);
        });
    }
}
