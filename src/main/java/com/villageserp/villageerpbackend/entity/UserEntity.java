package com.villageserp.villageerpbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;
import com.villageserp.villageerpbackend.entity.enums.UserRole;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username" , unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "email" , unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash" , nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role" , nullable = false)
    private UserRole role;

    @Column(name = "is_active" , nullable = false)
    private boolean isActive = true;

    @Column(name = "is_first_login" , nullable = false)
    private boolean isFirstLogin = true;

    @CreatedDate
    @Column(name = "created_at" , updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at" , nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted" , nullable = false)
    private boolean isDeleted = false;

}
