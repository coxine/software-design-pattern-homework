public class FileNode extends Node {
    private long size;

    public FileNode(String name, long size) {
        super(name);
        this.size = size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public long getSize(SizeContext ctx) {
        if (!ctx.visited.add(this)) {
            return 0;
        }
        return size;
    }

    @Override
    public boolean isFile() {
        return true;
    }
}
