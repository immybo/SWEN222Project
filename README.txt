TEAM:
- David Phillips
- Hamish Brown
- Joshua Hurst
- Martin Chau
- Robert Campbell
  _____ _               _       _                 _                      
 |_   _| |__   ___     / \   __| __   _____ _ __ | |_ _   _ _ __ ___ ___ 
   | | | '_ \ / _ \   / _ \ / _` \ \ / / _ | '_ \| __| | | | '__/ _ / __|
   | | | | | |  __/  / ___ | (_| |\ V |  __| | | | |_| |_| | | |  __\__ \
   |_| |_| |_|\___| /_/   \_\__,_| \_/ \___|_| |_|\__|\__,_|_|  \___|___/
                                                                         
         __   ____                                      _  __   __   _       
   ___  / _| |  _ \ _   _ _ __   ___     __ _ _ __   __| | \ \ / ___| | ___  
  / _ \| |_  | |_) | | | | '_ \ / _ \   / _` | '_ \ / _` |  \ V / _ | |/ _ \ 
 | (_) |  _| |  __/| |_| | |_) | (_) | | (_| | | | | (_| |   | |  __| | (_) |
  \___/|_|   |_|    \__,_| .__/ \___/   \__,_|_| |_|\__,_|   |_|\___|_|\___/ 
                         |_|                                                 

GAME BACKGROUND

Pupo and Yelo, two lovable anthropomorphic blobs, have lost each other!
Oh no! It's your quest to reunite the pair, battling through an incredibly
difficult (/s) puzzle adventure game. The two players must progress through
the levels until they reunite by standing next to one another.

RUNNING THE GAME

To run the game, first run Server (under the network.server package),
then run Main (default package) twice. Connect to the server with
the button in the bottom-right. Both clients must be connected before
anything will happen. LocalClient is a convenience script to
automatically connect to localhost.

IMPORTANT NOTE: You should also read Walkthrough.txt, a walkthrough of the
first level, so that you can test all of the game's features. Only one level
is provided.

KNOWN ISSUES:
The game will go off sync sometimes between the 2 clients resulting in clients 	having different world information.
Push interaction is there for most decorative items: totem, shop, sign, fakeportal
	however they do not currently work
The load game button has a bit of a delay before it works (sometimes). A 
System.out message is given when the world is loaded. Saving might also
take a while. Regardless, the actual data storage package works;
this is an issue with integration with the GUI/networking.

CONTROLS & GAME INFO

You can left click anywhere on the map, and the character will attempt to move
there. If the character can't move there, nothing will happen. If you left
click on an enemy, the character will attempt to attack it.

Right clicking on some objects will bring up a menu of interactions only
if that player is next to that object. The player can invoke an interaction
on that object; for example, a gate might have an unlock interaction. Left
click any of the interactions in the menu to activate it. This basically
includes everything the player can do except for moving and attacking.

Clicks are processed at the base of their tile; e.g., if a lamppost sticks 
up high, you must click on/near its base to interact with it.

Items are automatically picked up when the player walks over them, if the
player has room in their inventory.

Portals may be moved through to teleport the player between zones. Only 
objects in the current zone will be drawn, and Pupo and Yelo may start in
different zones.

OBJECTIVE

Once Pupo and Yelo are on adjacent tiles to one another, the game will end
in victory. This will pop up a message and... nothing much else will happen;
we didn't implement an exciting end to our game.