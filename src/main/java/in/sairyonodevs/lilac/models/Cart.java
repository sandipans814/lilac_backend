package in.sairyonodevs.lilac.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "carts")
public class Cart {
    @Id
    private String id;

    private String userId;

    @DBRef
    private Set<Item> cartItems = new HashSet<>();

    public Cart() {}

    public Cart(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Item> getCartItems() {
        return this.cartItems;
    }

    public void setCartItems(Set<Item> cartItems) {
        this.cartItems = cartItems;
    }

    public Boolean addToCart(Item item) {
        return this.cartItems.add(item);
    }

    public Boolean removeFromCart(Item item) {
        return this.cartItems.remove(item);
    }

    public Boolean addToCart(Set<Item> items) {
        return items.stream().map(item -> this.cartItems.add(item)).reduce(true, (acc, item) -> acc && item);
    }

    public Boolean removeFromCart(Set<Item> items) {
        return items.stream().map(item -> this.cartItems.remove(item)).reduce(true, (acc, item) -> acc && item);
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof Cart))
            return false;
        else {
            Cart c = (Cart) obj;
            return Objects.equals(id, c.id);
        }
    }

}
