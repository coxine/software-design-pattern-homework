import java.util.ArrayList;
import java.util.List;

public final class PathUtil {

    private PathUtil() {}

    /**
     * Normalize an absolute path. Returns null if the path is not absolute.
     * Handles: redundant /, ., .., trailing /.
     * Root's parent is still root.
     */
    public static String normalize(String path) {
        if (path == null || path.isEmpty() || path.charAt(0) != '/') {
            return null;
        }
        String[] parts = path.split("/");
        List<String> stack = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty() || part.equals(".")) {
                continue;
            }
            if (part.equals("..")) {
                if (!stack.isEmpty()) {
                    stack.remove(stack.size() - 1);
                }
            } else {
                stack.add(part);
            }
        }
        if (stack.isEmpty()) {
            return "/";
        }
        StringBuilder sb = new StringBuilder();
        for (String s : stack) {
            sb.append('/').append(s);
        }
        return sb.toString();
    }

    public static String getParentPath(String normalizedPath) {
        if (normalizedPath.equals("/")) {
            return null;
        }
        int lastSlash = normalizedPath.lastIndexOf('/');
        if (lastSlash == 0) {
            return "/";
        }
        return normalizedPath.substring(0, lastSlash);
    }

    public static String getBaseName(String normalizedPath) {
        if (normalizedPath.equals("/")) {
            return "";
        }
        int lastSlash = normalizedPath.lastIndexOf('/');
        return normalizedPath.substring(lastSlash + 1);
    }
}
