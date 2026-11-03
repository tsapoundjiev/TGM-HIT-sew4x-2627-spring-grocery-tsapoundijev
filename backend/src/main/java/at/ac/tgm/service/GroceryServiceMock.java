package at.ac.tgm.service;

import at.ac.tgm.dto.GroceryItemDto;
import at.ac.tgm.dto.GroceryListDto;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Geschäftslogik zur Verwaltung der Artikel einer Einkaufsliste mit gefakter Datenbankspeicherung
 *
 * @author Michael Pointner
 * @version 2025-02-04
 */
@Service
public class GroceryServiceMock implements GroceryService {
    private final List<GroceryItemDto> items = new ArrayList<>();
    private Long counter = 0L;
    
    public GroceryServiceMock() {
        items.add(new GroceryItemDto(++counter, "Apfel", 6, false, 1L));
        items.add(new GroceryItemDto(++counter, "Weckerl", 3, false, 1L));
        items.add(new GroceryItemDto(++counter, "Milch", 2, false, 1L));
        items.add(new GroceryItemDto(++counter, "Joghurt", 2, false, 1L));
        items.add(new GroceryItemDto(++counter, "Karotten", 4, false, 1L));
        items.add(new GroceryItemDto(++counter, "Zucchini", 2, false, 2L));
        items.add(new GroceryItemDto(++counter, "Käse", 1, false, 2L));
        items.add(new GroceryItemDto(++counter, "Müsli", 1, false, 2L));
        items.add(new GroceryItemDto(++counter, "Mehl", 1, false, 2L));
        items.add(new GroceryItemDto(++counter, "Eier", 10, false, 2L));
        items.add(new GroceryItemDto(++counter, "Honig", 1, false, 3L));
        items.add(new GroceryItemDto(++counter, "Apfelsaft", 2, false, 3L));
        items.add(new GroceryItemDto(++counter, "Zucker", 1, false, 3L));
        items.add(new GroceryItemDto(++counter, "Brokkoli", 1, false, 3L));
        items.add(new GroceryItemDto(++counter, "Kartoffel", 4, false, 3L));
        items.add(new GroceryItemDto(++counter, "Butter", 1, false, 3L));
    }
    
    @Override
    public GroceryItemDto createGroceryItem(GroceryItemDto item) {
        item.setId(++counter);
        items.add(item);
        return item;
    }

    @Override
    public GroceryItemDto updateGroceryItem(GroceryItemDto item) {
        for (int i = 0; i < items.size(); i++) {
            GroceryItemDto existing = items.get(i);
            if (Objects.equals(existing.getId(), item.getId())) {
                items.set(i, item);
                return item;
            }
        }
        throw new NoSuchElementException("Artikel mit id " + item.getId() + " nicht gefunden");
    }

    @Override
    public GroceryItemDto patchGroceryItem(Long id, boolean collected) {
        for (GroceryItemDto item : items) {
            if (Objects.equals(item.getId(), id)) {
                item.setCollected(collected);
                return item;
            }
        }
        throw new NoSuchElementException("Artikel mit id " + id + " nicht gefunden");
    }
    
    @Override
    public List<GroceryItemDto> getGroceryItems() {
        return items;
    }
    
    @Override
    public Optional<GroceryItemDto> getGroceryItem(Long id) {
        for (GroceryItemDto item : items) {
            if (Objects.equals(item.getId(), id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }
    
    @Override
    public void deleteGroceryItem(Long id) {
        if (id == null) {
            items.clear();
        } else {
            for (int i = 0; i < items.size(); i++) {
                GroceryItemDto item = items.get(i);
                if (Objects.equals(item.getId(), id)) {
                    items.remove(i);
                    return;
                }
            }
            throw new NoSuchElementException("Artikel mit id " + id + " nicht gefunden");
        }
    }

    @Override
    public List<GroceryListDto> getGroceryLists() {
        List<GroceryListDto> list = new ArrayList<>();
        List<GroceryItemDto> billaGroceryItems = new ArrayList<>();
        List<GroceryItemDto> sparGroceryItems = new ArrayList<>();
        List<GroceryItemDto> hoferGroceryItems = new ArrayList<>();
        for (GroceryItemDto item : items) {
            if(item.getId() <= 5) {
                billaGroceryItems.add(item);
            } else if (item.getId() <= 10) {
                sparGroceryItems.add(item);
            } else {
                hoferGroceryItems.add(item);
            }
        }
        list.add(new GroceryListDto(1L, "Billa", billaGroceryItems));
        list.add(new GroceryListDto(2L, "Spar", sparGroceryItems));
        list.add(new GroceryListDto(3L, "Hofer", hoferGroceryItems));
        return list;
    }
}
