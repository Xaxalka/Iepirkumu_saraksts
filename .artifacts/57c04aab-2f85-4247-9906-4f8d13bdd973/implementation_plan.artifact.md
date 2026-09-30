# Implementation Plan: Login and Registration System

This plan describes how to add a local authentication system (login and registration) to the "Iepirkumu saraksts" app using the existing Room database.

## User Review Required

> [!IMPORTANT]
> This implementation uses **plain text passwords** stored in a local SQLite database for simplicity, which is **not secure** for production apps. In a real-world scenario, passwords should be hashed (e.g., using BCrypt) and ideally handled by a backend or a service like Firebase.

> [!NOTE]
> The app will be updated to start with the `LoginActivity`. The user will need to register or log in before accessing the main shopping list.

## Proposed Changes

### Database Layer

#### [NEW] [User.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/User.java)
- Create a `User` entity with `id`, `username`, and `password` fields.

#### [NEW] [UserDao.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/UserDao.java)
- Define methods for user registration (`insert`) and authentication (`getUser`).

#### [MODIFY] [AppDatabase.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/AppDatabase.java)
- Add the `User` class to the `@Database` entities list.
- Increase the database version.
- Add an abstract method to provide `UserDao`.

---

### UI Layer (Layouts)

#### [NEW] `activity_login.xml`
- Create a layout with inputs for username and password, a "Login" button, and a link to the "Register" screen.

#### [NEW] `activity_register.xml`
- Create a layout with inputs for username, password, and password confirmation, and a "Register" button.

---

### Logic Layer (Activities)

#### [NEW] [LoginActivity.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/LoginActivity.java)
- Handle credential checking against the database and navigation to `MainActivity` on success.

#### [NEW] [RegisterActivity.java](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/java/com/example/iepirkumu_saraksts/RegisterActivity.java)
- Handle user creation in the database and navigation to `LoginActivity` after successful registration.

---

### Configuration

#### [MODIFY] [AndroidManifest.xml](file:///home/nikita/AndroidStudioProjects/Iepirkumu_saraksts/app/src/main/AndroidManifest.xml)
- Register `LoginActivity` and `RegisterActivity`.
- Set `LoginActivity` as the launcher activity (the one that starts first).

## Verification Plan

### Automated Tests
- Not applicable for this simple UI/DB flow at this stage.

### Manual Verification
1. Launch the app: It should show the Login screen.
2. Click "Register": It should navigate to the Registration screen.
3. Fill in registration details and click "Register": It should return to Login and show a success message.
4. Fill in login details and click "Login": It should navigate to the main shopping list.
5. Verify that incorrect credentials show an error message.
