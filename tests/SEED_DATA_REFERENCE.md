# Spring Music — Seed Data Reference

> All 29 albums loaded on fresh application start.  
> Source: `src/main/resources/albums.json`

---

## Complete Album List

| # | Artist | Title | Year | Genre |
|---|--------|-------|------|-------|
| 1 | Nirvana | Nevermind | 1991 | Rock |
| 2 | The Beach Boys | Pet Sounds | 1966 | Rock |
| 3 | Marvin Gaye | What's Going On | 1971 | Rock |
| 4 | Jimi Hendrix Experience | Are You Experienced? | 1967 | Rock |
| 5 | U2 | The Joshua Tree | 1987 | Rock |
| 6 | The Beatles | Abbey Road | 1969 | Rock |
| 7 | Fleetwood Mac | Rumours | 1977 | Rock |
| 8 | Elvis Presley | Sun Sessions | 1976 | Rock |
| 9 | Michael Jackson | Thriller | 1982 | Pop |
| 10 | The Rolling Stones | Exile on Main Street | 1972 | Rock |
| 11 | Bruce Springsteen | Born to Run | 1975 | Rock |
| 12 | The Clash | London Calling | 1980 | Rock |
| 13 | The Eagles | Hotel California | 1976 | Rock |
| 14 | Led Zeppelin | Led Zeppelin | 1969 | Rock |
| 15 | Led Zeppelin | IV | 1971 | Rock |
| 16 | Police | Synchronicity | 1983 | Rock |
| 17 | U2 | Achtung Baby | 1991 | Rock |
| 18 | The Rolling Stones | Let it Bleed | 1969 | Rock |
| 19 | The Beatles | Rubber Soul | 1965 | Rock |
| 20 | The Ramones | The Ramones | 1976 | Rock |
| 21 | Queen | A Night At The Opera | 1975 | Rock |
| 22 | Boston | Don't Look Back | 1978 | Rock |
| 23 | BB King | Singin' The Blues | 1956 | Blues |
| 24 | Albert King | Born Under A Bad Sign | 1967 | Blues |
| 25 | Muddy Waters | Folk Singer | 1964 | Blues |
| 26 | The Fabulous Thunderbirds | Rock With Me | 1979 | Blues |
| 27 | Robert Johnson | King of the Delta Blues | 1961 | Blues |
| 28 | Stevie Ray Vaughan | Texas Flood | 1983 | Blues |
| 29 | Stevie Ray Vaughan | Couldn't Stand The Weather | 1984 | Blues |

---

## Summary Statistics

| Metric | Value |
|--------|-------|
| Total albums | 29 |
| Genre: Rock | 22 |
| Genre: Pop | 1 |
| Genre: Blues | 6 |
| Earliest year | 1956 |
| Latest year | 1991 |
| Unique artists | 24 |
| Artists with multiple albums | Led Zeppelin (2), U2 (2), The Rolling Stones (2), The Beatles (2), Stevie Ray Vaughan (2) |

---

## Seed Data Fields

Each album in `albums.json` has:
- `_class`: `org.cloudfoundry.samples.music.domain.Album` (used by Jackson deserializer)
- `artist`: String (required)
- `title`: String (required)
- `releaseYear`: String (required, 4-digit year)
- `genre`: String (required)

Fields NOT in seed data (populated at runtime):
- `id`: Auto-generated random string (max 40 chars)
- `trackCount`: Defaults to 0
- `albumId`: Defaults to null

---

## Loading Behavior

1. `AlbumRepositoryPopulator` listens for `ApplicationReadyEvent`
2. Checks if repository is empty (`count() == 0`)
3. If empty: reads `albums.json`, deserializes to `Collection<Album>`, saves each
4. If not empty: does nothing (prevents duplicate loading)
