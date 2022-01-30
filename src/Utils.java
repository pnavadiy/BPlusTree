import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Utils {
    // Default input file location. A relative path from project dir.
    // File must contains data in the format as listed in the README.md
    public static final String INPUT_FILE = "src" + File.separator + "data" + File.separator + "partfile.txt";

    // Key value separator regex.
    private static final String KEY_VALUE_SEPARATOR = " {8}";  // 8 white space.

    /**
     * Returns and absolute file path of a relative 'filePath'
     *
     * @param filePath A relative file path from root.
     * @return A valid absolute file path.
     */
    private static String getAbsoluteFilePath(String filePath) {
        return Paths.get(".").toAbsolutePath().normalize().toString()
                + File.separator + filePath;
    }

    /**
     * Loads the data from file into the tree.
     *
     * @param filePath location of the file to load data from
     * @param tree     BPlusTree into which data is loaded
     */
    public static void loadBPlusTreeFromFile(String filePath, BPlusTree<String, String> tree) throws IOException {
        String absoluteFilePath = getAbsoluteFilePath(filePath);
        Files.lines(Paths.get(absoluteFilePath)).forEach(line -> {
            String[] values = line.split(KEY_VALUE_SEPARATOR, 2);
            if (values.length != 2) {
                throw new IllegalArgumentException("Input file contains invalid formatted line: " + line);
            }
            tree.insert(values[0], values[1]);
        });
    }

    /**
     * Saves BPlusTree into the file.
     *
     * @param filePath An absolute file path to store BPlusTree.
     * @param tree     A BPlusTree to store into the file.
     */
    public static void saveBPlusTreeFromFile(String filePath, BPlusTree<String, String> tree) throws IOException {
        FileWriter writer = new FileWriter(getAbsoluteFilePath(filePath));
        BPlusTree<String, String>.LeafNode current = tree.getLeftLeafNode();
        while (current != null) {
            for (int i = 0; i < current.degree; i++) {
                writer.write(current.keys.get(i) + "        " + current.values.get(i) + "\n");
            }
            current = current.right;
        }
        writer.flush();
        writer.close();
    }
}
