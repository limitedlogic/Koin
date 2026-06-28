# Koin — Agent Guidelines

## Project

**Koin** — Kotlin Multiplatform personal finance manager. Compose Multiplatform UI, Room database,
Ktor server backend, Koin DI, Voyager navigation.

- Package manager: Gradle (version catalog at `gradle/libs.versions.toml`)
- Targets: Android, iOS (arm64 + simulator), JVM
- Frontend checks: Development must build with `./gradlew :app:androidApp:build` unless other targets specified
- Server checks: `./gradlew :server:build`

## Quality bar

Every change is judged against all of these:

All features must be implemented to the absolute fullest, with sufficient planning, utilizing only the BEST technique to achieve the solution, no shortcuts at all. Plan extensively. If a solution genuinely is too much for the scope of a task, then mark it as a // TODO. NEVER try to implement something you would not push to production.

- **Correctness**: monetary arithmetic is in integer cents (`Long`), never `Double` or `Float`.
	Balance is always computed from transactions — never stored. These are invariants, not
	preferences.
- **Performance**: filter in SQL, never load all rows and filter in Kotlin. `Flow` for reactive
	reads, `suspend fun` for one-shot. Every DAO query should be the minimum data needed.
- **Security**: no plaintext storage of sensitive data. Server endpoints must authenticate requests.
- **Architecture**: strict layer separation — domain models know nothing outside `domain/`,
	repositories are the only bridge between entities and domain models, ViewModels never touch DAOs
	directly.

Verify before claiming done: build and compile the entire app and fix any errors before finishing

## Conventions

- **Kotlin style**: official style, tabs, 120-char soft limit. Expression body for single-expression
	functions.
- **`when` expressions** must be exhaustive — no `else` if all cases are covered by the sealed type.
- **Imports**: all at the top of the file. Never qualify symbols inline at the call site (e.g.
	`kotlinx.datetime.LocalDate` is imported, then used as `LocalDate`).
- **IDs**: `String`, generated via `uuid4()`. Never auto-increment integers.
- **Comments**: minimal. Code explains itself. `// computed` on computed `val` properties.
	`// TODO: implement X` for deferred work. No em dashes. Add comments to composable UIs to mark
	where different parts of the UI are. No separator comments like '─' or any other variation

## Architecture

### Layer rules

The layer boundaries are strict and exist to keep the codebase testable and refactorable:

- `domain/model` — pure Kotlin, no dependencies
- `domain/usecase` — calls repositories, never DAOs directly. Returns named sealed `Result` types.
- `data/repository` — the only place entities are mapped to domain models. `toDomain()` /
	`toEntity()` are private extension functions here.
- `data/database` — DAOs only. No business logic.
- ViewModels (`ScreenModel`) — call use cases, expose `StateFlow<UiState>`, never access DAOs.

A use case cannot call another use case. Compose behavior by injecting multiple repositories.

### Monetary values

Amounts are stored as `Long` cents (smallest currency unit). For 0-decimal currencies (JPY, KRW
etc.), amounts are whole units — `CurrencyInfo` determines this. Always format through
`CurrencyFormatter` before display. Raw cents never reach the UI.

### Polymorphic types in Room

`RecurringSchedule` and `Transaction` are `sealed interface` types stored as JSON strings via
TypeConverters with a `"type"` discriminator field. Adding a new variant requires updating both the
TypeConverter and any `when` exhaustiveness sites.

### Platform-specific database creation

`createDatabase()` is `expect`/`actual` per platform. Each platform's `actual` is also responsible
for seeding default data on first launch (when `dao.count() == 0`). Don't move seeding logic into
shared code — platform context (file paths, etc.) is needed.

### Koin DI scoping

`single` for database, DAOs, repositories, HTTP client. `factory` for use cases and ViewModels.
Modules live in `core/src/commonMain/.../di/`; platform-specific wiring via `expect`/`actual`.

## Known gotchas

- **Timestamps in `toEntity()`**: capture `Clock.System.now()` once into a local val and use it for
	both `createdAt` and `updatedAt`. Calling it twice gives inconsistent values.
- **Room foreign key indices**: Room does not create indices on foreign key columns automatically.
	Always add them manually — missing indices cause full table scans on joins.
- **Schema migrations**: `fallbackToDestructiveMigration()` is fine during development. Any
	production schema change needs a proper `Migration` object — don't ship destructive migration to
	users.
- **ConPTY / Windows**: n/a for this project, but note that `Dispatchers.IO` is required for all
	Room operations to avoid blocking the main thread on Android.