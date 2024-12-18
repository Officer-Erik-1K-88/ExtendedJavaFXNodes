package com.airent.extendedjavafxnodes.gaxml;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

// Java Program to Write XML Using DOM Parser
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XML {
    // Create a new Document
    private Document document;
    private Node root;
    private DOMSource source;
    private File file;

    public XML(@NotNull Path path, boolean isNew) throws IOException {
        this(path.toFile(), isNew);
    }

    public XML(File file, boolean isNew) throws IOException {
        if (!setFile(file, isNew, true, true)) {
            setDocument(Build.BUILD.newDocument());
            this.source = new DOMSource(this.document);
        }
    }

    public XML(Node node) {
        this.root = node;
        if (node instanceof Document doc) {
            this.document = doc;
        } else {
            this.document = this.root.getOwnerDocument();
        }
        this.source = new DOMSource(this.document);
    }

    public boolean isRootDocument() {
        return this.root.isEqualNode(this.document) || this.root == this.document;
    }

    public Node getRoot() {
        return root;
    }

    private void setDocument(Document document) {
        this.document = document;
        this.root = this.document;
    }

    public void setRoot(Node root) {
        if (root == null) {
            setRoot(this.document.getDocumentElement());
        } else {
            if (root.getOwnerDocument() != this.document) {
                throw new RuntimeException("Root Node must be of the same document as current root Node.");
            }
            this.root = root;
        }
    }

    public File getFile() {
        return file;
    }

    public boolean setFile(File file) throws IOException {
        return setFile(file, false);
    }

    public boolean setFile(File file, boolean useFileContent) throws IOException {
        return setFile(file, false, useFileContent, false);
    }

    private boolean setFile(File file, boolean isNew, boolean useFileContent, boolean passNotNew) throws IOException {
        this.file = file;
        if (isNew || !this.file.exists()) {
            System.out.println(this.file.getAbsolutePath());
            isNew = this.file.createNewFile();
        }
        if (useFileContent) {
            if (!isNew) {
                Document document1;
                try {
                    document1 = Build.BUILD.parse(this.file);
                } catch (SAXException e) {
                    document1 = Build.BUILD.newDocument();
                }
                setDocument(document1);
                this.source = new DOMSource(this.document);
                return true;
            } else {
                if (!passNotNew) {
                    throw new IOException("Cannot use file content that doesn't exist.");
                }
            }
        }
        if (this.source != null) {
            try {
                update();
            } catch (TransformerException e) {
                this.source = new DOMSource(this.document);
            }
        }
        return false;
    }

    /**
     * Applies the data content of this XML document
     * to it's linked file.
     *
     * @return This XML document.
     * @throws TransformerException
     */
    public XML update() throws TransformerException {
        this.source = new DOMSource(this.document);
        if (this.file == null) throw new RuntimeException("Cannot update a file that doesn't exist.");
        // Specify your local file path
        StreamResult result;
        try {
            result = new StreamResult(new FileOutputStream(this.file));
        } catch (FileNotFoundException e) {
            result = new StreamResult(this.file);
        }
        Build.BUILD.transform(this.source, result);
        return this;
    }

    public Element create(String tagName) {
        return this.document.createElement(tagName);
    }

    public Element create(String tagName, Node newChild) {
        Element elm = create(tagName);
        elm.appendChild(newChild);
        return elm;
    }

    public Node append(@NotNull Node parent, Node child) {
        return parent.appendChild(child);
    }
    public Node append(Node child) {
        return append(this.root, child);
    }

    public Node add(Node root, String tagName, String data) {
        Element elm = create(tagName, this.document.createTextNode(data));
        return append(root, elm);
    }
    public Node add(String tagName, String data) {
        return add(this.root, tagName, data);
    }

    public Node remove(@NotNull Node root, Node oldChild) {
        return root.removeChild(oldChild);
    }
    public Node remove(Node oldChild) {
        return remove(this.root, oldChild);
    }

    public NamedNodeMap getAttributes() {
        return this.root.getAttributes();
    }

    public Element getElementById(String elementId) {
        return this.document.getElementById(elementId);
    }

    public NodeList getElementsByTagName(String tagName) {
        if (isRootDocument()) {
            return this.document.getElementsByTagName(tagName);
        }
        if (root instanceof Element elm) {
            return elm.getElementsByTagName(tagName);
        }
        return null;
    }

    public boolean hasChildNodes() {
        if (getChildNodes() == null) {
            return false;
        }
        return getChildNodes().getLength() != 0;
    }

    public NodeList getChildNodes() {
        return this.root.getChildNodes();
    }

    public void forEachNode(@NotNull Node root, Consumer<Node> action) {
        NodeList nodes = root.getChildNodes();
        for (int i=0; i < nodes.getLength(); i++) {
            action.accept(nodes.item(i));
        }
    }

    public void forEachNode(Consumer<Node> action) {
        forEachNode(this.root, action);
    }

    public static class Build extends DocumentBuilder {
        // Create a DocumentBuilder
        public static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        public static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

        public static final Build BUILD = new Build();

        private final DocumentBuilder builder;
        private final Transformer transformer;

        public Build() {
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            try {
                transformer = transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * Obtain a new instance of a DOM {@link Document} object
         * to build a DOM tree with.
         *
         * @return A new instance of a DOM Document object.
         */
        @Override
        public Document newDocument() {
            return builder.newDocument();
        }

        /**
         * Obtain an instance of a {@link DOMImplementation} object.
         *
         * @return A new instance of a <code>DOMImplementation</code>.
         */
        @Override
        public DOMImplementation getDOMImplementation() {
            return builder.getDOMImplementation();
        }

        /**
         * Parse the content of the given input source as an XML document
         * and return a new DOM {@link Document} object.
         * An <code>IllegalArgumentException</code> is thrown if the
         * <code>InputSource</code> is <code>null</code>.
         *
         * @param is InputSource containing the content to be parsed.
         *
         * @return A new DOM Document object.
         *
         * @throws IOException If any IO errors occur.
         * @throws SAXException If any parse errors occur.
         * @throws IllegalArgumentException When <code>is</code> is <code>null</code>
         */
        @Override
        public Document parse(InputSource is) throws IOException, SAXException {
            return builder.parse(is);
        }

        /**
         * Indicates whether or not this parser is configured to
         * understand namespaces.
         *
         * @return true if this parser is configured to understand
         * namespaces; false otherwise.
         */
        @Override
        public boolean isNamespaceAware() {
            return builder.isNamespaceAware();
        }

        /**
         * Indicates whether or not this parser is configured to
         * validate XML documents.
         *
         * @return true if this parser is configured to validate
         * XML documents; false otherwise.
         */
        @Override
        public boolean isValidating() {
            return builder.isValidating();
        }

        /**
         * Specify the {@link EntityResolver} to be used to resolve
         * entities present in the XML document to be parsed. Setting
         * this to <code>null</code> will result in the underlying
         * implementation using it's own default implementation and
         * behavior.
         *
         * @param er The <code>EntityResolver</code> to be used to resolve entities
         *           present in the XML document to be parsed.
         */
        @Override
        public void setEntityResolver(EntityResolver er) {
            builder.setEntityResolver(er);
        }

        /**
         * Specify the {@link ErrorHandler} to be used by the parser.
         * Setting this to <code>null</code> will result in the underlying
         * implementation using it's own default implementation and
         * behavior.
         *
         * @param eh The <code>ErrorHandler</code> to be used by the parser.
         */
        @Override
        public void setErrorHandler(ErrorHandler eh) {
            builder.setErrorHandler(eh);
        }


        // transformer
        /**
         * <P>Transform the XML <code>Source</code> to a <code>Result</code>.
         * Specific transformation behavior is determined by the settings of the
         * <code>TransformerFactory</code> in effect when the
         * <code>Transformer</code> was instantiated and any modifications made to
         * the <code>Transformer</code> instance.</P>
         *
         * <P>An empty <code>Source</code> is represented as an empty document
         * as constructed by {@link javax.xml.parsers.DocumentBuilder#newDocument()}.
         * The result of transforming an empty <code>Source</code> depends on
         * the transformation behavior; it is not always an empty
         * <code>Result</code>.</P>
         *
         * @param xmlSource The XML input to transform.
         * @param outputTarget The <code>Result</code> of transforming the
         *   <code>xmlSource</code>.
         *
         * @throws TransformerException If an unrecoverable error occurs
         *   during the course of the transformation.
         */
        public void transform(Source xmlSource, Result outputTarget) throws TransformerException {
            transformer.transform(xmlSource, outputTarget);
        }

        /**
         * Add a parameter for the transformation.
         *
         * <P>Pass a qualified name as a two-part string, the namespace URI
         * enclosed in curly braces ({}), followed by the local name. If the
         * name has a null URL, the String only contain the local name. An
         * application can safely check for a non-null URI by testing to see if the
         * first character of the name is a '{' character.</P>
         * <P>For example, if a URI and local name were obtained from an element
         * defined with &lt;xyz:foo
         * xmlns:xyz="http://xyz.foo.com/yada/baz.html"/&gt;,
         * then the qualified name would be "{http://xyz.foo.com/yada/baz.html}foo".
         * Note that no prefix is used.</P>
         *
         * @param name The name of the parameter, which may begin with a
         * namespace URI in curly braces ({}).
         * @param value The value object.  This can be any valid Java object. It is
         * up to the processor to provide the proper object conversion or to simply
         * pass the object on for use in an extension.
         *
         * @throws NullPointerException If value is null.
         */
        public void setParameter(String name, Object value) {
            transformer.setParameter(name, value);
        }

        /**
         * Get a parameter that was explicitly set with setParameter.
         *
         * <P>This method does not return a default parameter value, which
         * cannot be determined until the node context is evaluated during
         * the transformation process.
         *
         * @param name of <code>Object</code> to get
         *
         * @return A parameter that has been set with setParameter.
         */
        public Object getParameter(String name) {
            return transformer.getParameter(name);
        }

        /**
         * Clear all parameters set with setParameter.
         */
        public void clearParameters() {
            transformer.clearParameters();
        }

        /**
         * Set the error event listener in effect for the transformation.
         *
         * @param listener The new error listener.
         *
         * @throws IllegalArgumentException if listener is null.
         */
        public void setErrorListener(ErrorListener listener)
                throws IllegalArgumentException {
            transformer.setErrorListener(listener);
        }

        /**
         * Get the error event handler in effect for the transformation.
         * Implementations must provide a default error listener.
         *
         * @return The current error handler, which should never be null.
         */
        public ErrorListener getErrorListener() {
            return transformer.getErrorListener();
        }
    }
}
