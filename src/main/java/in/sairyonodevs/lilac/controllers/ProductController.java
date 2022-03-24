package in.sairyonodevs.lilac.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.sairyonodevs.lilac.models.Product;
import in.sairyonodevs.lilac.models.request.AddProductRequest;
import in.sairyonodevs.lilac.models.response.MessageResponse;
import in.sairyonodevs.lilac.services.MyUserDetails;
import in.sairyonodevs.lilac.services.ProductService;
import in.sairyonodevs.lilac.services.UserService;

@RestController
@RequestMapping({ "api/products/", "api/products" })
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<Map<String, Object>> allProducts(@RequestParam(required = false) String category,
            @RequestParam(required = false) String type, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        try {
            Pageable paging = PageRequest.of(page, size);

            Page<Product> productPage;

            if (category == null && type == null) {
                productPage = productService.findAll(paging);
            } else if (type == null) {
                productPage = productService.findByCategory(category, paging);
            } else if (category == null) {
                productPage = productService.findByType(type, paging);
            } else {
                productPage = productService.findByCategoryAndType(category, type, paging);
            }

            Set<Product> productSet = new HashSet<>(productPage.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("products", productSet);
            response.put("currentPage", productPage.getNumber());
            response.put("totalItems", productPage.getTotalElements());
            response.put("totalPages", productPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping({ "{id}", "/{id}", "/{id}/, {id}/" })
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        logger.info(id);
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(productService.findById(id).get());
        }
    }

    @PutMapping({ "{id}", "/{id}", "/{id}/, {id}/" })
    public ResponseEntity<?> updateProduct(@Valid @RequestBody AddProductRequest addProductRequest, @PathVariable String id) {
        if (!productService.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            if(productService.updateProduct(addProductRequest, id)) {
                Product p = productService.findById(id).get();
                Map<String, Object> response = new HashMap<>();
                response.put("responseMessage","Update successful");
                response.put("updatedProduct", p);
                return ResponseEntity.ok(response);
            } 
        }
        return ResponseEntity.status(500).build();
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addProduct(@Valid @RequestBody List<AddProductRequest> addProductRequestSet) {

        Set<String> responseMessages = new HashSet<>();

        IntStream.range(0, addProductRequestSet.size()).forEach(i -> {
            Product p = new Product(
                addProductRequestSet.get(i).getName(), 
                addProductRequestSet.get(i).getPrice(),
                addProductRequestSet.get(i).getQuantity(), 
                addProductRequestSet.get(i).getCategory(), 
                addProductRequestSet.get(i).getType(),
                addProductRequestSet.get(i).getPicUrls()
            );

            if (addProductRequestSet.get(i).getDescription() != null) {
                p.setDescription(addProductRequestSet.get(i).getDescription());
            }

            if(productService.addProduct(p)) {
                responseMessages.add(i + ": Add Operation Successful");
            } else {
                responseMessages.add(i + ": Add Operation Failure");
            }

        });
        return ResponseEntity.ok(responseMessages);
    }

    @GetMapping({"new-arrivals/", "new-arrivals"})
    public ResponseEntity<?> newArrivals(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {
        Pageable paging = PageRequest.of(page, size);

        PagedListHolder<Product> productPage;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal().getClass());
        if (auth.getPrincipal() instanceof MyUserDetails) {
            try {
                MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
                userService.increaseNewArrivalsClicks(myUserDetails.getId());
            } catch (Exception e) {
                ResponseEntity.notFound();
            }
        }

        productPage = productService.getNewArrivals(paging);

        Set<Product> productSet = new HashSet<>(productPage.getPageList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productSet);
        response.put("currentPage", productPage.getPage());
        response.put("totalItems", productPage.getNrOfElements());
        response.put("totalPages", productPage.getPageCount());

        return ResponseEntity.ok(response);
    }

    @GetMapping({"sale/", "sale"})
    public ResponseEntity<?> discounted
    (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {
        System.out.println(page + " " + size);

        Pageable paging = PageRequest.of(page, size);

        PagedListHolder<Product> productPage;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal().getClass());
        if (auth.getPrincipal() instanceof MyUserDetails) {
            try {
                MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
                userService.increaseDiscountClicks(myUserDetails.getId());
            } catch (Exception e) {
                ResponseEntity.notFound();
            }
        }

        productPage = productService.getDiscounted(paging);

        Set<Product> productSet = new HashSet<>(productPage.getPageList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productSet);
        response.put("currentPage", productPage.getPage());
        response.put("totalItems", productPage.getNrOfElements());
        response.put("totalPages", productPage.getPageCount());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping({ "{id}", "/{id}", "/{id}/, {id}/" })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        if(productService.removeProduct(id)) {
            return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
        } else {
            return ResponseEntity.status(500).body(new MessageResponse("Product deletion failed."));
        }
    }

    @GetMapping({"home","home/"})
    public ResponseEntity<?> home
    (
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {

        Pageable paging = PageRequest.of(page, size);

        PagedListHolder<Product> productPage = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getPrincipal().getClass());
        if (auth.getPrincipal() instanceof MyUserDetails) {
            try {
                MyUserDetails myUserDetails = (MyUserDetails) auth.getPrincipal();
                productPage = productService.getHomeItems(myUserDetails.getId(), paging);
            } catch (Exception e) {
                ResponseEntity.status(500).body("Internal Server Error");
            }
        } else {
            productPage = productService.getDiscounted(paging);
        }

        Set<Product> productSet = new HashSet<>(productPage.getPageList());

        Map<String, Object> response = new HashMap<>();
        response.put("products", productSet);
        response.put("currentPage", productPage.getPage());
        response.put("totalItems", productPage.getNrOfElements());
        response.put("totalPages", productPage.getPageCount());

        return ResponseEntity.ok(response);
    }

}