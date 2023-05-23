import java.util.Random;

public class MazeGenerator {
    public static char[][] generateMaze(int width, int height) {
        var maze = new char[height][width];
        // Initialize maze with walls
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                maze[i][j] = '#';
            }
        }

        Random random = new Random();

        // Generate random passages
        for (int i = 1; i < height - 1; i += 2) {
            for (int j = 1; j < width - 1; j += 2) {
                maze[i][j] = ' ';  // Passage

                if (i < height - 2) {
                    if (random.nextBoolean()) {
                        maze[i + 1][j] = ' ';  // Passage below
                    } else {
                        maze[i][j + 1] = ' ';  // Passage to the right
                    }
                } else {
                    maze[i][j + 1] = ' ';  // Passage to the right
                }
            }
        }

        // Set entrance and exit
        maze[0][1] = 'S';  // Start
        maze[height - 1][width - 2] = 'E';  // End

        return maze;
    }
}
