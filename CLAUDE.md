# Iepirkumu saraksts — project map (Android, Java, Latvian UI)

Shopping-list app. Package `com.example.iepirkumu_saraksts`, minSdk 27, Room DB `shopping_db` (v2, destructive migration, main-thread queries allowed).
Build: `./gradlew assembleDebug`; unit tests: `./gradlew testDebugUnitTest`.

## Code (app/src/main/java/.../)
- `LoginActivity` (launcher) -> `RegisterActivity` / `MainActivity`
- `MainActivity` — list screen: search, category buttons (Visi/Pārtika/Dzērieni/Sadzīve/Citi), FAB -> `AddProductActivity`, menu icon -> `HelpActivity`. Filters in memory (`allProducts`), reloads in `onResume`.
- `AddProductActivity` — add/edit (edit when intent extra `"product"` is a serialized `Product`). Amount must be an integer.
- `ProductAdapter` — RecyclerView adapter, callbacks: checked / edit / delete.
- `ProductIcons` — emoji picture for a product: keyword match on name (lv/ru/en), else category fallback. Add new keywords in its static block.  `categoryFor(name)` also guesses the category; `AddProductActivity` applies it while typing until the user touches the spinner.
- `Product`, `User` (Room entities), `ProductDao`, `UserDao`, `AppDatabase` (singleton).

- Reminders: `Product.reminderTime` (epoch ms, 0 = none; DB v3 with Migration 2->3). `ReminderScheduler` sets an inexact AlarmManager alarm (request code = product id); `ReminderReceiver` shows the notification and re-arms alarms on BOOT_COMPLETED. Picker lives in `AddProductActivity` (POST_NOTIFICATIONS requested on API 33+).

- Admin: built-in account `admin` / `admin123` (seeded in `AppDatabase.seedAdmin`, `User.isAdmin`, DB v4). `LoginActivity` routes admins to `AdminActivity`: stats, open list, clear bought/all products, reset/delete users, change own password. Change the default password after first login.

- Auth: passwords hashed with salted PBKDF2 (`PasswordHasher`, DB v5 migration hashed old plaintext). `Session` remembers the logged-in user id (auto-login in `LoginActivity`; logout via the ⋮ menu in `MainActivity` / admin panel).
- List UX: swipe an item to delete with Snackbar undo, bought items sorted last, empty-state text.

- Photos: `Product.photoPath` (DB v6). `PhotoStore` saves a downsized JPEG in `filesDir/photos`; `AddProductActivity` picks from gallery (GetContent) or camera (TakePicture via FileProvider, cache/camera). Shown in the card instead of the emoji. Files are deleted with the product (after the swipe-undo Snackbar expires).

## Resources (app/src/main/res/)
- layouts: `activity_{login,register,main,add_product,help}.xml`, `item_product.xml` (productImage is a TextView showing the emoji)
- all UI strings in `values/strings.xml` (Latvian); colors in `values/colors.xml`

## Known issues / ideas
- Category is stored as the localized string, so renaming a string breaks existing rows.
- `ProductDao.searchProducts` is unused (search is done in memory).
- Tests: `src/test/.../AddProductActivityTest.java` is untracked; `androidTest/MainActivityTest.java` exists.
