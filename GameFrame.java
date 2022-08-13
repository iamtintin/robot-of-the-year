import java.awt.event.*;

/**
 * Class for representing the framework of the game, interfacing between the 
 * main controller class and and the game logic. 
 * Handles backend processing - text formatting; calls to displaying methods in 
 * the main controller to render maze display; as well as handling of keyboard 
 * input. 
 * Private static int delay - the delay between poll calls
 * Private int lenBuffer - buffer for snake length to check if it's changed
 * Private Snake snake - snake object representing game entities and logic
 * Private SnakeController controller - object representing main controller
 * for interfacing between game and maze-environment. 
 */
public class GameFrame implements KeyListener {

    private static int delay = 250;
    private int lenBuffer;
    private Snake snake;
    private SnakeController controller;

    /**
     * Sole Default Constructor - Initialises variables controller, snake and lenBuffer
     */
    public GameFrame(SnakeController controller) {
        this.controller = controller;
        snake = new Snake();
        lenBuffer = 0;
    }

    /**
     * Updates the Snake object such that it makes its move, checks for
     * collisions with itself and checks for the apple being eaten. Decreases 
     * delay by 10 between polls every 4 increments in length.
     */
    public void update() {
        // Snake is updated (Makes move, Checks for collisions, Checks for Apple Eaten)
        snake.update();

        // Increase speed of Snake (by decreasing delay) when length is changed to a number divisible by 4
        if((snake.getLength() % 4 == 0) && (delay != 10) && (lenBuffer != snake.getLength())) {
            delay -= 20;
            lenBuffer = snake.getLength();
        }

        // Trigger Event so delay is updated in Maze-Environment
        controller.updateDelay(delay);
    }

    /**
     * Updates the Maze state with the Snake and Apple: Draws the Snake and 
     * Apple onto the Maze state by changing the cell type of the squares that 
     * they comprise of appropriately. 
     */
    public void draw() {
        // Draw the Snake onto the Maze by iterating over its body
        for (int i = 0; i < snake.getLength(); i++) {
            controller.drawBlock(SnakeController.Block.SNAKE, snake.getBody(i));
        }

        // Draw the Apple onto the Maze
        controller.drawBlock(SnakeController.Block.APPLE, snake.getApple());
    }

    /**
     * Returns String to be displayed in Input Window using a HTML string for
     * variable formatting. Includes title, instructions and current score.
     * Will appropriately include game over statement when Snake is dead. 
     */
    public String getText() {
        return "<html><center><span style='font-size: 20px;'>Snake Game</span><br>" + 
        "<br>Use WASD on this window to<br>control the snake.<br><br>" + 
        ((snake.isGameOver()) ? "GAME OVER!<br><br>" : "") + 
        "Press space to " +  (snake.isAlive() ? "stop" : "start") + 
        ".<br><br>Score: " + (snake.getLength()-6) + "<br><br>" + 
        "Eat Green Squares and gain Points.<br>Blue Square randomly teleports Snake.</center></html>";
    }

    /**
     * {@inheritDoc}
     * Pipes the Direction ENUM corresponding to the key inputs (WASD) to the 
     * snake object to handle processing. Toggles Start/Stop Snake game when 
     * space is pressed. 
     * @param e keyEvent object - Contains details on key that was pressed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
            // Pipe appropriate Direction ENUM to snake object when WASD pressed
            case KeyEvent.VK_W:
                snake.setDirection(Snake.Direction.NORTH);
                break;
            case KeyEvent.VK_A:
                snake.setDirection(Snake.Direction.WEST);
                break;
            case KeyEvent.VK_D:
                snake.setDirection(Snake.Direction.EAST);
                break;
            case KeyEvent.VK_S:
                snake.setDirection(Snake.Direction.SOUTH);
                break;
            case KeyEvent.VK_SPACE:
                // Start or Stop Snake game depending on current state of game
                if (snake.isAlive()) {
                    snake.reset();
                } else {
                    reset();
                    snake.setAlive();
                }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override 
    public void keyReleased(KeyEvent e) {}

    /**
     * {@inheritDoc}
     */
    @Override 
    public void keyTyped(KeyEvent e) {}

    /**
     * Resets Snake object and Snake speed to initial values at start of a game
     */
    public void reset() {
        snake.reset();
        delay = 250;
        controller.updateDelay(delay);
    }
    
}
