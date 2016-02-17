package tsybulko.testproject.myhashmap;

import tsybulko.testproject.myhashmap.entity.Entry;

public class MyHashMap<K, V> {

    private int capacity;
    private float loadFactor;
    private Entry<K, V>[] table;
    private int size;

    public MyHashMap() {
        this(16, 0.75f);
    }

    public MyHashMap(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        table = new Entry[this.capacity];
        size = 0;
    }

    public V get(K key) {
        if (key == null) {
            return (table[0] != null) ? table[0].getValue() : null;
        }
        int myHash = getHash(key.hashCode());
        int index = getIndex(myHash, capacity);
        if (table[index].isLastInQueue()) {
            return table[index].getValue();
        } else {
            // in case of collision
            return getValueFromQueue(table[index], myHash);
        }
    }

    public void put(K key, V value) {
        put(key, value, table);
        size++;
    }

    private void put(K key, V value, Entry<K, V>[] destination) {
        int myHash = (key == null) ? 0 : getHash(key.hashCode());
        Entry<K, V> entry = new Entry<K, V>(key, value, myHash);
        int index = (key == null) ? 0 : getIndex(myHash, destination.length);
        if (destination[index] == null) {
            destination[index] = entry;
        } else {
            // in case of collision
            insertInQueue(destination[index], entry);
        }
        if ((size + 1.) / capacity > loadFactor) {
            rehash();
        }
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        // is it normal to leave deadlocks? maybe yes
        size = 0;
    }

    private void rehash() {
        capacity <<= 1;
        Entry<K, V>[] tableNew = new Entry[capacity];
        Entry<K, V> insertingEntry;
        for (int i = 0; i < capacity >> 1; i++) {
            insertingEntry = table[i];
            if (insertingEntry == null) {
                continue;
            }
            do {
                put(insertingEntry.getKey(), insertingEntry.getValue(), tableNew);
                insertingEntry = insertingEntry.getNext();
            } while (insertingEntry != null);
        }
        table = tableNew;
    }

    private int getHash(int keyHash) {
        return keyHash;
    }

    private int getIndex(int myHash, int length) {
        return myHash & (length - 1);
    }

    private void insertInQueue(Entry<K, V> queueUnit, Entry<K, V> entryToInsert) {
        if (queueUnit.isLastInQueue()) {
            queueUnit.setNext(entryToInsert);
        } else {
            insertInQueue(queueUnit.getNext(), entryToInsert);
        }
        //queueUnit.isLastInQueue() ? queueUnit.setNext(entryToInsert) : insertInQueue(queueUnit.getNext(), entryToInsert);
    }

    private V getValueFromQueue(Entry<K, V> queueUnit, int myHash) {
        return queueUnit.getHash() == myHash ? queueUnit.getValue() : getValueFromQueue(queueUnit.getNext(), myHash);
    }


    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public float getLoadFactor() {
        return loadFactor;
    }

    public void setLoadFactor(float loadFactor) {
        this.loadFactor = loadFactor;
    }

    public Entry<K, V>[] getTable() {
        return table;
    }

    public void setTable(Entry<K, V>[] table) {
        this.table = table;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

}
