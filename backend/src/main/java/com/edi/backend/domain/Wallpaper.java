package com.edi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "wallpapers")
@Getter
@Setter
@NoArgsConstructor
public class Wallpaper {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color = "#e8f0fe";

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    private User owner;
}
