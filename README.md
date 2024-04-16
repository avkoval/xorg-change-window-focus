## Motivation

I am a big fan of Linux and different WMs. Many years spent with i3wm, where everything is configurable,
but I found that I need something in-between - a possibility to place windows automatically, but also
possibility to have freedom of borders, space, etc.

Experimenting: KDE Plasma - looks nice, have a plenty of functions but buggy. I hate when it forgets configuration
items etc...

So far I tried Xfce - seems to be better, but it lacks keybindings to focus next window or place tiles.

https://github.com/calandoa/movescreen helps me to move / tile windows on a certain screen.

This program address the lack of software to change focus to right/left window.

## Usage

* Install babashka. https://babashka.org/
* Place this script somewhere into your executable $PATH (ex. ~/bin)
* Configure bindings as `~/bin/xorg-change-window-focus.clj right` and `~/bin/xorg-change-window-focus.clj left`

