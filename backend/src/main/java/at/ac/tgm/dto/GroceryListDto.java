package at.ac.tgm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Eine Einkaufsliste (z.B. "Billa") mit ihren Artikeln.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroceryListDto {
    private Long id;
    private String name;

    @Schema(description = "[EK] Bei Implementierung der EK befüllt, sonst null")
    private List<GroceryItemDto> items;
}
