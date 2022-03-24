package in.sairyonodevs.lilac.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    
    @Id
    private String id;

    @NotBlank
    @Size(max = 100)
    private String name;

    private String description;

    @NotBlank
    private Double price;

    @NotBlank
    private Integer quantity;

    @NotBlank
    private String category; // lowercase; [Shirt, TShirt, Dress, Denim, Trousers, Suit, Boots, Shoes]

    @NotBlank
    private String type; // "M" or "F"

    private Boolean discounted = false;

    private Double discountPrice = 0.0;

    Set<String> picUrls = new HashSet<>();

    @CreatedDate
    private Date createdAt;

    @LastModifiedDate
    private Date lastModified;

    public Product() {}

    public Product(String name, Double price, Integer quantity, String category, String type, Set<String> picUrls) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.type = type;
        this.picUrls = picUrls;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Boolean isDiscounted() {
        return this.discounted;
    }

    public Boolean getDiscounted() {
        return this.discounted;
    }

    public void setDiscounted(Boolean discounted) {
        this.discounted = discounted;
    }

    public Double getDiscountPrice() {
        return this.discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Set<String> getPicUrls() {
        return this.picUrls;
    }

    public void setPicUrls(Set<String> picUrls) {
        this.picUrls = picUrls;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Boolean decreaseQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
            return true;
        } else {
            return false;
        }
    }

    public Boolean decreaseQuantity(int c) {
        if (this.quantity > 0) {
            this.quantity -= c;
            return true;
        } else {
            return false;
        }
    }

    public void increaseQuantity(int c) {
        this.quantity += c;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof Product))
            return false;
        else {
            Product p = (Product) obj;
            return Objects.equals(id, p.id);
        }
    }
}
