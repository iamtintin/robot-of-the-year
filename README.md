# Modified Snake Game in Warwick Maze Env

Modified Snake Game created using java reflections on Warwick University's maze environment as a submission for the robot of the year competition. 

## Usage

Compile the main `SnakeController.java` file using maze-environment.jar (property of the University)
`javac -cp maze-enviroment.jar SnakeController.java`

Run the robot-maze environment
`java -jar maze-environment.jar`

After the UI has opened, add the compiled `SnakeController.Class` as a controller and press begin. After this do not use the maze-environment's UI again. 

Switch focus to the Input Window that has opened up and follow the instructions on there. It should say press space to start. 

### The Game

You can use WASD to move around the snake (if the input is not being registered, check that the focused window is the small window with the instructions)

- The snake is represented by orange squares and the 'apples' by green squares. 
- Eating green squares, increases the score counter, the snake's size and speed. 
- Blue squares randomly teleport the Snake to a location on the board. 
- The board is wrapped around, rather than having a border around the entire board. 
- Press space at any time to start/stop a game

### Screenshots

![Usage example](/screenshots/usage.png)