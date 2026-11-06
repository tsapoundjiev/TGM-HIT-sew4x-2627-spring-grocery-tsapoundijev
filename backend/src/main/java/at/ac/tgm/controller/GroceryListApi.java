package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryListDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * REST-Schnittstelle für Einkaufslisten.
 */
@Tag(name = "Grocery List", description = "Abfrage der Einkaufslisten")
@RequestMapping("/api/grocery/list")
public interface GroceryListApi {

    /**
     * G: Liste aller Einkaufslisten abfragen.
     *
     * @return 200 mit allen Einkaufslisten oder 204, wenn es keine gibt
     */
    @Operation(summary = "[G] Alle Einkaufslisten abfragen")
    @ApiResponse(responseCode = "200", description = "Alle Einkaufslisten")
    @ApiResponse(responseCode = "204", description = "Es existieren keine Einkaufslisten", content = @Content)
    @GetMapping
    ResponseEntity<List<GroceryListDto>> getGroceryLists();
}
