package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryItemDto;
import at.ac.tgm.dto.GroceryListDto;
import at.ac.tgm.service.GroceryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer tests for GroceryItemController and GroceryListController.
 *
 * <p>Runs against a real Spring MVC dispatch (full application context via
 * {@code @SpringBootTest} + {@code @AutoConfigureMockMvc}) rather than a manually
 * constructed standalone controller, so students remain free to shape endpoints
 * with {@code @RequestMapping}/{@code @GetMapping}/etc. however they like — the
 * real {@code DispatcherServlet} resolves them, and {@code GlobalExceptionHandler}
 * is picked up via component scanning instead of being wired in by hand.
 * The {@link GroceryService} bean is replaced with a Mockito mock via
 * {@code @MockitoBean}, so no database is actually exercised. Endpoint behavior
 * is asserted purely through real HTTP status codes/bodies rather than reflection
 * over a specific controller class, so any {@code @RequestMapping}-based routing
 * the student writes is exercised as-is.
 *
 * <p>Coverage:
 * <ul>
 *   <li>Correct HTTP status codes for every endpoint (200, 201, 204, 400, 404)</li>
 *   <li>Jakarta Bean-Validation constraints on the request body DTO (V1-V4)</li>
 *   <li>Business-rule corrections applied before persisting (K1-K5)</li>
 *   <li>Id-presence rules for POST (A3) and PUT (B3)</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("7a.2 Web-Endpunkte")
class GroceryControllerTest {

    // -----------------------------------------------------------------------
    // Infrastructure
    // -----------------------------------------------------------------------

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GroceryService groceryService;

    private final ObjectMapper objectMapper = new ObjectMapper();;

    /** Base path discovered from @RequestMapping on the class / interface. */
    private static final String BASE = "/api/grocery/item";
    private static final String BASE_LIST = "/api/grocery/list";

    // Shared fixtures
    private GroceryItemDto validItem;

    @BeforeEach
    void setUp() {
        validItem = new GroceryItemDto(1L, "Apfel", 1, false, 1L);
    }

    // -----------------------------------------------------------------------
    // D: GET /api/grocery/item — list all items
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("D: GET all grocery items")
    class GetAllItems {

        @Test
        @DisplayName("D1 – 200 OK when items are present")
        void d1_returns200WhenItemsPresent() throws Exception {
            when(groceryService.getGroceryItems())
                    .thenReturn(List.of(validItem,
                            new GroceryItemDto(2L, "Kiwi", 2, false, 1L)));

            mockMvc.perform(get(BASE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("D2 – 204 No Content when list is empty")
        void d2_returns204WhenEmpty() throws Exception {
            when(groceryService.getGroceryItems()).thenReturn(List.of());

            mockMvc.perform(get(BASE))
                    .andExpect(status().isNoContent());
        }
    }

    // -----------------------------------------------------------------------
    // E: GET /api/grocery/item/{id} — single item
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("E: GET single grocery item by id")
    class GetSingleItem {

        @Test
        @DisplayName("E1 – 200 OK for existing id")
        void e1_returns200ForExistingId() throws Exception {
            when(groceryService.getGroceryItem(1L)).thenReturn(Optional.of(validItem));

            mockMvc.perform(get(BASE + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Apfel"));
        }

        @Test
        @DisplayName("E2 – 404 Not Found for non-existent id")
        void e2_returns404ForMissingId() throws Exception {
            when(groceryService.getGroceryItem(anyLong())).thenReturn(Optional.empty());

            mockMvc.perform(get(BASE + "/999"))
                    .andExpect(status().isNotFound());
        }
    }

    // -----------------------------------------------------------------------
    // A: POST /api/grocery/item — create item
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("A: POST – add grocery item")
    class AddItem {

        @Test
        @DisplayName("A1 – 201 Created: id=null, full valid body")
        void a1_returns201ForValidItemWithNullId() throws Exception {
            GroceryItemDto request = new GroceryItemDto(null, "Apfel", 1, false, 1L);
            when(groceryService.createGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("A2 – 201 Created: minimal body (only name)")
        void a2_returns201WithOnlyName() throws Exception {
            when(groceryService.createGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apfel\"}"))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("A3 – 400 Bad Request: id must be null on POST")
        void a3_returns400WhenIdIsNotNull() throws Exception {
            GroceryItemDto request = new GroceryItemDto(1L, "Apfel", 1, false, 1L);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        // --- Validation (V) tests for POST ---

        @Test
        @DisplayName("V1 – 400 Bad Request: name == null")
        void v1_post_returns400WhenNameIsNull() throws Exception {
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":null}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V2 – 400 Bad Request: name is empty string")
        void v2_post_returns400WhenNameIsEmpty() throws Exception {
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V3 – 400 Bad Request: name is only whitespace")
        void v3_post_returns400WhenNameIsBlank() throws Exception {
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"   \"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V4 – 400 Bad Request: name exceeds 255 characters")
        void v4_post_returns400WhenNameTooLong() throws Exception {
            String longName = "Baguette-Sandwich gefüllt mit Schweineschinken, Emmentalerkäse, "
                    + "Salatgurken, Cocktailtomaten, Eisbergsalat, gegrillte Hühnerstreifen, "
                    + "Paprika, gegrillte Champignons, geröstete Zwiebeln, Essiggurkerl, Oliven, "
                    + "Jalapeños, Nachos, scharfer Senf und Salsa-Sauce";
            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"" + longName + "\"}"))
                    .andExpect(status().isBadRequest());
        }

        // --- Correction (K) tests for POST ---

        @Test
        @DisplayName("K1 – leading/trailing spaces in name are trimmed")
        void k1_post_trimpsNameWhitespace() throws Exception {
            when(groceryService.createGroceryItem(argThat(dto -> "Apfel".equals(dto.getName()))))
                    .thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\" Apfel \"}"))
                    .andExpect(status().isCreated());

            verify(groceryService).createGroceryItem(argThat(dto -> "Apfel".equals(dto.getName())));
        }

        @Test
        @DisplayName("K2 – amount == null corrected to 1")
        void k2_post_nullAmountBecomesOne() throws Exception {
            when(groceryService.createGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apfel\",\"amount\":null}"))
                    .andExpect(status().isCreated());

            verify(groceryService).createGroceryItem(argThat(dto -> Integer.valueOf(1).equals(dto.getAmount())));
        }

        @Test
        @DisplayName("K3 – amount == 0 corrected to 1")
        void k3_post_zeroAmountBecomesOne() throws Exception {
            when(groceryService.createGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apfel\",\"amount\":0}"))
                    .andExpect(status().isCreated());

            verify(groceryService).createGroceryItem(argThat(dto -> Integer.valueOf(1).equals(dto.getAmount())));
        }

        @Test
        @DisplayName("K4 – negative amount is negated (absolute value)")
        void k4_post_negativeAmountBecomesAbsolute() throws Exception {
            when(groceryService.createGroceryItem(any()))
                    .thenReturn(new GroceryItemDto(1L, "Apfel", 3, false, 1L));

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apfel\",\"amount\":-3}"))
                    .andExpect(status().isCreated());

            verify(groceryService).createGroceryItem(argThat(dto -> Integer.valueOf(3).equals(dto.getAmount())));
        }

        @Test
        @DisplayName("K5 – collected == null defaults to false")
        void k5_post_nullCollectedDefaultsFalse() throws Exception {
            when(groceryService.createGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(post(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"name\":\"Apfel\",\"collected\":null}"))
                    .andExpect(status().isCreated());

            verify(groceryService).createGroceryItem(argThat(dto -> Boolean.FALSE.equals(dto.getCollected())));
        }
    }

    // -----------------------------------------------------------------------
    // B: PUT /api/grocery/item — update item
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("B: PUT – update grocery item")
    class UpdateItem {

        @Test
        @DisplayName("B1 – 200 OK: full valid body with id")
        void b1_returns200ForValidUpdate() throws Exception {
            when(groceryService.updateGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Apfel\",\"amount\":1,\"collected\":false}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("B2 – 200 OK: minimal body (id + name)")
        void b2_returns200WithIdAndName() throws Exception {
            when(groceryService.updateGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Apfel\"}"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("B3 – 400 Bad Request: id == null on PUT")
        void b3_returns400WhenIdIsNull() throws Exception {
            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":null,\"name\":\"Apfel\"}"))
                    .andExpect(status().isBadRequest());
        }

        // --- Validation (V) tests for PUT ---

        @Test
        @DisplayName("V1 – 400 Bad Request: name == null (PUT)")
        void v1_put_returns400WhenNameIsNull() throws Exception {
            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":null}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V2 – 400 Bad Request: name is empty (PUT)")
        void v2_put_returns400WhenNameIsEmpty() throws Exception {
            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"\"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V3 – 400 Bad Request: name is blank (PUT)")
        void v3_put_returns400WhenNameIsBlank() throws Exception {
            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"   \"}"))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("V4 – 400 Bad Request: name > 255 chars (PUT)")
        void v4_put_returns400WhenNameTooLong() throws Exception {
            String longName = "Baguette-Sandwich gefüllt mit Schweineschinken, Emmentalerkäse, "
                    + "Salatgurken, Cocktailtomaten, Eisbergsalat, gegrillte Hühnerstreifen, "
                    + "Paprika, gegrillte Champignons, geröstete Zwiebeln, Essiggurkerl, Oliven, "
                    + "Jalapeños, Nachos, scharfer Senf und Salsa-Sauce";
            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"" + longName + "\"}"))
                    .andExpect(status().isBadRequest());
        }

        // --- Correction (K) tests for PUT ---

        @Test
        @DisplayName("K1 – name whitespace trimmed (PUT)")
        void k1_put_trimpsNameWhitespace() throws Exception {
            when(groceryService.updateGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\" Apfel \"}"))
                    .andExpect(status().isOk());

            verify(groceryService).updateGroceryItem(argThat(dto -> "Apfel".equals(dto.getName())));
        }

        @Test
        @DisplayName("K2 – amount == null corrected to 1 (PUT)")
        void k2_put_nullAmountBecomesOne() throws Exception {
            when(groceryService.updateGroceryItem(any())).thenReturn(validItem);

            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Apfel\",\"amount\":null}"))
                    .andExpect(status().isOk());

            verify(groceryService).updateGroceryItem(argThat(dto -> Integer.valueOf(1).equals(dto.getAmount())));
        }

        @Test
        @DisplayName("K4 – negative amount negated (PUT)")
        void k4_put_negativeAmountIsAbsolute() throws Exception {
            when(groceryService.updateGroceryItem(any()))
                    .thenReturn(new GroceryItemDto(1L, "Apfel", 3, false, 1L));

            mockMvc.perform(put(BASE)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"id\":1,\"name\":\"Apfel\",\"amount\":-3}"))
                    .andExpect(status().isOk());

            verify(groceryService).updateGroceryItem(argThat(dto -> Integer.valueOf(3).equals(dto.getAmount())));
        }
    }

    // -----------------------------------------------------------------------
    // F: DELETE /api/grocery/item/{id} and /api/grocery/item
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("F: DELETE – remove item(s)")
    class DeleteItem {

        @Test
        @DisplayName("F1 – 200 OK: valid id deleted")
        void f1_returns200WhenDeletedById() throws Exception {
            doNothing().when(groceryService).deleteGroceryItem(1L);

            mockMvc.perform(delete(BASE + "/1"))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("F2 – 404 Not Found: non-existent id")
        void f2_returns404ForNonExistentId() throws Exception {
            doThrow(new java.util.NoSuchElementException("Item with id -1 not found"))
                    .when(groceryService).deleteGroceryItem(-1L);

            mockMvc.perform(delete(BASE + "/-1"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("F3 – 200 OK: delete all (no id parameter)")
        void f3_returns200WhenDeleteAll() throws Exception {
            doNothing().when(groceryService).deleteGroceryItem(null);

            mockMvc.perform(delete(BASE))
                    .andExpect(status().isOk());
        }
    }

    // -----------------------------------------------------------------------
    // C: PATCH /api/grocery/item/{id}?collected=…
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("C: PATCH – update collected flag")
    class PatchCollected {

        @Test
        @DisplayName("C11+C21 – 200 OK: valid id, collected=true")
        void c11_c21_returns200WithCollectedTrue() throws Exception {
            GroceryItemDto patched = new GroceryItemDto(1L, "Apfel", 1, true, 1L);
            when(groceryService.patchGroceryItem(1L, true)).thenReturn(patched);

            mockMvc.perform(patch(BASE + "/1").param("collected", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collected").value(true));
        }

        @Test
        @DisplayName("C11+C22 – 200 OK: valid id, collected=false")
        void c11_c22_returns200WithCollectedFalse() throws Exception {
            GroceryItemDto patched = new GroceryItemDto(1L, "Apfel", 1, false, 1L);
            when(groceryService.patchGroceryItem(1L, false)).thenReturn(patched);

            mockMvc.perform(patch(BASE + "/1").param("collected", "false"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.collected").value(false));
        }

        @Test
        @DisplayName("C12 – 404 Not Found: non-existent id")
        void c12_returns404ForNonExistentId() throws Exception {
            when(groceryService.patchGroceryItem(-1L, true))
                    .thenThrow(new java.util.NoSuchElementException("Item mit id -1 nicht gefunden"));

            mockMvc.perform(patch(BASE + "/-1").param("collected", "true"))
                    .andExpect(status().isNotFound());
        }
    }

    // -----------------------------------------------------------------------
    // G: GET /api/grocery/list — list all grocery lists
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("G: GET all grocery lists")
    class GetAllLists {

        @Test
        @DisplayName("G1 – 200 OK when lists are present")
        void g1_returns200WhenListsPresent() throws Exception {
            when(groceryService.getGroceryLists())
                    .thenReturn(List.of(new GroceryListDto(1L, "Billa", List.of(validItem)),
                            new GroceryListDto(2L, "Spar", List.of())));

            mockMvc.perform(get(BASE_LIST))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("G2 – 204 No Content when list is empty")
        void g2_returns204WhenEmpty() throws Exception {
            when(groceryService.getGroceryLists()).thenReturn(List.of());

            mockMvc.perform(get(BASE_LIST))
                    .andExpect(status().isNoContent());
        }
    }

    // -----------------------------------------------------------------------
    // Reflection – DTO validation-annotation assertions (no Spring context required)
    // -----------------------------------------------------------------------

    @Nested
    @DisplayName("GroceryItemDto validation annotations")
    class DtoValidationAnnotations {

        @Test
        @DisplayName("GroceryItemDto – @NotBlank on name field")
        void groceryItemDtoNameHasValidationAnnotations() throws Exception {
            var field = at.ac.tgm.dto.GroceryItemDto.class.getDeclaredField("name");
            boolean hasNotBlank = field.isAnnotationPresent(
                    jakarta.validation.constraints.NotBlank.class);
            assertTrue(hasNotBlank, "GroceryItemDto.name must have @NotBlank");
        }

        @Test
        @DisplayName("GroceryItemDto – @Size(max=255) on name field")
        void groceryItemDtoNameHasSizeConstraint() throws Exception {
            var field = at.ac.tgm.dto.GroceryItemDto.class.getDeclaredField("name");
            var sizeAnn = field.getAnnotation(jakarta.validation.constraints.Size.class);
            assertNotNull(sizeAnn, "GroceryItemDto.name must have @Size");
            assertEquals(255, sizeAnn.max(), "@Size.max must be 255");
        }
    }
}