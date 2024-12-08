package com.github.therenegade.notification.manager.entity;

import com.github.therenegade.notification.manager.entity.enums.PlaceholderType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "placeholder", schema = "notifications")
@Getter
public class Placeholder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PlaceholderType alias;

    @Column(name = "placeholder_value")
    private String value;

    private String description;

    public Placeholder(PlaceholderType placeholderType) {
        this.alias = placeholderType;
        this.value = placeholderType.getPlaceholderName();
        this.description = placeholderType.getDescription();
    }

    protected Placeholder() {}
}
