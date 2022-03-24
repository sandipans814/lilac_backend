package in.sairyonodevs.lilac.services;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import in.sairyonodevs.lilac.models.Product;
import in.sairyonodevs.lilac.models.User;
import in.sairyonodevs.lilac.models.request.AddProductRequest;
import in.sairyonodevs.lilac.repositories.ProductRepository;
import in.sairyonodevs.lilac.repositories.UserRepository;

@Service
public class MongoProductService implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(MongoProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Boolean addProduct(Product product) {
        try {
            productRepository.save(product);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productRepository.findById(id);
    }

    @Override
    public Boolean existsById(String id) {
        return productRepository.existsById(id);
    }

    @Override
    public Page<Product> findByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByType(String type, Pageable pageable) {
        return productRepository.findByType(type, pageable);
    }

    @Override
    public Page<Product> findByPriceBetween(Double lower, Double upper, Pageable pageable) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Boolean removeProduct(String id) {
        try {
            productRepository.deleteById(id);
            return true;
        } catch(Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    public PagedListHolder<Product> getNewArrivals(Pageable pageable) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2021, 3, 20, 22, 3, 0);
        Date threshold = calendar.getTime();
        List<Product> newArrivals = productRepository.findAll().stream().filter(e -> e.getCreatedAt().after(threshold)).collect(Collectors.toList());
        
        PagedListHolder<Product> page = new PagedListHolder<>(newArrivals);
        page.setPageSize(pageable.getPageSize());

        page.setPage(pageable.getPageNumber());

        return page;
    }

    @Override
    public PagedListHolder<Product> getDiscounted(Pageable pageable) { //Pageable pageable
        List<Product> discounted = productRepository.findAll().stream().filter(Product::isDiscounted).collect(Collectors.toList());

        PagedListHolder<Product> page = new PagedListHolder<>(discounted);
        page.setPageSize(pageable.getPageSize());

        page.setPage(pageable.getPageNumber());

        return page;
    }

    @Override
    public Page<Product> findByCategoryAndType(String category, String type, Pageable pageable) {
        return productRepository.findByCategoryAndType(category, type, pageable);
    }

    @Override
    public Boolean updateProduct(AddProductRequest addProductRequest, String id) {
        try {
            Product p = productRepository.findById(id).get();
            p.setCategory(addProductRequest.getCategory());
            p.setPrice(addProductRequest.getPrice());
            p.setDescription(addProductRequest.getDescription());
            p.setQuantity(addProductRequest.getQuantity());
            p.setName(addProductRequest.getName());
            p.setPicUrls(addProductRequest.getPicUrls());
            p.setType(addProductRequest.getType());
        
            productRepository.save(p);
        
            return true;
        } catch (Exception e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    @Override
    public PagedListHolder<Product> getHomeItems(String id, Pageable pageable) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
        if (user.getDiscountClicks() >= user.getNewArrivalsClicks()) {
            return getDiscounted(pageable);
        } else {
            return getNewArrivals(pageable);
        }
    }
}
