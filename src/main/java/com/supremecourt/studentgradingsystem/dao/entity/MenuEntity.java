package com.supremecourt.studentgradingsystem.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import java.util.List;

@Entity(name="menus")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "menus")
@FieldDefaults(level= AccessLevel.PRIVATE)
public class MenuEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    String name;
    String icon;
    String path;
    Boolean isVisible;
    int orderNumber;
    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    List<ComponentEntity> components;
    @OneToOne
    @JoinColumn(name="claim_id")
    ClaimsEntity claims;
}
