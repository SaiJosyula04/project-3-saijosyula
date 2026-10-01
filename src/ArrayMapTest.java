import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

/**
 * Comprehensive test suite for ArrayMap implementation with 100% branch coverage.
 * 
 * @author Sai Josyula
 */
public class ArrayMapTest {
    private ArrayMap<String, Integer> map;
    
    @BeforeEach
    void setUp() {
        map = new ArrayMap<>();
    }
    
    @Test
    void testConstructor() {
        assertNotNull(map);
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        
        ArrayMap<String, Integer> customMap = new ArrayMap<>(32);
        assertNotNull(customMap);
        assertEquals(0, customMap.size());
    }
    
    @Test
    void testConstructorWithInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new ArrayMap<>(-1));
    }
    
    @Test
    void testPutAndGet() {
        assertNull(map.put("one", 1));
        assertNull(map.put("two", 2));
        assertNull(map.put("three", 3));
        
        assertEquals(3, map.size());
        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertEquals(3, map.get("three"));
        assertNull(map.get("four"));
    }
    
    @Test
    void testPutUpdate() {
        assertNull(map.put("key", 100));
        assertEquals(100, map.put("key", 200));
        assertEquals(1, map.size());
        assertEquals(200, map.get("key"));
    }
    
    @Test
    void testPutNullKey() {
        assertThrows(NullPointerException.class, () -> map.put(null, 1));
    }
    
    @Test
    void testContainsKey() {
        map.put("one", 1);
        map.put("two", 2);
        
        assertTrue(map.containsKey("one"));
        assertTrue(map.containsKey("two"));
        assertFalse(map.containsKey("three"));
    }
    
    @Test
    void testContainsValue() {
        map.put("one", 1);
        map.put("two", 2);
        
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));
        assertFalse(map.containsValue(3));
        assertFalse(map.containsValue(null));
    }
    
    @Test
    void testRemove() {
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        assertEquals(2, map.remove("two"));
        assertNull(map.remove("two")); // Already removed
        assertEquals(2, map.size());
        assertFalse(map.containsKey("two"));
        
        assertEquals(1, map.remove("one"));
        assertEquals(3, map.remove("three"));
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }
    
    @Test
    void testClear() {
        map.put("one", 1);
        map.put("two", 2);
        
        assertEquals(2, map.size());
        map.clear();
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertFalse(map.containsKey("one"));
    }
    
    @Test
    void testEntrySetSize() {
        assertEquals(0, map.entrySet().size());
        
        map.put("one", 1);
        map.put("two", 2);
        
        assertEquals(2, map.entrySet().size());
    }
    
    @Test
    void testEntrySetContains() {
        map.put("one", 1);
        map.put("two", 2);
        
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        
        assertTrue(entrySet.contains(new SimpleEntry<>("one", 1)));
        assertTrue(entrySet.contains(new SimpleEntry<>("two", 2)));
        assertFalse(entrySet.contains(new SimpleEntry<>("three", 3)));
        assertFalse(entrySet.contains("not an entry"));
        assertFalse(entrySet.contains(new SimpleEntry<>("one", 999))); // Wrong value
        assertFalse(entrySet.contains(new SimpleEntry<>("wrong", 1))); // Wrong key
    }
    
    @Test
    void testEntrySetIterator() {
        map.put("one", 1);
        map.put("two", 2);
        
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entrySet.iterator();
        
        assertTrue(iterator.hasNext());
        Map.Entry<String, Integer> entry1 = iterator.next();
        assertTrue(iterator.hasNext());
        Map.Entry<String, Integer> entry2 = iterator.next();
        assertFalse(iterator.hasNext());
        
        // Verify entries (order not guaranteed)
        Set<SimpleEntry<String, Integer>> expected = new HashSet<>();
        expected.add(new SimpleEntry<>("one", 1));
        expected.add(new SimpleEntry<>("two", 2));
        
        assertTrue(expected.contains(new SimpleEntry<>(entry1.getKey(), entry1.getValue())));
        assertTrue(expected.contains(new SimpleEntry<>(entry2.getKey(), entry2.getValue())));
    }
    
    @Test
    void testEntrySetIteratorNoSuchElement() {
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
    
    @Test
    void testEntrySetIteratorRemove() {
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        
        Map.Entry<String, Integer> entry = iterator.next();
        String removedKey = entry.getKey();
        
        iterator.remove();
        assertEquals(2, map.size());
        assertFalse(map.containsKey(removedKey));
        
        // Test double remove without next
        assertThrows(IllegalStateException.class, iterator::remove);
    }
    
    @Test
    void testEntrySetIteratorRemoveAll() {
        map.put("one", 1);
        map.put("two", 2);
        
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        
        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }
    
    @Test
    void testArrayGrowth() {
        // Test with small initial capacity to trigger growth
        ArrayMap<String, Integer> smallMap = new ArrayMap<>(2);
        
        smallMap.put("one", 1);
        smallMap.put("two", 2);
        smallMap.put("three", 3); // This should trigger growth
        
        assertEquals(3, smallMap.size());
        assertEquals(1, smallMap.get("one"));
        assertEquals(2, smallMap.get("two"));
        assertEquals(3, smallMap.get("three"));
    }
    
    @Test
    void testEmptyMapOperations() {
        assertFalse(map.containsKey("nonexistent"));
        assertNull(map.get("nonexistent"));
        assertNull(map.remove("nonexistent"));
        
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        assertFalse(iterator.hasNext());
    }
    
    @Test
    void testEqualsAndHashCodeConsistency() {
        map.put("a", 1);
        map.put("b", 2);
        
        Set<Map.Entry<String, Integer>> entrySet = map.entrySet();
        SimpleEntry<String, Integer> entry = new SimpleEntry<>("a", 1);
        
        assertTrue(entrySet.contains(entry));
        assertEquals(entry.hashCode(), new SimpleEntry<>("a", 1).hashCode());
    }
    
    @Test
    void testIterationAfterModification() {
        map.put("one", 1);
        map.put("two", 2);
        
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        
        // Modify map through iterator (should be allowed)
        iterator.next();
        iterator.remove();
        
        assertEquals(1, map.size());
    }
    
    @Test
    void testToString() {
        // Test that toString works (inherited from AbstractMap)
        map.put("key1", 1);
        String str = map.toString();
        assertTrue(str.contains("key1"));
        assertTrue(str.contains("1"));
    }
    
    @Test
    void testValuesCollection() {
        // Test values() method from AbstractMap
        map.put("one", 1);
        map.put("two", 2);
        
        Collection<Integer> values = map.values();
        assertEquals(2, values.size());
        assertTrue(values.contains(1));
        assertTrue(values.contains(2));
    }
    
    @Test
    void testKeySet() {
        // Test keySet() method from AbstractMap
        map.put("one", 1);
        map.put("two", 2);
        
        Set<String> keySet = map.keySet();
        assertEquals(2, keySet.size());
        assertTrue(keySet.contains("one"));
        assertTrue(keySet.contains("two"));
    }
    
    @Test
    void testEntrySetIsEmpty() {
        assertTrue(map.entrySet().isEmpty());
        
        map.put("test", 1);
        assertFalse(map.entrySet().isEmpty());
    }
    
    @Test
    void testIteratorRemoveWithoutNext() {
        map.put("one", 1);
        Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
        
        assertThrows(IllegalStateException.class, iterator::remove);
    }
    
    @Test
    void testLargeNumberOfEntries() {
        // Test with many entries to ensure array growth works properly
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, i);
        }
        
        assertEquals(1000, map.size());
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, map.get("key" + i));
        }
    }
}