# Booking History Handoff

Date: 2026-11-01

## Summary

Booking History now guides customers to the relevant reservation after a successful booking, from Home's **View booking** action, and from the duplicate-booking popup. Booking entrance and highlight effects use separate transient flags. Booking submission logic has also been moved into services.

## Implemented Behavior

### Reservation highlighting

- `Reservation.highlight` is independent of `doneBooking`; neither flag is persisted to JSON.
- Home's **View booking** action sets the target reservation ID and highlight flag.
- The booking page's `popUpBHClick()` finds the customer's active reservation for the selected date and time, then marks it for highlighting.
- The target status group starts expanded. After a 250 ms delay, the page scrolls to the card if it is not already fully visible, allowing a 16 px margin.
- Highlight scrolling takes 400 ms and centers the card where the content boundaries permit.
- Once scrolling finishes, the card scales from 100% to 103% and back over 400 ms. Its border transitions to green (`#8caf72`) and returns to the original CSS border.
- The highlight flag is cleared when the pulse starts. The target group stays locked against collapse until the effect finishes.

### Successful booking entrance

- `doneBooking` retains the card entrance effect rather than triggering a highlight by itself.
- The card initially has zero height and opacity. After a 250 ms delay, scrolling approaches the position needed to center the expanded card where possible.
- Scroll duration follows distance: `clamp(distance * 0.75, 200, 500)` milliseconds. Distances of 2 px or less skip the scroll animation.
- A 120 ms pause separates the approach from the entrance.
- The card expands to its original preferred height and slides in over 430 ms. Scrolling follows the expanding layout to maintain the intended viewport position.
- The Booked group is locked until the entrance finishes. If a highlight follows, the lock remains until that effect finishes as well.
- A reservation whose status changed while the success screen was open stays hidden if its current status group starts collapsed.

### Cancellation popup

- Booking History remembers the scroll position before opening the popup.
- Closing the popup returns focus to its trigger when possible.
- Confirming cancellation focuses the ScrollPane instead, because the trigger button will be hidden when the reservation status changes.
- Scroll position is restored synchronously before the closing animation. The delayed restoration that caused visible jumps has been removed.

## Implementation Map

- `bookingController`: handles submission results and the duplicate-booking navigation action.
- `homeController`: marks the Home reservation for highlighting before navigation.
- `bookingHistoryController`: coordinates target groups, scrolling, entrance callbacks, collapse locks, and popup focus.
- `bookingHistoryCardController`: dispatches the appropriate entrance/highlight sequence and consumes the transient flags.
- `bookingHeadStatusCard`: rejects collapse requests while locked.
- `AnimationUtils`: owns scroll calculations, timing, card entrance, border/scale animation, and an optional popup-close callback.
- `bookingHistory.fxml`: exposes the ScrollPane as `historyScroll`.
- `bookingHistoryContainer.css`: defines the highlight border color.
- `BookingService.submitBooking()`: returns `SubmitResult` with `BOOKED`, `ALREADY_BOOKED`, `FULL`, or `UNAVAILABLE`; the controller only selects the corresponding UI response.
- `ReservationService.findCustomerBooking()`: returns `Optional<Reservation>` for an active booking matching customer, date, and time. `isCustomerBooked()` reuses this lookup.

## Important Implementation Details

- ScrollPane content may not be attached to the scene until its skin processes CSS and layout. Prepare CSS/layout through the scene root before measuring or checking the card's scene.
- Resolve the normal and highlighted borders from CSS after the highlight delay. Reading them too early previously returned `null` for both borders.
- Animate the ScrollPane's `vvalue` directly for the initial approach; using the entrance's intermediate offset for this phase previously caused jumps.
- Keep business rules and reusable data lookups in services. UI animation coordination belongs in controllers and UI utilities.
- SceneManager's existing transition callback behavior is unchanged by this commit.

## Validation

- Production compilation passed with JDK 25 using `rtk ./mvnw -q compile`.
- `rtk git diff --check` passed before committing.
- Automated tests could not run: the local Maven cache is missing `org.apache.maven.surefire:surefire-junit-platform:3.2.5`. Offline execution fails for that reason; earlier download attempts were blocked by cache permissions and Maven Central DNS resolution.
- The user confirmed that the highlight border became visible after the CSS timing fix and provided logs confirming distinct border values and successful application.
- The user confirmed that cancellation while scrolled to the bottom passed after the focus fix.
- No automated visual tests were added. Visual checks are performed by the user according to `AGENTS.md`.

## Remaining Verification

- Leave the success screen open until the reservation's status changes, then open History. Verify that a card in an initially collapsed group remains hidden and expands to its natural height when the group is opened.
- After any further animation changes, check short and long histories, target cards near both content boundaries, and collapse requests during pending effects at 360×800.
- Run the Maven test suite when its missing test-runner dependency is available.

## Repository Guidelines Updated

`AGENTS.md` now directs agents to keep controllers focused on UI work and move business logic into services. It also delegates visual UI and animation verification to the user; agents should provide a focused manual checklist rather than launch the app or run visual tests.
