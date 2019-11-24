# forge-mtg-tools

Tools for the MTG Forge project

## Usage

### Download the top 8 from an event

```
forge-mtg-tools.mtgtop8> (download-files "https://www.mtgtop8.com/event?e=23638&d=363913")
```

copy the resulting dir

```
$ cp -r resources/MTGO_Legacy_PTQ_10_11_19_dck_files ~/Library/Application\ Support/Forge/decks/constructed/
```

## License

Copyright © 2019 James Borden

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
