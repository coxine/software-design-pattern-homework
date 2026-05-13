import java.util.Map;
import java.util.TreeMap;

public class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override
    public long getSize() {
        long total = 0;
        for (Node child : children.values()) {
            total += child.getSize();
        }
        return total;
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    public Node getChild(String name) {
        return children.get(name);
    }

    public void putChild(Node node) {
        children.put(node.getName(), node);
    }

    public Iterable<String> getChildNames() {
        return children.keySet();
    }
}
