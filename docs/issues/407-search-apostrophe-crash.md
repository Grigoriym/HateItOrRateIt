# 407 — Crash when attempting to search apostrophe

**Status:** Done
**Link:** https://github.com/Grigoriym/HateItOrRateIt/issues/407   **Updated:** 2026-09-26

## Report

> Whenever I try to search anything with an apostrophe the app immediately crashes to my
> home screen. Very unfortunate when trying to look up entries for Sam's Club.

- **Symptom:** typing `'` into the Home search field crashes the app.
- **Environment:** Google Play build 1.5.2; Pixel 7a and Samsung A54 (two devices, so not
  device-specific).
- **Reporter's diagnosis:** none offered.
- **Omitted:** no stack trace. Not needed — the cause is reproducible from source (below).

## Findings

1. The search text is concatenated straight into a SQL string with no escaping.
   `SqlQueryBuilder.buildWhereClause` wraps the raw query as `'%<query>%'`
   (`data/repo-impl/src/main/java/com/grappim/hateitorrateit/data/repoimpl/helpers/SqlQueryBuilder.kt:24`,
   `:44`, `:49`). The KDoc on `wrapWithSingleQuotes` says it exists "to avoid SQL syntax
   errors", but a quote *inside* the value closes the literal early.
2. The string is passed to Room as a raw query with no bind args:
   `SimpleSQLiteQuery(sqLiteQuery)` →
   `ProductsDao.getAllProductsByRawQueryFlow`
   (`data/repo-impl/.../ProductsRepositoryImpl.kt:145-146`,
   `data/db/.../dao/ProductsDao.kt:29-30`).
3. **Reproduced.** Running the exact generated SQL for `Sam's` against SQLite:
   ```
   SELECT * FROM p WHERE (name LIKE '%Sam's%' OR ...) AND isCreated=1
   → OperationalError near "s": syntax error
   ```
   On Android this surfaces as `SQLiteException` when Room's flow executes the query.
4. Nothing catches it. `HomeViewModel.getProducts` collects the flow in
   `viewModelScope.launch { ... .collect() }` with no `catch`
   (`feature/home/ui/.../HomeViewModel.kt:62-81`), and the repository flow has none either.
   An uncaught exception in `viewModelScope` goes to the thread's default handler → process
   crash. Because `viewState.flatMapLatest` re-queries on every state change, the crash
   fires on the keystroke that inserts the `'`.
5. **Confirmed in production (Crashlytics, gplay).** A fatal
   `SQLiteException: near "=": syntax error` while compiling
   `... WHERE (name LIKE '%='%' OR shop LIKE '%='%' OR description LIKE '%='%') AND isCreated=1 ...`,
   thrown from `ProductsDao_Impl.getAllProductsByRawQueryFlow`. The search text there was
   `='`: a different string, but the same failure — the `'` ends the literal early. The SQL
   exactly matches the shape `SqlQueryBuilder` produces.
6. Existing tests (`data/repo-impl/src/test/.../helpers/SqlQueryBuilderTest.kt`) only
   assert the exact SQL string for plain alphanumeric input; none cover special characters.

## Root cause

`SqlQueryBuilder.buildWhereClause` (`SqlQueryBuilder.kt:24`) interpolates user input into
a SQL string literal instead of using a bound parameter, so any `'` in the search text
produces invalid SQL. The resulting `SQLiteException` is uncaught on the path from
`ProductsRepositoryImpl.getProductsFlow` to `HomeViewModel.getProducts`, which crashes the app.

## Impact

- Every user, both flavors, any search containing `'` — common in shop/product names
  (Sam's, Trader Joe's, McDonald's). Crash is 100% reproducible.
- Workaround: search without the apostrophe (e.g. `Sam`).
- Adjacent problems from the same line, not reported but same cause:
  - Input is SQL-injectable, e.g. `%' OR 1=1 OR name LIKE '` bypasses the `isCreated=1`
    filter and shows unfinished draft products. Local DB only, so low security severity,
    but it's the same bug class.
  - `%` and `_` typed by the user act as LIKE wildcards (searching `_` matches everything).
    *Inference from SQLite LIKE semantics; not a crash.*

## Open questions

- None blocking. Whether to also treat `%`/`_` literally is a product choice (see Options).

## Options

### A. Bind the search term as a parameter (recommended)
Have the builder return SQL with `?` placeholders plus bind args, and pass them via
`SimpleSQLiteQuery(sql, bindArgs)`. Also escape `%`/`_`/`\` in the term and add
`ESCAPE '\'` so user input is matched literally. Bind `type` the same way.

- **Pros:** fixes the crash and the injection at the root; standard, idiomatic Room/SQLite.
- **Cons:** changes `SqlQueryBuilder`'s return type, so its existing string-equality tests
  must be rewritten; `ESCAPE` makes every LIKE clause slightly longer.
- **Risk / blast radius:** low. `SqlQueryBuilder` is used only by `ProductsRepositoryImpl`.

### B. Escape single quotes (`'` → `''`) in the current string builder
- **Pros:** one-line change; existing test shape stays.
- **Cons:** hand-rolled escaping of SQL — easy to get subtly wrong later; leaves `%`/`_`
  wildcard behaviour; still string-built SQL.
- **Risk:** low for this symptom, but it's a patch on the wrong abstraction.

### C. Add `.catch {}` in `HomeViewModel`
- **Pros:** also guards against any future DB error crashing Home.
- **Cons:** does not fix the bug — searching `Sam's` would silently show nothing instead
  of Sam's Club. Masks the cause. Only reasonable as an addition to A, not instead of it.

**Recommendation: A.** It removes the class of bug rather than the one character, and the
scope is a single helper plus its caller.

## Decision

Option A, approved by the maintainer (Grigoriym) on 2026-09-26.

## Plan

1. Regression tests in `SqlQueryBuilderTest`: search text containing `'`, `%`, `_` and `\`
   must go into the bind args, never into the SQL, and the SQL must use `?` placeholders
   with `ESCAPE '\'`. These fail against the current builder.
2. Fix: `SqlQueryBuilder` returns SQL plus bind args; `ProductsRepositoryImpl` passes them to
   `SimpleSQLiteQuery(sql, args)`. Update `ProductsRepositoryImplTest` for the new return type.
3. Manual check on the emulator: search `Sam's` and `='` on Home; the app must not crash
   and `Sam's` must match a product named "Sam's Club". Automated DB-level coverage isn't
   practical here: `:data:repo-impl` has no Robolectric or Room test setup, and the only
   Room tests are instrumented tests in `:data:db`, which can't see the builder.

## What landed

- `SqlQueryBuilder` now returns `SqlQuery(sql, args)` (new `helpers/SqlQuery.kt`). The search
  term and `type` are bound as `?` parameters. `%`, `_` and `\` in the search term are escaped,
  and each `LIKE` uses `ESCAPE '\'`, so user input always matches literally.
- `ProductsRepositoryImpl.getProductsFlow` passes the args via
  `SimpleSQLiteQuery(sql, args.toTypedArray())`.
- Tests: `SqlQueryBuilderTest` rewritten for the new shape (11 tests), including `Sam's`, the
  Crashlytics input `='`, and escaping of `%`, `_` and `\`. `ProductsRepositoryImplTest` gained
  `getProductsFlow with query should pass bind args to dao`, which captures the
  `SupportSQLiteQuery` and asserts its `argCount`. All 30 tests in both classes pass;
  `ktlintCheck` and `detekt` are clean. The old builder can't compile against these tests, so
  "fails before" is shown by the SQLite repro in Findings, not by a red run.
- Emulator check (fdroid debug, `Medium_Phone_API_36.1`, seeded DB): `Sam's` →
  "Sam's Club membership"; `='` → no results; `%` → only "50% off coupon"; `_` → no results;
  the draft (`isCreated=0`) never showed. Same PID throughout, and no `FATAL`/`SQLiteException`
  in logcat.
- Deliberately left out: a `catch` in `HomeViewModel` (option C). With nothing left that can
  produce invalid SQL from user input, it would only hide future errors.

## Follow-up: other ways to break the query

Checked after the fix, 2026-09-26.

- **No other SQL is built from user input.** The only `@RawQuery`/`SimpleSQLiteQuery` is the
  product search. Every Room `@Query` uses compile-time SQL with `:params`, and the
  `contentResolver.query` calls in `FileInfoRetrieverImpl` pass no selection string.
- **Fuzzed the new query** with Python `sqlite3` 3.53.3, using the builder's exact SQL and
  escaping: every ASCII char 0x01–0x7F, a list of injection strings (`' OR 1=1 --`, `; DROP
  TABLE`, `/*`, `?1`, `:name`, `$x`, …), unicode/emoji/RTL marks, and 20,000 random strings
  of up to 40 chars. There were 0 SQL errors, and every input without a NUL matched exactly
  the expected rows.
- **Open: very long input still throws.** SQLite rejects a `LIKE` pattern longer than
  50,000 **bytes** (UTF-8) with `LIKE or GLOB pattern too complex`. Measured thresholds: about
  50,000 ASCII letters, 25,000 `%`/`_`/`\` (escaping doubles each one), about 25,000 `é`,
  or 12,500 emoji. This exception is also uncaught, so it would crash Home. It takes a huge
  paste into the search box, so it's unlikely. That Android's bundled SQLite uses the same
  default limit is *inference* (it's the upstream default); not verified on device.
- **NUL (`\u0000`) in the search text** cuts the pattern short, so it matches every product.
  Wrong results, but no crash and no error. It can't realistically be typed into a text field.
