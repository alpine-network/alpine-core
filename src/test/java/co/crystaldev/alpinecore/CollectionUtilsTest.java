package co.crystaldev.alpinecore;

import co.crystaldev.alpinecore.util.CollectionUtils;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Thomas Wearmouth
 */
class CollectionUtilsTest {
    // region list()
    @Test
    void list_withVarargs_returnsCorrect() {
        String[] controlArray = { "This", "is", "a", "test!" };
        List<String> testList = CollectionUtils.list(controlArray);

        assertEquals(testList.size(), controlArray.length);

        for (String controlString : controlArray) {
            assertTrue(testList.contains(controlString),
                    "Returned list missing expected element");
        }
    }

    @Test
    void list_withEmptyVarargs_returnsEmpty() {
        assertTrue(CollectionUtils.list().isEmpty(),
                "Returned list not empty");
    }

    @Test
    void list_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.list((Object[]) null));
    }
    // endregion

    // region linkedList()
    @Test
    void linkedList_withVarargs_returnsCorrect() {
        String[] controlArray = { "This", "is", "a", "test!" };
        LinkedList<String> testList = CollectionUtils.linkedList(controlArray);

        assertEquals(testList.size(), controlArray.length);

        for (int i = 0; i < testList.size(); i++) {
            String controlString = controlArray[i];
            String testString = testList.get(i);

            assertEquals(testString, controlString);
        }
    }

    @Test
    void linkedList_withEmptyVarargs_returnsEmpty() {
        assertTrue(CollectionUtils.linkedList().isEmpty(),
                "Returned list not empty");
    }

    @Test
    void linkedList_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.linkedList((Object[]) null));
    }
    // endregion

    // region set()
    @Test
    void set_withVarargs_returnsCorrect() {
        String[] controlArray = { "This", "is", "a", "a", "test!" };
        Set<String> testSet = CollectionUtils.set(controlArray);

        assertEquals(testSet.size(), Arrays.stream(controlArray).distinct().count());

        for (String controlString : controlArray) {
            assertTrue(testSet.contains(controlString),
                    "Returned set missing expected element");
        }
    }

    @Test
    void set_withEmptyVarargs_returnsEmpty() {
        assertTrue(CollectionUtils.set().isEmpty(),
                "Returned set not empty");
    }

    @Test
    void set_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.set((Object[]) null));
    }
    // endregion

    // region linkedSet()
    @Test
    void linkedSet_withVarargs_returnsCorrect() {
        String[] controlArray = { "This", "is", "a", "a", "test!", "a" };
        LinkedHashSet<String> testSet = CollectionUtils.linkedSet(controlArray);
        String[] deduplicatedArray = Arrays.stream(controlArray).distinct().toArray(String[]::new);

        assertEquals(testSet.size(), deduplicatedArray.length);

        int i = 0;
        for (String testString : testSet) {
            String controlString = deduplicatedArray[i];
            assertEquals(testString, controlString);
            i++;
        }
    }

    @Test
    void linkedSet_withEmptyVarargs_returnsEmpty() {
        assertTrue(CollectionUtils.linkedSet().isEmpty(),
                "Returned set not empty");
    }

    @Test
    void linkedSet_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.linkedSet((Object[]) null));
    }
    // endregion

    // region map()
    @Test
    void map_withVarargs_returnsCorrect() {
        Object[] controlArray = new Object[] {
                1, "1",
                2, "2",
                3, "3",
                4, "4",
                5, "5"
        };
        // The underlying implementation does not allow duplicate keys - we can use this to make our life easier
        Map<Integer, String> testMap = CollectionUtils.map(1, "1", controlArray);

        assertEquals(testMap.size(), controlArray.length / 2);

        for (int i = 0; i < (controlArray.length / 2) * 2; i += 2) {
            Integer controlKey = (Integer) controlArray[i];
            String controlValue = (String) controlArray[i + 1];

            assertTrue(testMap.containsKey(controlKey),
                    "Returned map missing expected key");

            assertEquals(testMap.get(controlKey), controlValue);
        }
    }

    @Test
    void map_withEmptyVarargs_returnsCorrect() {
        Map<Integer, String> testMap = CollectionUtils.map(1, "1");

        assertEquals(testMap.size(), 1);

        assertEquals(testMap.get(1), "1");
    }

    @Test
    void map_withInvalidVarargs_returnsCorrect() {
        Map<Integer, String> testMap = CollectionUtils.map(1, "1", 2);

        assertEquals(testMap.size(), 1);

        assertEquals(testMap.get(1), "1");
    }

    @Test
    void map_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.map(1, "1", (Object[]) null));
    }
    // endregion

    // region linkedMap()
    @Test
    void linkedMap_withVarargs_returnsCorrect() {
        Object[] controlArray = new Object[] {
                1, "1",
                2, "2",
                3, "3",
                4, "4",
                5, "5"
        };
        // The underlying implementation does not allow duplicate keys - we can use this to make our life easier
        LinkedHashMap<Integer, String> testMap = CollectionUtils.linkedMap(1, "1", controlArray);

        assertEquals(testMap.size(), controlArray.length / 2);

        LinkedList<Integer> keyList = new LinkedList<>(testMap.keySet());

        for (int i = 0; i < (controlArray.length / 2) * 2; i += 2) {
            Integer controlKey = (Integer) controlArray[i];
            String controlValue = (String) controlArray[i + 1];

            int adjustedIndex = (int) Math.ceil(i / 2.0D);
            Integer testKey = keyList.get(adjustedIndex);

            assertEquals(testKey, controlKey);

            assertEquals(testMap.get(testKey), controlValue);
        }
    }

    @Test
    void linkedMap_withEmptyVarargs_returnsCorrect() {
        LinkedHashMap<Integer, String> testMap = CollectionUtils.linkedMap(1, "1");

        assertEquals(testMap.size(), 1);

        assertEquals(testMap.get(1), "1");
    }

    @Test
    void linkedMap_withInvalidVarargs_returnsCorrect() {
        LinkedHashMap<Integer, String> testMap = CollectionUtils.linkedMap(1, "1", 2);

        assertEquals(testMap.size(), 1);

        assertEquals(testMap.get(1), "1");
    }

    @Test
    void linkedMap_withNullVarargs_thenFail() {
        assertThrows(NullPointerException.class, () -> CollectionUtils.linkedMap(1, "1", (Object[]) null));
    }
    // endregion
}
