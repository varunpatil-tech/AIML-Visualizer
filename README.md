# AIML VISUALIZER

### An Android-Based Interactive Learning Platform for Artificial Intelligence and Machine Learning

AIML VISUALIZER is an educational Android application designed to make Artificial Intelligence and Machine Learning concepts easier to understand through interactive algorithm demonstrations and visualizations.

Instead of learning algorithms only through theoretical explanations, students can explore how they work step by step.

## Features

- Interactive demonstrations of AI algorithms.
- Visual representation of algorithm execution.
- Educational activities organized into six learning units.
- Interactive problem-solving and algorithm visualizations.
- Neural network learning demonstrations.

## Algorithms and Concepts

### Unit 1 – Search and Problem Solving
- A* Search
- Hill Climbing
- Best First Search
- Means-Ends Analysis
- Constraint Satisfaction Problems (CSP)
- AO* Search

### Unit 2 – Planning
- Blocks World
- Goal Stack Planning
- Non-Linear Planning
- Hierarchical Planning
- Least Commitment Planning

### Unit 3 – Neural Networks
- Perceptron
- Neural Network Visualization
- Forward Propagation
- Backpropagation
- Activation Functions

*Additional learning activities are organized into Units 4–6.*

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application development |
| XML | User interface design |
| Android Studio | Development environment |
| Gradle | Build automation |
| Android SDK | Android application platform |

## Application Details

- **Project:** AIML VISUALIZER
- **Package:** `com.example.aiml_project`
- **Platform:** Android
- **Minimum SDK:** API 24 (Android 7.0)
- **Academic Year:** 2026–27
- **Institution:** COEP Technological University
- **Department:** Robotics and Artificial Intelligence

## Getting Started

### Prerequisites

- Android Studio
- Android SDK
- Java Development Kit compatible with the project's Gradle configuration
- Android device or emulator

### Installation

1. Clone this repository:

   ```bash
   git clone https://github.com/varunpatil-tech/AIML-Visualizer.git
   ```

2. Open the project in Android Studio.

3. Allow Gradle to synchronize the project.

4. Connect an Android device or start an emulator.

5. Build and run the application.

### Build APK using PowerShell

From the project root, execute:

```powershell
.\gradlew assembleDebug
```

The generated debug APK can be found at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Project Structure

```text
AIML-Visualizer/
│
├── app/
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/aiml_project/
│       │   ├── res/
│       │   │   ├── drawable/
│       │   │   ├── layout/
│       │   │   └── values/
│       │   └── AndroidManifest.xml
│       └── test/
│
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
└── README.md
```

## Project Objective

The objective of AIML VISUALIZER is to provide students with an interactive environment for exploring fundamental Artificial Intelligence and Machine Learning concepts, improving conceptual understanding through visualization and experimentation.

## Repository

[GitHub – AIML VISUALIZER](https://github.com/varunpatil-tech/AIML-Visualizer)

---

**Developed as an academic project at COEP Technological University, Academic Year 2026–27.**