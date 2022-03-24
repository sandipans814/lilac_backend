package in.sairyonodevs.lilac.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.sairyonodevs.lilac.models.Order;
import in.sairyonodevs.lilac.services.OrderService;

@RestController
@RequestMapping({ "api/orders", "api/orders/" })
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping({"/",""})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getOrders(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size
    ) {
        try {
            Pageable paging = PageRequest.of(page, size);

            Page<Order> orderPage;
            orderPage = orderService.findAll(paging);

            Set<Order> orderSet = new HashSet<>(orderPage.getContent());

            Map<String, Object> response = new HashMap<>();
            response.put("products", orderSet);
            response.put("currentPage", orderPage.getNumber());
            response.put("totalItems", orderPage.getTotalElements());
            response.put("totalPages", orderPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Some error occurred. Please try again later.");
        }
    }
}
