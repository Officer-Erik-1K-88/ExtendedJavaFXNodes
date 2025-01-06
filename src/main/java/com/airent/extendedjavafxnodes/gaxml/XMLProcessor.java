package com.airent.extendedjavafxnodes.gaxml;

import com.airent.extendedjavafxnodes.gaxml.javascript.Script;
import com.airent.extendedjavafxnodes.gaxml.story.Segment;
import com.airent.extendedjavafxnodes.gaxml.story.StoryPart;
import com.airent.extendedjavafxnodes.gaxml.themes.Light;
import com.airent.extendedjavafxnodes.gaxml.themes.Theme;
import com.airent.extendedjavafxnodes.utils.Convert;
import com.airent.extendedjavafxnodes.utils.Pair;
import com.airent.extendedjavafxnodes.utils.math.Equation;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class XMLProcessor {
    private final static StoryPart<Segment> segments = new StoryPart<Segment>() {
        @Override
        public String getName() {
            return "Segments";
        }
    };
    private final Path filePath;
    private final XML file;
    private final Script script;
    private Theme theme;
    private double defaultWidth = 668;

    private void addToSegments() {
        Segment segment = new Segment(this.filePath.toString(), this);
        segments.add(segment);
    }

    public XMLProcessor(@NotNull URL url) {
        String path = url.getFile();
        if (segments.containsKey(path)) {
            XMLProcessor actual = segments.get(path).getProcessor();
            this.file = actual.getFile();
            this.filePath = actual.getFilePath();
            this.script = actual.getScript();
            this.theme = actual.getTheme();
            this.alreadyPreloaded = actual.alreadyPreloaded;
        } else {
            try {
                file = new XML(Path.of(url.toURI()), false);
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
            this.filePath = this.file.getFile().toPath();
            this.script = new Script(this);
            this.theme = new Light();
            addToSegments();
        }
    }

    public XMLProcessor(@NotNull XML file) {
        String path = file.getFile().toPath().toString();
        if (segments.containsKey(path)) {
            XMLProcessor actual = segments.get(path).getProcessor();
            this.file = actual.getFile();
            this.filePath = actual.getFilePath();
            this.script = actual.getScript();
            this.theme = actual.getTheme();
            this.alreadyPreloaded = actual.alreadyPreloaded;
        } else {
            this.file = file;
            this.filePath = this.file.getFile().toPath();
            this.script = new Script(this);
            this.theme = new Light();
            addToSegments();
        }
    }

    public XMLProcessor(String path) {
        filePath = findPath(path, true);
        String path2 = filePath.toString();
        if (segments.containsKey(path2)) {
            XMLProcessor actual = segments.get(path2).getProcessor();
            this.file = actual.getFile();
            this.script = actual.getScript();
            this.theme = actual.getTheme();
            this.alreadyPreloaded = actual.alreadyPreloaded;
        } else {
            try {
                file = new XML(filePath, false);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.script = new Script(this);
            this.theme = new Light();
            addToSegments();
        }
    }

    private Path checkParent(Path parent, String find, int level) {
        return checkDeepParent(parent, find, level).getKey();
    }

    private Pair<Path, Boolean> checkDeepParent(Path parent, String find, int level) {
        Pair<Path, Boolean> ret = new Pair<>(null, false);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
            for (Path entry : stream) {
                if (level != 0) {
                    ret = checkDeepParent(entry, find, level-1);
                    if (ret.getValue()) {
                        return ret;
                    }
                }
                String[] path = entry.toString().split(String.valueOf(File.separatorChar));
                if (path[path.length-1].equals(find)) {
                    return new Pair<>(entry, true);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    private void pathNotExists(Path path) {
        if (path == null || !path.toFile().exists()) {
            throw new RuntimeException("The provided path doesn't exist.");
        }
    }

    public Path findPath(String path, boolean checkRoot) {
        Path filePath1 = null;
        path = path.replace("\\\\", "/");
        Pattern startDial = Pattern.compile("\\.\\.|/\\.\\.");
        boolean usesWild = startDial.matcher(path).find();
        int wildCount = 0;
        boolean wildAsBack = true;
        Path parent = null;
        if (this.filePath != null) {
            parent = this.filePath.getParent();
        }
        if (usesWild) {
            if (parent == null) {
                throw new RuntimeException("Cannot use wilds in given path when stored file path has no parent or is empty.");
            } else {
                String[] splitPath = path.split("/");
                for (String pathSeg : splitPath) {
                    if (pathSeg.equals("..")) {
                        wildCount++;
                    } else {
                        if (wildCount != 0) {
                            if (wildAsBack) {
                                while (wildCount != 0) {
                                    parent = parent.getParent();
                                    pathNotExists(parent);
                                    wildCount--;
                                }
                            }
                        }
                        parent = checkParent(parent, pathSeg, wildCount);
                        pathNotExists(parent);
                        wildAsBack = false;
                    }
                }
            }
            return parent;
        } else {
            if (parent != null) {
                filePath1 = parent.resolve(path);
            }
        }
        if (!checkRoot || (filePath1 != null && filePath1.toFile().exists())) {
            return filePath1;
        }
        URL resource = this.getClass().getClassLoader().getResource(path);
        if (resource == null) {
            filePath1 = Paths.get(path);
            if (!filePath1.toFile().exists()) {
                filePath1 = Paths.get("target/classes/", path);
            }
        } else {
            try {
                filePath1 = Paths.get(resource.toURI());
            } catch (URISyntaxException e) {
                filePath1 = Paths.get(resource.getPath());
            }
        }
        return filePath1;
    }

    public Path getFilePath() {
        return filePath;
    }

    public XML getFile() {
        return file;
    }

    public Script getScript() {
        return script;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public double getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(double defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public void save() {
        try {
            file.update();
        } catch (TransformerException e) {
            System.out.println("Failed to save story file.");
        }
    }

    public VBox display() {
        VBox vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.getChildren().addAll(load());
        return vBox;
    }

    public List<Node> load() {
        return load(null);
    }

    public List<Node> load(Attributes attributes) {
        loadScripts();
        return load(file, 0, attributes);
    }

    /**
     * Loads some XML data to be displayed.
     * <BR><BR>
     * The available nodes:
     *
     * <H2>page</H2>
     * <P>
     *     The top level element,
     *     can also be used for defining reusable templates
     *     that can be called for by a call of an inline page tag.
     *     All inline page tags must have a 'path' attribute that represents
     *     the directory to the GA-XML file. The path must be separated using '/',
     *     for example the path must be like "path/to/file.xml".
     * </P>
     * <P>
     *     Inline pages are defined as a page tag that has the 'type' attribute
     *     set as either "inline" or "inline-notice".
     *     Normal inline has no identifying markings that separate the inline page
     *     from the rest of th page. While, inline-notice makes it so that there
     *     are identifying marks. To add a defined marking (as a text statement)
     *     define the 'notice' attribute otherwise the defined marking is the path.
     * </P>
     *
     * <H2>br</H2>
     * <P>
     *     A line break, can be used to add vertical distance between elements.
     *     Can change space by changing of the 'size' attribute.
     * </P>
     *
     * <H2>hr</H2>
     * <P>
     *     A horizontal line, can be used to define separation between content.
     *     The line created can be changed in width by setting the 'size' attribute,
     *     by default the size is 4 and the height is 40. The height is set with the
     *     'height' attribute, and is the largest the size is allowed to be.
     *     Height is also the amount of space that the hr tag takes up, any space
     *     that isn't taken up by the actual line, is roughly distributed between the
     *     top and bottom of the line, making the line to be centered in the extra space.
     * </P>
     *
     * <H2>p</H2>
     * <P>
     *     The paragraph tag, this tag's main purpose is to state large blocks of
     *     content that should be grouped and kept separate from the content out
     *     side of the tag.
     * </P>
     *
     * <H2>span</H2>
     * <P>
     *     The span tag, like the paragraph tag, is used to group content that
     *     is separate from the rest of the content, but is similar to the content
     *     around it where the tag's content should still remain inline with
     *     the content that is around it. Meaning that the span tag is to separate
     *     content to format.
     * </P>
     *
     * <H2>pre</H2>
     * <P>
     *     The pre tag is the exact same as the paragraph tag, but doesn't
     *     remove excess space.
     * </P>
     *
     * <H2>variable</H2>
     * <P>
     *     The variable tag defines a value that can be called by
     *     a var tag.
     * </P>
     *
     * <H2>var</H2>
     * <P>
     *     The var tag is the caller of the content in a variable tag.
     * </P>
     *
     * <H2>math</H2>
     * <P>
     *     The math tag defines an equation that needs to be calculated
     *     displaying the answer to the equation.
     * </P>
     *
     * <H2>script</H2>
     * <P>
     *     The script tag defines JavaScript that can be processed and used
     *     to draw out event calls and functions defined in a script tag
     *     can also be called by var and variable tags given "script." is
     *     provided before the function execution call in the var or variable tag.
     * </P>
     *
     * @param xml The XML data to load.
     * @param level How deep the XML data is from the top level of the XML file.
     * @param baseFormat The default format. (The format of the XML data's parent element.)
     * @return The list of nodes to be added as actual page content and of page options.
     */
    @NotNull
    @Contract("_, _, _ -> new")
    private List<Node> load(@NotNull XML xml, int level, Attributes baseFormat) {
        if (baseFormat != null) baseFormat.updateAttributes(true);
        ArrayList<Node> paras = new ArrayList<>();

        if (xml.getChildNodes() == null) {
            throw new RuntimeException("Cannot load XML with no child nodes.");
        }

        if (xml.getChildNodes().getLength() == 1) {
            org.w3c.dom.Node node = xml.getChildNodes().item(0);
            if (node.getNodeType() == 1 && ((Element) node).getTagName().equals("page")) {
                Attributes format;
                if (baseFormat != null) format = new Attributes(node, baseFormat);
                else format = new Attributes(node);
                return load(new XML(node), level+1, format);
            }
        }

        xml.forEachNode(node -> {
            if (theme == null) theme = new Light();
            Formatter text = new Formatter(baseFormat, theme);
            text.setDefaultWidth(defaultWidth);
            Attributes attrs = attributes(node);
            if (node.getNodeType() == 1) {
                Element elm = (Element) node;
                XML elmXML = new XML(elm);
                switch (elm.getTagName()) {
                    case "page" -> {
                        if (elm.hasAttribute("combine")) {
                            if (elm.getAttribute("combine").equals("true")) {
                                List<Node> aPage = load(elmXML, level+1, text.getBaseFormat(new Attributes(attrs, true)));
                                text.add(aPage);
                            }
                        } else {
                            if (elm.hasAttribute("type")) {
                                if (elm.getAttribute("type").startsWith("inline")) {
                                    boolean isNotice = elm.getAttribute("type").contains("notice");
                                    String path = elm.getTextContent();
                                    if (elm.hasAttribute("path")) path = elm.getAttribute("path");
                                    String notice = path;
                                    if (elm.hasAttribute("notice")) notice = elm.getAttribute("notice");
                                    if (isNotice) {
                                        text.add("<" + notice + " ---->", new Attributes(attrs, true));
                                    }
                                    text.add(new XMLProcessor(path).load(text.getBaseFormat(new Attributes(attrs, true))));
                                    if (elm.getChildNodes().getLength() != 0) {
                                        text.add(load(elmXML, level + 1, text.getBaseFormat(new Attributes(attrs, true))));
                                    }
                                    if (isNotice) {
                                        text.add("<End " + notice + " ---->", new Attributes(attrs, true));
                                    }
                                } else {
                                    throw new RuntimeException("Cannot put non-inline page here.");
                                }
                            } else {
                                throw new RuntimeException("Cannot put non-inline page here.");
                            }
                        }
                    }
                    case "br" -> {
                        text.addBreak(new Attributes(attrs, true));
                    }
                    case "hr" -> {
                        text.addLine(new Attributes(attrs, true));
                    }
                    case "p", "span", "a", "pre" -> {
                        if (elm.getTagName().equals("p") || elm.getTagName().equals("pre")) {
                            if (!attrs.containsKey("display")) {
                                attrs.put("display", "block");
                            }
                        }
                        if (elm.getTagName().equals("pre")) {
                            if (!attrs.containsKey("allowSpace")) {
                                attrs.put("allowSpace", "true");
                            }
                        }
                        text.add(load(elmXML, level + 1, text.getBaseFormat(new Attributes(attrs, true))));
                    }
                    case "var", "math" -> {
                        //System.out.println(text.getBaseFormat().getAttributes());
                        String charName = "";
                        if (elm.hasAttribute("name")) {
                            charName = elm.getAttribute("name");
                        } else {
                            if (text.getBaseFormat().containsKey("name")) {
                                charName = text.getBaseFormat().get("name");
                            }
                        }
                        if (elm.getTagName().equals("math")) {
                            String[] vars = getVars(elm);
                            charName = mathCheck(canBeValue(elm), vars);
                        } else {
                            if (charName.isBlank()) {
                                throw new RuntimeException("Cannot have a variable call with no name attribute.");
                            }
                            charName = variableParser(false, new String[]{"inFile." + charName})[0];
                        }
                        text.add(charName, new Attributes(attrs));
                    }
                }
            } else if (node.getNodeType() == 3) {
                boolean allowSpace = text.getBaseFormat().check("allowSpace", "true");
                if (!node.getTextContent().isBlank() || allowSpace) {
                    String msg = node.getTextContent();
                    if (!allowSpace) {
                        if (!text.getBaseFormat().check("allowLineBreaks", "true")) {
                            msg = msg.replaceAll("\n", " ");
                        }
                        if (!text.getBaseFormat().check("allowNonEmptySpace", "true")) {
                            msg = msg.replaceAll(" +", " ");
                        }
                        if (!text.getBaseFormat().check("allowBeginSpace", "true")) {
                            int bi = 0;
                            while (msg.charAt(bi) == ' ') {
                                bi++;
                            }
                            if (bi != 0) {
                                msg = msg.substring(bi);
                            }
                        }
                    }
                    try {
                        Node last = paras.getLast();
                        if (last instanceof Text text1) {
                            if (!text1.getText().endsWith(" ") && !msg.startsWith(" ")) {
                                Pattern spec = Pattern.compile("^[,.!?;:]");
                                if (!spec.matcher(msg).find()) {
                                    msg = " "+msg;
                                }
                            }
                        }
                    } catch (NoSuchElementException ignored) {}
                    paras.add(text.format(msg, new Attributes(attrs)));
                }
            }
            if (!text.isEmpty()) {
                List<Node> nodes;
                if (attrs.check("display", "block")) {
                    nodes = new ArrayList<>();

                    nodes.add(text.applyBlockFormat(text.getTextList(), new Attributes(attrs)));
                } else {
                    nodes = text.getTextList();
                }
                for (Node node1 : nodes) {
                    applyEvents(node1, attrs);
                }
                paras.addAll(nodes);
            }
        });

        return paras;
    }

    private boolean alreadyPreloaded = false;

    public HashMap<String, Object> loadScripts() {
        if (!alreadyPreloaded) {
            alreadyPreloaded = true;
            return loadScripts(file);
        }
        return null;
    }

    public HashMap<String, Object> loadScripts(@NotNull XML xml) {
        HashMap<String, Object> scriptReturns = new HashMap<>();
        if (xml.hasChildNodes()) {
            xml.forEachNode(node -> {
                Pair<String, Pair<Boolean, Object>> loaded = loadScript(node);
                if (loaded.getKey() != null) {
                    if (loaded.getValue().getKey()) {
                        scriptReturns.put(loaded.getKey(), loaded.getValue().getValue());
                    }
                } else {
                    XML xmlNode = new XML(node);
                    if (xmlNode.hasChildNodes()) {
                        scriptReturns.putAll(loadScripts(xmlNode));
                    }
                }
            });
        }
        return scriptReturns;
    }

    @NotNull
    @Contract("_ -> new")
    private Pair<String, Pair<Boolean, Object>> loadScript(org.w3c.dom.Node node) {
        Attributes attrs = attributes(node);
        if (node.getNodeType() == 1) {
            Element elm = (Element) node;
            XML elmXML = new XML(elm);
            Object val = null;
            boolean canAdd = false;
            switch (elm.getTagName()) {
                case "script" -> {
                    canAdd = true;
                    if (elmXML.hasChildNodes() && elmXML.getChildNodes().item(0).getNodeType() == 1) {
                        val = loadScripts(elmXML);
                    } else {
                        if (!attrs.check("parse", "false")) {
                            if (attrs.containsKey("src")) {
                                val = script.parse(findPath(attrs.get("src"), false).toFile());
                            } else {
                                val = script.parse(elm.getTextContent());
                            }
                        } else {
                            canAdd = false;
                        }
                    }
                }
                case "function" -> {
                    canAdd = true;
                    AtomicReference<String> name = new AtomicReference<>();
                    List<String> props = new ArrayList<>();
                    AtomicReference<String> source = new AtomicReference<>();
                    if (elmXML.hasChildNodes() && elmXML.getChildNodes().item(0).getNodeType() == 1) {
                        elmXML.forEachNode(node1 -> {
                            if (node1.getNodeType() == 1) {
                                Element elm1 = (Element) node1;
                                if (elm1.getTagName().equals("name")) {
                                    name.set(elm1.getTextContent());
                                } else if (elm1.getTagName().equals("properties")) {
                                    NodeList nl = elm1.getChildNodes();
                                    for (int i=0; i<nl.getLength(); i++) {
                                        org.w3c.dom.Node n = nl.item(i);
                                        if (n.getNodeType() == 1) {
                                            Element e = (Element) n;
                                            props.add(e.getTextContent());
                                        }
                                    }
                                } else if (elm1.getTagName().equals("source")) {
                                    source.set(elm1.getTextContent());
                                }
                            }
                        });
                    } else {
                        name.set(null);
                        source.set(elm.getTextContent());
                    }
                    Script.Func func;
                    if (name.get() == null || name.get().isBlank()) {
                        func = script.newFunction(source.get());
                    } else {
                        func = script.newFunction(name.get(), props, source.get());
                    }
                    if (attrs.check("parse", "true")) {
                        String[] vars = variableParser(false, getVars(elm));
                        if (attrs.check("construct", "true")) {
                            val = func.construct(vars);
                        } else {
                            val = func.call(vars);
                        }
                    } else {
                        val = func;
                    }
                }
            }
            return new Pair<>(attrs.get("name"), new Pair<>(canAdd, val));
        }
        return new Pair<>(null, null);
    }

    public Object loadScript(String name) {
        return loadScript(file, name);
    }

    public Object loadScript(@NotNull XML xml, String name) {
        return loadScript(xml, name, "script");
    }

    private Object loadScript(@NotNull XML xml, @NotNull String name, String tagName) {
        Object o = null;
        if (name.startsWith("script:")) {
            name = name.substring(7);
        } else {
            String[] splitName = name.split("\\.");
            Scriptable scriptable = script.getScope();
            for (int i=0; i<splitName.length; i++) {
                String n = splitName[i];
                if (scriptable.has(n, scriptable)) {
                    o = scriptable.get(name, scriptable);
                    if (o instanceof Scriptable) {
                        scriptable = (Scriptable) o;
                    } else {
                        if (i != splitName.length-1) {
                            o = null;
                            break;
                        }
                    }
                } else {
                    o = null;
                    break;
                }
            }
            if (o != null) {
                return o;
            }
            /*
            if (script.getScope().has(name, script.getScope())) {
                o = script.getScope().get(name, script.getScope());
                if (o instanceof Function function) {
                    return function;
                }
            }*/
        }
        NodeList nl = xml.getElementsByTagName(tagName);
        Pair<String, Pair<Boolean, Object>> loaded = null;
        for (int i=0; i<nl.getLength(); i++) {
            Element elm = (Element) nl.item(i);
            Attributes attrs = attributes(elm);
            if (attrs.check("name", name)) {
                loaded = loadScript(elm);
                break;
            }
        }
        if (loaded == null && tagName.equals("script")) {
            return loadScript(xml, name, "function");
        }
        if (loaded != null) {
            return loaded.getValue().getValue();
        }
        return o;
    }

    public Object executeScript(String name, String[] vars) {
        String[] props = variableParser(false, vars);
        Object[] args = new Object[props.length];
        for (int i=0; i<props.length; i++) {
            String prop = props[i];
            try {
                args[i] = new BigDecimal(prop);
            } catch (NumberFormatException e) {
                args[i] = prop;
            }
        }
        Object val = loadScript(name);
        if (val instanceof Script.Func func) {
            return func.call(args);
        } else if (val instanceof Function func) {
            Object ret;
            try (Context cx = Context.enter()) {
                ret = func.call(cx, script.getScope(), func, args);
            }
            return ret;
        }
        return val;
    }

    @NotNull
    private Map<String, Object> executeEventScript(String event) {
        Pair<List<String>, Map<String, List<String>>> scripts = getEventScript(event);
        return executeEventScript(scripts);
    }

    @NotNull
    private Map<String, Object> executeEventScript(Pair<List<String>, Map<String, List<String>>> scripts) {
        HashMap<String, Object> items = new HashMap<>();
        executeScriptedEvent(scripts, items::put);
        return items;
    }

    private void executeScriptedEvent(@NotNull Pair<List<String>, Map<String, List<String>>> scripts, BiConsumer<String, Object> onReturn) {
        String accurate = "ACCURATE:INLINE:SCRIPT";
        if (scripts.getKey().getLast().equals(accurate)) {
            onReturn.accept(accurate, script.parse(scripts.getValue().get(accurate).getFirst()));
        } else {
            for (String name : scripts.getKey()) {
                onReturn.accept(name, executeScript(name, scripts.getValue().get(name).toArray(new String[0])));
            }
        }
    }

    @NotNull
    @Contract("_ -> new")
    private Pair<List<String>, Map<String, List<String>>> getEventScript(@NotNull String event) {
        StringBuilder parts = new StringBuilder();
        HashMap<String, List<String>> scripts = new HashMap<>();
        List<String> scriptNames = new ArrayList<>();
        String scriptName = null;
        List<String> scriptArgs = new ArrayList<>();
        int braceLevel = 0;
        boolean inlineScript = event.startsWith("inlineScript:");
        //System.out.println(event);
        if (!inlineScript) {
            for (char c : event.toCharArray()) {
                if (c == '(') {
                    if (braceLevel == 0) {
                        scriptName = parts.toString();
                        parts = new StringBuilder();
                    } else {
                        parts.append(c);
                    }
                    braceLevel++;
                } else if (c == ')') {
                    braceLevel--;
                    if (braceLevel == 0) {
                        scriptArgs.add(parts.toString());
                        parts = new StringBuilder();
                        scripts.put(scriptName, scriptArgs);
                        scriptNames.add(scriptName);
                        scriptArgs = new ArrayList<>();
                        scriptName = null;
                    } else {
                        parts.append(c);
                    }
                } else if (c == ',') {
                    if (braceLevel == 1) {
                        scriptArgs.add(parts.toString());
                        parts = new StringBuilder();
                    }
                } else {
                    if (braceLevel == 0 && c == ';') {
                        if (!parts.isEmpty()) {
                            scriptName = parts.toString();
                            parts = new StringBuilder();
                        }
                        continue;
                    }
                    parts.append(c);
                }
            }
            if (!parts.isEmpty()) {
                scriptName = parts.toString();
            }
            //System.out.println(scriptName);
            if (scriptName != null) {
                scriptNames.add(scriptName);
                scripts.put(scriptName, new ArrayList<>());
            }
        } else {
            event = event.substring(13);
            scriptName = "ACCURATE:INLINE:SCRIPT";
            scriptNames.add(scriptName);
            scripts.put(scriptName, List.of(event));
        }
        //System.out.println(scriptNames);
        return new Pair<>(scriptNames, scripts);
    }

    @NotNull
    @Contract("_ -> new")
    public static Attributes attributes(@NotNull org.w3c.dom.Node node) {
        return attributes(node, false);
    }

    @NotNull
    @Contract("_, _ -> new")
    private static Attributes attributes(@NotNull org.w3c.dom.Node node, boolean onlyPassable) {
        return new Attributes(node, onlyPassable);
    }

    private void applyEvents(Node node, @NotNull Attributes attrs) {
        // events
        if (attrs.containsKey("onclick")) {
            String clickEvent = attrs.get("onclick");
            Pair<List<String>, Map<String, List<String>>> scripts = getEventScript(clickEvent);
            onClick(node, event -> {
                executeEventScript(scripts);
            });
        }
    }

    static void onClick(Node toClick, EventHandler<Event> action) {
        if (toClick instanceof ButtonBase btn) {
            EventHandler<ActionEvent> btnClick = btn.getOnAction();
            btn.setOnAction((btnClick!=null?event -> {
                btnClick.handle(event);
                action.handle(event);
            } : action::handle));
        } else {
            EventHandler<? super MouseEvent> click = toClick.getOnMouseClicked();
            toClick.setOnMouseClicked((click!=null?event -> {
                click.handle(event);
                action.handle(event);
            } : action));
        }
    }

    private String canBeValue(@NotNull Element elm) {
        if (elm.hasAttribute("value")) return elm.getAttribute("value");
        return elm.getTextContent();
    }

    private String[] getVars(@NotNull Element elm) {
        String[] vars = new String[0];
        if (elm.hasAttribute("variables")) {
            vars = getVars(elm.getAttribute("variables"));
        }
        return vars;
    }

    @NotNull
    @Contract(pure = true)
    private String[] getVars(@NotNull String vars) {
        return vars.split(";");
    }

    public String[] variableParser(boolean onlyNumber, @NotNull String[] vars) {
        for (int i=0; i<vars.length; i++) {
            String var = vars[i];
            String name = "";
            String[] named = var.split(":");
            String[] parsing = new String[0];
            String value = null;
            if (named.length == 2) {
                var = named[1];
                name = named[0]+":";
            } else {
                int diff = 0;
                if (named[0].startsWith("name.")) {
                    name = named[0].substring(5);
                    var = named[1];
                    parsing = new String[named.length-2];
                    diff = 2;
                } else {
                    var = named[0];
                    parsing = new String[named.length-1];
                    diff = 1;
                }
                System.arraycopy(named, diff, parsing, 0, named.length - diff);
            }
            if (var.startsWith("inFile")) {
                var = var.substring(7);
                NodeList inVars = file.getElementsByTagName("variable");
                for (int j=0; j < inVars.getLength(); j++) {
                    Element elm = (Element) inVars.item(j);
                    Attributes attributes = attributes(elm);
                    if (attributes.check("name", var)) {
                        if (attributes.check("mathParse", "true")) {
                            value = mathCheck(
                                    canBeValue(elm),
                                    (attributes.containsKey("variables")?attributes.get("variables").split(";"):new String[0])
                            );
                        } else {
                            value = canBeValue(elm);
                        }
                    }
                    if (value != null && value.startsWith("script.")) {
                        value = variableParser(false, new String[]{value})[0];
                    }
                }
            } else if (var.startsWith("script")) {
                var = var.substring(7);
                Map<String, Object> val = executeEventScript(var);
                if (val.size() == 1) {
                    String key = val.keySet().toArray(new String[0])[0];
                    value = val.get(key).toString();
                } else {
                    throw new RuntimeException("Variable calling a script must only call one script.");
                }
            }
            if (value!=null) {
                try {
                    value = new BigDecimal(value).toPlainString();
                } catch (NumberFormatException e) {
                    if (onlyNumber) {
                        continue;
                    }
                }
                for (String s : parsing) {
                    value = String.valueOf(Convert.convert(s, value));
                }
                vars[i] = name+value;
            }
        }
        return vars;
    }

    @NotNull
    public String mathCheck(String equateMsg, @NotNull String[] vars) {
        Equation equation = new Equation(equateMsg);
        if (vars.length == 0) {
            return equation.calculate().toPlainString();
        }
        return equation.calculate(variableParser(true, vars)).toPlainString();
    }
}
