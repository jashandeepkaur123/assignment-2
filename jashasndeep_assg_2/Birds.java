package jashasndeep_assg_2;

/*a basic outline and code snippets to get you started on implementing the described game.For simplicity,
  I'll focus on creating the GUI and the basic interactions without a complex game loop.

To create this game, we'll use Java's Swing library for the GUI. Here's an outline of the steps we'll take:

Create a JFrame to hold the game.
Add birds and a bread bucket as graphical components.
Implement mouse events to control the bread's movement.
Check for collisions between the birds and the bread.
Keep track of the player's happy points and display them.
Let's start coding!*/


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class Birds extends JFrame {
    private final int WINDOW_WIDTH = 800;
    private final int WINDOW_HEIGHT = 600;
    private final int BIRD_SIZE = 40;
    private final int BREAD_SIZE = 20;
    private final int BUCKET_WIDTH = 80;
    private final int BUCKET_HEIGHT = 100;
    private final int HAPPY_POINTS_PER_BREAD = 10;
    
    private boolean gameOver;
    private List<Point> birds;
    private Point breadPosition;
    private Point bucketPosition;
    private int happyPoints;
     private Level[] levels;
    private int currentLevelIndex;
    private int spawnCounter;

    public Birds() {
        setTitle("Bird Game");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        birds = new ArrayList<>();
        breadPosition = new Point(-100, -100); // initialize bread off-screen
        bucketPosition = new Point(WINDOW_WIDTH / 2 - BUCKET_WIDTH / 2, WINDOW_HEIGHT - BUCKET_HEIGHT);
        happyPoints = 0;
        gameOver = false;
        levels = new Level[] {
            new Level(5, 2, 100), 
            new Level(8, 3, 80),
            new Level(10, 4, 50), 
            // Add more levels...
        };
        currentLevelIndex = 0;
        spawnCounter = 0;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClicked(e);
            }
        });

        // Add birds randomly
        spawnBirds(5);

        Timer timer = new Timer(100, e -> {
            moveBirds();
            checkCollisions();
            spawnCounter++;

            if (spawnCounter >= levels[currentLevelIndex].getSpawnInterval()) {
                spawnBirds(levels[currentLevelIndex].getNumBirds());
                spawnCounter = 0;
            }

            repaint();
        });
        timer.start();
    }

    private void spawnBirds(int count) {
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int x = rand.nextInt(WINDOW_WIDTH - BIRD_SIZE);
            int y = rand.nextInt(WINDOW_HEIGHT - BIRD_SIZE - BUCKET_HEIGHT);
            birds.add(new Point(x, y));
        }
    }

    private void moveBirds() {
        Random rand = new Random();
        for (Point bird : birds) {
            bird.translate(rand.nextInt(5) - 2, rand.nextInt(5) - 2);
            // Ensure the bird stays inside the window
            bird.x = Math.max(0, Math.min(bird.x, WINDOW_WIDTH - BIRD_SIZE));
            bird.y = Math.max(0, Math.min(bird.y, WINDOW_HEIGHT - BIRD_SIZE - BUCKET_HEIGHT));
        }
    }

    private void checkCollisions() {
        Rectangle breadBounds = new Rectangle(breadPosition.x, breadPosition.y, BREAD_SIZE, BREAD_SIZE);
        Rectangle bucketBounds = new Rectangle(bucketPosition.x, bucketPosition.y, BUCKET_WIDTH, BUCKET_HEIGHT);

        for (int i = birds.size() - 1; i >= 0; i--) {
            Point bird = birds.get(i);
            Rectangle birdBounds = new Rectangle(bird.x, bird.y, BIRD_SIZE, BIRD_SIZE);

            if (birdBounds.intersects(breadBounds)) {
                birds.remove(i);
                happyPoints += HAPPY_POINTS_PER_BREAD;
            } else if (birdBounds.intersects(bucketBounds)) {
                birds.remove(i);
            } if (birds.isEmpty()) {
                gameOver = true;
            }
        }
    }

    private void resetGame() {
        birds.clear();
        spawnBirds(5);
        breadPosition.setLocation(-100, -100);
        happyPoints = 0;
        gameOver = false;
    }

    private void drawGameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        g.drawString("Game Over", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Click to play again", WINDOW_WIDTH / 2 - 80, WINDOW_HEIGHT / 2 + 30);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (gameOver) {
            drawGameOver(g);
        } else {
            drawBirds(g);
            drawBread(g);
            drawBucket(g);
            drawHappyPoints(g);
        }
    }

    private void drawBirds(Graphics g) {
        g.setColor(Color.RED);
        for (Point bird : birds) {
            g.fillOval(bird.x, bird.y, BIRD_SIZE, BIRD_SIZE);
        }
    }

    private void drawBread(Graphics g) {
        g.setColor(Color.ORANGE);
        g.fillOval(breadPosition.x, breadPosition.y, BREAD_SIZE, BREAD_SIZE);
    }

    private void drawBucket(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(bucketPosition.x, bucketPosition.y, BUCKET_WIDTH, BUCKET_HEIGHT);
    }

    private void drawHappyPoints(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawString("Happy Points: " + happyPoints, 20, 20);
    }

    private void handleMouseClicked(MouseEvent e) {
        if (gameOver) {
            resetGame();
            repaint();
        } else {
            breadPosition.setLocation(e.getX() - BREAD_SIZE / 2, e.getY() - BREAD_SIZE / 2);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Birds game = new Birds();
            game.setVisible(true);
        });
    }
}

class Quadtree {
    private static final int MAX_OBJECTS = 4;
    private static final int MAX_LEVELS = 5;
    private final int BIRD_SIZE = 40;

    private int level;
    private List<Point> objects;
    private Rectangle bounds;
    private Quadtree[] nodes;

    public Quadtree(int level, Rectangle bounds) {
        this.level = level;
        this.bounds = bounds;
        objects = new ArrayList<>();
        nodes = new Quadtree[4];
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    private void split() {
        int subWidth = bounds.width / 2;
        int subHeight = bounds.height / 2;
        int x = bounds.x;
        int y = bounds.y;

        nodes[0] = new Quadtree(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    private int getIndex(Point p) {
        int index = -1;
        double verticalMidpoint = bounds.x + bounds.width / 2;
        double horizontalMidpoint = bounds.y + bounds.height / 2;

        boolean topQuadrant = p.y < horizontalMidpoint && p.y + BIRD_SIZE < horizontalMidpoint;
        boolean bottomQuadrant = p.y > horizontalMidpoint;

        if (p.x < verticalMidpoint && p.x + BIRD_SIZE < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (p.x > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }

        return index;
    }

    public void insert(Point p) {
        if (nodes[0] != null) {
            int index = getIndex(p);

            if (index != -1) {
                nodes[index].insert(p);
                return;
            }
        }

        objects.add(p);

        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    public List<Point> retrieve(List<Point> returnObjects, Point p) {
        int index = getIndex(p);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, p);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }
}

class Level {
    private int numBirds;
    private int difficulty;
    private int spawnInterval;

    public Level(int numBirds, int difficulty, int spawnInterval) {
        this.numBirds = numBirds;
        this.difficulty = difficulty;
        this.spawnInterval = spawnInterval;
    }

    public int getNumBirds() {
        return numBirds;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getSpawnInterval() {
        return spawnInterval;
    }
}

/*Please note that this code provides only a basic implementation.
  To make it a full-fledged game, you may need to add more features, such as proper game mechanics, additional levels, and more advanced
  collision detection. Additionally, handling images for the birds, bread, and bucket could enhance the game's visual appeal. Nonetheless, 
this should serve as a starting point for your bird game project.*/