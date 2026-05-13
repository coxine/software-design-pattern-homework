import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemFs {
    private final DirectoryNode root;

    public MemFs() {
        this.root = new DirectoryNode("");
    }

    /**
     * Navigate to the node at the given absolute path.
     * Returns null if the path doesn't resolve to an existing node.
     */
    private Node resolve(String path) {
        if (path.equals("/")) {
            return root;
        }
        String[] segments = PathUtil.splitPath(path);
        if (segments == null) {
            return null;
        }
        Node current = root;
        for (String segment : segments) {
            if (!current.isDirectory()) {
                return null;
            }
            DirectoryNode dir = (DirectoryNode) current;
            current = dir.getChild(segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    /**
     * Navigate to the parent directory of the given path.
     * Returns null if the parent doesn't exist or path is invalid/root.
     */
    private DirectoryNode resolveParent(String path) {
        String parentPath = PathUtil.getParentPath(path);
        if (parentPath == null) {
            return null;
        }
        Node node = resolve(parentPath);
        if (node == null || !node.isDirectory()) {
            return null;
        }
        return (DirectoryNode) node;
    }

    public void mkdir(String path) {
        String[] segments = PathUtil.splitPath(path);
        if (segments == null) {
            return;
        }
        if (path.equals("/")) {
            return; // root always exists
        }
        DirectoryNode parent = resolveParent(path);
        if (parent == null) {
            return;
        }
        String name = PathUtil.getBaseName(path);
        Node existing = parent.getChild(name);
        if (existing != null && existing.isDirectory()) {
            return; // already a directory, keep as is
        }
        parent.putChild(new DirectoryNode(name));
    }

    public void touch(String path, long size) {
        String[] segments = PathUtil.splitPath(path);
        if (segments == null || segments.length == 0) {
            return; // can't touch root
        }
        DirectoryNode parent = resolveParent(path);
        if (parent == null) {
            return;
        }
        String name = PathUtil.getBaseName(path);
        parent.putChild(new FileNode(name, size));
    }

    public List<String> ls(String path) {
        Node node = resolve(path);
        if (node == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        if (node.isDirectory()) {
            DirectoryNode dir = (DirectoryNode) node;
            for (String name : dir.getChildNames()) {
                result.add(name);
            }
        } else {
            result.add(node.getName());
        }
        return result;
    }

    public Long info(String path) {
        Node node = resolve(path);
        if (node == null) {
            return null;
        }
        return node.getSize();
    }
}
