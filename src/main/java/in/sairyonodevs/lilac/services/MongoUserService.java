package in.sairyonodevs.lilac.services;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import in.sairyonodevs.lilac.models.Cart;
import in.sairyonodevs.lilac.models.ERole;
import in.sairyonodevs.lilac.models.Item;
import in.sairyonodevs.lilac.models.Order;
import in.sairyonodevs.lilac.models.Product;
import in.sairyonodevs.lilac.models.Role;
import in.sairyonodevs.lilac.models.User;
import in.sairyonodevs.lilac.repositories.RoleRepository;
import in.sairyonodevs.lilac.repositories.UserRepository;
import in.sairyonodevs.lilac.repositories.CartRepository;
import in.sairyonodevs.lilac.repositories.ProductRepository;
import in.sairyonodevs.lilac.repositories.ItemRepository;
import in.sairyonodevs.lilac.repositories.OrderRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.springframework.stereotype.Service;

@Service
public class MongoUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private OrderRepository orderRepository;

	private static final Logger logger = LoggerFactory.getLogger(MongoUserService.class);

	@Override
	public Page<User> findAll(Pageable pageable) {
		return userRepository.findAll(pageable);
	}
    
	@Override
	@Transactional
    public void register(User user, Set<String> strRoles) {

		// roles for user
        Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		// user's cart
		Cart cart = new Cart(user.getId());

		cartRepository.save(cart);

		user.setCart(cart);
		userRepository.save(user);

    }

	@Override
	public Boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

	@Override
	public Boolean existsById(String id) {
		return userRepository.existsById(id);
	}

	@Override
	public Optional<User> findById(String id) {
		return userRepository.findById(id);
	}

	@Override
	@Transactional
	public Boolean deleteUser(String id) {
		try {

			if (!userRepository.existsById(id)) {
				return false;
			}

			User user = userRepository.findById(id).get();

			// cancelling and deleting user's orders
			user.getOrders().forEach(e -> {
				cancelOrder(e.getId());
				orderRepository.deleteById(e.getId());
			});

			// deleting this user's cart object
			cartRepository.delete(user.getCart());

			// deleting the user itself
			userRepository.delete(user);

			return true;

		} catch (Exception e) {
			throw new Error("500 Internal Server Error: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public Boolean addToCart(String userId, String productId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
		Cart cart = user.getCart();

		Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found."));

		Item item = new Item(cart.getId(), product);
		item = itemRepository.save(item);

		cart.addToCart(item);
		cart = cartRepository.save(cart);

		userRepository.save(user);

		return true;
	}

	@Override
	@Transactional
	public Boolean removeFromCart(String userId, String itemId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found."));
		Cart cart = user.getCart();

		logger.info("{}, {}", userId, itemId);

		Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Cart item not found."));

		cart.removeFromCart(item);

		cartRepository.save(cart);
		itemRepository.delete(item);
		userRepository.save(user);

		return true;

	}

	@Override
	@Transactional
	public Boolean placeOrder(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found."));

		Order order = new Order(user.getId());

		order.setStatus("PENDING");

		if(user.getCart().getCartItems().size() <= 0) {
			return false;
		}

		order.setOrderItems(user.getCart().getCartItems());

		order.getOrderItems().forEach(e -> {
			System.out.println(e.getId());
			if(!e.getProduct().decreaseQuantity()) {
				throw new RuntimeException("Product: " + e.getProduct().getName() + " is currently out of stock. Please try again later.");
			}
		});

		order.getOrderItems().forEach(e -> {
			productRepository.save(e.getProduct());
		});

		order.calcTotalPrice();

		System.out.println(order.getTotalPrice());

		user.getCart().setCartItems(new HashSet<>());

		orderRepository.save(order);
		cartRepository.save(user.getCart());

		user.addOrder(order);
		userRepository.save(user);

		return true;
	}

	@Override
	@Transactional
	public Boolean cancelOrder(String id) {
		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found!"));

		if (order.getStatus().equals("CANCELLED")) {
			throw new RuntimeException("Order has already been cancelled!");
		}

		order.getOrderItems().forEach(e -> {
			e.getProduct().increaseQuantity(1);
			productRepository.save(e.getProduct());
		});

		order.setStatus("CANCELLED");

		orderRepository.save(order);

		return true;
	}

	@Override
	public Boolean approveOrder(String id) {
		Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found!"));	

		order.setStatus("APPROVED");
		orderRepository.save(order);
		return true;
	}

	@Override
	public Boolean increaseDiscountClicks(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		user.setDiscountClicks(user.getDiscountClicks() + 1);
		userRepository.save(user);
		return true;
	}

	@Override
	public Boolean increaseNewArrivalsClicks(String id) {
		User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
		user.setNewArrivalsClicks(user.getNewArrivalsClicks() + 1);
		userRepository.save(user);
		return true;
	}
}
