package tsybulko.testproject.myhashmap;

import tsybulko.testproject.myhashmap.entity.Entry;

/**
 * @author Vitalii Tsybulko
 * @version 2.0
 * @since 02/23/2016 14:41
 */
public class MyNewHashMap<K, V> implements IMap<K, V> {

    private int capacity;
    private float loadFactor;
    private Entry<K, V>[] table;
    private int size;

    public MyNewHashMap() {
        this(16, 0.75f);
    }

    public MyNewHashMap(int capacity, float loadFactor) {
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        table = new Entry[this.capacity];
        size = 0;
    }

    public V get(K key) {
        Entry<K, V> entry = get(key, table);
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    public V put(K key, V value) {
        Entry<K, V> entry = new Entry<K, V>(key, value, hash(key));
        entry = put(entry, table);
        if (entry == null) {
            return null;
        } else {
            return entry.getValue();
        }
    }

    public void clear() {
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        size = 0;
    }

    private Entry<K, V> put(Entry<K, V> e, Entry<K, V>[] table) {
        int hash = hash(e.getKey());
        int index = index(hash, table.length);
        Entry<K, V> entryInTable = get(e.getKey(), table);
        Entry<K, V> result = entryInTable;
        if (entryInTable == null) {
            insert(e, index, table);
            if ((size + 1.) / capacity > loadFactor) {
                rehash();
            }
            size++;
        } else {
            entryInTable.setValue(e.getValue());
        }
        return result;
    }

    private Entry<K, V> get(K k, Entry<K, V>[] table) {
        int hash = hash(k);
        int index = index(hash, table.length);
        Entry<K, V> entry4Compare = table[index];
        while (entry4Compare != null) {
            if (entry4Compare.getHash() == hash &&
                ((k == null & entry4Compare.getKey() == null) ||
                    (k != null && k.equals(entry4Compare.getKey())))) {
                return entry4Compare;
            }
            entry4Compare = entry4Compare.getNext();
        }
        return null;
    }

    private void insert(Entry<K, V> e, int index, Entry<K, V>[] table) {
        Entry<K, V> entryFromBasket = table[index];
        if (entryFromBasket == null) {
            table[index] = e;
        } else {
            while (!entryFromBasket.isLastInQueue()) {
                entryFromBasket = entryFromBasket.getNext();
            }
            entryFromBasket.setNext(e);
        }
    }

    private void rehash() {
        capacity <<= 1;
        Entry<K, V>[] tableNew = new Entry[capacity];
        for (Entry<K, V> oldEntry : table) {
            reassignEntries(oldEntry, tableNew);
        }
        table = tableNew;
    }

    private void reassignEntries(Entry<K, V> oldEntry, Entry<K, V>[] newTable) {
        if (oldEntry == null) {
            return;
        }
        int newIndex = index(oldEntry.getHash(), capacity);
        Entry<K, V> nextEntry = oldEntry.getNext();
        oldEntry.setNext(null);
        insert(oldEntry, newIndex, newTable);
        reassignEntries(nextEntry, newTable);
    }

    private int hash(K k) {
        if (k == null) {
            return 0;
        }
        return k.hashCode();
    }

    private int index(int hash, int capacity) {
        return hash & (capacity - 1);
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