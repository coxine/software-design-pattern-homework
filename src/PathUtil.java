import java.util.ArrayList;
import java.util.List;

public final class PathUtil {

    private PathUtil() {}

    /**
     * Validates and splits an absolute path into segments.
     * Returns null if the path is invalid:
     *   - contains "//"
     *   - ends with "/" (unless it's just "/")
     *   - contains "." or ".." as a segment
     */
    public static String[] splitPath(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        if (!path.startsWith("/")) {
            return null;
        }
        // Root path
        if (path.equals("/")) {
            return new String[0];
        }
        // Trailing slash not allowed
        if (path.endsWith("/")) {
            return null;
        }
        // Split by "/", skip the first empty element before "/"
        String[] parts = path.split("/");
        // parts[0] is always "" because path starts with "/"
        List<String> segments = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            // "//" produces empty segments
            if (part.isEmpty()) {
                return null;
            }
            // "." or ".." not allowed
            if (part.equals(".") || part.equals("..")) {
                return null;
            }
            segments.add(part);
        }
        return segments.toArray(new String[0]);
    }

    /**
     * Returns the parent path of a given absolute path, or null for root.
     * Assumes the path is already validated.
     */
    public static String getParentPath(String path) {
        if (path.equals("/")) {
            return null;
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == 0) {
            return "/";
        }
        return path.substring(0, lastSlash);
    }

    /**
     * Returns the last component (name) of a path.
     */
    public static String getBaseName(String path) {
        if (path.equals("/")) {
            return "";
        }
        int lastSlash = path.lastIndexOf('/');
        return path.substring(lastSlash + 1);
    }
}
