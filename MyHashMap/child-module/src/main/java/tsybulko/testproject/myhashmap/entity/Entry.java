package tsybulko.testproject.myhashmap.entity;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/16/2016
 */

public class Entry<K, V> {

    K key;
    V value;
    int hash;
    Entry<K, V> next;

    public Entry(K key, V value, int hash) {
        this.key = key;
        this.value = value;
        this.hash = hash;
        this.next = null;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public int getHash() {
        return hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public Entry<K, V> getNext() {
        return next;
    }

    public void setNext(Entry<K, V> next) {
        this.next = next;
    }

    public boolean isLastInQueue() {
        return next == null;
    }
}
