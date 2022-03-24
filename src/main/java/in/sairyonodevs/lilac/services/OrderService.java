package in.sairyonodevs.lilac.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import in.sairyonodevs.lilac.models.Order;

public interface OrderService {
    Page<Order> findAll(Pageable pageable);
}
