package com.devsuperior.dscommerce.repositories;

import com.devsuperior.dscommerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByAuthority(String authority);
}
