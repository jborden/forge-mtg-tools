# forge-mtg-tools

Tools for working with the MTG Forge project

## Usage

### Download the top 8 from an event

```
forge-mtg-tools.mtgtop8> (download-files "https://www.mtgtop8.com/event?e=23638&d=363913")
```

copy the resulting dir

```
$ cp -r resources/MTGO_Legacy_PTQ_10_11_19_dck_files ~/Library/Application\ Support/Forge/decks/constructed/
```

# From the command line

## command line
```
lein run -m forge-mtg-tools.mtgtop8 "<url>"
```

## License

Copyright © 2019 James Borden

MIT License
