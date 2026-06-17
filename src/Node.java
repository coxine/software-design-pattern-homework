public abstract class Node {
    protected final String name;

    protected Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract long getSize(SizeContext ctx);

    public boolean isDirectory() {
        return false;
    }

    public boolean isFile() {
        return false;
    }

    public boolean isLink() {
        return false;
    }
}
