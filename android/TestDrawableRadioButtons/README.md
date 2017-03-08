# Test Drawable Radio Buttons

This "demo" shows that adding custom radio buttons programmatically does not
seem to work correctly. Specifically, the radio buttons assume their size
is the size of the native Android radio buttons when added programmatically.

When added at compile time via the layout.xml, Android is able to correctly
calculate their sizes.

The difference can be seen in the right-most face icon in each Activity.
