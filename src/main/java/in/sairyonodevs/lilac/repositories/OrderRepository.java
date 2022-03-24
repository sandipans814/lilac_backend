package in.sairyonodevs.lilac.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.Order;

public interface OrderRepository extends MongoRepository<Order, String> {
    
}
