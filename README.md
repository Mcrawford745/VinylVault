# VinylVault

Serverless desktop app for managing your physical music collection — Vinyl LPs, 7" singles, CDs, and Cassettes.

Built with **Java 21 + JavaFX 21 + Maven**. All data lives in a single `~/.vinylvault/vinylvault_db.json` file.

---

## Features

- **Collection** — add, edit, delete owned records with full metadata (label, catalog number, barcode, tracklist, condition grades)
- **Wishlist** — track want-list items; promote to Collection with one click
- **Listening History** — log every listen with optional session notes; tracks play counts per album
- **Statistics** — total owned, total spent, breakdown by format, decade, and top artists
- **I'm Feeling Lucky** — random pick, filter by format, mood, or "Forgotten Favorites" (lowest play count)
- **Export Web View** — generates a self-contained `dist/index.html` for GitHub Pages or sharing
- **MusicBrainz Lookup** — auto-fill label, catalog number, barcode, tracklist, and release year

---

## Prerequisites

- Java 21 JDK
- Apache Maven 3.8+

---

## Run

```bash
mvn javafx:run
```

## Build fat JAR (optional)

Add the `maven-shade-plugin` or `jpackage` to `pom.xml` to produce a standalone installer.

---

## Export Web View

Click **⬇ Export Web View** in the toolbar. The file is written to `./dist/index.html` relative to the working directory. Push it to a GitHub Pages branch to share your collection online.

---

## Data Storage

`~/.vinylvault/vinylvault_db.json` — human-readable JSON, back it up like any important file.

---

## License

MIT
