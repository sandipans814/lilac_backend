package in.sairyonodevs.lilac.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.User;

public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}
