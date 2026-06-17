import java.util.Map;
import java.util.TreeMap;

public class DirectoryNode extends Node {
    private final Map<String, Node> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    @Override
    public long getSize(SizeContext ctx) {
        if (!ctx.visited.add(this)) {
            return 0;
        }
        long total = 0;
        for (Node child : children.values()) {
            total += child.getSize(ctx);
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

    public Node removeChild(String name) {
        return children.remove(name);
    }

    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public Iterable<String> getChildNames() {
        return children.keySet();
    }
}
