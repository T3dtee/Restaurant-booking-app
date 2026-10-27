# Repository Guidelines

## Project Structure & Module Organization

BestMeal is a JavaFX restaurant booking desktop app with a fixed 360×800 window.

- `src/main/java/com/theerayut/app/`: application code; `controller/` handles screens, `model/` holds domain objects, `service/` owns business logic, and `util/` provides shared helpers.
- `src/main/resources/com/example/app/ui/`: FXML layouts. The adjacent `style/` directory contains CSS, fonts, and images.
- `AppData` connects services and stores login state. Keep booking rules in services and persistence in `JsonStorage`.
- `data/`: local JSON records and configuration; ignored by Git. `target/` contains generated build output.

Java packages and resource paths intentionally differ; preserve resource references when changing layouts.

## Build, Test, and Development Commands

Use JDK 25, matching `pom.xml`, and run commands from the repository root:

- `./mvnw javafx:run`: launch the desktop application.
- `./mvnw -q compile`: compile production code.
- `./mvnw test`: run the Maven test phase.
- `./mvnw package`: build application JARs, including the shaded `-all.jar`.

Use `mvnw.cmd` on Windows or installed Maven (`mvn`) instead of the wrapper.

## Coding Style & Naming Conventions

Use four-space indentation and match surrounding Java formatting. Prefer `UpperCamelCase` for new classes and `lowerCamelCase` for methods and fields. Existing controller names include lowercase initials; avoid unrelated renames, and keep FXML controller bindings synchronized.

Keep visual styling in CSS, prefer style classes over inline styles, and reuse `SceneManager` and `AnimationUtils`. No formatter or lint plugin is configured.

## Testing Guidelines

JUnit Jupiter 5.12.1 is configured, but no `src/test/` suite or coverage threshold exists. Add tests under `src/test/java/com/theerayut/app/`, mirroring production packages, with names such as `BookingServiceTest`.

For behavior changes, cover relevant booking boundaries and persistence behavior. Compile and manually exercise affected customer, staff, or admin flows; inspect layout changes at 360×800.

## Commit & Pull Request Guidelines

Follow existing Conventional Commit subjects, such as `feat: add weekly closed days` or `fix: open date picker on first click`. Use short body bullets grouped by logical behavior change.

PRs should describe changed behavior, link relevant issues, record validation commands and results, and include screenshots for UI changes. Keep local customer records and credentials out of commits.

## Agent Instructions

Follow `/Users/t3dtee/.codex/RTK.md`; prefix shell commands with `rtk` in this environment.
