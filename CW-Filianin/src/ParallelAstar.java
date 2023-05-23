import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelAstar {
    private final char[][] maze;
    private final int width;
    private final int height;
    private final int[][] distances;
    private final int[][] heuristic;
    private final boolean[][] visited;
    private final int[][] parentX;
    private final int[][] parentY;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public ParallelAstar(char[][] maze) {
        this.maze = maze;
        this.height = maze.length;
        this.width = maze[0].length;
        this.distances = new int[height][width];
        this.heuristic = new int[height][width];
        this.visited = new boolean[height][width];
        this.parentX = new int[height][width];
        this.parentY = new int[height][width];
    }

    public List<int[]> findShortestPath(int threads) {
        var executorService = Executors.newFixedThreadPool(threads);
        initializeData();
        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        queue.offer(new int[]{startX, startY, 0});
        visited[startX][startY] = true;

        while (!queue.isEmpty()) {
            int iterationsAmount = Math.min(queue.size(), threads);
            var iterationFutures = new ArrayList<Future<?>>();
            for (int i = 0; i < iterationsAmount; i++) {
                var iterationTask = new Runnable() {
                    @Override
                    public void run() {
                        int[] current;
                        synchronized (queue) {
                            current = queue.poll();
                        }
                        int x = current[0];
                        int y = current[1];

                        visited[x][y] = true;

                        // Explore neighboring cells
                        exploreNeighboringCells(queue, x - 1, y, current[2] + 1, x, y); // Up
                        exploreNeighboringCells(queue, x + 1, y, current[2] + 1, x, y); // Down
                        exploreNeighboringCells(queue, x, y - 1, current[2] + 1, x, y); // Left
                        exploreNeighboringCells(queue, x, y + 1, current[2] + 1, x, y); // Right
                    }
                };
                var iterationFuture = executorService.submit(iterationTask);
                iterationFutures.add(iterationFuture);
            }
            iterationFutures.forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
            if(visited[endX][endY]) {
                return reconstructPath();
            }
        }
        executorService.shutdown();
        return null; // No path found
    }

    private void initializeData() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                distances[i][j] = Integer.MAX_VALUE;
                heuristic[i][j] = Math.abs(i - endX) + Math.abs(j - endY);
                visited[i][j] = false;
                parentX[i][j] = -1;
                parentY[i][j] = -1;

                if (maze[i][j] == 'S') {
                    startX = i;
                    startY = j;
                } else if (maze[i][j] == 'E') {
                    endX = i;
                    endY = j;
                }
            }
        }
        distances[startX][startY] = 0;
    }

    private void exploreNeighboringCells(PriorityQueue<int[]> queue, int x, int y, int newDistance, int parentX, int parentY) {
        if (isValidCell(x, y) && !visited[x][y] && maze[x][y] != '#') {
            if (newDistance < distances[x][y]) {
                distances[x][y] = newDistance;
                this.parentX[x][y] = parentX;
                this.parentY[x][y] = parentY;
                synchronized (queue) {
                    queue.offer(new int[]{x, y, newDistance + heuristic[x][y]});
                }
            }
        }
    }

    private boolean isValidCell(int x, int y) {
        return x >= 0 && x < height && y >= 0 && y < width;
    }

    private List<int[]> reconstructPath() {
        List<int[]> path = new ArrayList<>();
        int x = endX;
        int y = endY;

        while (x != startX || y != startY) {
            path.add(0, new int[]{x, y});
            int parentX = this.parentX[x][y];
            int parentY = this.parentY[x][y];
            x = parentX;
            y = parentY;
        }

        path.add(0, new int[]{startX, startY});
        return path;
    }
}
