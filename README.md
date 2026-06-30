# Restaurant Booking App

A JavaFX desktop application for managing restaurant table reservations, designed with a mobile-style UI (360×800).

## Features

- **Customer** — enter name and phone number to log in, book a table by date and time slot, view booking history, cancel bookings
- **Staff** — view all active reservations for today, check in guests or cancel bookings
- **Admin** — manage staff accounts (add, edit, delete), configure restaurant settings (tables, opening hours, time slot interval, advance booking days)

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

## Usage

### Customer Login
Enter name and phone number on the login screen. If the phone number has not been used before, a new account is created automatically.

### Staff / Admin Login
On the customer login screen, click the **BESTMEAL** title at the top — a link to the staff login page will appear.

**Default credentials**

| Username | Password | Role  |
|----------|----------|-------|
| staff1   | 12345    | Staff |
| admin1   | 12345    | Admin |

Staff accounts can be managed by an Admin through the admin panel.

## Data Storage

All data is stored locally as JSON files under `data/` (excluded from this repository):

| File | Contents |
|------|----------|
| `reservations.json` | All reservations |
| `customers.json` | Registered customers |
| `staffList.json` | Staff accounts |
| `config.json` | Restaurant configuration |

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
