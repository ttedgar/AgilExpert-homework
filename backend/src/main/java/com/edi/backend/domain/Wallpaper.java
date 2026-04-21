package com.edi.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "wallpapers")
public class Wallpaper extends AppearanceItem {
}
