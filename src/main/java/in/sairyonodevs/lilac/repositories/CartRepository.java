package in.sairyonodevs.lilac.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.Cart;

public interface CartRepository extends MongoRepository<Cart, String> {
    
}
