package com.airent.extendedjavafxnodes.gaxml.nodes;

import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class TextNode extends Text implements org.w3c.dom.Text {

    private final org.w3c.dom.Text text;

    public TextNode(org.w3c.dom.Text text) {
        super(text.getData());
        this.text = text;
        textProperty().addListener((observable, oldValue, newValue) -> {
            if (!this.text.getData().equals(newValue)) {
                this.text.setData(newValue);
            }
        });
    }

    @Override
    public String getData() throws DOMException {
        return getText();
    }

    @Override
    public void setData(String data) throws DOMException {
        setText(data);
    }

    @Override
    public int getLength() {
        return text.getLength();
    }

    @Override
    public String substringData(int offset, int count) throws DOMException {
        return text.substringData(offset, count);
    }

    @Override
    public void appendData(String arg) throws DOMException {
        setData(getData()+arg);
    }

    @Override
    public void insertData(int offset, String arg) throws DOMException {
        text.insertData(offset, arg);
        setData(text.getData());
    }

    @Override
    public void deleteData(int offset, int count) throws DOMException {
        text.insertData(offset, arg);
        setData(text.getData());
    }

    @Override
    public void replaceData(int offset, int count, String arg) throws DOMException {
        text.insertData(offset, arg);
        setData(text.getData());
    }

    @Override
    public TextNode splitText(int offset) throws DOMException {
        text.insertData(offset, arg);
        setData(text.getData());
        return new TextNode(newData);
    }

    @Override
    public boolean isElementContentWhitespace() {
        return getData().trim().isEmpty();
    }

    @Override
    public String getWholeText() {
        return getData();
    }

    @Override
    public TextNode replaceWholeText(String content) throws DOMException {
        setData(content);
        return this;
    }

    @Override
    public String getNodeName() {
        return "#text";
    }

    @Override
    public String getNodeValue() throws DOMException {
        return getData();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        setData(nodeValue);
    }

    @Override
    public short getNodeType() {
        return TEXT_NODE;
    }

    @Override
    public org.w3c.dom.Node getParentNode() {
        return text.getParentNode();
    }

    @Override
    public org.w3c.dom.NodeList getChildNodes() {
        return text.getChildNodes();
    }

    @Override
    public org.w3c.dom.Node getFirstChild() {
        return text.getFirstChild();
    }

    @Override
    public org.w3c.dom.Node getLastChild() {
        return text.getLastChild();
    }

    @Override
    public org.w3c.dom.Node getPreviousSibling() {
        return text.getPreviousSibling();
    }

    @Override
    public org.w3c.dom.Node getNextSibling() {
        return text.getNextSibling();
    }

    @Override
    public org.w3c.dom.NamedNodeMap getAttributes() {
        return text.getAttributes();
    }

    @Override
    public org.w3c.dom.Document getOwnerDocument() {
        return null; // To be implemented if part of a Document
    }

    @Override
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text nodes do not have children");
    }

    @Override
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text nodes do not have children");
    }

    @Override
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text nodes do not have children");
    }

    @Override
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR, "Text nodes do not have children");
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public TextNode cloneNode(boolean deep) {
        return new TextNode(getData());
    }

    @Override
    public void normalize() {
        // No-op for Text nodes
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return false;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        throw new DOMException(DOMException.NAMESPACE_ERR, "Text nodes do not have prefixes");
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        return 0;
    }

    @Override
    public String getTextContent() throws DOMException {
        return getData();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        setData(textContent);
    }

    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return this == other;
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    @Override
    public boolean isEqualNode(org.w3c.dom.Node other) {
        if (other instanceof org.w3c.dom.Text) {
            return getData().equals(((org.w3c.dom.Text) other).getData());
        }
        return false;
    }

    @Override
    public Object getFeature(String feature, String version) {
        return null;
    }

    @Override
    public Object setUserData(String key, Object data, org.w3c.dom.UserDataHandler handler) {
        return null;
    }

    @Override
    public Object getUserData(String key) {
        return null;
    }
}
