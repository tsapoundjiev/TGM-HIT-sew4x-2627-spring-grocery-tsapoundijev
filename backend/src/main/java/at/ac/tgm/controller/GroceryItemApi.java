package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * REST-Schnittstelle für Artikel. Alle Mapping- und Swagger-Annotationen, die nicht an der
 * Controller-Klasse stehen müssen, sind hierher ausgelagert.
 */
@Tag(name = "Grocery Item", description = "Verwaltung der Artikel einer Einkaufsliste")
@RequestMapping("/api/grocery/item")
public interface GroceryItemApi {

    /**
     * D: Liste aller Artikel abfragen.
     *
     * @return 200 mit allen Artikeln oder 204, wenn es keine Artikel gibt
     */
    @Operation(summary = "[D] Alle Artikel abfragen")
    @ApiResponse(responseCode = "200", description = "Alle Artikel")
    @ApiResponse(responseCode = "204", description = "Es existieren keine Artikel", content = @Content)
    @GetMapping
    ResponseEntity<List<GroceryItemDto>> getGroceryItems();

    /**
     * A: Neuen Artikel hinzufügen.
     *
     * @param groceryItemDto Neuer Artikel, die id muss {@code null} sein
     * @return 201 mit dem erstellten Artikel, 400 bei ungültigen Daten
     */
    @Operation(summary = "[A] Artikel hinzufügen",
            description = "Legt einen neuen Artikel an. Die id muss null sein und wird vom Server vergeben. "
                    + "Name wird getrimmt, amount null/0 wird zu 1, negative amount wird negiert, collected null wird zu false.")
    @ApiResponse(responseCode = "201", description = "Artikel erstellt",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroceryItemDto.class)))
    @ApiResponse(responseCode = "400", description = "Ungültige Daten (id != null, Name leer oder länger als 255 Zeichen)",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @PostMapping
    ResponseEntity<GroceryItemDto> addGroceryItem(@Valid @RequestBody GroceryItemDto groceryItemDto);

    /**
     * B: Bestehenden Artikel aktualisieren.
     *
     * @param groceryItemDto Zu aktualisierender Artikel, die id muss gesetzt sein
     * @return 200 mit dem aktualisierten Artikel, 400 bei ungültigen Daten, 404 wenn die id nicht existiert
     */
    @Operation(summary = "[B] Artikel aktualisieren",
            description = "Ersetzt den Artikel mit der angegebenen id. Es gelten dieselben Korrekturen wie beim Hinzufügen.")
    @ApiResponse(responseCode = "200", description = "Artikel aktualisiert",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GroceryItemDto.class)))
    @ApiResponse(responseCode = "400", description = "Ungültige Daten (id == null, Name leer oder länger als 255 Zeichen)",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(responseCode = "404", description = "Kein Artikel mit dieser id vorhanden",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @PutMapping
    ResponseEntity<GroceryItemDto> updateGroceryItem(@Valid @RequestBody GroceryItemDto groceryItemDto);

    /**
     * E: Einen Artikel anhand seiner Id abfragen.
     *
     * @param id Id des gewünschten Artikels
     * @return 200 mit dem Artikel oder 404, wenn kein Artikel mit dieser Id existiert
     */
    @Operation(summary = "[E] Artikel mit angegebener Id abfragen",
            description = "Liefert den Artikel mit der übergebenen Id. Existiert kein solcher Artikel, wird 404 geliefert.")
    @ApiResponse(responseCode = "200", description = "Artikel gefunden",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GroceryItemDto.class),
                    examples = @ExampleObject(name = "Apfel", value = """
                            {
                              "id": 1,
                              "name": "Apfel",
                              "amount": 6,
                              "collected": false,
                              "listId": 1
                            }""")))
    @ApiResponse(responseCode = "404", description = "Kein Artikel mit dieser Id vorhanden (leerer Body)",
            content = @Content)
    @GetMapping("/{id}")
    ResponseEntity<GroceryItemDto> getGroceryItem(
            @Parameter(description = "Id des Artikels", example = "1") @PathVariable Long id);
}
