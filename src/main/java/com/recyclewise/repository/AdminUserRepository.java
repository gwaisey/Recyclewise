package com.recyclewise.repository;

import com.recyclewise.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByEmailAndActiveTrue(String email);
    List<AdminUser> findByRoleOrderByFullNameAsc(AdminUser.AdminRole role);
    List<AdminUser> findAllByOrderByRoleAscFullNameAsc();
}
