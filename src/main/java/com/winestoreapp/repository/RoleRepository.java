package com.winestoreapp.repository;

import com.winestoreapp.model.Role;
import com.winestoreapp.model.RoleName;
import io.micrometer.observation.annotation.Observed;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Observed
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByName(RoleName roleName);
}
