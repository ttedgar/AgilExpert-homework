package com.edi.backend.repository;

import com.edi.backend.domain.Wallpaper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallpaperRepository extends JpaRepository<Wallpaper, String> {
}
