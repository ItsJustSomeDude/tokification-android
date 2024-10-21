# Tokification: TVal Tracker for Android

Tokification is a standalone Android app for Egg Inc speedruns to allow for tracking your own TVal,
or if run by the Token Sink, the TVals of all the players in the co-op. The app reads incoming
notifications to get information about received tokens, and has a simple menu to record Outgoing
tokens.

This data can then be used to generate Reports, either for your own viewing, or in case of the Sink
running the app, for posting in Discord for the other co-op members to use.

Tokification is written in Kotlin and uses Jetpack Compose for a Material Design 3 UI, while being
very lightweight, and running on any Android version from Android 7 onward.

## Short Version

###### Because Maj can't read...

Download and install the APK
from [Github Releases](https://github.com/ItsJustSomeDude/tokification-android/releases). Launch the
app, head to settings and turn on EVERY SWITCH. Create a new co-op using the List Icon, set the
co-op name, KevID, and start time, then use the Send Tokens button to record any tokens you send. If
you're the sink, send your teammates the report after entering an End Time estimate.

Got all that? No? Keep reading.

## Setup

Download the latest APK
from [Github Releases](https://github.com/ItsJustSomeDude/tokification-android/releases/latest) and
install just like any other APK file.

On opening the app, you'll be asked to grant permissions, and then you'll be at a mostly blank
screen. Head over to Settings, and set your IGN, default co-op mode, and whether you want Egg, Inc's
notifications to be auto-dismissed after being processed.

## Usage

When you're ready to track a co-op, launch the app and click the "List" icon in the upper right.
This is how you will create new co-ops, or select a previous co-op. Once a co-op is created, fill
out the Co-op Name and KevID. The app will try to
read [Co-op Tracker](https://eicoop-carpet.netlify.app/) and [EggCoop](https://eggcoop.org/) links
from the clipboard to populate this info. **Make sure this information is set before recording any
tokens.**

Set the co-op start time, and, as soon as it is known, the end time estimate. These are both needed
for accurate TVal calculations. If you are the Token Broker/Sink, select Sink Mode, otherwise use
Normal Mode.

A notification will be posted with the buttons you will need to do the actual tracking for easy
access while playing. In Sink Mode, there is a button to select which player has just received their
tokens. In Normal mode, there is a button to quickly record a single token being sent to the Sink.
The Refresh button can be used to see up-to-date values in the notification.

In Normal Mode, a report will show your own Delta TVal, as well as a breakdown of how many tokens
have been sent and received. In Sink Mode, a report will show who has boosted and who is still
waiting, sorted by the current TVal of each player. These reports will be shown both on the main
screen of the app, and in the notification.

## Permissions and Privacy

Tokification requests a few permissions:

Notification Access

- Used to read Egg, Inc. notifications to track incoming tokens and chicken runs.
    * Only Egg, Inc. and Tokification's own notifications are read. See the `Convert.kt`
      file [here]() if concerned about what these are used for.
- Optionally used to dismiss notifications after processing.

Show Notifications

- Used to show the Actions for quickly recording Sent tokens.
- Optionally used for sending fake Egg, Inc. notifications for debugging purposes. See
  the `NotificationHelper.kt` file [here]().

Network

- Optionally used to check for updates.
- Optionally used to send usage information and analytics.

