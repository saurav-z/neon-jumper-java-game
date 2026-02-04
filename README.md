# Neon Jumper

A polished, neon-themed 2D platformer built with **Java 21**, **Spring Boot 3**, and **JavaFX**. Features character selection with unique effects, 20 procedurally generated levels, and a star-based progression system.

## 🚀 How to Run

### Option 1: Using Docker (Recommended)
No local Java installation required.

1.  **Windows**: Install [Docker Desktop](https://www.docker.com/products/docker-desktop/).
2.  **macOS**: Install [Docker Desktop](https://www.docker.com/products/docker-desktop/).
3.  **Linux**: Grant X11 access with `xhost +local:docker`.
4.  **Launch**:
    ```bash
    docker compose up --build
    ```

### Option 2: Running Locally (Native)
Requires **Java 21** and **Maven**.

1.  **Clone & Enter**:
    ```bash
    git clone <repository-url>
    cd neon-jumper-java-game
    ```
2.  **Run**:
    ```bash
    mvn spring-boot:run
    ```

### Option 3: VS Code (Windows/macOS/Linux)
1. Install the **Extension Pack for Java**.
2. Open the `neon-jumper-java-game` folder in VS Code.
3. Wait for the Java language server to finish importing.
4. Press **F5** or find the `NeonJumperApplication` class and click **Run**.

## 🛠️ Tech Stack
- **Engine**: JavaFX 21
- **Framework**: Spring Boot 3.2.2
- **Build Tool**: Maven
- **Serialization**: Jackson (JSON Save Data)

## 🕹️ Controls
- **WASD / Arrows**: Move & Jump
- **Space**: Jump
- **Shift**: Nitro Boost (Fast Right/Left)
- **ESC**: Pause / Menu
- **Mouse**: Navigate UI

## 🎮 Features
- **Nitro System**: High-speed movement with regenerating fuel.
- **Score System**: Collect gold coins for points.
- **Moving Platforms**: Horizontal and vertical platforms with riding physics (move with the platform).
- **Procedural Levels**: 20 unique levels with increasing difficulty and obstacles.
- **Visual Polish**: 3D spinning coins, neon glow effects, and particle trails.
- **Character Skins**: Choose between Neon Cube and Neon Comet (persistent trail).
- **Save System**: Local persistence for progress and stars.
