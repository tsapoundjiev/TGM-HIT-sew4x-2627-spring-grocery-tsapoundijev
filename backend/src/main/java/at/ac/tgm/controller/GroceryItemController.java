package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryItemDto;
import at.ac.tgm.service.GroceryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller für die Artikel-Endpunkte unter {@code /api/grocery/item}.
 * Die Mappings stehen im Interface {@link GroceryItemApi}.
 */
@RestController
@RequiredArgsConstructor
public class GroceryItemController implements GroceryItemApi {

    private final GroceryService groceryService;

    /**
     * D: Liefert alle Artikel.
     *
     * @return 200 OK mit allen Artikeln, 204 No Content wenn keine Artikel vorhanden sind
     */
    @Override
    public ResponseEntity<List<GroceryItemDto>> getGroceryItems() {
        List<GroceryItemDto> items = groceryService.getGroceryItems();
        if (items.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(items);
    }
}
