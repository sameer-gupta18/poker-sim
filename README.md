# Poker Simulator

A shell-based poker simulator built with **Kotlin** and **Gradle**.

## Overview

Poker Simulator is a command-line project designed to bring poker gameplay into a terminal environment. It provides a foundation for implementing poker game logic, managing gameplay through text-based interactions, and exploring object-oriented software design in Kotlin.

## Tech Stack

- **Language:** Kotlin
- **Build System:** Gradle (Kotlin DSL)
- **Runtime:** JVM

## Getting Started

### Prerequisites

- A JDK compatible with the project's Gradle configuration
- Git

### Installation

Clone the repository:

```bash
git clone https://github.com/sameer-gupta18/poker-sim.git
cd poker-sim
```

### Build the Project

On Windows:

```bash
gradlew.bat build
```

On macOS or Linux:

```bash
chmod +x gradlew
./gradlew build
```

### Running the Project

The project uses Gradle, so you can inspect the available tasks with:

```bash
./gradlew tasks
```

On Windows, use `gradlew.bat tasks`.

To run the simulator, use the appropriate Gradle run task if configured, or execute the application's main class through your IDE.

## Project Structure

```text
poker-sim/
├── src/
│   └── main/
│       └── java/       # Kotlin source files
├── gradle/
│   └── wrapper/        # Gradle Wrapper
├── build.gradle.kts    # Build configuration
├── settings.gradle.kts # Project settings
├── gradlew             # Gradle Wrapper (Unix)
└── gradlew.bat         # Gradle Wrapper (Windows)
```

## Future Improvements

Potential extensions to the project include:

- Multiplayer gameplay and matchmaking
- More comprehensive poker hand evaluation
- Automated players with different strategies
- Game statistics and probability analysis
- A graphical interface alongside the terminal experience

## Author

**Sameer Gupta**

[GitHub Repository](https://github.com/sameer-gupta18/poker-sim)
