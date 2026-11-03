package at.ac.tgm.service;

import at.ac.tgm.dto.GroceryItemDto;
import at.ac.tgm.dto.GroceryListDto;

import java.util.List;
import java.util.Optional;

/**
 * Geschäftslogik zur Verwaltung der Artikel und Einkaufslisten
 *
 * @author Michael Pointner
 * @version 2026-08-06
 */
public interface GroceryService {

    /**
     * A: Artikel hinzufügen
     *
     * @param groceryItemDto Zu erstellender Artikel (id == null)
     * @return Erstellter Artikel mit gesetzter eindeutiger id != null
     */
    GroceryItemDto createGroceryItem(GroceryItemDto groceryItemDto);

    /**
     * B: Artikel aktualisieren
     *
     * @param groceryItemDto Zu aktualisierender Artikel (id != null)
     * @return Aktualisierter Artikel (d.h. Rückgabewert der Datenbank)
     */
    GroceryItemDto updateGroceryItem(GroceryItemDto groceryItemDto);

    /**
     * C: Artikel zum Teil aktualisieren
     *
     * @param id        Id des Artikels
     * @param collected Zu setzender collected-Wert
     * @return Gesamtes aus der Datenbank geladenes Dto inklusive aktualisiertem collected-Wert
     */
    GroceryItemDto patchGroceryItem(Long id, boolean collected);

    /**
     * D: Liste aller Artikel abfragen
     *
     * @return Alle gespeicherten Artikel
     */
    List<GroceryItemDto> getGroceryItems();

    /**
     * E: Artikel mit angegebene Id erhalten
     *
     * @param id Id des gewünschten Artikels
     * @return Der gewünschte Artikel
     */
    Optional<GroceryItemDto> getGroceryItem(Long id);

    /**
     * F: Artikel mit der übergebenen Id oder alle löschen
     *
     * @param id Id des zu löschenden Artikels oder null für alle Artikel
     */
    void deleteGroceryItem(Long id);

    /**
     * G: Liste aller Einkaufslisten abfragen
     *
     * @return Alle gespeicherten Einkaufslisten
     */
    List<GroceryListDto> getGroceryLists();
}
