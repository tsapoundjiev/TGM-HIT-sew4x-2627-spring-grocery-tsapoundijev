package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryListDto;
import at.ac.tgm.service.GroceryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller für die Einkaufslisten-Endpunkte unter {@code /api/grocery/list}.
 * Die Mappings stehen im Interface {@link GroceryListApi}.
 */
@RestController
@RequiredArgsConstructor
public class GroceryListController implements GroceryListApi {

    private final GroceryService groceryService;

    /**
     * G: Liefert alle Einkaufslisten.
     *
     * @return 200 OK mit allen Listen, 204 No Content wenn keine Listen vorhanden sind
     */
    @Override
    public ResponseEntity<List<GroceryListDto>> getGroceryLists() {
        List<GroceryListDto> lists = groceryService.getGroceryLists();
        if (lists.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lists);
    }
}
