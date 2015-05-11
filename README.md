# bb-learn-fix-score-max
Small utility to fix SCORE_MAX error when editing Bb Learn Test questions.

This can be used to fix SCORE_MAX errors when editing Test questions in Bb Learn 9.1.

###To use:

1. Clone this repo. 
2. Export the test from Bb Learn (it will be downloadable as a ZIP file).
3. Navigate to the repo folder, and run `gradle runApp`. *Note this will also build a JAR: build/libs/bb-learn-fix-score-max-1.0-capsule.jar which has the dependencies bundled with it.
4. Select the downloaded ZIP file in the GUI.
5. There will be a zip file named "Fixed__" + original name in the same directory as the original, upload that to Bb Learn.

Note that the **Test Name** will be the same once uploaded Bb Learn, but you can rename this once you've edited test questions. Check the Date Modified column to tell which is which.
