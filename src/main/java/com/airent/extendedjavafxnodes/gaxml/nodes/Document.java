package com.airent.extendedjavafxnodes.gaxml.nodes;

import com.airent.extendedjavafxnodes.gaxml.XML;
import com.airent.extendedjavafxnodes.gaxml.nodes.tags.Page;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

public class Document implements Element, org.w3c.dom.Document {
    private org.w3c.dom.Document document;
    private Node root;
    private Page page;

    public Document(org.w3c.dom.Document document) {
        setDocument(document);
        //System.out.println("good");
        //setRoot(null);
    }
    public Document(Node root) {
        setRoot(root);
    }

    /* *************************************************************************
     *                                                                         *
     * Information                                                             *
     *                                                                         *
     **************************************************************************/

    @Override
    public boolean isDisplayable() {
        return true;
    }

    @Override
    public final org.w3c.dom.Document getNode() {
        return document;
    }

    public final boolean isRootDocument() {
        return this.root.isEqualNode(this.document) || this.root == this.document;
    }

    @Override
    public final Node getRoot() {
        return root;
    }

    public Page getPage() {
        return page;
    }

    private void setDocument(org.w3c.dom.Document document) {
        this.document = document;
        this.root = this.document;
        this.parse(false);
    }

    public final void setRoot(Node root) {
        if (root == null) {
            setRoot(this.document.getDocumentElement());
        } else {
            if (this.document == null) {
                this.document = root.getOwnerDocument();
            }
            if (!root.getOwnerDocument().isSameNode(this.document)) {
                throw new RuntimeException("Root Node must be of the same document as current root Node.");
            }
            this.root = root;
            this.parse(false);
        }
    }

    @Override
    public DocumentType getDoctype() {
        return document.getDoctype();
    }

    @Override
    public DOMImplementation getImplementation() {
        return document.getImplementation();
    }

    @Override
    public org.w3c.dom.Element getDocumentElement() {
        return document.getDocumentElement();
    }

    /* *************************************************************************
     *                                                                         *
     * Parsing                                                                 *
     *                                                                         *
     **************************************************************************/

    private void parse(boolean isRoot) {
        Node n = (isRoot?this.root:document.getDocumentElement());
        org.w3c.dom.Element pageElm = null;
        if (n instanceof org.w3c.dom.Element elm) {
            pageElm = elm;
        }
        if (pageElm == null) throw new DOMException((short)8, "The Node to become the page is not an Element.");
        if (!pageElm.getTagName().equals("page")) {
            throw new DOMException((short) 3, "The Page Element must be of the page tag, but found: "+pageElm.getTagName());
        }
        page = new Page(pageElm);
    }

    /* *************************************************************************
     *                                                                         *
     * Modification                                                            *
     *                                                                         *
     **************************************************************************/

    public final org.w3c.dom.Element create(String tagName) {
        return createElement(tagName);
    }

    public final org.w3c.dom.Element create(String tagName, Node newChild) {
        org.w3c.dom.Element elm = create(tagName);
        elm.appendChild(newChild);
        return elm;
    }

    public final Node append(@NotNull Node parent, Node child) {
        return parent.appendChild(child);
    }
    public final Node append(Node child) {
        return append(this.root, child);
    }

    public final Node add(Node root, String tagName, String data) {
        org.w3c.dom.Element elm = create(tagName, createTextNode(data));
        return append(root, elm);
    }
    public final Node add(String tagName, String data) {
        return add(this.root, tagName, data);
    }

    public final Node remove(@NotNull Node root, Node oldChild) {
        return root.removeChild(oldChild);
    }
    public final Node remove(Node oldChild) {
        return remove(this.root, oldChild);
    }

    @Override
    public org.w3c.dom.Element createElement(String tagName) throws DOMException {
        return Element.super.createElement(tagName);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return document.createDocumentFragment();
    }

    @Override
    public Text createTextNode(String data) {
        return Element.super.createTextNode(data);
    }

    @Override
    public Comment createComment(String data) {
        return Element.super.createComment(data);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return Element.super.createCDATASection(data);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return document.createProcessingInstruction(target, data);
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        return Element.super.createAttribute(name);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        return document.createEntityReference(name);
    }

    @Override
    public org.w3c.dom.Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return Element.super.createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return Element.super.createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        return document.importNode(importedNode, deep);
    }

    @Override
    public Node adoptNode(Node source) throws DOMException {
        return document.adoptNode(source);
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        return document.renameNode(n, namespaceURI, qualifiedName);
    }

    /* *************************************************************************
     *                                                                         *
     * Search                                                                  *
     *                                                                         *
     **************************************************************************/

    @Override
    @NotNull
    public NodeList getElementsByTagName(String tagname) {
        return Element.super.getElementsByTagName(tagname);
    }

    @NotNull
    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return Element.super.getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public org.w3c.dom.Element getElementById(String elementId) {
        return Element.super.getElementById(elementId);
    }

    /* *************************************************************************
     *                                                                         *
     * Data                                                                    *
     *                                                                         *
     **************************************************************************/

    @Override
    public String getInputEncoding() {
        return document.getInputEncoding();
    }

    @Override
    public String getXmlEncoding() {
        return document.getXmlEncoding();
    }

    @Override
    public boolean getXmlStandalone() {
        return document.getXmlStandalone();
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        document.setXmlStandalone(xmlStandalone);
    }

    @Override
    public String getXmlVersion() {
        return document.getXmlVersion();
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        document.setXmlVersion(xmlVersion);
    }

    @Override
    public boolean getStrictErrorChecking() {
        return document.getStrictErrorChecking();
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        document.setStrictErrorChecking(strictErrorChecking);
    }

    @Override
    public String getDocumentURI() {
        return document.getDocumentURI();
    }

    @Override
    public void setDocumentURI(String documentURI) {
        document.setDocumentURI(documentURI);
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return document.getDomConfig();
    }

    @Override
    public void normalizeDocument() {
        document.normalizeDocument();
    }
}
