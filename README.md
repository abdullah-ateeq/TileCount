# ▦ TileCount

**TileCount** is a modern, high-precision Android application designed to quickly and accurately calculate tile requirements, surface area, waste percentages, and box quantities for floor and wall tiling projects.

Built from the ground up using **Jetpack Compose**, **Material 3**, and modern Android architecture standards, TileCount delivers a smooth, intuitive, and 100% offline experience for contractors, interior designers, and DIY enthusiasts.

---

## ✨ Features

- 📐 **High-Precision Tile Calculator**:
  - Calculates surface area, tile area, coverage with waste, and required tiles.
  - Utilizes `BigDecimal` with 50-digit precision (`MathContext.HALF_UP`) to prevent floating-point inaccuracy.
  - Employs strict ceiling rounding (`CEILING`) on tile counts to ensure material shortages never happen on the job site.

- 📦 **Box & Surplus Estimation**:
  - Computes exact box count needed based on tiles per box.
  - Shows total purchased tiles and leftover surplus tiles.

- 📏 **Multi-Unit & Instant Conversion**:
  - Length Units: **Meters (m)**, **Centimeters (cm)**, **Millimeters (mm)**, **Feet (ft)**, **Inches (in)**.
  - Area Units: **Square Meters (m²)**, **Square Feet (ft²)**, **Square Centimeters (cm²)**, **Square Inches (in²)**.
  - Supports mixed-unit dimension inputs with real-time automatic conversion.

- 🏢 **Floor & Wall Surface Support**:
  - Tailored measurement inputs and validation logic for both floor and wall surfaces.

- 📊 **Step-by-Step Breakdown & Visual Diagram**:
  - Interactive tile grid diagram previewing layout orientation and pattern.
  - Complete mathematical formula breakdown explaining every step of the calculation.

- 📜 **Calculation History**:
  - Save past calculations locally.
  - Search, filter, share, or reload past calculations back into the main calculator screen.

- 🎨 **Modern Material 3 Design**:
  - Supports Light Mode, Dark Mode, and System Theme.
  - Tactile haptic feedback integration.
  - Smooth Compose animations and bottom navigation.

- 🔒 **100% Offline & Private**:
  - Zero internet permissions required.
  - No ads, no tracking, and no external dependencies.

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin 2.2](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: MVVM / Clean Architecture (Data, Domain, UI)
- **State Management**: Kotlin Flow & `StateFlow`
- **Navigation**: [Jetpack Compose Navigation](https://developer.android.com/guide/navigation)
- **Asynchronous Programming**: Kotlin Coroutines
- **Testing**: Comprehensive JUnit 4 unit tests covering core math formulas, conversions, parser, validator, and edge cases.

---

## 📁 Project Structure

```text
com.example.tilecount
├── data/
│   ├── model/         # Data classes (AppSettings, AreaUnit, LengthUnit, SurfaceType, TileCountResult, etc.)
│   └── repository/    # Local storage repositories (HistoryRepository, SettingsRepository)
├── domain/
│   └── math/          # Core math engine (TileCountCalculator, UnitConverters, Validator, Formatter)
├── ui/
│   ├── about/         # About screen & app information
│   ├── components/    # Reusable UI components (CalculationStepsCard, FormulaCard, TileDiagram, etc.)
│   ├── history/       # Calculation history & management screen
│   ├── home/          # Main tile calculator screen
│   ├── navigation/    # Navigation host & bottom nav bar setup
│   ├── settings/      # App settings & preferences screen
│   ├── splash/        # Splash screen
│   ├── theme/         # Material 3 color schemes, typography, and theme definitions
│   └── TileCountViewModel.kt # Central StateFlow ViewModel
└── util/              # Utilities (Haptic feedback helper, Share utils)
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug / 2024.2.1 or newer
- **JDK**: JDK 11 or JDK 17
- **Android SDK**: Min SDK 26 (Android 8.0) | Target SDK 37 (Android 15)

### Build & Run
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/TileCount.git
   cd TileCount
   ```
2. Open the project in **Android Studio**.
3. Sync Gradle and run on an Android emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 🧪 Running Tests

To run the unit tests covering mathematical formulas, unit conversions, and edge cases:
```bash
./gradlew test
```

---

## 📄 License

```text
Copyright 2026 Abdullah Ateeq

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
