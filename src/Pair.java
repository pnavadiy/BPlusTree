public class Pair<K, V> {
    private K key;
    private V val;

    // A simple pair class.
    public Pair(K key, V val){
        this.key = key;
        this.val = val;
    }

    public K getKey() {
        return key;
    }

    public V getVal() {
        return val;
    }

    @Override
    public String toString(){
        return key + " : " + val;
    }
}
