package tsybulko.testproject.myhashmap;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/19/2016 18:00
 */
public interface IMap<K, V> {

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p>
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * <p>
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     *
     * @see #put(Object, Object)
     */
    V get(K key);

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with <tt>key</tt>, or
     * <tt>null</tt> if there was no mapping for <tt>key</tt>.
     * (A <tt>null</tt> return can also indicate that the map
     * previously associated <tt>null</tt> with <tt>key</tt>.)
     */
    void put(K key, V value);

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    void clear();

    /**
     * @return Return amount of inserted entities
     */
    int getSize();
}
