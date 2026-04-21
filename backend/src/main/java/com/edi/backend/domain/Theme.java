package com.edi.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "themes")
public class Theme extends AppearanceItem {
}
