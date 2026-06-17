import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        MemFs fs = new MemFs();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            processCommand(fs, line);
        }
    }

    private static void processCommand(MemFs fs, String line) {
        String[] tokens = line.split("\\s+");
        if (tokens.length == 0) {
            return;
        }
        String cmd = tokens[0];
        switch (cmd) {
            case "MKDIR":
                if (tokens.length >= 2) {
                    fs.mkdir(tokens[1]);
                }
                break;
            case "TOUCH":
                if (tokens.length >= 3) {
                    try {
                        long size = Long.parseLong(tokens[2]);
                        if (size >= 0) {
                            fs.touch(tokens[1], size);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                break;
            case "LS":
                if (tokens.length >= 2) {
                    List<String> result = fs.ls(tokens[1]);
                    for (String name : result) {
                        System.out.println(name);
                    }
                }
                break;
            case "INFO":
                if (tokens.length >= 2) {
                    Long size = fs.info(tokens[1]);
                    if (size != null) {
                        System.out.println(size);
                    }
                }
                break;
            case "FIND":
                if (tokens.length >= 3) {
                    List<String> result = fs.find(tokens[1], tokens[2]);
                    for (String p : result) {
                        System.out.println(p);
                    }
                }
                break;
            case "RM":
                if (tokens.length >= 2) {
                    fs.rm(tokens[1]);
                }
                break;
            case "LINK":
                if (tokens.length >= 3) {
                    fs.link(tokens[1], tokens[2]);
                }
                break;
        }
    }
}
