package com.airent.extendedjavafxnodes.gaxml.javascript;

import com.airent.extendedjavafxnodes.gaxml.XMLProcessor;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Script {
    private final String name;
    private Scope scope;
    private final XMLProcessor processor;

    public Script(@NotNull XMLProcessor processor) {
        this(null, null, processor);
    }

    public Script(@NotNull String name) {
        this(name, null, null);
    }

    public Script(@NotNull String name, Scope scope) {
        this(name, scope, null);
    }

    public Script(String name, Scope scope, XMLProcessor processor) {
        if (name == null) {
            if (processor == null) {
                throw new NullPointerException("A XMLProcessor must be stated if name is null.");
            } else {
                name = processor.getFile().getFile().getName().split("\\.")[0];
            }
        }
        this.name = name;
        this.processor = processor;
        if (scope == null) {
            this.scope = new Scope(this);
        } else {
            setScope(scope);
        }
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        if (!name.endsWith(".js")) {
            return name+".js";
        }
        return name;
    }

    public Scope getScope() {
        return scope;
    }

    private void setScope(Scope scope) {
        this.scope = scope;
        this.scope.setScript(this);
    }

    public XMLProcessor getProcessor() {
        return processor;
    }

    Object parseReturn;

    public Object parse(String source) {
        Object ret = null;
        try (Context cx = Context.enter()) {
            org.mozilla.javascript.Script script = cx.compileString(source, getFileName(), 0, null);
            ret = script.exec(cx, this.scope);
        }
        if (parseReturn != null) {
            ret = parseReturn;
            parseReturn = null;
        }
        return ret;
    }

    public Object parse(@NotNull File jsFile) {
        if (jsFile.exists() && jsFile.getPath().endsWith(".js")) {
            Object ret = null;
            try (Context cx = Context.enter()) {
                org.mozilla.javascript.Script script = cx.compileReader(new FileReader(jsFile), jsFile.getName(), 0, null);
                ret = script.exec(cx, this.scope);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (parseReturn != null) {
                ret = parseReturn;
                parseReturn = null;
            }
            return ret;
        }
        throw new RuntimeException("Cannot parse JavaScript file.");
    }

    public Func newFunction(String name, List<String> properties, String source) {
        StringBuilder trueSource = new StringBuilder();
        trueSource.append("function ");
        trueSource.append(name);
        trueSource.append("(");
        if (properties != null) {
            for (String prop : properties) {
                trueSource.append(prop).append(", ");
            }
        }
        if (trueSource.toString().endsWith(", ")) {
            trueSource.delete(trueSource.length()-2, trueSource.length());
        }
        trueSource.append(") {");
        trueSource.append(source);
        trueSource.append("}");
        return newFunction(trueSource.toString());
    }

    public Func newFunction(String source) {
        return new Func(source);
    }

    public class Func {
        private String source;
        private Function function;

        public Func(String source) {
            setSource(source);
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
            parse();
        }

        public Function getFunction() {
            return function;
        }

        private void parse() {
            try (Context cx = Context.enter()) {
                this.function = cx.compileFunction(Script.this.scope, this.source, Script.this.getFileName(), 0, null);
            }
        }

        public Object call(Object[] args) {
            Object ret;
            try (Context cx = Context.enter()) {
                ret = this.function.call(cx, Script.this.scope, this.function, args);
            }
            return ret;
        }

        public Scriptable construct(Object[] args) {
            Scriptable ret;
            try (Context cx = Context.enter()) {
                ret = this.function.construct(cx, Script.this.scope, args);
            }
            return ret;
        }
    }
}
