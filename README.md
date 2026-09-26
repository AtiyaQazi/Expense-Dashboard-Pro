# 📊 Expense Dashboard Pro

A modern, lightweight, and interactive native Android financial tracking application built specifically for university students. Designed with **Kotlin** and **Jetpack Compose**, **Expense Dashboard Pro** offers dynamic budget monitoring, expense breakdown charts, and seamless local data persistence.

---

## ✨ Features

- 📈 **Dynamic Visual Charts:** Real-time horizontal bar charts (`animateFloatAsState`) that visually break down spending across categories (Food, Transport, University, etc.).
- ⚠️ **Smart Budget Warnings:** Custom monthly budget tracking with instant visual alerts (text turns red upon exceeding limits).
- 🔄 **Full CRUD Functionality:** Easily add new transactions with automated date/time stamps and delete history logs.
- 💾 **Persistent Storage:** Local storage mechanism (SQLite / SharedPreferences) to permanently preserve transaction records and budget settings.
- 📱 **Responsive UI:** Smooth, continuous full-page scrolling layout using `LazyColumn` for efficiently rendering long transaction lists.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Declarative UI)
- **State Management:** `remember` & `mutableStateOf`
- **Animations:** Compose `animateFloatAsState`
- **Data Persistence:** SQLite Database / SharedPreferences
- **Build System:** Gradle (Kotlin DSL `.kts`)
- **Minimum SDK:** Android 7.0 (API Level 24+)

---

## 📁 Repository Structure

```text
ExpenseDashboardPro/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/expensedashboardpro/
│   │   │   │   ├── MainActivity.kt        # Main UI & Compose logic
│   │   │   │   ├── DatabaseHelper.kt      # Local data persistence
│   │   │   │   ├── Task.kt                # Data models
│   │   │   │   └── ui/theme/              # Compose Theme, Color & Type setups
│   │   │   ├── res/                       # App icons, themes, and resources
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts                   # App-level dependencies
├── build.gradle.kts                       # Project-level build script
├── settings.gradle.kts
└── README.md
```

🚀 Getting Started
Prerequisites
Android Studio (Ladybug or newer recommended)

JDK 17 or higher

Android Emulator or physical device running Android 7.0 (API Level 24)+

Installation & Run
Clone the repository:

Bash
git clone [https://github.com/AtiyaQazi/Expense-Dashboard-Pro.git]
Open in Android Studio:

Launch Android Studio.

Select Open and choose the cloned ExpenseDashboardPro project directory.

Build & Sync:

Wait for Gradle to finish downloading dependencies and syncing project files.

Run Application:

Connect an emulator or physical device via USB debugging.

Click Run (Shift + F10) to build and launch the application.

👤 Author
Attia Qamar-un-Nisa

Course: Mobile Application Development 
