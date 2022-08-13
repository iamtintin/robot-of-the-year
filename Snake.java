import java.awt.Point;
import java.util.Random;

/**
 * Class for representing the Snake game and its logic. Contains game logic 
 * carried out on each poll of the controller, as well as post-validation of the
 * keyboard input. 
 * enum Direction - cardinal directions that snake can move in
 * Private int snakeLength - length of snake
 * Private int[][] snakeBody - 2D array of coordinates for body parts of snake
 * Private Point apple - Location of apple
 * Private Direction direction - Current direction snake moves in
 * Private boolean alive - Status of snake (is game in progress?)
 * Private boolean gameOver - Status of game (is game over?)
 * Private boolean dirChanged - Status of input direction (has it been implemented by snake?)
 */
public class Snake {

    /**
     * Directions that the snake can move in corresponding to the cardinal 
     * directions in the maze
     */
    enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    private int snakeLength; 
    private int snakeBody[][];
    private Point apple;
    private Direction direction;
    private boolean alive; 
    private boolean gameOver;

    private boolean dirChanged;

    /**
     * Sole Default Constructor - Initialises variables snakeLength, direction,
     * alive, gameOver, snakeBody and generates initial position for apple. 
     */
    public Snake() {
        snakeLength = 6;
        direction = Direction.EAST;
        alive = false;
        gameOver = false;
        snakeBody = new int[900][2];
        apple = generateLocation();
    }

    /**
     * Modifier for private class variable alive (can only set it to true)
     */
    public void setAlive() { this.alive = true; }

    /**
     * Accessor for private class variable alive 
     * @return boolean - whether snake is alive or not
     */
    public boolean isAlive() { return this.alive; }

    /**
     * Accessor for private class variable gameOver
     * @return boolean - whether game is over or not
     */
    public boolean isGameOver() { return this.gameOver; }

    /**
     * Accessor for private class variable snakeLength
     * @return int - length of snake 
     */
    public int getLength() { return snakeLength; }

    /**
     * Accessor for indexed body element of private class variable snakeBody. 
     * Retrieves location from array and returns as point object. 
     * @param i int - index of snake body to find location of 
     * @return Point object - location of snake body corresponding to index
     */
    public Point getBody(int i) { return new Point(snakeBody[i][0], snakeBody[i][1]); }

    /**
     * Accessor for private class variable apple
     * @return Point object - location of apple
     */
    public Point getApple() { return apple; }

    /**
     * Update snake for current poll. Snakes progress forwards in appropriate 
     * direction. Accounts for out-of-bounds moves, where Snake teleports to 
     * other side (since Maze is wrapped around itself). Checks whether snake 
     * has eaten itself, an apple or the robot, and will carry out corresponding
     * logic in each cases, through subsidiary methods. 
     */
    public void update() {
        // Only update snake while it's alive (i.e. game in progress)
        if (alive) {
            // Move snake body parts forwards 
            for(int i = snakeLength; i > 0; i--) {
                snakeBody[i] = snakeBody[i-1].clone();
            }
            
            /* Move snake head forward appropriately according to direction and 
            teleport snake when it heads out of bounds (wrap maze around to itself)*/
            switch(direction) {
                case NORTH:
                    snakeBody[0][1] = snakeBody[0][1] - 1;
                    if (snakeBody[0][1] < 0) snakeBody[0][1] = 29;
                    break;
                case SOUTH:
                    snakeBody[0][1] = snakeBody[0][1] + 1;
                    if (snakeBody[0][1] > 29) snakeBody[0][1] = 0;
                    break;
                case WEST:
                    snakeBody[0][0] = snakeBody[0][0] - 1;
                    if (snakeBody[0][0] < 0) snakeBody[0][0] = 29;
                    break;
                case EAST:
                    snakeBody[0][0] = snakeBody[0][0] + 1;
                    if (snakeBody[0][0] > 29) snakeBody[0][0] = 0;
                    break;
            }

            dirChanged = false;

            // Set gameOver and alive to false when snake has bit itself
            if (checkSelfCollision()) {
                alive = false;
                gameOver = true;
            }

            // Check for collision with apple and bottom-right corner robot
            checkAppleCollision();
            randomTeleportCheck();
        }
        
    }

    /** 
     * Checks if head of the snake is colliding with another part of the snake 
     * body (i.e. has eaten itself) and if so returns true.
     *
     * @return boolean - true if snake has eaten itself, else false
     */
    private boolean checkSelfCollision() {
        for(int i = snakeLength; i > 0; i--){
            if(snakeBody[i][0] == snakeBody[0][0] && snakeBody[i][1] == snakeBody[0][1]) return true;
        }
        return false;
    }

    /**
     * Checks if head of the snake is colliding with the apple (i.e. has eaten
     * it) and if so increments length of snake and generates new random apple
     */
    private void checkAppleCollision() {
        if (apple.equals(new Point(snakeBody[0][0], snakeBody[0][1]))) {
            snakeLength++;
            apple = generateLocation();
        }
    }

    /**
     * Checks if provided location is empty in maze or is occupied 
     * (i.e. by the snake or robot) by checking bottom-right corner and 
     * also looping through the snake body elements
     * @param x int - x coordinate of location to check
     * @param y int - y coordinate of location to check
     * @return boolean - true if location is empty, else false
     */
    private boolean isFree(int x, int y) {
        // Check if location corresponds to bottom-right corner (i.e. the robot)
        if (x == 29 && y == 29) return false;
        // Check if location corresponds to a part of the snake body
        for (int i = 0; i < snakeLength; i++) {
            if (snakeBody[i][0] == x && snakeBody[i][1] == y) return false;
        }
        return true;
    }

    /**
     * Generates a random location in the maze that is not occupied by an
     * item (i.e. snake / robot) and returns it as a Point object. 
     *
     * @return Point object - random empty location in maze
     */
    public Point generateLocation() {
        Random random = new Random();
        int x, y;
        // Keep generating random coordinates until corresponding location is empty
        do {
            x = random.nextInt(30);
            y = random.nextInt(30);
        } while (!isFree(x, y));
        return new Point(x, y);
    }

    /**
     * If the head of the snake is at the bottom right corner (i.e. the robot), 
     * Teleports the head of the snake to random location in the maze that is 
     * not occupied by another item (i.e. its body / the robot)
     */
    public void randomTeleportCheck() {
        // If snake head at bottom-right corner, teleport to random empty location
        if (snakeBody[0][0] == 29 && snakeBody[0][1] == 29) {
            Point randP = generateLocation();
            snakeBody[0][0] = (int) randP.getX();
            snakeBody[0][1] = (int) randP.getY();
        }
    }

    /**
     * Sets the Direction piped from the Keyboard input method depending on 
     * whether the snake isn't moving in the opposite direction and whether the 
     * previous valid direction input has been implemented by the snake. This 
     * validates the move and ensures no impossible moves are made. 
     * 
     * @param d Direction ENUM - direction corresponding to the keyboard input
     */
    public void setDirection(Direction d) {
        /* If Snake isn't moving in the opposite direction and previous direction 
        has been forced onto snake, then set the direction from input */
        switch(d){
            case NORTH: 
                if (direction != Direction.SOUTH && !dirChanged) {
                    dirChanged = true;
                    direction = Direction.NORTH;
                }
                break;
            case EAST: 
                if (direction != Direction.WEST && !dirChanged) {
                    dirChanged = true;
                    direction = Direction.EAST;
                }
                break;
            case SOUTH:
                if (direction != Direction.NORTH && !dirChanged) {
                    dirChanged = true;
                    direction = Direction.SOUTH;
                }
                break;
            case WEST: 
                if (direction != Direction.EAST && !dirChanged) {
                    dirChanged = true;
                    direction = Direction.WEST;
                }
        }
    }

    /**
     * Resets the class variables to the required initial values for the start 
     * of a new game
     */
    public void reset() {
        snakeLength = 6;
        alive = false;
        gameOver = false;
        direction = Direction.EAST;
        snakeBody = new int[900][2];
    }
}
