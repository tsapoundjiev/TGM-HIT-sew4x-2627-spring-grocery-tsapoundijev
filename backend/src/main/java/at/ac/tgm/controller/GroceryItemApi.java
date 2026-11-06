package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    @ApiResponse(responseCode = "204", description = "Es existieren keine Artikel", content = @io.swagger.v3.oas.annotations.media.Content)
    @GetMapping
    ResponseEntity<List<GroceryItemDto>> getGroceryItems();
}
