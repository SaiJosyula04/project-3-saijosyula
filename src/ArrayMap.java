import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * A map implementation using two arrays for keys and values.
 * This implementation provides basic map operations and grows dynamically.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class ArrayMap<K, V> extends AbstractMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private Object[] keys;
    private Object[] values;
    private int size;
    
    /**
     * Constructs an empty ArrayMap with the default initial capacity (16).
     */
    public ArrayMap() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Constructs an empty ArrayMap with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the ArrayMap
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public ArrayMap(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
        }
        this.keys = new Object[initialCapacity];
        this.values = new Object[initialCapacity];
        this.size = 0;
    }
    
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key, the old value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    @Override
    public V put(K key, V value) {
    	
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        
        // Check if key already exists
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                @SuppressWarnings("unchecked")
                V oldValue = (V) values[i];
                values[i] = value;
                return oldValue;
            }
        }
        
        // Key doesn't exist, add new mapping
        if (size == keys.length) {
            growArrays();
        }
        
        keys[size] = key;
        values[size] = value;
        size++;
        return null;
    }
    
    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return (V) values[i];
            }
        }
        return null;
    }
    
    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map
     */
    @Override
    public int size() {
        return size;
    }
    
    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if this map contains no key-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns true if this map maps one or more keys to the specified value.
     *
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(values[i], value)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Removes the mapping for a key from this map if it is present.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    @Override
    @SuppressWarnings("unchecked")
    public V remove(Object key) {
        for (int i = 0; i < size; i++) {
            if (keys[i].equals(key)) {
                V oldValue = (V) values[i];
                
                // Shift all subsequent elements left
                for (int j = i; j < size - 1; j++) {
                    keys[j] = keys[j + 1];
                    values[j] = values[j + 1];
                }
                
                // Clear last elements
                keys[size - 1] = null;
                values[size - 1] = null;
                size--;
                
                return oldValue;
            }
        }
        return null;
    }
    
    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        Arrays.fill(keys, 0, size, null);
        Arrays.fill(values, 0, size, null);
        size = 0;
    }
    
    /**
     * Returns a Set view of the mappings contained in this map.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        return new ArrayMapEntrySet();
    }
    
    /**
     * Doubles the capacity of the key and value arrays when they are full.
     */
    private void growArrays() {
        int newCapacity = keys.length == 0 ? DEFAULT_CAPACITY : keys.length * 2;
        keys = Arrays.copyOf(keys, newCapacity);
        values = Arrays.copyOf(values, newCapacity);
    }
    
    /**
     * Inner class representing the set of entries in the ArrayMap.
     */
    private class ArrayMapEntrySet extends AbstractSet<Entry<K, V>> {
        
        /**
         * Returns the number of elements in this set.
         *
         * @return the number of elements in this set
         */
        @Override
        public int size() {
            return ArrayMap.this.size();
        }
        
        /**
         * Returns true if this set contains the specified element.
         *
         * @param o element whose presence in this set is to be tested
         * @return true if this set contains the specified element
         */
        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            
            Entry<K, V> entry = (Entry<K, V>) o;
            K key = entry.getKey();
            V value = entry.getValue();
            
            for (int i = 0; i < size; i++) {
                if (keys[i].equals(key)) {
                    @SuppressWarnings("unchecked")
                    V currentValue = (V) values[i];
                    return Objects.equals(currentValue, value);
                }
            }
            return false;
        }
        
        /**
         * Returns an iterator over the elements in this set.
         *
         * @return an iterator over the elements in this set
         */
        @Override
        public Iterator<Entry<K, V>> iterator() {
            return new ArrayMapEntrySetIterator();
        }
    }
    
    /**
     * Inner class providing an iterator over the entries in the ArrayMap.
     */
    private class ArrayMapEntrySetIterator implements Iterator<Entry<K, V>> {
        private int currentIndex = 0;
        private boolean canRemove = false;
        
        /**
         * Returns true if the iteration has more elements.
         *
         * @return true if the iteration has more elements
         */
        @Override
        public boolean hasNext() {
            return currentIndex < size;
        }
        
        /**
         * Returns the next element in the iteration.
         *
         * @return the next element in the iteration
         * @throws NoSuchElementException if the iteration has no more elements
         */
        @Override
        @SuppressWarnings("unchecked")
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements");
            }
            
            K key = (K) keys[currentIndex];
            V value = (V) values[currentIndex];
            Entry<K, V> entry = new SimpleEntry<>(key, value);
            
            currentIndex++;
            canRemove = true;
            
            return entry;
        }
        
        /**
         * Removes from the underlying collection the last element returned by this iterator.
         *
         * @throws IllegalStateException if the next method has not yet been called,
         *         or the remove method has already been called after the last call to the next method
         */
        @Override
        public void remove() {
            if (!canRemove) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            
            // Remove the element we just returned
            int removeIndex = currentIndex - 1;
            
            // Shift all subsequent elements left
            for (int i = removeIndex; i < size - 1; i++) {
                keys[i] = keys[i + 1];
                values[i] = values[i + 1];
            }
            
            // Clear last elements
            keys[size - 1] = null;
            values[size - 1] = null;
            size--;
            
            currentIndex--; // Adjust current index since we removed an element
            canRemove = false;
        }
    }
}