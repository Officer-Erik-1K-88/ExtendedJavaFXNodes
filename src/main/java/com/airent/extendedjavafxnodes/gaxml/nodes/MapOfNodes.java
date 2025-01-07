package com.airent.extendedjavafxnodes.gaxml.nodes;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class MapOfNodes extends ListOfNodes implements NamedNodeMap {
    private final ArrayList<String> names;
    private final ArrayList<Integer> namedIndex;
    private final ArrayList<String> namespaces;
    private final ArrayList<String> localNames;
    private final ArrayList<Integer> nsIndex;

    public MapOfNodes() {
        super();
        names = new ArrayList<>();
        namedIndex = new ArrayList<>();

        namespaces = new ArrayList<>();
        localNames = new ArrayList<>();
        nsIndex = new ArrayList<>();
    }

    public int namedSize() {
        return names.size();
    }

    public int nsSize() {
        return namespaces.size();
    }

    public boolean isNamedEmpty() {
        return names.isEmpty();
    }

    public boolean isNSEmpty() {
        return namespaces.isEmpty();
    }

    public int namedIndexOf(String name) {
        int index = names.indexOf(name);
        if (index != -1) return namedIndex.get(index);
        return -1;
    }

    public int namedNSIndexOf(String namespaceURI, String localName) {
        int index = namespaces.indexOf(namespaceURI);
        int index2 = localNames.indexOf(localName);
        if (index == index2) {
            if (index != -1) return nsIndex.get(index);
        }
        return -1;
    }
    public int namedNSLastIndexOf(String namespaceURI, String localName) {
        int index = namespaces.lastIndexOf(namespaceURI);
        int index2 = localNames.lastIndexOf(localName);
        if (index == index2) {
            if (index != -1) return nsIndex.get(index);
        }
        return -1;
    }

    @Override
    public Node getNamedItem(String name) {
        return item(namedIndexOf(name));
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        int index = namedIndexOf(arg.getNodeName());
        if (index != -1) {
            Node old = item(index);
            set(index, arg);
            return old;
        }
        names.add(arg.getNodeName());
        namedIndex.add(size());
        add(arg);
        return null;
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        int index = namedIndexOf(name);
        if (index != -1) {
            namedIndex.remove(Integer.valueOf(index));
            for (int i=names.indexOf(name); i < namedIndex.size(); i++) {
                namedIndex.set(i, namedIndex.get(i)-1);
            }
            names.remove(name);
            return remove(index);
        }
        return null;
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return item(namedNSIndexOf(namespaceURI, localName));
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        int index = namedNSIndexOf(arg.getNamespaceURI(), arg.getLocalName());
        if (index != -1) {
            Node old = item(index);
            set(index, arg);
            return old;
        }
        namespaces.add(arg.getNamespaceURI());
        localNames.add(arg.getLocalName());
        nsIndex.add(size());
        add(arg);
        return null;
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        int index = namedNSIndexOf(namespaceURI, localName);
        if (index != -1) {
            nsIndex.remove(Integer.valueOf(index));
            for (int i=namespaces.indexOf(namespaceURI); i < nsIndex.size(); i++) {
                nsIndex.set(i, nsIndex.get(i)-1);
            }
            namespaces.remove(namespaceURI);
            localNames.remove(localName);
            return remove(index);
        }
        return null;
    }
}
