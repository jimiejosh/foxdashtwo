# I want to make a game! #
### And don't know what engine to go with? ###

Fox Dash Two features:
  * Easy to use and setup
  * Open Source under an easy to use license
  * OpenGL ES 2.0
  * No messing with low level code or shaders
  * Simple Physics, lights, particles, and other features built in
  * Textured quads, animated quads, generated compressed quads, and more

# How Do I use it already?! #
### Get Eclipse, SVN, and ADT ###

  1. Download and install [Eclipse](http://www.eclipse.org/downloads/packages/release/indigo/sr2)
  1. Get the [ADT plugin](https://developer.android.com/sdk/installing/installing-adt.html) for Eclipse
  1. Update your SDK...In Eclipse -> Window -> Android SDK Manager. Check at least version API 9, but if you're OCD and like buttons, click them all and install [Image](http://i.imgur.com/Qur8D.png)
  1. Download and install an SVN client (such as [Tortoise](http://tortoisesvn.net/downloads.html) or [SCPlugin ](http://scplugin.tigris.org/))

### Setup a device ###

  1. On an Android device...Settings -> Developer Options -> Android debugging

### Grab the library branch of FD2 ###

Use an SVN client to checkout the library branch of Fox Dash 2.
  * Command line: svn checkout https://foxdashtwo.googlecode.com/svn/branch/library/
  * Tortoise: right click a folder -> SVN checkout with the url above

### Keep the library branch up to date ###

Every few days or so use the SVN client to update the library branch of Fox Dash 2.
  * Command line: svn update
  * Tortoise: right click folder -> SVN update

### Open FD2 in Eclipse and Create a game project ###

  1. Open up eclipse [Image](http://i.imgur.com/pPud1.png)
  1. File -> import...General -> Existing project into workspace [Image](http://i.imgur.com/eOp2c.png)
  1. Browse for the Fox Dash 2 branch checkout
  1. Stare in amazement [Image](http://i.imgur.com/IqU4b.png)

  1. File -> New -> Other...Android -> Android Application Project
  1. Be sure to set minimum file version to API 9
  1. Set recommended file version to whatever your device is running [Image](http://i.imgur.com/Iz9Kv.png)
  1. Proceed through the rest of the wizard...next...next...next...
  1. End with this [Image](http://i.imgur.com/WWL8h.png)

### Link FD2 and game project ###

  1. Right click project -> properties [Image](http://i.imgur.com/Te2lP.png)
  1. Find Android on the left, and add Fox Dash 2 as a library on the right [Image](http://i.imgur.com/UGtWT.png)

### Setup the manifest file ###

  1. Find the manifest file in the root directory of project, open it up, and change to text edit mode [Image](http://i.imgur.com/1yMiw.png)
  1. Add the following
```
    <uses-permission android:name="android.permission.WAKE_LOCK" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	
    <uses-feature android:name="android.hardware.screen.sensorLandscape"/>
	<uses-feature android:glEsVersion="0x00020000" android:required="true" />
```
  1. Also add
```
android:theme="@android:style/Theme.NoTitleBar.Fullscreen" 
            android:screenOrientation="sensorLandscape"
```
  1. [Image](http://i.imgur.com/x2GQg.png)

### Setup the activity file ###

  1. Open the Activity file [Image](http://i.imgur.com/wdYuX.png)
  1. Delete unnecessary code [Image](http://i.imgur.com/yPZU1.png)
  1. Make it extend GameActivity
```
public class MainActivity extends com.kobaj.foxdashtwo.GameActivity {
```
[Image](http://i.imgur.com/IZIw8.png)
  1. Set the prefs text to match the project
```
public MainActivity()
{
    this.shared_prefs_name = "com.my_site.my_awesome_game_prefs";
}
```
[Image](http://i.imgur.com/pXFeP.png)

### Take a break ###

  1. At this point the application can run [Image](http://i.imgur.com/99vNG.png)
  1. Its working if there is an FPS counter in the top left corner of the android device [Image](http://i.imgur.com/wMLXV.png)
  1. Have some tea

### Create a screen file ###

  1. Right click src package -> New -> class [Image](http://i.imgur.com/iBxPt.png)
  1. Make it extend com.kobaj.screen.BaseScreen [Image](http://i.imgur.com/XOgie.png) [Image](http://i.imgur.com/5pwGk.png)
  1. Go Back to the activity file and add the screen to the game
```
//setup my screen
GameActivity.mGLView.my_game.onChangeScreen(new com.my_site.myawesomegame.MyScreen());
```
[Image](http://i.imgur.com/H3nqk.png)

### Drawing numbers to the screen ###

  1. Go to the screen file
  1. add the following lines of code in the method onDrawConstant
```
//convert screen coords to shader coords
double x_pos = Functions.screenXToShaderX(200);
double y_pos = Functions.screenYToShaderY(200);
		
//draw the number 198765432 at the position (200,200)
Constants.text.drawNumber(198765432, x_pos, y_pos, EnumDrawFrom.bottom_left);
```
[Image](http://i.imgur.com/w6T3o.png)
[Image](http://i.imgur.com/VkIgp.png)

### Drawing Text to the screen ###

  * Will come soon

### Draw images to the screen ###

  1. Add desired graphic to the rest/drawable-hdpi folder [Image](http://i.imgur.com/wak8L.png)
  1. Set, Initialize, and draw graphic. But nothing shows up?!
```
//Set 
Quad my_drawable;

//Initialize
//file name, file width, file height
my_drawable = new Quad(R.drawable.untitled, 200, 100);

//Draw
my_drawable.onDrawAmbient();
```
[Image](http://i.imgur.com/pJbMB.png)
  1. Add an ambient light in a similar manner
```
//Set
QuadColorShape my_ambient_light;

//Initialize
//screen width, screen height, color, blur_amount
my_ambient_light = new QuadColorShape(com.kobaj.math.Constants.width, com.kobaj.math.Constants.height, Color.WHITE, 0);

//Draw
my_ambient_light.onDrawAmbient(Constants.identity_matrix, Constants.my_proj_matrix, touch_color, true);
```
  1. But now the app crashes? Don't load in the constructor, load in the load method [Image](http://i.imgur.com/asc1r.png)
  1. Move the graphic around if needed
```
double x_pos = Functions.screenXToShaderX(100);
double y_pos = Functions.screenYToShaderY(300);
		
my_drawable.setPos(x_pos, y_pos, EnumDrawFrom.center);
```
[Image](http://i.imgur.com/WwNsX.png)
[Image](http://i.imgur.com/qUFhh.png)

### Draw compressed images to screen ###

  * Will come soon

### Tackle the Update Loop ###

  * Will come soon

### Touch Input ###

  1. Add an if statment to the update loop to see if finger zero has been touched
```
touch_color = Color.WHITE;
if(Constants.input_manager.getTouched(0))
    touch_color = Color.BLUE;
```
[Image](http://i.imgur.com/eyr0z.png)

### Advanced Touch Input ###

  * Will come soon

### Playing Sound ###

  * Will come soon

### Playing Music ###

  * Will come soon

### Exporting an APK for Google Play Market ###

  1. Right click project and click Android Tools -> Export signed application package [Image](http://i.imgur.com/2akPh.png)
  1. Follow the wizard to reuse an old keystore or make a new one
  1. keep the keystore safe, don't lose it, and don't give it away.

### Loading things with XML ###

  * Will come soon

### Networking ###

  * Will come soon

### Other Specs ###

  * To turn off fps: In the activity add this code to onCreate method
```
GameActivity.mGLView.my_game.draw_fps = false;
```
[Image](http://i.imgur.com/LxY0a.png)
  * Make the screen orient in another direction: Change the manifest file here
```
android:screenOrientation="sensorLandscape"
```