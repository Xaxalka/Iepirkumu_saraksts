# Implementation Plan - High-Quality Functionality for Shopping List

This plan outlines the steps to implement a fully functional shopping list app with data persistence, item management, search, and filtering.

## User Review Required

> [!IMPORTANT]
> The implementation will use **Room Database** for local persistence. I will stick to **Java** to remain consistent with the existing codebase (`MainActivity.java`, `AddProductActivity.java`).

## Proposed Changes

### Dependencies & Setup

#### [MODIFY] [libs.versions.toml](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/gradle/libs.versions.toml)
- Add Room library versions and definitions.

#### [MODIFY] [app/build.gradle.kts](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/build.gradle.kts)
- Apply Room dependencies.

---

### Data Layer (Room)

#### [NEW] [Product.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/Product.java)
- Entity class representing a shopping item (id, name, amount, category, isChecked).

#### [NEW] [ProductDao.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/ProductDao.java)
- Data Access Object for CRUD operations (Insert, Delete, Update, Query all).

#### [NEW] [AppDatabase.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/AppDatabase.java)
- Room Database singleton.

---

### UI Components

#### [NEW] [ProductAdapter.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/ProductAdapter.java)
- RecyclerView adapter to display products, handle checkboxes, and edit/delete actions.

---

### Logic Integration

#### [MODIFY] [MainActivity.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/MainActivity.java)
- Initialize RecyclerView and Room database.
- Implement Search logic (`searchInput`).
- Implement Category filtering (`Visi_button`, `Partika_button`, etc.).
- Handle item deletions and check/uncheck status updates.

#### [MODIFY] [AddProductActivity.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/AddProductActivity.java)
- Implement logic to save a new product to the database.
- Add support for editing existing products (when passed via Intent).

---

## Verification Plan

### Automated Tests
- I will verify the build by running `./gradlew assembleDebug`.

### Manual Verification
1. Open the app and verify the list is initially empty.
2. Click "+" to add a product, fill in details, and save. Verify it appears in the list.
3. Check the checkbox and verify the state persists (if we reload the app).
4. Type in the search bar to filter items.
5. Click category buttons to filter by category.
6. Delete an item and verify it disappears.
7. Edit an item and verify changes are saved.
