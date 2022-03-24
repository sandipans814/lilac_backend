package in.sairyonodevs.lilac.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import in.sairyonodevs.lilac.models.ERole;
import in.sairyonodevs.lilac.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    
    Optional<Role> findByName(ERole name);
}
