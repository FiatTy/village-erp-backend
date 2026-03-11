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

import com.villageserp.villageerpbackend.entity.enums.HouseStatus;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "houses")
@EntityListeners(AuditingEntityListener.class)
public class HouseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column( updatable = false, nullable = false)
    private UUID id;

    @Column(name = "house_number" , unique = true, nullable = false, length = 20)
    private String houseNumber;

    @Column(name = "zone" , length = 50)
    private String zone;

    @Column(name = "house_type" , length = 50)
    private String houseType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false)
    private HouseStatus status = HouseStatus.VACANT;

    @CreatedDate
    @Column(name = "created_at" ,updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at" ,nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted" ,nullable = false)
    private boolean isDeleted = false;
}
