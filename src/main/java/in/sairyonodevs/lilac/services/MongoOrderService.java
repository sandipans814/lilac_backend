package in.sairyonodevs.lilac.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import in.sairyonodevs.lilac.models.Order;
import in.sairyonodevs.lilac.repositories.OrderRepository;

@Service
public class MongoOrderService implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private static final Logger logger = LoggerFactory.getLogger(MongoOrderService.class);

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
}
