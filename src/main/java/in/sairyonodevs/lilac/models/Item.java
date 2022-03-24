package in.sairyonodevs.lilac.models;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "items")
public class Item {

    @Id
    private String id;

    private String cartId;

    @DBRef
    private Product product;

    public Item() {
    }

    public Item(String cartId, Product product) {
        this.cartId = cartId;
        this.product = product;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getCartId() {
        return this.cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof Item))
            return false;
        else {
            Item i = (Item) obj;
            return Objects.equals(id, i.id);
        }
    }
}