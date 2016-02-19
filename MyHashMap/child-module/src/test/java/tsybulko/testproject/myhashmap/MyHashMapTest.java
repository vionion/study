package tsybulko.testproject.myhashmap;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Vitalii Tsybulko
 * @version 1.0
 * @since 02/17/2016
 */
public class MyHashMapTest {

    private MyHashMap<String, String> generalTestMap;

    @Before
    public void setUp() throws Exception {
        generalTestMap = new MyHashMap<String, String>();
    }

    @Test
    public void testConstructorWithoutParams() throws Exception {
        MyHashMap testMap = new MyHashMap();
        assertEquals(16, testMap.getCapacity());
        assertEquals(0.75f, testMap.getLoadFactor(), 2);
    }

    @Test
    public void testConstructorWithParams() throws Exception {
        MyHashMap testMap = new MyHashMap(20, 0.155151f);
        assertEquals(20, testMap.getCapacity());
        assertEquals(0.155151f, testMap.getLoadFactor(), 6);
    }

    @Test
    public void testPutNull() throws Exception {
        generalTestMap.put(null, "nullValue");
        assertSame("nullValue", generalTestMap.get(null));
        generalTestMap.put("test1key", "test1value");
        generalTestMap.put(null, "nullValue2");
        assertSame("nullValue2", generalTestMap.get(null));
        generalTestMap.put("test1key", "test2value");
        assertSame("test2value", generalTestMap.get("test1key"));
    }

    @Test
    public void testPutNullAfterUsualInsert() throws Exception {
        generalTestMap.put("test1key", "test1value");
        generalTestMap.put(null, "nullValue");
        assertSame("nullValue", generalTestMap.get(null));
    }

    @Test
    public void testPut() throws Exception {
        generalTestMap.put("test1key", "test1value");
        assertSame("test1value", generalTestMap.get("test1key"));
        generalTestMap.put("test4key", "test4value1");
        generalTestMap.put("test4key", "test4value2");
        assertSame("test4value2", generalTestMap.get("test4key"));
    }

    @Test
    public void testGet() throws Exception {
        assertNull(generalTestMap.get(null));
        generalTestMap.put("test2key", "test2value");
        assertSame("test2value", generalTestMap.get("test2key"));
    }

    @Test
    public void testClear() throws Exception {
        generalTestMap.put("test3key", "test3value");
        assertTrue(generalTestMap.getSize() > 0);
        generalTestMap.clear();
        assertTrue(generalTestMap.getSize() == 0);
    }

    @Test
    public void testRehash() throws Exception {
        int initialCapacity = generalTestMap.getCapacity();
        int tableSize = generalTestMap.getSize();
        int minimalNeededToRehashEntityCount = (int) Math.ceil((float) generalTestMap.getCapacity() * generalTestMap.getLoadFactor()) + 1;
        for (int i = 0; i < minimalNeededToRehashEntityCount; i++) {
            generalTestMap.put(String.valueOf((char) (97 + i)), "testRehashValue" + i);
            tableSize++;
        }
        assertEquals(tableSize, generalTestMap.getSize());
        assertEquals(tableSize, minimalNeededToRehashEntityCount);
        assertTrue(initialCapacity < generalTestMap.getCapacity());
    }

    @Test
    public void testRehashWithCollisions() throws Exception {
        int initialCapacity = generalTestMap.getCapacity();
        int tableSize = generalTestMap.getSize();
        int minimalNeededToRehashEntityCount = (int) Math.ceil((float) generalTestMap.getCapacity() * generalTestMap.getLoadFactor()) + 1;
        for (int i = 0; i < minimalNeededToRehashEntityCount; i++) {
            generalTestMap.put("testRehashCollisionsKey", "testRehashValue" + i);
            tableSize++;
        }
        assertEquals(tableSize, generalTestMap.getSize());
        assertEquals(tableSize, minimalNeededToRehashEntityCount);
        assertTrue(initialCapacity < generalTestMap.getCapacity());
    }

    @Test
    public void testWithUsualCollisions() throws Exception {
        MyHashMap<MyObjectWithStrangeHash, String> specialTestMap = new MyHashMap<MyObjectWithStrangeHash, String>(20, 0.75f);
        MyObjectWithStrangeHash firstInCollision = new MyObjectWithStrangeHash(16);
        MyObjectWithStrangeHash secondInCollision = new MyObjectWithStrangeHash(20);
        MyObjectWithStrangeHash thirdInCollision = new MyObjectWithStrangeHash(28);
        MyObjectWithStrangeHash fourthInCollision = new MyObjectWithStrangeHash(48);
        specialTestMap.put(firstInCollision, "firstInCollision");
        specialTestMap.put(secondInCollision, "secondInCollision");
        specialTestMap.put(thirdInCollision, "thirdInCollision");
        specialTestMap.put(fourthInCollision, "fourthInCollision");
        assertEquals(4, specialTestMap.getSize());
        assertEquals("firstInCollision", specialTestMap.get(firstInCollision));
        assertEquals("secondInCollision", specialTestMap.get(secondInCollision));
        assertEquals("thirdInCollision", specialTestMap.get(thirdInCollision));
        assertEquals("fourthInCollision", specialTestMap.get(fourthInCollision));
    }

    private class MyObjectWithStrangeHash {
        int hash;

        public MyObjectWithStrangeHash(int hash) {
            this.hash = hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}