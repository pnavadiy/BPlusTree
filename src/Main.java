public class Main {

    public static void main(String[] args) {
        BPlusTree<String, String> tree = new BPlusTree<>(2, 3, 8, 16);
        GUI gui = new GUI(tree);
        gui.initializeGui();
    }

}
