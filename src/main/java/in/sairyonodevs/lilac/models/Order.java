package in.sairyonodevs.lilac.models;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "orders")
public class Order {
    
    @Id
    private String id;

    private String userId;

    private Double totalPrice = 0.0;

    private String status; // pending, approved, rejected

    @CreatedDate
    private Date createdAt;

    @DBRef
    private Set<Item> orderItems = new HashSet<>();

    public Order() {}

    public Order(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return this.totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Item> getOrderItems() {
        return this.orderItems;
    }

    public void setOrderItems(Set<Item> orderItems) {
        this.orderItems = orderItems;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void calcTotalPrice() {
        if (this.orderItems.size() == 0) {
            return;
        } else {
            this.totalPrice = this.orderItems.stream()
                                .map(e -> e.getProduct().getPrice())
                                .reduce(0.0, (acc, i) -> acc + i);
        }
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else if (!(obj instanceof Order))
            return false;
        else {
            Order o = (Order) obj;
            return Objects.equals(id, o.id);
        }
    }
}
