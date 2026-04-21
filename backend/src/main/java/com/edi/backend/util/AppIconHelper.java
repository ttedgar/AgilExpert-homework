package com.edi.backend.util;

import com.edi.backend.domain.AppType;
import org.springframework.stereotype.Component;

@Component("appIconHelper")
public class AppIconHelper {

    public String icon(AppType type) {
        return switch (type) {
            case MINESWEEPER -> "bi-flag-fill";
            case OPENMAP     -> "bi-map-fill";
            case PAINT       -> "bi-brush-fill";
            case CONTACTS    -> "bi-person-lines-fill";
        };
    }

}
