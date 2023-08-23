import java.util.*;
class Board {
    private final int[][] tiles;
    private final int N;
    private int blankRow, blankCol;
    public Board(int[][] tiles) {
        N = tiles.length;
        this.tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tiles[i][j] = tiles[i][j];
                if (tiles[i][j] == 0) {
                    blankRow = i;
                    blankCol = j;
                }
            }
        }
    }
    public int getSize() {
        return N;
    }
    public int getTile(int row, int col) {
        return tiles[row][col];
    }
    public int hamming() {
        int count = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (tiles[i][j] != 0 && tiles[i][j] != i * N + j + 1) {
                    count++;
                }
            }
        }
        return count;
    }
    public int manhattan() {
        int distance = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int value = tiles[i][j];
                if (value != 0) {
                    int targetRow = (value - 1) / N;
                    int targetCol = (value - 1) % N;
                    distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }
        return distance;
    }
    public boolean isGoal() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == N - 1 && j == N - 1) {
                    return tiles[i][j] == 0;
                } else if (tiles[i][j] != i * N + j + 1) {
                    return false;
                }
            }
        }
        return true;
    }
    public Iterable<Board> neighbors() {
        List<Board> neighbors = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        for (int[] dir : directions) {
            int newRow = blankRow + dir[0];
            int newCol = blankCol + dir[1];
            if (newRow >= 0 && newRow < N && newCol >= 0 && newCol < N) {
                int[][] newTiles = new int[N][N];
                for (int i = 0; i < N; i++) {
                    newTiles[i] = Arrays.copyOf(tiles[i], N);
                }
                newTiles[blankRow][blankCol] = newTiles[newRow][newCol];
                newTiles[newRow][newCol] = 0;

                neighbors.add(new Board(newTiles));
            }
        }
        return neighbors;
    }
}
class Solver {
    private final PriorityQueue<SearchNode> pq = new PriorityQueue<>();
    private SearchNode goalNode;
    private class SearchNode implements Comparable<SearchNode> {
        private final Board board;
        private final SearchNode parent;
        private final int moves;
        private final int priority;
        public SearchNode(Board board, SearchNode parent) {
            this.board = board;
            this.parent = parent;
            if (parent == null) {
                this.moves = 0;
            } else {
                this.moves = parent.moves + 1;
            }
            this.priority = board.manhattan() + moves;
        }
        public int compareTo(SearchNode other) {
            return Integer.compare(this.priority, other.priority);
        }
    }
    public Solver(Board initial) {
        pq.add(new SearchNode(initial, null));

        while (!pq.isEmpty()) {
            SearchNode currentNode = pq.poll();

            if (currentNode.board.isGoal()) {
                goalNode = currentNode;
                break;
            }

            for (Board neighbor : currentNode.board.neighbors()) {
                if (currentNode.parent == null || !neighbor.equals(currentNode.parent.board)) {
                    pq.add(new SearchNode(neighbor, currentNode));
                }
            }
        }
    }
    public boolean isSolvable() {
        return goalNode != null;
    }

    public int moves() {
        if (!isSolvable()) {
            return -1;
        }
        return goalNode.moves;
    }
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }
        Stack<Board> solutionPath = new Stack<>();
        SearchNode currentNode = goalNode;
        while (currentNode != null) {
            solutionPath.push(currentNode.board);
            currentNode = currentNode.parent;
        }
        List<Board> solutionList = new ArrayList<>();
        while (!solutionPath.isEmpty()) {
            solutionList.add(solutionPath.pop());
        }

        return solutionList;
    }
}
public class EightPuzzleSolver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] initialTiles = new int[3][3];
        System.out.println("Enter the initial configuration of the 8-puzzle (3x3 grid):");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                initialTiles[i][j] = scanner.nextInt();}
        }scanner.close();
        Board initialBoard = new Board(initialTiles);
        Solver solver = new Solver(initialBoard);
        if (solver.isSolvable()) {
            System.out.println("Minimum number of moves = " + solver.moves());
            System.out.println("Solution:");
            for (Board board : solver.solution()) {
                for (int i = 0; i < board.getSize(); i++) {
                    for (int j = 0; j < board.getSize(); j++) {
                        System.out.print(board.getTile(i, j) + " ");
                    }
                    System.out.println();
                }
                System.out.println();}} else {
            System.out.println("No solution exists.");}}}
