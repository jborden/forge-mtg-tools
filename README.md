# forge-mtg-tools

The not quite ready for prime time MTG Forge tools Clojure package!

Tools for working with the MTG Forge project. Its current sole function is to
download an event from mtgtop8 and convert the files into dck files that it uses.
I use it to scrape events and make gauntlets out of them! Useful for keeping up with the current meta.

[Forge](https://www.slightlymagic.net/wiki/Forge) is a great way to goldfish Magic the Gathering
decks. The AI in some cases can actually make decent decisions and is superior to Xmage's AI
system. I also recommend its drafts. Though the AI for drafting isn't that great, it does help
you get acquainted better with a set by being able to see the deck you drafted in action.
The mobile version (Android only) of the software has a great UI..now you can practice Magic in
the check-out line!

## Politeness Reminder

I have no affiliation with [mtgtop8](https://mtgtop8.com). I am thankful for the service they provide
and ask that you please don't abuse their website with excessive traffic. If you're downloading a few
events you're fine, but please don't scrape hundreds and hundreds of events from them.

## Requirements

Java installation

Install [leiningen](https://github.com/technomancy/leiningen#installation)

## REPL Usage

### Download the top 8 from an event

```
forge-mtg-tools.mtgtop8> (download-files "https://www.mtgtop8.com/event?e=23638&d=363913")
```

copy the resulting dir

```
$ cp -r resources/MTGO_Legacy_PTQ_10_11_19_dck_files ~/Library/Application\ Support/Forge/decks/constructed/
```

## Command Line Usage

You can skip running Clojure if you'd prefer.

```
$ lein run -m forge-mtg-tools.mtgtop8 "<url>"
```

You can edit the -main function in src/forge_mtg_tools/mtgtop8.clj to provide a target dir
Alternatively, you can run with an addition target directory

```
$ lein run -m forget-mtg-tools.mtgtop8 "<url>" "<target-dir>"
```

On macOS, for example

```
$ lein run -m forge-mtg-tools.mtgtop8 "https://www.mtgtop8.com/event?e=24413&f=LE" "/Users/james/Library/Application Support/Forge/decks/constructed"
```

## License

Copyright © 2020 James Borden

MIT License
