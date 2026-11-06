package at.ac.tgm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ein Artikel einer Einkaufsliste.
 * <p>
 * Getter, Setter, {@code equals}, {@code hashCode}, {@code toString} sowie die Konstruktoren
 * werden von Lombok zur Compile-Zeit generiert.
 */
@Schema(description = "[GK]")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroceryItemDto {
    private Long id;

    @NotBlank
    @Size(max = 255)
    private String name;

    private Integer amount;
    private Boolean collected;
    private Long listId;
}
