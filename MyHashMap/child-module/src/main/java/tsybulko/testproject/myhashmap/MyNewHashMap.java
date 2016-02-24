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

    /**
     * Inserts e in the desired position in the table.
     *
     * @param e     entry for inserting
     * @param table table, where entry should be inserted in
     * @return the previous entry associated with <tt>e.getKey()</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>e.getKey()</tt>.
     * (A <tt>e.getKey()</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>e.getKey()</tt>.)
     */
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

    /**
     * Returns Entry from table, which is associated with k.
     *
     * @param k     key of unknown entity
     * @param table table, where entry should be found in
     * @return the previous entry associated with <tt>k</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>k</tt>.
     */
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

    /**
     * Inserts Entry e in table[index] or finds an appropriate place for this Entry
     * in queue of entries in the basket table[index].
     *
     * @param e     entry for inserting
     * @param index index of basket in table for inserting
     * @param table table, where entry should be stored in
     */
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

    /**
     * Should be triggered at exceeding of the table size a given loadFactor.
     * Table increases twice, and all old entries achieves new places due to
     * their hashes and due to the size of new table.
     */
    private void rehash() {
        capacity <<= 1;
        Entry<K, V>[] tableNew = new Entry[capacity];
        for (Entry<K, V> oldEntry : table) {
            reassignEntries(oldEntry, tableNew);
        }
        table = tableNew;
    }

    /**
     * Finds an appropriate place for old entries from one of queues of old table
     *
     * @param oldEntry the first (may be also the last or even null) entry in queue
     *                 of old table`s basket
     * @param newTable table for reassigning entries
     */
    private void reassignEntries(Entry<K, V> oldEntry, Entry<K, V>[] newTable) {
        Entry<K, V> tmpNextEntry;
        while (oldEntry != null) {
            int newIndex = index(oldEntry.getHash(), capacity);
            tmpNextEntry = oldEntry.getNext();
            oldEntry.setNext(null);
            insert(oldEntry, newIndex, newTable);
            oldEntry = tmpNextEntry;
        }

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