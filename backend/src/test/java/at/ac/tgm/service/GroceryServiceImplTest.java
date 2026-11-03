package at.ac.tgm.service;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GroceryServiceImpl.
 *
 * Design decisions:
 *  - All service calls go through reflection so the tests compile and
 *    report "method not found" failures even if the interface or
 *    individual methods do not yet exist on the implementation.
 *  - Database state is seeded/verified with plain JdbcTemplate SQL against
 *    the schema described by the assignment (grocery_list / grocery_item),
 *    NOT via GroceryItemRepository/GroceryListRepository/entity classes.
 *    This means the test itself has zero compile-time dependency on
 *    at.ac.tgm.entity/at.ac.tgm.repository — it compiles and reports
 *    meaningful runtime failures (missing bean, missing table) no matter
 *    which of those classes exist yet.
 *  - A fresh grocery_list row is inserted before each test so items
 *    can be linked to a real list id.
 */
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("7a.3 Datenbank")
class GroceryServiceImplTest {

    // -----------------------------------------------------------------------
    // Spring-managed beans
    // -----------------------------------------------------------------------

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** The service under test – injected as Object so the file compiles even
     *  when the class does not implement an interface. */
    @Autowired
    private GroceryService groceryService;   // actual type: GroceryServiceImpl

    // -----------------------------------------------------------------------
    // Helpers – raw SQL setup/verification (no entity/repository classes)
    // -----------------------------------------------------------------------

    private void clearDb() {
        jdbcTemplate.update("DELETE FROM grocery_item");
        jdbcTemplate.update("DELETE FROM grocery_list");
    }

    private Long insertList(String name) {
        jdbcTemplate.update("INSERT INTO grocery_list (name) VALUES (?)", name);
        return jdbcTemplate.queryForObject("SELECT MAX(id) FROM grocery_list", Long.class);
    }

    private Long insertItem(String name, int amount, boolean collected, Long listId) {
        jdbcTemplate.update(
                "INSERT INTO grocery_item (name, amount, collected, list_id) VALUES (?, ?, ?, ?)",
                name, amount, collected, listId);
        return jdbcTemplate.queryForObject("SELECT MAX(id) FROM grocery_item", Long.class);
    }

    /** Returns the raw column values of the grocery_item row, or null if absent. */
    private Map<String, Object> findItem(Long id) {
        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList("SELECT * FROM grocery_item WHERE id = ?", id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    private boolean itemExists(Long id) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM grocery_item WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    private long itemCount() {
        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM grocery_item", Long.class);
        return count == null ? 0 : count;
    }

    // -----------------------------------------------------------------------
    // Helpers – reflective DTO construction
    // -----------------------------------------------------------------------

    /**
     * Returns a new GroceryItemDto instance via reflection.
     * Falls back gracefully so the test fails with a clear message rather
     * than a compile error when the DTO class does not exist yet.
     */
    private Object buildItemDto(Long id, String name, int amount,
                                boolean collected, Long listId) throws Exception {
        Class<?> dtoClass = Class.forName("at.ac.tgm.dto.GroceryItemDto");
        Object dto = dtoClass.getDeclaredConstructor().newInstance();
        setField(dto, "id",        id);
        setField(dto, "name",      name);
        setField(dto, "amount",    amount);
        setField(dto, "collected", collected);
        setField(dto, "listId",    listId);   // may be absent – swallowed
        return dto;
    }

    /** Tolerant setter: does nothing if the field does not exist. */
    private void setField(Object target, String fieldName, Object value) {
        try {
            var f = findField(target.getClass(), fieldName);
            if (f == null) return;
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception ignored) { }
    }

    private Field findField(Class<?> clazz, String name) {
        while (clazz != null) {
            try { return clazz.getDeclaredField(name); }
            catch (NoSuchFieldException e) { clazz = clazz.getSuperclass(); }
        }
        return null;
    }

    private Object getField(Object target, String fieldName) throws Exception {
        var f = findField(target.getClass(), fieldName);
        assertNotNull(f, "Field '" + fieldName + "' not found on " + target.getClass());
        f.setAccessible(true);
        return f.get(target);
    }

    /**
     * Resolves a method by name on the service, trying both the concrete
     * class and every interface it implements, so the lookup works regardless
     * of whether the service exposes the method via an interface or directly.
     */
    private Method resolveMethod(String name, Class<?>... paramTypes) {
        Class<?> clazz = groceryService.getClass();
        // Try concrete class first (catches package-private / non-interface methods)
        try { return clazz.getMethod(name, paramTypes); }
        catch (NoSuchMethodException ignore) { }
        // Walk declared methods for exact match
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(name) && Arrays.equals(m.getParameterTypes(), paramTypes)) {
                m.setAccessible(true);
                return m;
            }
        }
        fail("Method '" + name + "' with params " + Arrays.toString(paramTypes)
                + " not found on " + clazz.getName());
        return null; // unreachable
    }

    /** Unwraps InvocationTargetException so assertions see the real cause. */
    private Object invoke(Method m, Object... args) throws Exception {
        try {
            return m.invoke(groceryService, args);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception ex) throw ex;
            throw new RuntimeException(cause);
        }
    }

    // -----------------------------------------------------------------------
    // Test lifecycle
    // -----------------------------------------------------------------------

    private Long listId;   // id of the grocery_list row created per test

    @BeforeEach
    void setUp() {
        clearDb();
        listId = insertList("Test List");
    }

    // -----------------------------------------------------------------------
    // A – createGroceryItem
    // -----------------------------------------------------------------------

    @Test
    @Order(1)
    @DisplayName("A: createGroceryItem persists item and returns DTO with generated id")
    void createGroceryItem_persistsItem() throws Exception {
        Object dto = buildItemDto(null, "Milk", 2, false, listId);
        Method m = resolveMethod("createGroceryItem", dto.getClass());

        Object result = invoke(m, dto);

        assertNotNull(result, "createGroceryItem must not return null");
        Long returnedId = (Long) getField(result, "id");
        assertNotNull(returnedId, "Returned DTO must have a generated id");

        // Verify the row actually exists in the database
        Map<String, Object> row = findItem(returnedId);
        assertNotNull(row, "Item must be found in the database after creation");
        assertEquals("Milk", row.get("name"));
        assertEquals(2, ((Number) row.get("amount")).intValue());
        assertFalse((Boolean) row.get("collected"));
    }

    @Test
    @Order(2)
    @DisplayName("A: createGroceryItem without list id still persists the item")
    void createGroceryItem_withoutList() throws Exception {
        Object dto = buildItemDto(null, "Bread", 1, false, null);
        Method m = resolveMethod("createGroceryItem", dto.getClass());

        Object result = invoke(m, dto);
        Long returnedId = (Long) getField(result, "id");
        assertNotNull(returnedId);
        assertTrue(itemExists(returnedId));
    }

    // -----------------------------------------------------------------------
    // B – updateGroceryItem
    // -----------------------------------------------------------------------

    @Test
    @Order(3)
    @DisplayName("B: updateGroceryItem changes name and amount in the database")
    void updateGroceryItem_changesFields() throws Exception {
        Long id = insertItem("Eggs", 6, false, null);

        Object dto = buildItemDto(id, "Eggs (free range)", 12, false, null);
        Method m = resolveMethod("updateGroceryItem", dto.getClass());

        Object result = invoke(m, dto);
        assertNotNull(result);

        Map<String, Object> updated = findItem(id);
        assertEquals("Eggs (free range)", updated.get("name"));
        assertEquals(12, ((Number) updated.get("amount")).intValue());
    }

    // -----------------------------------------------------------------------
    // C – patchGroceryItem
    // -----------------------------------------------------------------------

    @Test
    @Order(4)
    @DisplayName("C: patchGroceryItem sets collected=true in the database")
    void patchGroceryItem_setsCollectedTrue() throws Exception {
        Long id = insertItem("Butter", 1, false, null);

        Method m = resolveMethod("patchGroceryItem", Long.class, boolean.class);
        Object result = invoke(m, id, true);
        assertNotNull(result);

        Map<String, Object> patched = findItem(id);
        assertTrue((Boolean) patched.get("collected"), "collected must be true after patch");
        // name and amount must not change
        assertEquals("Butter", patched.get("name"));
    }

    @Test
    @Order(5)
    @DisplayName("C: patchGroceryItem sets collected=false in the database")
    void patchGroceryItem_setsCollectedFalse() throws Exception {
        Long id = insertItem("Cheese", 1, true, null);

        Method m = resolveMethod("patchGroceryItem", Long.class, boolean.class);
        invoke(m, id, false);

        assertFalse((Boolean) findItem(id).get("collected"));
    }

    @Test
    @Order(6)
    @DisplayName("C: patchGroceryItem throws NoSuchElementException for unknown id")
    void patchGroceryItem_unknownId_throws() {
        Method m = resolveMethod("patchGroceryItem", Long.class, boolean.class);
        assertThrows(NoSuchElementException.class, () -> invoke(m, Long.MAX_VALUE, true));
    }

    // -----------------------------------------------------------------------
    // D – getGroceryItems
    // -----------------------------------------------------------------------

    @Test
    @Order(7)
    @DisplayName("D: getGroceryItems returns all persisted items")
    void getGroceryItems_returnsAll() throws Exception {
        insertItem("Apple", 3, false, null);
        insertItem("Banana", 5, false, null);

        Method m = resolveMethod("getGroceryItems");
        List<?> result = (List<?>) invoke(m);

        assertNotNull(result);
        assertEquals(2, result.size(), "Should return exactly 2 items");

        Set<String> names = new HashSet<>();
        for (Object item : result) names.add((String) getField(item, "name"));
        assertTrue(names.containsAll(Set.of("Apple", "Banana")));
    }

    @Test
    @Order(8)
    @DisplayName("D: getGroceryItems returns empty list when database is empty")
    void getGroceryItems_emptyDb() throws Exception {
        Method m = resolveMethod("getGroceryItems");
        List<?> result = (List<?>) invoke(m);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // E – getGroceryItem
    // -----------------------------------------------------------------------

    @Test
    @Order(9)
    @DisplayName("E: getGroceryItem returns correct item by id")
    void getGroceryItem_found() throws Exception {
        Long id = insertItem("Yoghurt", 2, false, null);

        Method m = resolveMethod("getGroceryItem", Long.class);
        Optional<?> result = (Optional<?>) invoke(m, id);

        assertTrue(result.isPresent(), "Item must be present");
        assertEquals("Yoghurt", getField(result.get(), "name"));
        assertEquals(2, getField(result.get(), "amount"));
    }

    @Test
    @Order(10)
    @DisplayName("E: getGroceryItem returns empty Optional for unknown id")
    void getGroceryItem_notFound() throws Exception {
        Method m = resolveMethod("getGroceryItem", Long.class);
        Optional<?> result = (Optional<?>) invoke(m, Long.MAX_VALUE);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // F – deleteGroceryItem
    // -----------------------------------------------------------------------

    @Test
    @Order(11)
    @DisplayName("F: deleteGroceryItem(id) removes only the targeted item")
    void deleteGroceryItem_byId_removesOne() throws Exception {
        Long keepId   = insertItem("Keep", 1, false, null);
        Long removeId = insertItem("Remove", 1, false, null);

        Method m = resolveMethod("deleteGroceryItem", Long.class);
        invoke(m, removeId);

        assertFalse(itemExists(removeId), "Deleted item must not exist");
        assertTrue(itemExists(keepId),    "Other item must still exist");
    }

    @Test
    @Order(12)
    @DisplayName("F: deleteGroceryItem(null) removes all items")
    void deleteGroceryItem_null_deletesAll() throws Exception {
        insertItem("A", 1, false, null);
        insertItem("B", 1, false, null);

        Method m = resolveMethod("deleteGroceryItem", Long.class);
        invoke(m, (Object) null);

        assertEquals(0, itemCount(), "All items must be deleted");
    }

    @Test
    @Order(13)
    @DisplayName("F: deleteGroceryItem(id) throws NoSuchElementException for unknown id")
    void deleteGroceryItem_unknownId_throws() {
        Method m = resolveMethod("deleteGroceryItem", Long.class);
        assertThrows(NoSuchElementException.class, () -> invoke(m, Long.MAX_VALUE));
    }

    // -----------------------------------------------------------------------
    // G – getGroceryLists
    // -----------------------------------------------------------------------

    @Test
    @Order(14)
    @DisplayName("G: getGroceryLists returns all persisted lists")
    void getGroceryLists_returnsAll() throws Exception {
        // setUp() already created one list; add a second one
        insertList("Weekend Shopping");

        Method m = resolveMethod("getGroceryLists");
        List<?> result = (List<?>) invoke(m);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @Order(15)
    @DisplayName("G: getGroceryLists returns empty list when no lists are stored")
    void getGroceryLists_empty() throws Exception {
        clearDb();

        Method m = resolveMethod("getGroceryLists");
        List<?> result = (List<?>) invoke(m);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -----------------------------------------------------------------------
    // Cross-cutting: create then read roundtrip
    // -----------------------------------------------------------------------

    @Test
    @Order(16)
    @DisplayName("Roundtrip: createGroceryItem → getGroceryItem returns identical data")
    void roundtrip_createThenGet() throws Exception {
        Object dto = buildItemDto(null, "Orange Juice", 3, false, listId);
        Method create = resolveMethod("createGroceryItem", dto.getClass());
        Object created = invoke(create, dto);
        Long id = (Long) getField(created, "id");

        Method get = resolveMethod("getGroceryItem", Long.class);
        Optional<?> fetched = (Optional<?>) invoke(get, id);

        assertTrue(fetched.isPresent());
        assertEquals("Orange Juice", getField(fetched.get(), "name"));
        assertEquals(3,              getField(fetched.get(), "amount"));
        assertEquals(false,          getField(fetched.get(), "collected"));
    }

    @Test
    @Order(17)
    @DisplayName("Roundtrip: create → patch → getGroceryItem reflects collected=true")
    void roundtrip_createPatchGet() throws Exception {
        Object dto = buildItemDto(null, "Tomatoes", 4, false, null);
        Method create = resolveMethod("createGroceryItem", dto.getClass());
        Object created = invoke(create, dto);
        Long id = (Long) getField(created, "id");

        Method patch = resolveMethod("patchGroceryItem", Long.class, boolean.class);
        invoke(patch, id, true);

        Method get = resolveMethod("getGroceryItem", Long.class);
        Optional<?> fetched = (Optional<?>) invoke(get, id);

        assertTrue(fetched.isPresent());
        assertEquals(true, getField(fetched.get(), "collected"));
    }
}
