import java.lang.reflect.Field;
import java.lang.reflect.Method;
import sun.misc.Unsafe;
import java.awt.Point;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.FlowLayout;

import uk.ac.warwick.dcs.maze.logic.*;
import uk.ac.warwick.dcs.maze.gui.MazeGridPanel;

/*
Instructions when running the Snake Game with the Maze Environment 
- Add the SnakeController.Class to the Maze-Environment as a Controller
- Press Begin (After this do not use the Maze-Environment UI again)
- Switch Focus to the Input Window and follow instructions on there 
    \- It should say press space to start
*/

/**
 * Main Class which is instantiated in the Maze-Environment and contains the 
 * controlRobot method which is called on every poll. This is where the maze is 
 * retrieved and setup for the game through reflection and the input window is 
 * setup. This also contains the methods required to interface between the game 
 * framework and the maze-environment for displaying the desired output onto the 
 * maze, by changing the block types appropriately. On every subsequent poll of 
 * controlRobot, appropriate calls to the game framework are made to update the 
 * game logic and then the maze display is updated using the implemented methods 
 * in this class.
 * enum Block - types of blocks required for the game
 * Private int polls - Poll counter
 * Private JFrame window - Swing Frame for Input Window
 * Private JLabel label - Swing Label for Text in Input Window
 * Private GameFrame gameFrame - Game Framework object
 * Private Maze maze - Maze object from Maze Environment
 */
public class SnakeController{

    /**
     * Types of Blocks that are considered for the rendering of the game onto 
     * the maze display
     */
    enum Block {
        APPLE, EMPTY, SNAKE
    }

    private int polls;
    private JFrame window;
    private JLabel label;
    private GameFrame gameFrame; 
    private Maze maze;

    /**
     * Sole Default Constructor - Initialisies poll counter variables, Sets up
     * Input Windows, Initialises gameFrame with the framework of the game, and 
     * links it to the window as a key listener.
     */
    public SnakeController() {
        // Initialise Poll counter
        polls = 0;

        // Setup Input Window
        window = new JFrame("Snake Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(275, 275);
        window.setVisible(true);
        window.setFocusable(true);
        window.setResizable(false);
        window.setLayout(new FlowLayout());

        label = new JLabel("");

		window.add(label);

        // Initialise game framework and add key listener
        gameFrame = new GameFrame(this);
        window.addKeyListener(gameFrame);
    }

    /**
     * Method that is called on every poll by the maze-environment. 
     * Retrieves Maze through reflection on first call and setups up the Maze 
     * Environment for the Snake game through reflection - maze size, block 
     * colours, robot. 
     * On other polls, updates the maze display and input window according to 
     * the state of the game. 
     * 
     * @param robot IRobot object representing the robot
     */
    public void controlRobot(IRobot robot){
        if (polls == 0) {
            // At start 
            try {
                // Get maze using Reflection
                Field mazeF = robot.getClass().getDeclaredField("maze");
                mazeF.setAccessible(true);
                maze = (Maze) mazeF.get(robot);
                
                // Setup the Maze and Robot for the Snake game
                setupEnvironment(robot);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // Clear maze, Update game state
        System.out.println("Clear");
        clear();
        System.out.println("Snake Update");
        gameFrame.update();

        //Draw new state onto maze
        System.out.println("Draw");
        gameFrame.draw();
        System.out.println("Display Update");
        update();

        // Update Text to be displayed on Input Window
        System.out.println("Update Text");
        label.setText(gameFrame.getText());
        System.out.println("Poll Finished");

        // Increment poll counter
        polls++;
    }

    /**
     * Setups the Maze Environment by altering the maze and robot in preparation 
     * for the snake game. Changes the colours of the types of squares in the 
     * maze. Generates a new maze of the correct size. Moves Robot to the 
     * bottom-right corner and deactives its movement. This is all done through
     * reflections which may throw errors - handled by try-catch block. 
     *
     * @param robot object representing robot in the maze environment
     */
    private void setupEnvironment(IRobot robot) {
        // Change colours of the walls, passages and beenbefores
        try {
            setBGColor("BEENBG", new Color(0, 0, 255));
            setBGColor("WALLBG", new Color(251, 133, 0));
            setBGColor("PASSAGEBG", new Color(18, 18, 18));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Generate new maze of the correct size
        try {
            Field gridField = maze.getClass().getDeclaredField("grid");
            gridField.setAccessible(true);
            gridField.set(maze, new int[30][30]);
        } catch (Exception e){
            e.printStackTrace();
        }

        // Move robot to bottom-right corner of maze and deactive its movement
        try {
            Field startField = maze.getClass().getDeclaredField("start");
            startField.setAccessible(true);
            startField.set(maze, new Point(29, 29));

            Field activeF = robot.getClass().getDeclaredField("active");
            activeF.setAccessible(true);
            activeF.set(robot, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the entire screen by replacing all the blocks in the maze with 
     * the square corresponding to EMPTY blocks 
     */
    private void clear() {
        for(int x = 0; x < 30; x++){
            for(int y = 0; y < 30; y++){
                drawBlock(Block.EMPTY, new Point(x, y));
            }
        }
    }

    /**
     * Changes the colour of the square at the given position by changing it's 
     * type - Passage or Wall - depending on what the block is meant to 
     * represent. If the square is to be an apple, it moves the target of the 
     * maze instead. 
     * 
     * @param type Block type denoted by Block ENUM
     * @param position Point object representing location of square to modify
     */
    public void drawBlock(Block type, Point position) {
        switch (type) {
            case APPLE:
                // If block is to be apple: Change position of target to that specified using reflection
                try {
                    Field targetField = maze.getClass().getDeclaredField("finish");
                    targetField.setAccessible(true);
                    targetField.set(maze, position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case EMPTY:
                // If block is to be empty: Change square at position to PASSAGE
                maze.setCellType((int)position.getX(), (int)position.getY(), Maze.PASSAGE);
                break;
            case SNAKE:
                // If block is to be snake body: Change square at position to WALL
                maze.setCellType((int)position.getX(), (int)position.getY(), Maze.WALL);
        }
    }

    /**
     * Triggers update of Maze display with the updates made during the current
     * poll, by broadcasting an Event, that triggers the Maze-Environment to
     * to refresh the display of Maze
     */
    private void update() {
        // Trigger update of Maze display in Maze Environment 
        try {
            EventBus.broadcast(new Event(IEvent.NEW_MAZE, maze));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tirggers change of the speed of the game by altering the delay between
     * poll calls. To ensure this is taken up by the maze-environment, an Event 
     * is broadcasted that corresponds to the updating of the delay. 
     */
    public void updateDelay(int delay) {
        // Trigger update of delay between poll calls to controlRobot
        try {
            EventBus.broadcast(new Event(IEvent.DELAY, Integer.valueOf(delay)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes thle value of static final field corresponding to the colour of 
     * the square type, specified in the arguments, in the MazeGridPane class, 
     * by using reflection and the unsafe class.
     * 
     * @param field String - name of field to be changed
     * @param coloor Color object - colour to change the square background to
     */
    private static void setBGColor(String field, Color colour) throws Exception {
        final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        final Unsafe unsafe = (Unsafe) unsafeField.get(null);
        final Field ourField = MazeGridPanel.class.getDeclaredField(field);
        final Object staticFieldBase = unsafe.staticFieldBase(ourField);
        final long staticFieldOffset = unsafe.staticFieldOffset(ourField);
        unsafe.putObject(staticFieldBase, staticFieldOffset, colour);
    }
    
    /**
     * Method called when controller is reset through the maze-environment UI
     * Resets the game framework and thereby the snake game for a new game and 
     * also reset the speed of the snake (i.e. the delay between polls)
     */
    public void reset() {
        // Reset poll counter and game state
        polls = 1;
        updateDelay(250);
        gameFrame.reset();
    }
}