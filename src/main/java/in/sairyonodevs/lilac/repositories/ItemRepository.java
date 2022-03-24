package in.sairyonodevs.lilac.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.Item;

public interface ItemRepository extends MongoRepository<Item, String> {
    
}

