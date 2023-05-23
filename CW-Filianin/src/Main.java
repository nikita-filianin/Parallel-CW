import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        var maze = MazeGenerator.generateMaze(1000, 1000);
//        Arrays.asList(maze).forEach(System.out::println);

        var parallelAstar = new ParallelAstar(maze);
        var sequentAstar = new AStarAlgorithm(maze);

        var begin = System.nanoTime();
        sequentAstar.findShortestPath();
        var end = System.nanoTime();
        System.out.println("Sequent: " + ((end-begin) / 1000000) + " ms");

        var begin2 = System.nanoTime();
        parallelAstar.findShortestPath(2);
        var end2 = System.nanoTime();
        System.out.println("Parallel 2 threads: " + ((end2 - begin2) / 1000000) + " ms");

        var begin3 = System.nanoTime();
        parallelAstar.findShortestPath(4);
        var end3 = System.nanoTime();
        System.out.println("Parallel 4 threads: " + ((end3 - begin3) / 1000000) + " ms");

        var begin4 = System.nanoTime();
        parallelAstar.findShortestPath(6);
        var end4 = System.nanoTime();
        System.out.println("Parallel 6 threads: " + ((end4 - begin4) / 1000000) + " ms");

        var begin5 = System.nanoTime();
        parallelAstar.findShortestPath(8);
        var end5 = System.nanoTime();
        System.out.println("Parallel 8 threads: " + ((end5 - begin5) / 1000000) + " ms");

        var begin6 = System.nanoTime();
        parallelAstar.findShortestPath(12); // var path =
        var end6 = System.nanoTime();
        System.out.println("Parallel 12 threads: " + ((end6 - begin6) / 1000000) + " ms");

//        if (path != null) {
//            System.out.println("Shortest path found:");
//            for (int[] point : path) {
////                System.out.println("(" + point[0] + ", " + point[1] + ")");
//                if(maze[point[0]][point[1]] == ' ') {
//                    maze[point[0]][point[1]] = '.';
//                }
//            }
//            Arrays.asList(maze).forEach(System.out::println);
//        } else {
//            System.out.println("No path found.");
//        }

    }
}
