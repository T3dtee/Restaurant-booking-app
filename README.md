# Restaurant Booking App

A JavaFX desktop application for managing restaurant table reservations, designed with a mobile-style UI (360×800).

## Features

- **Customer** — book a table by date/time, view and cancel booking history
- **Staff** — view today's reservations, check in or cancel bookings
- **Admin** — manage staff accounts, configure restaurant settings (tables, hours, time slots)

## Tech Stack

- Java 25 / JavaFX / Maven
- Gson for JSON persistence (no database)

## Requirements

- Java 21+
- Maven

## Run

```bash
mvn javafx:run
```

## Project Structure

```
src/main/java/com/theerayut/app/
├── controller/   # JavaFX controllers (one per screen)
├── model/        # Data classes (Reservation, Customer, Staff, ...)
├── service/      # Business logic & JSON storage
└── util/         # SceneManager, AnimationUtils, JsonStorage

src/main/resources/com/example/app/ui/
├── *.fxml        # Screen layouts
└── *.css         # Styles
```

Data files (`data/*.json`) are excluded from the repository.
