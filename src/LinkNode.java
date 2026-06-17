public class LinkNode extends Node {
    private final Node target;

    public LinkNode(String name, Node target) {
        super(name);
        this.target = target;
    }

    public Node getTarget() {
        return target;
    }

    @Override
    public long getSize(SizeContext ctx) {
        return target.getSize(ctx);
    }

    @Override
    public boolean isLink() {
        return true;
    }
}
