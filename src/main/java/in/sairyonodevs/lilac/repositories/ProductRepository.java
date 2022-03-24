package in.sairyonodevs.lilac.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    
    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByType(String type, Pageable pageable);

    Page<Product> findByCategoryAndType(String category, String type, Pageable pageable);
}
