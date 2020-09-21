# atvFlutterExample
Example Android demo showing flutter hosted video controls. The related
flutter activity can be found [here](https://github.com/atomicfruitcake/atvFlutterExampleActivity)

## Known Issue

When a video is playing inside the `fragments/VideoDetailsFragment.java`,
a flutter activity (linked above) is then launched. This is a transparent
activity that hosts the video controls. When this activity is active, the
google assistant integration functionality no longer works. 

When the flutter activity is not launched, google assistant is able to control
the player using a callback in `fragments/PlaybackVideoFragment.java`.
