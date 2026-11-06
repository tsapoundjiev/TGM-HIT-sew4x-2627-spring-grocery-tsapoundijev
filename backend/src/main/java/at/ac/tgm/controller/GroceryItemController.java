package at.ac.tgm.controller;

import at.ac.tgm.dto.GroceryItemDto;
import at.ac.tgm.service.GroceryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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

    /**
     * A: Legt einen neuen Artikel an. Die Daten werden vor dem Speichern korrigiert
     * (siehe {@link #correct(GroceryItemDto)}).
     *
     * @param groceryItemDto Neuer Artikel (id muss null sein, name nicht leer und maximal 255 Zeichen)
     * @return 201 Created mit Location-Header und dem erstellten Artikel,
     * 400 Bad Request wenn id != null oder die Validierung fehlschlägt
     */
    @Override
    public ResponseEntity<GroceryItemDto> addGroceryItem(GroceryItemDto groceryItemDto) {
        if (groceryItemDto.getId() != null) {
            throw new IllegalArgumentException("id muss beim Hinzufügen null sein");
        }
        GroceryItemDto created = groceryService.createGroceryItem(correct(groceryItemDto));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * B: Aktualisiert einen bestehenden Artikel. Die Daten werden wie bei A korrigiert.
     *
     * @param groceryItemDto Zu aktualisierender Artikel (id darf nicht null sein)
     * @return 200 OK mit dem aktualisierten Artikel, 400 Bad Request wenn id == null oder die Validierung
     * fehlschlägt, 404 Not Found (über den Service) wenn die id nicht existiert
     */
    @Override
    public ResponseEntity<GroceryItemDto> updateGroceryItem(GroceryItemDto groceryItemDto) {
        if (groceryItemDto.getId() == null) {
            throw new IllegalArgumentException("id darf beim Aktualisieren nicht null sein");
        }
        return ResponseEntity.ok(groceryService.updateGroceryItem(correct(groceryItemDto)));
    }

    /**
     * C: Setzt nur das collected-Flag des Artikels.
     *
     * @param id        Id des Artikels
     * @param collected Neuer collected-Wert
     * @return 200 OK mit dem vollständigen aktualisierten Artikel,
     * 404 Not Found (über den Service) wenn die id nicht existiert
     */
    @Override
    public ResponseEntity<GroceryItemDto> patchGroceryItem(Long id, boolean collected) {
        return ResponseEntity.ok(groceryService.patchGroceryItem(id, collected));
    }

    /**
     * F: Löscht alle Artikel (der Service löscht bei id == null alles).
     *
     * @return 200 OK
     */
    @Override
    public ResponseEntity<Void> deleteAllGroceryItems() {
        groceryService.deleteGroceryItem(null);
        return ResponseEntity.ok().build();
    }

    /**
     * F: Löscht den Artikel mit der angegebenen Id.
     *
     * @param id Id des zu löschenden Artikels
     * @return 200 OK, 404 Not Found (über den Service) wenn die id nicht existiert
     */
    @Override
    public ResponseEntity<Void> deleteGroceryItem(Long id) {
        groceryService.deleteGroceryItem(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Korrigiert fehlerhafte, aber eindeutig behebbare Eingaben:
     * Name wird getrimmt, amount null oder 0 wird zu 1, negatives amount wird negiert,
     * collected null wird zu false.
     */
    private GroceryItemDto correct(GroceryItemDto dto) {
        dto.setName(dto.getName().trim());
        Integer amount = dto.getAmount();
        if (amount == null || amount == 0) {
            dto.setAmount(1);
        } else if (amount < 0) {
            dto.setAmount(-amount);
        }
        if (dto.getCollected() == null) {
            dto.setCollected(false);
        }
        return dto;
    }

    /**
     * E: Liefert den Artikel mit der angegebenen Id.
     *
     * @param id Id des gewünschten Artikels
     * @return 200 OK mit dem Artikel, 404 Not Found wenn es keinen Artikel mit dieser Id gibt
     */
    @Override
    public ResponseEntity<GroceryItemDto> getGroceryItem(Long id) {
        // ResponseEntity.of: Optional mit Wert -> 200 + Body, leeres Optional -> 404 ohne Body
        return ResponseEntity.of(groceryService.getGroceryItem(id));
    }
}
