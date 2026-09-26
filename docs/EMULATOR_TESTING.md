# HateItOrRateIt — Emulator testing

Project-specific facts for the `emulator-testing` skill. Generic adb/uiautomator
technique lives in the skill itself, not here — this file is only what's true about
*this* app.

## Device facts

- AVD: `Medium_Phone_API_36.1` (1080x2400)
- Package id(s): `com.grappim.hateitorrateit.fdroid.debug` (fdroid debug, used for day-to-day
  verification — no Firebase setup needed), `com.grappim.hateitorrateit.debug` (gplay debug)
- Activity: `com.grappim.hateitorrateit.ui.screens.main.MainActivity`
- Build + install: `./gradlew assembleFdroidDebug` then
  `adb -s emulator-5554 install -r app/build/outputs/apk/fdroid/debug/app-fdroid-debug.apk`

## App-specific gotchas

- **Seeding products without the UI.** The AVD image has no `sqlite3` binary. Launch the app
  once so Room creates `databases/hateitorrateit_debug.db`, `force-stop`, pull the `.db`,
  `-wal` and `-shm` via `adb exec-out run-as <pkg> cat databases/<file>`, insert rows on the
  host with Python's `sqlite3`, then `pragma wal_checkpoint(TRUNCATE)` +
  `pragma journal_mode=DELETE` and pipe the single `.db` back with
  `cat db | adb shell "run-as <pkg> sh -c 'cat > databases/hateitorrateit_debug.db && rm -f databases/hateitorrateit_debug.db-wal databases/hateitorrateit_debug.db-shm'"`.
  `products_table.createdDate` is `ISO_DATE_TIME` text (e.g. `2026-09-26T10:00:00+00:00`);
  only rows with `isCreated=1` appear on Home.
- **Home search field** is the `EditText` at bounds `[42,105][1038,269]`; results update on
  every keystroke, no submit needed.
- **Typing an apostrophe with `input text`**: `adb shell "input text \"Sam\\'s\""` types the
  backslash literally. Use `adb shell "input text 'Sam'\\''s'"` from the host shell instead.
