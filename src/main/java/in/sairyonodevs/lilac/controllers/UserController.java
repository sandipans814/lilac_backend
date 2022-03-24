package in.sairyonodevs.lilac.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.sairyonodevs.lilac.models.Order;
import in.sairyonodevs.lilac.models.User;
import in.sairyonodevs.lilac.models.response.MessageResponse;
import in.sairyonodevs.lilac.services.MyUserDetails;
import in.sairyonodevs.lilac.services.UserService;

@RestController
@RequestMapping({ "api/users", "api/users/" })
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping({ "/", "" })
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        try {
            Pageable paging = PageRequest.of(page, size);

            Page<User> userPage;

            userPage = userService.findAll(paging);

            Set<User> userSet = new HashSet<>(userPage.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("users", userSet);
            response.put("currentPage", userPage.getNumber());
            response.put("totalItems", userPage.getTotalElements());
            response.put("totalPages", userPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping({ "{id}", "/{id}", "/{id}/, {id}/" })
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable String id) {

        if (isAuthorized(id)) {
            return ResponseEntity.ok(userService.findById(id).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping({ "{id}", "/{id}", "/{id}/", "{id}/" })
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {

        try {
            if (userService.deleteUser(id)) {
                return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping({"{id}/cart", "/{id}/cart", "/{id}/cart/", "{id}/cart/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getCart(@PathVariable String id) {
        try {
            if (isAuthorized(id)) {
                User user = userService.findById(id).get();
                return ResponseEntity.ok(user.getCart());
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping({"{id}/cart", "/{id}/cart", "/{id}/cart/", "{id}/cart/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> addToCart(@PathVariable String id, @Valid @RequestBody Map<String, String> addToCartMap) {
        try {

            if(isAuthorized(id)) {
                if(userService.addToCart(id, addToCartMap.get("productId")))
                    return ResponseEntity.ok("Product added to cart successfully!");
                else
                    return ResponseEntity.status(500).body("Some error occurred. Please try again later.");
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping({"{id}/cart/{itemId}", "/{id}/cart/{itemId}/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> removeFromCart(@PathVariable String id, @PathVariable String itemId) {
        try {

            if (isAuthorized(id)) {
                if(userService.removeFromCart(id, itemId))
                    return ResponseEntity.ok("Product deleted from cart successfully!");
                else
                    return ResponseEntity.status(500).body("Some error occurred. Please try again later.");
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping({"{id}/orders", "/{id}/orders", "/{id}/orders/", "{id}/orders/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getOrders(
        @PathVariable String id,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {
        try {

            if (isAuthorized(id)) {
                Pageable paging = PageRequest.of(page, size);

                User user = userService.findById(id).get();
    
                Set<Order> orderSet = user.getOrders();
                Page<Order> orders = new PageImpl<>(orderSet.stream().collect(Collectors.toList()), paging, orderSet.size());
    
                Map<String, Object> response = new HashMap<>();
                response.put("orders", new HashSet<>(orders.getContent()));
                response.put("currentPage", orders.getNumber());
                response.put("totalItems", orders.getTotalElements());
                response.put("totalPages", orders.getTotalPages());
    
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(404).build();
            }

        } catch (Exception e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping({"{id}/orders", "/{id}/orders", "/{id}/orders/", "{id}/orders/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> placeOrder(@PathVariable String id) {
        try {
            if(isAuthorized(id)) {
                if(userService.placeOrder(id))
                    return ResponseEntity.ok("Order placed successfully");
                else 
                    return ResponseEntity.badRequest().body("Cart is empty! Cannot place order.");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping({"{id}/orders/{orderId}", "/{id}/orders/{orderId}/"})
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<?> getOrder(
        @PathVariable String id, 
        @PathVariable String orderId, 
        @RequestParam(required = false) String action
    ) {
        try {
            if(isAuthorized(id)) {

                if (action == null) {
                    User user = userService.findById(id).get();
                    Optional<Order> order = user.getOrders().stream().filter(e -> e.getId().equals(orderId)).findAny();
    
                    if (order.isPresent()) {
                        return ResponseEntity.ok(order.get());
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                }

                if (action.equals("cancel")) {
                    userService.cancelOrder(orderId);
                    return ResponseEntity.ok("Your order cancellation was successful!");
                } else if (action.equals("approve")) {
                    userService.approveOrder(orderId);
                    return ResponseEntity.ok("Your order approval was successful!");
                }
            }
            return ResponseEntity.ok("Order placed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    private Boolean isAuthorized(String id) {
        MyUserDetails userDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal().getClass());

        if (!userDetails.getAuthorities().stream().anyMatch(e -> e.toString() == "ROLE_ADMIN")) {
            logger.info("{}, {}", userDetails.getId(), id);
            if (userDetails.getId().equals(id)) {
                if (userService.existsById(id)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (userService.existsById(id)) {
            return true;
        } else {
            return false;
        }
    }
}
