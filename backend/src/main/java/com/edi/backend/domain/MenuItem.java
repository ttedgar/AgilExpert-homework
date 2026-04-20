package com.edi.backend.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "menu_id")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "application_id")
    private Application application;

    @ManyToOne
    @JoinColumn(name = "sub_menu_id")
    private Menu subMenu;

    @PrePersist
    public void validate() {
        if (application != null && subMenu != null) {
            throw new IllegalStateException("MenuItem cannot have both an application and a subMenu");
        }
        if (application == null && subMenu == null) {
            throw new IllegalStateException("MenuItem must have either an application or a subMenu");
        }
    }
}
