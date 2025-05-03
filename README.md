# TextAIApp

## Overview
TextAIApp is an Android application designed to help users create, correct, and improve texts using artificial intelligence. The app provides a user-friendly interface for interacting with AI-powered text processing features.

## Features
- **Splash Screen**: A welcoming splash screen displayed when the app is launched.
- **Personal Data Input**: A form to collect user information such as name, age, gender, and contact details.
- **Text Prompt and Response**: A screen where users can input text prompts and receive AI-generated responses.

## App Flow
1. **Splash Screen**: The app starts with a splash screen (`SplashActivity`) that transitions to the main activity after a short delay.
2. **Personal Data Form**: Users are directed to the `PersonalDataFragment`, where they can input their personal details.
3. **Text Interaction**: After submitting personal data, users are navigated to the `PromptAndResponseFragment`, where they can interact with the AI by entering text prompts and receiving responses.

## Technical Details
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Android Navigation Component
- **UI Components**:
    - ConstraintLayout for responsive layouts
    - Data Binding for binding UI components to ViewModels

## How to Run
1. Clone the repository to your local machine.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical device.

## Future Enhancements
- Integration with an AI API for text processing.
- Improved UI/UX for better user interaction.
- Additional features for text formatting and customization.

## Folder Structure
- `app/src/main/java/com/rodolfoz/textaiapp`: Contains the main application logic.
- `app/src/main/res/layout`: Contains the XML layout files for the app's UI.
- `app/src/main/AndroidManifest.xml`: Defines the app's components and permissions.

## License
This project is licensed under the MIT License. See the LICENSE file for details.