# Loxy

##### This game reproduce electricity in 2D but in way more simpler like redstone in minecraft.
It has different tiles like wire, switch and logic OR, AND, XOR, INV gates.</br>
With all of this you can possibly recreate a computer or at least a processor.

If you want to test this game, you can download it in the release tab or compile it in eclipse (make sure to add all lwjgl jars and Isis jar in build path, their natives are in folder lib/lib/all_natives).

This is what it looks like :</br>
![game](/preview.jpg?raw=true)

##### Tile info :
- The **circle** is a **switch**
- The **cross** is a **wire**
- The **purple** is an **AND** gate
- The **arrow** is an **inverter** gate
- The **yellow** an **OR** gate
- The **pink** a **XOR** gate

##### Key bindings :
- Move with **Z, Q, S, D, control, espace**
- Select a tile with **mouse wheel**
- **Left click** to place the selected tile
- **Right click** to remove it
- **E** to interact
- **F11** full screen
- **F5** update hovered tile (if there is a bug)
- Maintain **escape** to quit the game

##### Commands : (type in your terminal `java -jar Loxy.jar`)
- `/save <name>` save the map with this name
- `/load <name>` load the map with the given name
- `/delete <name>` delete the map with the given name
- `/map update <int>` slow the update of the map (default = 1)

The game is running on lwjgl3</br>
Thanks to [Antonio Hernández Bejarano](https://ahbejarano.gitbook.io/lwjglgamedev/) for his engine.
