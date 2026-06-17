import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MemFs {
    private final DirectoryNode root;

    public MemFs() {
        this.root = new DirectoryNode("");
    }

    // -- Resolve methods --

    /**
     * Follow links until a non-link node is reached. Cycle-safe.
     */
    private Node followLinks(Node node) {
        Set<Node> seen = new HashSet<>();
        while (node instanceof LinkNode) {
            if (!seen.add(node)) break;
            node = ((LinkNode) node).getTarget();
        }
        return node;
    }

    /**
     * Resolve path, following links at every component including the last.
     * Returns the ultimate target node, or null if not found.
     */
    private Node resolveFollow(String normalizedPath) {
        if (normalizedPath.equals("/")) return root;

        String[] segs = normalizedPath.substring(1).split("/");
        Node cur = root;
        for (String seg : segs) {
            cur = followLinks(cur);
            if (!(cur instanceof DirectoryNode)) return null;
            Node child = ((DirectoryNode) cur).getChild(seg);
            if (child == null) return null;
            cur = child;
        }
        return followLinks(cur);
    }

    /**
     * Resolve path, following links for intermediate components but NOT the last.
     * Returns the node at the final component as-is (could be a link).
     */
    private Node resolveNoFollowLast(String normalizedPath) {
        if (normalizedPath.equals("/")) return root;

        String[] segs = normalizedPath.substring(1).split("/");
        Node cur = root;
        for (int i = 0; i < segs.length; i++) {
            cur = followLinks(cur);
            if (!(cur instanceof DirectoryNode)) return null;
            Node child = ((DirectoryNode) cur).getChild(segs[i]);
            if (child == null) return null;
            cur = child;
        }
        return cur;
    }

    /**
     * Resolve a path as a directory (following links). Returns null if not a directory.
     */
    private DirectoryNode resolveAsDir(String normalizedPath) {
        Node node = resolveFollow(normalizedPath);
        if (node instanceof DirectoryNode) return (DirectoryNode) node;
        return null;
    }

    // -- Commands --

    public void mkdir(String path) {
        String n = PathUtil.normalize(path);
        if (n == null || n.equals("/")) return;

        String parentPath = PathUtil.getParentPath(n);
        DirectoryNode parent = resolveAsDir(parentPath);
        if (parent == null) return;

        String name = PathUtil.getBaseName(n);
        Node existing = parent.getChild(name);
        if (existing != null && existing.isDirectory()
                && !(existing instanceof LinkNode)) {
            return; // already a directory, keep
        }
        parent.putChild(new DirectoryNode(name));
    }

    public void touch(String path, long size) {
        String n = PathUtil.normalize(path);
        if (n == null || n.equals("/")) return;

        String parentPath = PathUtil.getParentPath(n);
        DirectoryNode parent = resolveAsDir(parentPath);
        if (parent == null) return;

        String name = PathUtil.getBaseName(n);
        Node existing = parent.getChild(name);
        if (existing instanceof FileNode) {
            ((FileNode) existing).setSize(size);
        } else {
            parent.putChild(new FileNode(name, size));
        }
    }

    public List<String> ls(String path) {
        String n = PathUtil.normalize(path);
        if (n == null) return Collections.emptyList();

        Node node = resolveNoFollowLast(n);
        if (node == null) return Collections.emptyList();

        List<String> result = new ArrayList<>();

        if (node instanceof LinkNode) {
            Node target = followLinks(node);
            if (target instanceof DirectoryNode) {
                for (String childName : ((DirectoryNode) target).getChildNames()) {
                    result.add(childName);
                }
            } else {
                // Link points to file (or another link that ends at file)
                result.add(node.getName());
            }
        } else if (node instanceof DirectoryNode) {
            for (String childName : ((DirectoryNode) node).getChildNames()) {
                result.add(childName);
            }
        } else {
            // File
            result.add(node.getName());
        }

        return result;
    }

    public Long info(String path) {
        String n = PathUtil.normalize(path);
        if (n == null) return null;

        Node node = resolveNoFollowLast(n);
        if (node == null) return null;

        return node.getSize(new SizeContext());
    }

    public List<String> find(String path, String name) {
        String n = PathUtil.normalize(path);
        if (n == null) return Collections.emptyList();

        Node node = resolveNoFollowLast(n);
        if (node == null) return Collections.emptyList();

        List<String> results = new ArrayList<>();
        Set<Node> expanded = new HashSet<>();
        findRecursive(n, node, name, expanded, results);
        Collections.sort(results);
        return results;
    }

    private void findRecursive(String absPath, Node node, String name,
                               Set<Node> expanded, List<String> results) {
        if (node.getName().equals(name)) {
            results.add(absPath);
        }

        Node target = followLinks(node);

        if (target instanceof DirectoryNode) {
            if (!expanded.add(target)) return;

            DirectoryNode dir = (DirectoryNode) target;
            // Process non-link children first so canonical paths are expanded before
            // linked directories, ensuring correct dedup order.
            List<String> nonLinks = new ArrayList<>();
            List<String> links = new ArrayList<>();
            for (String childName : dir.getChildNames()) {
                Node child = dir.getChild(childName);
                if (child instanceof LinkNode) {
                    links.add(childName);
                } else {
                    nonLinks.add(childName);
                }
            }
            processFindChildren(absPath, dir, nonLinks, name, expanded, results);
            processFindChildren(absPath, dir, links, name, expanded, results);
        }
    }

    private void processFindChildren(String absPath, DirectoryNode dir, List<String> names,
                                     String name, Set<Node> expanded, List<String> results) {
        for (String childName : names) {
            Node child = dir.getChild(childName);
            String childPath = absPath.equals("/") ? "/" + childName : absPath + "/" + childName;
            findRecursive(childPath, child, name, expanded, results);
        }
    }

    public void rm(String path) {
        String n = PathUtil.normalize(path);
        if (n == null || n.equals("/")) return;

        Node node = resolveNoFollowLast(n);
        if (node == null) return;

        // If it's a directory (not a link to directory), it must be empty
        if (node.isDirectory() && !node.isLink()) {
            DirectoryNode dir = (DirectoryNode) node;
            if (!dir.isEmpty()) return;
        }

        String parentPath = PathUtil.getParentPath(n);
        Node parent = resolveFollow(parentPath);
        if (!(parent instanceof DirectoryNode)) return;

        String name = PathUtil.getBaseName(n);
        ((DirectoryNode) parent).removeChild(name);
    }

    public void link(String srcPath, String dstPath) {
        String srcNorm = PathUtil.normalize(srcPath);
        String dstNorm = PathUtil.normalize(dstPath);
        if (srcNorm == null || dstNorm == null || dstNorm.equals("/")) return;

        Node srcNode = resolveNoFollowLast(srcNorm);
        if (srcNode == null) return;

        String dstParentPath = PathUtil.getParentPath(dstNorm);
        DirectoryNode dstParent = resolveAsDir(dstParentPath);
        if (dstParent == null) return;

        // Link to the underlying node (follow links on src)
        Node target = followLinks(srcNode);

        String dstName = PathUtil.getBaseName(dstNorm);
        dstParent.putChild(new LinkNode(dstName, target));
    }
}
