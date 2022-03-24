package in.sairyonodevs.lilac.models.request;

import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AddProductRequest {
    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull
    private Double price;

    @NotNull
    private Integer quantity;

    @NotBlank
    private String category;

    @NotBlank
    private String type;

    @NotNull
    Set<String> picUrls;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getPicUrls() {
        return this.picUrls;
    }

    public void setPicUrls(Set<String> picUrls) {
        this.picUrls = picUrls;
    }
}
