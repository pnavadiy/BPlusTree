import java.util.ArrayList;
import java.util.List;

public class BPlusTree<K extends Comparable<K>, V> {

    // minimum number of nodes internal node can contain. Minimum 1.
    private final int internalNodeMinDegree;
    // maximum number of nodes internal node can contain. Minimum 1.
    private final int internalNodeMaxDegree;
    // minimum number of values that leaf node can contain. Minimum 1.
    private final int minNumberOfValues;
    // maximum number of values that leaf node can contain. Minimum 1.
    private final int maxNumberOfValues;
    // Root Node of the tree.
    private Node root;
    // First left leaf node.
    private LeafNode leftLeafNode;
    // Height of the tree.
    private int height;
    // Number of parent/internal fusions.
    private int internalNodeFusions;
    // Number of leaf node fusions.
    private int leafNodeFusions;
    // Number of internal node splits.
    private int internalNodeSplits;
    // Number of leaf node splits.
    private int leafNodeSplits;

    /**
     * Create a new BPlusTree.
     *
     * @param internalNodeMinDegree minimum number of nodes internal node can contain
     * @param internalNodeMaxDegree maximum number of nodes internal node can contain
     * @param minNumberOfValues     minimum number of values that leaf node can contain
     * @param maxNumberOfValues     maximum number of values that leaf node can contain
     */
    public BPlusTree(int internalNodeMinDegree,
                     int internalNodeMaxDegree,
                     int minNumberOfValues,
                     int maxNumberOfValues) {
        this.internalNodeMinDegree = internalNodeMinDegree;
        this.internalNodeMaxDegree = internalNodeMaxDegree;
        this.maxNumberOfValues = maxNumberOfValues;
        this.minNumberOfValues = minNumberOfValues;
        this.leftLeafNode = new LeafNode();
        this.root = this.leftLeafNode;
        this.height = 0;
        this.internalNodeFusions = 0;
        this.internalNodeSplits = 0;
        this.leafNodeFusions = 0;
        this.leafNodeSplits = 0;
    }

    /**
     * @return root of the tree.
     */
    public Node getRoot() {
        return root;
    }

    /**
     * @return the first left leaf node.
     */
    public LeafNode getLeftLeafNode() {
        return leftLeafNode;
    }

    /**
     * @return current height of the tree.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return number of internal node fusions.
     */
    public int getInternalNodeFusions() {
        return internalNodeFusions;
    }

    /**
     * @return number of internal node splits.
     */
    public int getInternalNodeSplits() {
        return internalNodeSplits;
    }

    /**
     * @return number of leaf node fusions.
     */
    public int getLeafNodeFusions() {
        return leafNodeFusions;
    }

    /**
     * @return number of leaf node splits.
     */
    public int getLeafNodeSplits() {
        return leafNodeSplits;
    }

    /**
     * @return number of total fusions.
     */
    public int getFusions() {
        return internalNodeFusions + leafNodeFusions;
    }

    /**
     * @return number of total splits.
     */
    public int getSplits() {
        return internalNodeSplits + leafNodeSplits;
    }

    /**
     * Finds the key.
     *
     * @param key key to look for
     * @return Value if key is found otherwise null
     */
    public V find(K key) {
        V val = this.root.find(key);
        if (val == null) System.out.println("Value not found");
        return val;
    }

    /**
     * Return list of N values from key (inclusive).
     * @param key key to search from.
     * @param n number of pairs to return from next of key.
     * @return list of values to return.
     */
    public List<Pair<K, V>> getNKeyValPair(K key, int n){
        return this.root.getNKeyValPair(key, n);
    }

    /**
     * Inserts the key and value inside the Node.
     *
     * @param key   key for the value
     * @param value value to insert
     */
    public void insert(K key, V value) {
        if (key == null) return;
        Node newRoot = this.root.insert(key, value);
        if (newRoot != null) this.root = newRoot;
        this.leftLeafNode = this.root.refreshLeft();
    }

    /**
     * Removes the value associated with the key.
     *
     * @param key key to look for
     * @return Value if key is found otherwise null
     */
    public boolean remove(K key) {
        boolean success = this.root.remove(key);
        if (!success) System.out.println("Removed failed.");
        return success;
    }

    /**
     * Modifies value of a key. It does it by removing the key first then inserting (key,value).
     * @param key key to modify
     * @param value new value for the key
     * @return True if modify was successful otherwise false.
     */
    public boolean modify(K key, V value){
        if(!remove(key)){
            return false;
        } else {
            insert(key, value);
        }
        return true;
    }

    /**
     * Increments height of the three.
     */
    private void incrementHeight() {
        this.height++;
    }

    /**
     * Decrement height of the three.
     */
    private void decrementHeight() {
        this.height--;
    }

    /**
     * Increments number of internal node fusions.
     */
    private void incrementInternalNodeFusions() {
        internalNodeFusions++;
    }

    /**
     * Increments number of internal node splits.
     */
    private void incrementInternalNodeSplits() {
        internalNodeSplits++;
    }

    /**
     * Increments number of leaf node fusions.
     */
    private void incrementLeafNodeFusions() {
        leafNodeFusions++;
    }

    /**
     * Increments number of internal node splits.
     */
    private void incrementLeafNodeSplits() {
        leafNodeSplits++;
    }

    /**
     * Prints all the values inside the Tree.
     */
    public void PrintValues() {
        System.out.println("Tree:");
        this.root.Print();
    }

    /**
     * Merges rightNode into the leftNode.
     *
     * @param leftNode  Node to which merge the values
     * @param rightNode Node whose values to merge.
     */
    private void mergeNode(Node leftNode, Node rightNode) {
        leftNode.degree += rightNode.degree;
        leftNode.keys.addAll(rightNode.keys);
        if (leftNode.isLeafNode()) {
            LeafNode leafLeftNode = (LeafNode) leftNode;
            LeafNode leafRightNode = (LeafNode) rightNode;
            leafLeftNode.values.addAll(leafRightNode.values);
        } else {
            InternalNode internalLeftNode = (InternalNode) leftNode;
            InternalNode internalRightNode = (InternalNode) rightNode;
            internalLeftNode.childs.addAll(internalRightNode.childs);
        }
    }

    /**
     * Abstract Node class for both InternalNode and LeafNode.
     */
    public abstract class Node {
        //parent node
        protected InternalNode parent;

        // number of keys (child nodes)
        protected int degree;

        // Node minDegree
        protected int minDegree;

        // Node maxDegree
        protected int maxDegree;

        //key
        protected ArrayList<K> keys;

        /**
         * Create a new Node.
         *
         * @param minDegree minimum number for keys (childs)
         * @param maxDegree maximum number of keys(childs)
         */
        public Node(int minDegree, int maxDegree) {
            this.degree = 0;
            this.minDegree = minDegree;
            this.maxDegree = maxDegree;
            this.parent = null;
            this.keys = new ArrayList<>(this.maxDegree);
        }

        /**
         * Finds the key.
         *
         * @param key key to look for
         * @return Value if key is found otherwise null
         */
        protected abstract V find(K key);

        /**
         * Inserts the key and value inside the Node.
         *
         * @param key   key for the value
         * @param value value to insert
         * @return void
         */
        protected abstract Node insert(K key, V value);

        /**
         * Removed key from the tree.
         * @param key key to remove.
         * @return true if successful otherwise false.
         */
        protected abstract boolean remove(K key);

        /**
         * Return list of N values from key (inclusive).
         * @param key key to search from.
         * @param n number of pairs to return from next of key.
         * @return list of values to return.
         */
        public abstract List<Pair<K, V>> getNKeyValPair(K key, int n);

        /**
         * @return latest Leaf left node
         */
        protected abstract LeafNode refreshLeft();

        /**
         * @return the min key inside the subtree.
         */
        protected abstract K minKey();

        /**
         * Prints the tree.
         */
        public abstract void Print();

        /**
         * Determines if the Node is deficient or not.
         *
         * @return a boolean indicating whether the Node is deficient or not
         */
        public boolean isDeficient() {
            return this.degree < this.minDegree;
        }

        /**
         * Determines if the Node is capable of lending one of its degree to a deficient node.
         *
         * @return a boolean indicating whether or not the Node has enough degree degree in order to give one away.
         */
        public boolean isLendable() {
            return this.degree > this.minDegree;
        }

        /**
         * Determines if the Node is capable of being merged with.
         *
         * @return a boolean indicating whether or not the Node can be merged with.
         */
        public boolean isMergeable() {
            return this.degree == this.minDegree;
        }

        /**
         * Convenient method to determine if the current node is a LeafNode or not.
         *
         * @return True if Node is a LeafNode else returns False.
         */
        public boolean isLeafNode() {
            return false;
        }
    }

    public class InternalNode extends Node {

        // Child nodes.
        protected ArrayList<Node> childs;
        protected InternalNode left;
        protected InternalNode right;

        public InternalNode() {
            super(internalNodeMinDegree, internalNodeMaxDegree);
            this.childs = new ArrayList<>(this.maxDegree + 1);
            this.left = null;
            this.right = null;
        }

        @Override
        protected V find(K key) {
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) > 0) {
                    break;
                }
                i++;
            }
            return this.childs.get(i).find(key);
        }

        @Override
        public List<Pair<K, V>> getNKeyValPair(K key, int n){
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) > 0) {
                    break;
                }
                i++;
            }
            return this.childs.get(i).getNKeyValPair(key, n);
        }

        @Override
        protected Node insert(K key, V value) {
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) > 0) {
                    break;
                }
                i++;
            }
            return this.childs.get(i).insert(key, value);
        }

        @Override
        protected boolean remove(K key) {
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) > 0) {
                    break;
                }
                i++;
            }
            return this.childs.get(i).remove(key);
        }

        @Override
        protected LeafNode refreshLeft() {
            return this.childs.get(0).refreshLeft();
        }


        /**
         * Recursively insert nodes into the parent. Split the InternalNode if split is required.
         *
         * @param left  left node of split to insert
         * @param right right node of split to insert
         * @param key   old key value of the split.
         * @return New parent node if split is done otherwise null.
         */
        protected Node insertNode(Node left, Node right, K key) {
            // Directly inserts the keys and childs if the degree is zero.
            if (this.degree == 0) {
                this.keys.add(key);
                this.childs.add(left);
                this.childs.add(right);
                this.degree++;
                left.parent = this;
                right.parent = this;
                return this;
            }

            // Find the location of key
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) > 0) {
                    break;
                }
                i++;
            }

            // Insert left at index i and right at the end.
            this.keys.add(i, key);
            this.childs.add(i + 1, right);
            this.degree++;

            // Determine if you need to split
            if (this.degree <= this.maxDegree) {
                // No need to split.
                return null;
            }

            // Split is required. Split from the middle.
            int middle = this.degree / 2;

            // Create a new right node.
            InternalNode newRight = new InternalNode();
            newRight.degree = this.degree - middle - 1;
            newRight.parent = this.parent;

            // If the parent node is empty, then create a new InternalNode as parent.
            if (this.parent == null) {
                InternalNode newParent = new InternalNode();
                newRight.parent = newParent;
                this.parent = newParent;
                incrementHeight();
            }
            newRight.keys.addAll(this.keys.subList(middle + 1, this.degree));
            newRight.childs.addAll(this.childs.subList(middle + 1, this.degree + 1));

            // Update the parent pointer of new right child nodes.
            newRight.childs.forEach(child -> child.parent = newRight);

            // Modify left and right pointers
            newRight.right = this.right;
            this.right = newRight;
            newRight.left = this;

            // Make the current node left.
            this.degree = middle;
            K oldKey = this.keys.get(middle);
            this.keys = new ArrayList<>(this.keys.subList(0, middle));
            this.childs = new ArrayList<>(this.childs.subList(0, middle + 1));

            // Recursively call insertNode for newly created left and right node.
            incrementInternalNodeSplits();
            return this.parent.insertNode(this, newRight, oldKey);
        }

        /**
         * If internal node contains removedKey then updates key value by choosing correct value from child nodes.
         * Recursively travels up to the root node to make sure that no internal node contains removedKey.
         * @param removedKey key that has been removed from leaf node.
         */
        protected void refreshKey(K removedKey) {
            int index = this.keys.indexOf(removedKey);
            if (index >= 0) {
                this.keys.set(index, this.childs.get(index + 1).minKey());
            }

            // Base case
            if (this.parent != null) {
                this.parent.refreshKey(removedKey);
            }
        }

        @Override
        protected K minKey() {
            if (this.degree > 0) {
                return this.childs.get(0).minKey();
            }
            return null;
        }

        @Override
        public void Print() {
            System.out.println(this.keys.toString());
            this.childs.forEach(Node::Print);
        }

    }

    public class LeafNode extends Node {
        protected ArrayList<V> values;
        protected LeafNode left;
        protected LeafNode right;

        public LeafNode() {
            super(minNumberOfValues, maxNumberOfValues);
            this.values = new ArrayList<>(this.maxDegree);
            this.left = null;
            this.right = null;
        }

        @Override
        public boolean isLeafNode() {
            return true;
        }

        @Override
        protected V find(K key) {
            if (this.degree <= 0) {
                return null;
            }
            int i = 0;
            for (K k : this.keys) {
                if (k.compareTo(key) == 0) {
                    break;
                }
                i++;
            }
            if (i < this.degree) return this.values.get(i);
            return null;
        }

        @Override
        public List<Pair<K, V>> getNKeyValPair(K key, int n){
            List<Pair<K, V>> values = new ArrayList<>();

            // Find The index
            int i = 0;
            for (K k : this.keys) {
                if (k.compareTo(key) == 0) {
                    break;
                }
                i++;
            }

            // Add pairs.
            if (i < this.degree) {
                values.add(new Pair<>(this.keys.get(i), this.values.get(i)));

                // Add the next N pair
                while(++i < this.degree && n > 0){
                    values.add(new Pair<>(this.keys.get(i), this.values.get(i)));
                    n--;
                }
                if(n >0 && this.right != null){
                    values.addAll(this.right.getNKeyValPair(n));
                }

            }
            return values;
        }

        /**
         * Recursive method to get N elements from a node.
         * @param n Number of elements require.
         * @return List of N elements pair.
         */
        public List<Pair<K, V>> getNKeyValPair(int n){
            List<Pair<K, V>> values = new ArrayList<>();
            int i = 0;

            // Add the next N pair
            while(i < this.degree && n > 0){
                values.add(new Pair<>(this.keys.get(i), this.values.get(i)));
                n--;
                i++;
            }
            if(n >0 && this.right != null){
                values.addAll(this.right.getNKeyValPair(n));
            }
            return values;
        }

        @Override
        protected K minKey() {
            if (this.degree > 0) {
                return this.keys.get(0);
            }
            return null;
        }

        /**
         * @return new parent node if insert required a split else null.
         */
        @Override
        protected Node insert(K key, V value) {
            // Inserts the data first
            int i = 0;
            while (i < this.degree) {
                if (this.keys.get(i).compareTo(key) >= 0) {
                    break;
                }
                i++;
            }
            this.keys.add(i, key);
            this.values.add(i, value);
            this.degree++;

            // Determine if we need to split or not.
            if (this.degree <= this.maxDegree) {
                // No need to split.
                return null;
            }

            // Split is require.
            // Make the current node left and generate right node from middle.
            int middle = this.degree / 2;
            LeafNode newRight = new LeafNode();
            newRight.degree = this.degree - middle;
            newRight.parent = this.parent;

            // If the parent node is empty, then create a new InternalNode as the parent of current node.
            if (this.parent == null) {
                InternalNode newParent = new InternalNode();
                this.parent = newParent;
                newRight.parent = newParent;
                incrementHeight();
            }
            newRight.keys.addAll(this.keys.subList(middle, this.degree));
            newRight.values.addAll(this.values.subList(middle, this.values.size()));

            // Modify current to make it left.
            this.degree = middle;
            this.keys = new ArrayList<>(this.keys.subList(0, middle));
            this.values = new ArrayList<>(this.values.subList(0, middle));

            // Modify left and right pointers
            newRight.right = this.right;
            this.right = newRight;
            newRight.left = this;

            // Split is done. Insert the generated into the parent.
            incrementLeafNodeSplits();
            K oldKey = null;
            if (newRight.keys.size() > 0) oldKey = newRight.keys.get(0);
            return this.parent.insertNode(this, newRight, oldKey);
        }

        @Override
        protected boolean remove(K key) {
            int index = this.keys.indexOf(key);
            if (index < 0) {
                return false;
            }
            this.keys.remove(index);
            this.values.remove(index);
            this.degree--;

            if (this.parent != null) {
                this.parent.refreshKey(key);
            }
            return true;
        }

        @Override
        protected LeafNode refreshLeft() {
            if (this.degree < 0) {
                return null;
            }
            return this;
        }

        @Override
        public void Print() {
            System.out.println(this.values.toString());
        }
    }
}
