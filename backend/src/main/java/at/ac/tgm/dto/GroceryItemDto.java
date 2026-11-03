package at.ac.tgm.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "[GK]")
public class GroceryItemDto {
    private Long id;
    private String name;
    private Integer amount;
    private Boolean collected;
    private Long listId;

    public GroceryItemDto(Long id, String name, Integer amount, Boolean collected, Long listId) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.collected = collected;
        this.listId = listId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getAmount() {
        return amount;
    }

    public Boolean getCollected() {
        return collected;
    }

    public void setCollected(Boolean collected) {
        this.collected = collected;
    }
}