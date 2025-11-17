package org.example.usuarios.repository;

import org.example.usuarios.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByIdAndActivoTrue(Long id);

    List<UserEntity> findByActivoTrue();

    Optional<UserEntity> findByVerificationToken(String token);

    Optional<UserEntity> findByEmailAndActivoTrue(String email);

    boolean existsByUsername(String username);
}
