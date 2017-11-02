1.0.1 (2017-11-01)
=====
* Bugfix for removing signups for a raid, could lead to ConcurrentModificationException

1.0.0 (2017-10-31)
=====
* Fixed readme for both english and swedish including images
* Fixed getting started guide for english locale
* Input parameter to application with default locale (en or sv)
* raid group removed 5 mins after finished group, not at exact time
* !raid overview - automatically updated "!raid list" every 60 seconds, 
should be put in a read-only channel
* Link to getting started guides from !raid usage
* Persistent !raid track - is now stored in database. Up to 3 pokemons to track per user.
* Raid creator can remove their created raid, if there are no signups
* Feedback after !raid new removed after 15 seconds
* !raid mapinchat - raid map forced in server chat, despite server settings (meant for admins in servers with such config)
* Minor text changes here and there
* Possible to change time for a raid group: !raid change group (time) (gym)
* !raid list should show next ETA
* Clean up signups for expired group
* Persistent tracking of certain pokemon raids
* User can set their own locale
* Move time for raid group: !raid change group (time) (gym)
* Moved attaching to overview to its own event listener
* Fuzzy search for pokemon names

0.9.1 (2017-10-14)
======
* Minor bugfixes
* No emoticon if a \+ command signup goes wrong. No feedback whatsoever, to save chat space and not scare users.

0.9.0 (2017-10-13)
======
* \+ command for signups, as users are used to it (+{number} {time} {gym} is now a signup)
* More automatic cleanup of messages/feedback
* "What's new"-command so people can see what new features.

0.8.0 (2017-10-12)
=====
* Group signup, existing sign ups blanked out if a user changes the time within the same raid.
* Cut down on text for a lot of messages.
* Commands that go wrong will be deleted after 15 seconds, along with the bot's feedback message, to keep chat clean.

0.7.1 (2017-10-11)
=====
* Fixed issue where many raids could cause the embedded message's description to reach its limit.
* Minor text adjustments.
* Fixed link handling for Google Maps so they work on iPhones as well.

0.7.0 (2017-10-10)
=====
* Team buttons removed from raid group signup (feedback from Uppsala)
* Improved texts
* Minor fixes

0.6.0 (2017-10-08)
=====
* Defaultconfig for channel can be set/changed in runtime by an admin
* Config should be put in the database
* "Change raid"-command if you make a mistake creating the raid (!raid change x)
* Delete raid command, for channel admin/owner only
* "man" command to replace !raid usage, which is sooo big. Make !raid usage small, and !raid man {topic} have details
* !raid add x should be able to take existing signup and add to it, not get an error,
unless you exceed limit
* Fixed: Clear all raids at the end of raiding for the day via scheduled job (22:00) - 
bot send message about it. - Handled it instead via checking expire both on date and time.
* Config command should enable servers to be configured in runtime on the fly (but only by server owner)
* Better raid status overview (emote buttons to register people in a group arriving at a certain time)

Earlier versions
================
* Raid list, sort by pokemon then by time (The FInal Shadow)
* Command to list server config
* Donate-command
* !raid list {pokemon} filters raids based on pokemon
* Fixed: raid track sends message whether the command was ok or not. The message should say why they
are getting the message (tracking), and also instructions on how to remove tracking.
* Fixed (always shows start/end): If endtime is more than 1 hour from current time, 
also include start/hatch time in message for !raid list and !raid status
* !raid untrack {pokemon} - !raid untrack without params clears all tracking

