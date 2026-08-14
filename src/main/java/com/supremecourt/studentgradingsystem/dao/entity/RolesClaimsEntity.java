package com.supremecourt.studentgradingsystem.dao.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity(name="roles_claims")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "roles_claims")
@FieldDefaults(level= AccessLevel.PRIVATE)
public class RolesClaimsEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="roles_id",referencedColumnName = "id")
    RoleEntity role;
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name="claims_id",referencedColumnName = "id")
    ClaimEntity claim;

}
