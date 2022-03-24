package in.sairyonodevs.lilac.services;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.sairyonodevs.lilac.models.User;

public interface UserService {
    Page<User> findAll(Pageable pageable);
    void register(User user, Set<String> strRoles);
    Boolean existsByEmail(String email);
    Boolean existsById(String id);
    Optional<User> findById(String id);
    Boolean deleteUser(String id);
    Boolean addToCart(String userId, String productId);
    Boolean removeFromCart(String userId, String productId);
    Boolean placeOrder(String id);
    Boolean cancelOrder(String id);
    Boolean approveOrder(String id);

    Boolean increaseDiscountClicks(String id);
    Boolean increaseNewArrivalsClicks(String id);
}
