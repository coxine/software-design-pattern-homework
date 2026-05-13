public abstract class Node {
    protected final String name;

    protected Node(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract long getSize();

    public abstract boolean isDirectory();
}
