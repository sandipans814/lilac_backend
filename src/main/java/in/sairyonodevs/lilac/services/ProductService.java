package in.sairyonodevs.lilac.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.sairyonodevs.lilac.models.Product;
import in.sairyonodevs.lilac.models.request.AddProductRequest;

public interface ProductService {
    Boolean addProduct(Product product);
    
    Optional<Product> findById(String id);

    Boolean existsById(String id);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByCategoryAndType(String category, String type, Pageable pageable);

    Page<Product> findByType(String type, Pageable pageable);

    Page<Product> findByPriceBetween(Double lower, Double upper, Pageable pageable);
    
    Boolean removeProduct(String id);

    PagedListHolder<Product> getNewArrivals(Pageable pageable);

    PagedListHolder<Product> getDiscounted(Pageable pageable); // Pageable pageable

    PagedListHolder<Product> getHomeItems(String id, Pageable pageable);

    Boolean updateProduct(AddProductRequest addProductRequest, String id);
}