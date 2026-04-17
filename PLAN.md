# AgilExpert Homework – Implementation Plan

## Áttekintés

Egy intelligens eszközökre tervezett kliens-szerver OS szimulációja.
A felhasználók saját asztalt (menüt, ikonokat, háttérképet, arculatot) konfigurálnak,
az adatok egy központi szerveren tárolódnak.

**Pozíció:** Fullstack fejlesztő
**Stack:** Java 21, Spring Boot 4.x, Spring Data JPA, Hibernate, Thymeleaf, Bootstrap 5, PostgreSQL (prod) / H2 (dev)
**Build:** Maven (single module: `backend`)
**Deploy:** Railway (PostgreSQL addon)

---

## Domain modell

```
User
  - id (String/UUID)
  - name
  - mainMenu (Menu) 1:1
  - wallpapers (List<Wallpaper>) 1:many
  - activeWallpaper (Wallpaper, nullable)
  - themes (List<Theme>) 1:many
  - activeTheme (Theme, nullable)

Menu
  - id (String/UUID)
  - name
  - owner (User)
  - parentMenu (Menu, nullable)   ← null = főmenü
  - items (List<MenuItem>) 1:many

MenuItem
  - id (String/UUID)
  - name
  - position (int)
  - menu (Menu)
  - application (Application, nullable)   ← app shortcut VAGY almenü, nem mindkettő
  - subMenu (Menu, nullable)

Application                               ← globális seed adat, nem user-owned
  - id (String/UUID)
  - name (enum: MINESWEEPER, OPENMAP, PAINT, CONTACTS)

Wallpaper
  - id (String/UUID)
  - name
  - owner (User)

Theme
  - id (String/UUID)
  - name
  - owner (User)
```

---

## URL struktúra

```
GET  /                          → redirect /users
GET  /users                     → user lista
GET  /users/new                 → user létrehozás form
POST /users                     → user mentés
GET  /users/{id}/edit           → user szerkesztés form
POST /users/{id}                → user frissítés
POST /users/{id}/delete         → user törlés

GET  /users/{id}/desktop        → user asztala (fő nézet)
GET  /users/{id}/menu/edit      → menü szerkesztés
POST /users/{id}/menu/items     → ikon hozzáadása
POST /users/{id}/menu/items/{itemId}/delete → ikon törlése

POST /users/{id}/wallpapers     → háttérkép hozzáadása
POST /users/{id}/wallpapers/{wpId}/activate → háttérkép kiválasztása
POST /users/{id}/themes         → arculat hozzáadása
POST /users/{id}/themes/{themeId}/activate  → arculat váltás

GET  /app/{type}                → app placeholder oldal
POST /simulation                → demo adatok betöltése
```

---

## Fázisok

---

### Fázis 1 – Projekt inicializálás + Domain entitások

**Cél:** az app elindul hibák nélkül, Hibernate legenerálja a sémát.

#### Projekt struktúra (single module)
```
backend/src/main/
  java/com/edi/backend/
    domain/       ← JPA entitások
    repository/   ← Spring Data repository interfészek
    service/      ← üzleti logika
    web/          ← Spring MVC controllerek
  resources/
    templates/    ← Thymeleaf HTML fájlok
    static/
      css/        ← saját stílus
      js/         ← saját szkriptek
    application.properties
    application-dev.properties
    application-prod.properties
```

#### Entitások (`backend/src/main/java/.../domain/`)
- `User.java` – `@Entity`, `@Id`, mezők, `@OneToOne` mainMenu, `@OneToMany` wallpapers/themes
- `Menu.java` – `@Entity`, `@ManyToOne` owner + parentMenu, `@OneToMany` items
- `MenuItem.java` – `@Entity`, `@ManyToOne` menu + application + subMenu
- `Application.java` – `@Entity`, name mint enum
- `Wallpaper.java` – `@Entity`, `@ManyToOne` owner
- `Theme.java` – `@Entity`, `@ManyToOne` owner

**Tesztelés:** `mvn spring-boot:run` → app elindul hibák nélkül, Hibernate sémagenerálás logban látható

---

### Fázis 2 – Repository + Service réteg + Seed adat

**Cél:** adatok keletkeznek induláskor, service réteg működik.

#### Repository-k (`backend/src/main/java/.../repository/`)
- `UserRepository extends JpaRepository<User, String>`
- `MenuRepository`, `MenuItemRepository`, `ApplicationRepository`
- `WallpaperRepository`, `ThemeRepository`

#### Service-k (`backend/src/main/java/.../service/`)
- `UserService` – CRUD + főmenü auto-létrehozás user mentésekor
- `MenuService` – ikon hozzáadás/törlés/módosítás, almenü kezelés
- `ApplicationService` – app lekérdezés típus szerint

#### Seed adat (`DataSeeder.java` – `ApplicationRunner`)
- 4 `Application` rekord létrehozása ha még nem léteznek: MINESWEEPER, OPENMAP, PAINT, CONTACTS
- A fejlesztő (te) hozzáadása userként induláskor ha még nem létezik
- Kedvenc appok hozzáadása a főmenühöz

**Tesztelés:** app indul → H2 console-on láthatók az `APPLICATION` tábla sorai + a fejlesztő user rekordja a kedvenc appjaival

---

### Fázis 3 – User CRUD UI

**Cél:** böngészőből lehet usereket kezelni.

#### Controller
- `UserController` – GET/POST végpontok a user listához és formokhoz

#### Templates (`backend/src/main/resources/templates/`)
- `layout/base.html` – Thymeleaf layout fragment: navbar, Bootstrap 5 CDN, Bootstrap Icons CDN, Inter font CDN
- `users/list.html` – user kártyák grid-ben + "Load Demo Data" gomb + "New User" gomb
- `users/form.html` – create/edit form (name mező, mentés, mégse)

#### Vizuális stílus alapjai (`backend/src/main/resources/static/css/style.css`)
- Inter font
- Lekerekített kártyák (border-radius: 16px)
- Fehér/világosszürke háttér (#f5f5f7 – Apple gray)
- Egy kék accent szín (#007AFF – iOS blue)

**Tesztelés:** `http://localhost:8080/users` → user lista megjelenik → új user hozzáadható, szerkeszthető, törölhető

---

### Fázis 4 – Desktop nézet + Menü szerkesztés

**Cél:** egy user asztala megjelenik és szerkeszthető.

#### Controller
- `MenuController` – desktop GET, ikon CRUD végpontok

#### Templates
- `users/desktop.html` – iOS-szerű ikon rács, bottom bar wallpaper/theme infóval
- `menu/edit.html` – ikon hozzáadás (app kiválasztás dropdown + név), törlés, almenü létrehozás

#### Logika
- Desktop megnyitásakor a user főmenüjének ikonjai jelennek meg
- Almenü ikonra kattintva az almenü ikonjai jelennek meg (breadcrumb navigációval visszafelé)
- "+" gomb ikon hozzáadáshoz

**Tesztelés:** user kártyára kattintva megnyílik az asztala → ikonok láthatók → + gombbal új ikon adható hozzá → ikon törölhető

---

### Fázis 5 – Háttérkép és Arculat

**Cél:** wallpaper és theme kezelés működik, a desktop vizuálisan reagál rájuk.

#### Végpontok (UserController-be integrálva)
- Háttérkép hozzáadása (csak név, nincs képfájl feltöltés)
- Aktív háttérkép kiválasztása → desktop háttérszíne megváltozik (CSS változóval, név hash alapján)
- Arculat hozzáadása és váltása → CSS class csere (pl. `theme-dark`, `theme-light`)

#### Desktop bottom bar
- Wallpaper dropdown: aktuális neve + váltás
- Theme dropdown: aktuális neve + váltás

**Tesztelés:** desktop-on kiválasztható wallpaper és theme → a háttér/stílus megváltozik

---

### Fázis 6 – App placeholder oldalak

**Cél:** az ikonra kattintva megnyílik az app oldala.

#### Controller
- `ApplicationController` – `GET /app/{type}` → típus alapján template kiválasztás

#### Templates
- `apps/minesweeper.html` – statikus 8x8 CSS grid, kattintásra semmi
- `apps/openmap.html` – OpenStreetMap iframe (tile.openstreetmap.org, API kulcs nélkül)
- `apps/paint.html` – üres `<canvas>` element + dummy toolbar
- `apps/contacts.html` – üres lista "No contacts yet" placeholder-rel

**Tesztelés:** minden app ikonra kattintva megnyílik a saját placeholder oldala, vissza gomb visszavisz a desktopra

---

### Fázis 7 – Szimuláció

**Cél:** egy gombnyomásra betöltődik a brief-ben leírt példa adat.

#### SimulationService
A brief szerinti family scenario:
- Apa user: GPS (OpenMap) app a menüben
- Anya user: receptek helyett Paint app (a brief receptes appot említ de csak a 4 példa app implementált)
- 2 gyerek user: Minesweeper + egyéb játék
- Mindenki kap alapmenüt, aztán saját testreszabást
- Wallpaperek és theme-ek hozzáadva mindenkinek

#### Controller
- `POST /simulation` → `SimulationService.run()` → redirect `/users`

**Tesztelés:** "Load Demo Data" gomb → user lista megtelik a family scenario adataival → minden user desktopja helyes tartalommal jelenik meg

---

### Fázis 8 – Railway deploy

**Cél:** az app él a neten.

#### Konfiguráció
- `railway.toml` – build és start parancs
- `application-prod.properties` – `${DATABASE_URL}` environment variable használata
- Railway dashboard: PostgreSQL addon hozzáadása, `DATABASE_URL` env var auto-beállítva

#### Lépések
1. Railway projekt létrehozása
2. GitHub repo összekapcsolása
3. PostgreSQL addon hozzáadása
4. `SPRING_PROFILES_ACTIVE=prod` env var beállítása
5. Deploy

**Tesztelés:** Railway URL-en elérhető az app, demo adatok betölthetők, minden fázis funkciói működnek

---

## Commit stratégia

Minden fázis végén legalább egy commit, de közben is commitolunk ha egy logikai egység elkészül:

```
feat: add JPA entities with relationships
feat: add repositories and service layer
feat: seed application data and developer user on startup
feat: add user CRUD UI
feat: add desktop view with icon grid
feat: add menu item management
feat: add wallpaper and theme switching
feat: add app placeholder pages
feat: add simulation with family scenario
feat: add Railway deployment config
```

---

## Inicializálás – KÉSZ ✅

- Spring Boot 4.x backend inicializálva
- Függőségek: web, thymeleaf, data-jpa, h2, postgresql
- Dev/prod profilok konfigurálva
- App elindul, Tomcat fut 8080-on
