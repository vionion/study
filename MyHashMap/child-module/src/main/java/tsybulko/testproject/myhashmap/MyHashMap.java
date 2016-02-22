package tsybulko.testproject.myhashmap;

import tsybulko.testproject.myhashmap.entity.Entry;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/16/2016
 */

public class MyHashMap<K, V> implements IMap<K, V> {

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
        int myHash = (key == null) ? 0 : getHash(key.hashCode());
        Entry<K, V> entry = new Entry<K, V>(key, value, myHash);
        int index = getIndex(myHash, table.length);
        boolean nullOldEntryKey = table[index] == null || table[index].getKey() == null;
        boolean nullNewEntryKey = entry.getKey() == null;
        if (table[index] == null) {
            table[index] = entry;
        } else if (nullNewEntryKey & nullOldEntryKey) {
            replaceByNew(entry, index);
        } else if (nullOldEntryKey) {
            if (table[index].isLastInQueue()) {
                table[index].setNext(entry);
            } else {
                insertInQueue(table[index].getNext(), entry, table[index]);
            }
        } else if (nullNewEntryKey) {
            entry.setNext(table[index]);
            table[index] = entry;
        } else if (table[index].getKey().equals(entry.getKey())) {
            replaceByNew(entry, index);
        } else {
            insertInQueue(table[index], entry, null);
        }
        if ((size + 1.) / capacity > loadFactor) {
            rehash();
        }
        size++;
    }

    private void replaceByNew(Entry<K, V> newEntry, int oldEntryIndex) {
        newEntry.setNext(table[oldEntryIndex].getNext());
        table[oldEntryIndex] = newEntry;
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    /**
     * Function for enlarging {@link MyHashMap#table}, when {@link MyHashMap#loadFactor} is exceeded
     */
    private void rehash() {
        capacity <<= 1;
        Entry<K, V>[] tableNew = new Entry[capacity];
        for (int i = 0; i < table.length; i++) {
            if (table[i] == null) {
                continue;
            }
            int newIndex = getIndex(table[i].getHash(), capacity);
            tableNew[newIndex] = table[i];
        }
        table = tableNew;
    }

    /**
     * Trivial method of this class for achieving hash code
     * from key
     *
     * @param keyHash Just hashCode of inserted key
     * @return int This returns hash, calculated by the internal rule.
     */
    private int getHash(int keyHash) {
        return keyHash;
    }

    /**
     * Method for calculating basket index in {@link MyHashMap#table} for
     * some hash of some inserted key
     *
     * @param myHash Hash of some inserted key
     * @param length Length of table for inserting
     * @return int This returns index of basket for inserting.
     */
    private int getIndex(int myHash, int length) {
        return myHash & (length - 1);
    }

    /**
     * Function for inserting entry for cases with collisions
     *
     * @param queueUnit     Entry under observation
     * @param entryToInsert Entry which need to be inserted
     * @param previousEntry Entry before {@param queueUnit}; can be null, if {@param queueUnit} is in the very beginning of queue
     */
    private void insertInQueue(Entry<K, V> queueUnit, Entry<K, V> entryToInsert, Entry<K, V> previousEntry) {
        if (queueUnit.getKey() != null && queueUnit.getKey().equals(entryToInsert.getKey())) {
            entryToInsert.setNext(queueUnit.getNext());
            if (previousEntry != null) {
                previousEntry.setNext(entryToInsert);
            }
        } else {
            if (queueUnit.isLastInQueue()) {
                queueUnit.setNext(entryToInsert);
            } else {
                insertInQueue(queueUnit.getNext(), entryToInsert, queueUnit);
            }
        }
    }

    /**
     * Method for receiving Entry by its hash from queue; used in cases of collisions
     *
     * @param queueUnit Entry under observation
     * @param myHash    Hash of seeking Entry
     * @return V This returns value of seeking Entry.
     */
    private V getValueFromQueue(Entry<K, V> queueUnit, int myHash) {
        return queueUnit.getHash() == myHash ? queueUnit.getValue() : getValueFromQueue(queueUnit.getNext(), myHash);
    }

    public int getCapacity() {
        return capacity;
    }

    public float getLoadFactor() {
        return loadFactor;
    }

    public int getSize() {
        return size;
    }

}
