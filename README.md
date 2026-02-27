# CodeIDE-X

A VSCode-like code editor for Android with syntax highlighting, file explorer, and tab support.

## Features

- 📁 File Explorer - Browse local storage files
- 📑 Tab System - Multiple files open at once
- 🎨 Syntax Highlighting - Support for 15+ languages
- 🔍 Search/Replace - Find and replace in files
- 🌙 Dark Mode - Material3 theming
- ⚡ Jetpack Compose - Modern Android UI

## Supported Languages

- Java, Kotlin, Python
- JavaScript, TypeScript
- HTML, CSS, JSON, XML
- Go, Rust, C, C++

## Tech Stack

- Kotlin + Jetpack Compose
- EditorKit for code editing
- Material Design 3
- MVVM Architecture
- Hilt for Dependency Injection

## Build

### Local Build (x86_64 only)
```bash
./gradlew assembleDebug
```

### GitHub Actions (Recommended)
1. Push to GitHub
2. Open Actions tab
3. Run "Build APK" workflow
4. Download APK from Artifacts

## Project Structure

```
app/src/main/java/com/codeide/x/
├── CodeEditorApp.kt
├── MainActivity.kt
├── core/
│   ├── di/AppModule.kt
│   └── theme/
├── domain/model/
│   ├── FileItem.kt
│   ├── EditorTab.kt
│   └── AppSettings.kt
├── data/repository/
│   └── FileRepository.kt
└── presentation/
    ├── editor/
    ├── explorer/
    └── navigation/
```

## License

MIT
