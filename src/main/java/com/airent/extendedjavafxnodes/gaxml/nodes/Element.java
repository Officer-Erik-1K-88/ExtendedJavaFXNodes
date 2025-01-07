package com.airent.extendedjavafxnodes.gaxml.nodes;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public interface Element extends org.w3c.dom.Element, Document {
    org.w3c.dom.Node getNode();

    default Node getRoot() {
        return getNode();
    }

    /* *************************************************************************
     *                                                                         *
     * Information                                                             *
     *                                                                         *
     **************************************************************************/

    @NotNull
    @Override
    default String getNodeName() {
        return getNode().getNodeName();
    }

    @Override
    default String getNodeValue() throws DOMException {
        return getNode().getNodeValue();
    }

    @Override
    default void setNodeValue(String nodeValue) throws DOMException {
        getNode().setNodeValue(nodeValue);
    }

    @Override
    default short getNodeType() {
        return getNode().getNodeType();
    }

    @Override
    default String getTagName() {
        if (getNode() instanceof org.w3c.dom.Element elm) {
            return elm.getTagName();
        }
        return null;
    }

    @Override
    default TypeInfo getSchemaTypeInfo() {
        if (getNode() instanceof org.w3c.dom.Element elm) {
            return elm.getSchemaTypeInfo();
        }
        return null;
    }

    @Override
    default String getLocalName() {
        return getNode().getLocalName();
    }

    @Override
    default String getBaseURI() {
        return getNode().getBaseURI();
    }

    @Override
    default String getNamespaceURI() {
        return getNode().getNamespaceURI();
    }

    @Override
    default DocumentType getDoctype() {
        if (getNode() instanceof Document document) {
            return document.getDoctype();
        }
        return getOwnerDocument().getDoctype();
    }

    @Override
    default DOMImplementation getImplementation() {
        if (getNode() instanceof Document document) {
            return document.getImplementation();
        }
        return getOwnerDocument().getImplementation();
    }

    /* *************************************************************************
     *                                                                         *
     * Attributes                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * This variable only exists encase {@link #getNode()}
     * is not an {@link org.w3c.dom.Element}.
     */
    NamedNodeMap attributes = new MapOfNodes();

    /**
     * This element, by default will check to see if {@link #getNode()}'s
     * {@code getAttributes()} method returns {@code null}.
     * If {@code null} is returned then this method shall return {@link #attributes},
     * otherwise, it'll return the attributes of the linked node.
     *
     * @return A {@link NamedNodeMap} that represents this Element's attributes.
     */
    @Override
    default NamedNodeMap getAttributes() {
        NamedNodeMap attrs = getRoot().getAttributes();
        if (attrs == null) {
            attrs = attributes;
        }
        return attrs;
    }

    @NotNull
    @Override
    default String getAttribute(String name) {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.getAttribute(name);
        }
        Node node = getAttributes().getNamedItem(name);
        String value = null;
        if (node != null) {
            if (node instanceof Attr attr) {
                value = attr.getValue();
            } else {
                value = node.getNodeValue();
            }
        }
        if (value == null) value = "";
        return value;
    }

    @Override
    default void setAttribute(String name, String value) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.setAttribute(name, value);
        } else {
            Attr attr = createAttribute(name);
            attr.setValue(value);
            getAttributes().setNamedItem(attr);
        }
    }

    @Override
    default void removeAttribute(String name) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.removeAttribute(name);
        } else {
            getAttributes().removeNamedItem(name);
        }
    }

    @Override
    default Attr getAttributeNode(String name) {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.getAttributeNode(name);
        }
        Node node = getAttributes().getNamedItem(name);
        if (node != null) {
            if (node instanceof Attr attr) return attr;
        }
        return null;
    }

    @Override
    default Attr setAttributeNode(Attr newAttr) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.setAttributeNode(newAttr);
        }
        Node node = getAttributes().setNamedItem(newAttr);
        if (node != null) {
            if (node instanceof Attr attr) return attr;
        }
        return null;
    }

    @Override
    default Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.removeAttributeNode(oldAttr);
        }
        Node node = getAttributes().removeNamedItem(oldAttr.getName());
        if (node != null) {
            if (node instanceof Attr attr) return attr;
        }
        return null;
    }

    @NotNull
    @Override
    default String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.getAttributeNS(namespaceURI, localName);
        }
        Node node = getAttributes().getNamedItemNS(namespaceURI, localName);
        String value = null;
        if (node != null) {
            if (node instanceof Attr attr) {
                value = attr.getValue();
            } else {
                value = node.getNodeValue();
            }
        }
        if (value == null) value = "";
        return value;
    }

    @Override
    default void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.setAttributeNS(namespaceURI, qualifiedName, value);
        } else {
            Attr attr = createAttributeNS(namespaceURI, qualifiedName);
            attr.setValue(value);
            getAttributes().setNamedItemNS(attr);
        }
    }

    @Override
    default void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.removeAttributeNS(namespaceURI, localName);
        } else {
            getAttributes().removeNamedItemNS(namespaceURI, localName);
        }
    }

    @Override
    default Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.getAttributeNodeNS(namespaceURI, localName);
        }
        Node node = getAttributes().getNamedItemNS(namespaceURI, localName);
        if (node != null) {
            if (node instanceof Attr attr) return attr;
        }
        return null;
    }

    @Override
    default Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.setAttributeNodeNS(newAttr);
        }
        Node node = getAttributes().setNamedItemNS(newAttr);
        if (node != null) {
            if (node instanceof Attr attr) return attr;
        }
        return null;
    }

    @Override
    default boolean hasAttributes() {
        return getRoot().hasAttributes() || (getAttributes().getLength() != 0);
    }

    @Override
    default boolean hasAttribute(String name) {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.hasAttribute(name);
        }
        for (int i=0; i < getAttributes().getLength(); i++) {
            Node attrNode = getAttributes().item(i);
            if (attrNode instanceof Attr attr) {
                if (attr.getName().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    default boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            return elm.hasAttributeNS(namespaceURI, localName);
        }
        for (int i=0; i < getAttributes().getLength(); i++) {
            Node attrNode = getAttributes().item(i);
            if (attrNode instanceof Attr attr) {
                if (attr.getNamespaceURI().equals(namespaceURI) &&
                        attr.getLocalName().equals(localName)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    default void setIdAttribute(String name, boolean isId) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.setIdAttribute(name, isId);
        }
        // TODO: Make this compatible with getAttributes()
    }

    @Override
    default void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.setIdAttributeNS(namespaceURI, localName, isId);
        }
        // TODO: Make this compatible with getAttributes()
    }

    @Override
    default void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        if (getRoot() instanceof org.w3c.dom.Element elm) {
            elm.setIdAttributeNode(idAttr, isId);
        }
        // TODO: Make this compatible with getAttributes()
    }

    /* *************************************************************************
     *                                                                         *
     * Children                                                                *
     *                                                                         *
     **************************************************************************/

    @NotNull
    @Override
    default NodeList getElementsByTagName(String name) {
        if (getNode() instanceof org.w3c.dom.Element elm) {
            return elm.getElementsByTagName(name);
        } else if (getNode() instanceof Document document) {
            return document.getElementsByTagName(name);
        }
        ListOfNodes nodes = new ListOfNodes();
        for (int i=0; i < getChildNodes().getLength(); i++) {
            Node node = getChildNodes().item(i);
            if (node instanceof org.w3c.dom.Element elm) {
                if (elm.getTagName().equals(name) || name.equals("*")) {
                    nodes.add(elm);
                }
            }
        }
        return nodes;
    }

    @NotNull
    @Override
    default NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        if (getNode() instanceof org.w3c.dom.Element elm) {
            return elm.getElementsByTagNameNS(namespaceURI, localName);
        } else if (getNode() instanceof Document document) {
            return document.getElementsByTagNameNS(namespaceURI, localName);
        }
        ListOfNodes nodes = new ListOfNodes();
        for (int i=0; i < getChildNodes().getLength(); i++) {
            Node node = getChildNodes().item(i);
            if (node instanceof org.w3c.dom.Element &&
                    (node.getNamespaceURI().equals(namespaceURI) || namespaceURI.equals("*") ) &&
                    (node.getLocalName().equals(localName) || localName.equals("*"))) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    @Override
    default org.w3c.dom.Element getElementById(String elementId) {
        if (getNode() instanceof Document document) {
            return document.getElementById(elementId);
        }
        for (int i=0; i< getChildNodes().getLength(); i++) {
            Node node = getChildNodes().item(i);
            if (!(node instanceof org.w3c.dom.Element)) continue;
            for (int j=0; j < node.getAttributes().getLength(); j++) {
                Node attrNode = node.getAttributes().item(j);
                if (attrNode instanceof Attr attr) {
                    if (attr.isId() && attr.getValue().equals(elementId)) {
                        return (org.w3c.dom.Element) node;
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    default NodeList getChildNodes() {
        return getRoot().getChildNodes();
    }

    @Override
    default Node getFirstChild() {
        return getRoot().getFirstChild();
    }

    @Override
    default Node getLastChild() {
        return getRoot().getLastChild();
    }

    @Override
    default Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return getRoot().insertBefore(newChild, refChild);
    }

    @Override
    default Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return getRoot().replaceChild(newChild, oldChild);
    }

    @Override
    default Node removeChild(Node oldChild) throws DOMException {
        return getRoot().removeChild(oldChild);
    }

    @Override
    default Node appendChild(Node newChild) throws DOMException {
        return getRoot().appendChild(newChild);
    }

    @Override
    default Node importNode(Node importedNode, boolean deep) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.importNode(importedNode, deep);
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default Node adoptNode(Node source) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.adoptNode(source);
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.renameNode(n, namespaceURI, qualifiedName);
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default boolean hasChildNodes() {
        return getRoot().hasChildNodes();
    }

    /* *************************************************************************
     *                                                                         *
     * Create                                                                  *
     *                                                                         *
     **************************************************************************/

    @Override
    default org.w3c.dom.Element createElement(String tagName) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createElement(tagName);
        }
        return getOwnerDocument().createElement(tagName);
    }

    @Override
    default org.w3c.dom.Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createElementNS(namespaceURI, qualifiedName);
        }
        return getOwnerDocument().createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    default DocumentFragment createDocumentFragment() {
        if (getNode() instanceof Document document) {
            return document.createDocumentFragment();
        }
        return getOwnerDocument().createDocumentFragment();
    }

    @Override
    default Text createTextNode(String data) {
        if (getNode() instanceof Document document) {
            return document.createTextNode(data);
        }
        return getOwnerDocument().createTextNode(data);
    }

    @Override
    default Comment createComment(String data) {
        if (getNode() instanceof Document document) {
            return document.createComment(data);
        }
        return getOwnerDocument().createComment(data);
    }

    @Override
    default CDATASection createCDATASection(String data) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createCDATASection(data);
        }
        return getOwnerDocument().createCDATASection(data);
    }

    @Override
    default ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createProcessingInstruction(target, data);
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default Attr createAttribute(String name) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createAttribute(name);
        }
        return getOwnerDocument().createAttribute(name);
    }

    @Override
    default Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createAttributeNS(namespaceURI, qualifiedName);
        }
        return getOwnerDocument().createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    default EntityReference createEntityReference(String name) throws DOMException {
        if (getNode() instanceof Document document) {
            return document.createEntityReference(name);
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    /* *************************************************************************
     *                                                                         *
     * Data                                                                    *
     *                                                                         *
     **************************************************************************/

    @Override
    default org.w3c.dom.Element getDocumentElement() {
        if (getNode() instanceof Document document) {
            return document.getDocumentElement();
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default Document getOwnerDocument() {
        return getNode().getOwnerDocument();
    }

    @Override
    default Node getParentNode() {
        return getNode().getParentNode();
    }

    @Override
    default Node getPreviousSibling() {
        return getNode().getPreviousSibling();
    }

    @Override
    default Node getNextSibling() {
        return getNode().getNextSibling();
    }

    @Override
    default Node cloneNode(boolean deep) {
        return getNode().cloneNode(deep);
    }

    @Override
    default DOMConfiguration getDomConfig() {
        if (getNode() instanceof Document document) {
            return document.getDomConfig();
        }
        throw new DOMException((short)9, "Operation not Allowed.");
    }

    @Override
    default void normalizeDocument() {
        if (getNode() instanceof Document document) {
            document.normalizeDocument();
        } else {
            throw new DOMException((short)9, "Operation not Allowed.");
        }
    }

    @Override
    default void normalize() {
        getNode().normalize();
    }

    @Override
    default boolean isSupported(String feature, String version) {
        return getNode().isSupported(feature, version);
    }

    @Override
    default String getPrefix() {
        return getNode().getPrefix();
    }

    @Override
    default void setPrefix(String prefix) throws DOMException {
        getNode().setPrefix(prefix);
    }

    @Override
    default short compareDocumentPosition(Node other) throws DOMException {
        return getNode().compareDocumentPosition(other);
    }

    @Override
    default String getTextContent() throws DOMException {
        return getNode().getTextContent();
    }

    @Override
    default void setTextContent(String textContent) throws DOMException {
        getNode().setTextContent(textContent);
    }

    @Override
    default boolean isSameNode(Node other) {
        return getNode().isSameNode(other);
    }

    @Override
    default String lookupPrefix(String namespaceURI) {
        return getNode().lookupPrefix(namespaceURI);
    }

    @Override
    default boolean isDefaultNamespace(String namespaceURI) {
        return getNode().isDefaultNamespace(namespaceURI);
    }

    @Override
    default String lookupNamespaceURI(String prefix) {
        return getNode().lookupNamespaceURI(prefix);
    }

    @Override
    default boolean isEqualNode(Node arg) {
        return getNode().isEqualNode(arg);
    }

    @Override
    default Object getFeature(String feature, String version) {
        return getNode().getFeature(feature, version);
    }

    @Override
    default Object setUserData(String key, Object data, UserDataHandler handler) {
        return getNode().setUserData(key, data, handler);
    }

    @Override
    default Object getUserData(String key) {
        return getNode().getUserData(key);
    }

    @Override
    default String getInputEncoding() {
        if (getNode() instanceof Document document) {
            return document.getInputEncoding();
        }
        return null;
    }

    @Override
    default String getXmlEncoding() {
        if (getNode() instanceof Document document) {
            return document.getXmlEncoding();
        }
        return null;
    }

    @Override
    default boolean getXmlStandalone() {
        if (getNode() instanceof Document document) {
            return document.getXmlStandalone();
        }
        return false;
    }

    @Override
    default void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        if (getNode() instanceof Document document) {
            document.setXmlStandalone(xmlStandalone);
        } else {
            throw new DOMException((short)9, "Operation not Allowed.");
        }
    }

    @Override
    default String getXmlVersion() {
        if (getNode() instanceof Document document) {
            return document.getXmlVersion();
        }
        return getOwnerDocument().getXmlVersion();
    }

    @Override
    default void setXmlVersion(String xmlVersion) throws DOMException {
        if (getNode() instanceof Document document) {
            document.setXmlVersion(xmlVersion);
        } else {
            throw new DOMException((short)9, "Operation not Allowed.");
        }
    }

    @Override
    default boolean getStrictErrorChecking() {
        if (getNode() instanceof Document document) {
            return document.getStrictErrorChecking();
        }
        return true;
    }

    @Override
    default void setStrictErrorChecking(boolean strictErrorChecking) {
        if (getNode() instanceof Document document) {
            document.setStrictErrorChecking(strictErrorChecking);
        } else {
            throw new DOMException((short)9, "Operation not Allowed.");
        }
    }

    @Override
    default String getDocumentURI() {
        if (getNode() instanceof Document document) {
            return document.getDocumentURI();
        }
        return null;
    }

    @Override
    default void setDocumentURI(String documentURI) {
        if (getNode() instanceof Document document) {
            document.setDocumentURI(documentURI);
        } else {
            throw new DOMException((short)9, "Operation not Allowed.");
        }
    }
}
