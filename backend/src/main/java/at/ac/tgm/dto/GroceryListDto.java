package at.ac.tgm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GroceryListDto {
    private Long id;
    private String name;

    @Schema(description = "[EK] Bei Implementierung der EK befüllt, sonst null")
    private List<GroceryItemDto> items;

    public GroceryListDto(Long id, String name, List<GroceryItemDto> items) {
        this.id = id;
        this.name = name;
        this.items = items;
    }
}
