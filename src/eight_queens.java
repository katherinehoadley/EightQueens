//Kat Hoadley - 8 Queens Project

import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class eight_queens {

    public static Random random = new Random();

    public static List<int[]> boardList = new ArrayList<>();

    public static int MAX_RESTARTS = 1000;
    public static int totalSuccessorsGenerated;
    public static int totalRestarts;
    public static int totalFailures;

    public static int randomBoardStates;

    public static void main(String[] args) {
        greetUser();
    }

    public static void greetUser() {
        Scanner input = new Scanner(System.in);
        System.out.println("Hello! How many random board states should be initialized?");
        randomBoardStates = input.nextInt();
        initializer();
    }

    public static void initializer() {
        generateBoards();

        randomRestart();
        int rrSuccessors = totalSuccessorsGenerated;
        int rrRestarts = totalRestarts;
        int rrFailures = totalFailures;

        firstChoice();
        int fcSuccessors = totalSuccessorsGenerated;
        int fcRestarts = totalRestarts;
        int fcFailures = totalFailures;

        if (randomBoardStates > 1){
            System.out.println(randomBoardStates + " puzzles.");
            System.out.println(MAX_RESTARTS + " restarts allowed per search.");

            randomRestartStatistics(rrSuccessors, rrRestarts, rrFailures);
            firstChoiceStatistics(fcSuccessors, fcRestarts, fcFailures);
        }
    }

    public static void generateBoards() {
        for (int i = 0; i < randomBoardStates; i++) {
            boardList.add(randomizeQueenMap());
        }
    }

    public static void visualizer(String algorithmName, int[] initalBoard, int initialHeuristic,
                                  int[] finalBoard, int finalHeuristic, int steps, int restarts,
                                  boolean solved) {
        System.out.println(algorithmName);
        System.out.println("This is the initial placement of the queens");
        System.out.println("It has " + initialHeuristic + " conflicts");
        printBoard(initalBoard);
        System.out.println();

        if (solved) {
            System.out.println("We have found a solution in " + steps + " steps.");
            System.out.println("This required " + restarts + " random restarts.");
            System.out.println("Solution is:");
            printBoard(finalBoard);
        }
        else {
            System.out.println("Search failed after " + steps + " steps and " + restarts + " restarts.");
            System.out.println("Final board heuristic: " + finalHeuristic);
            System.out.println("Final board:");
            printBoard(finalBoard);
        }
    }

    public static void printBoard(int[] board){
        for (int row = 0; row < 8; row++){
            System.out.print(" ");
            for (int col = 0; col < 8; col++){
                if (board[col] == row){
                    System.out.print("Q ");
                }
                else{
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
    }

    public static void randomRestart() {
        totalSuccessorsGenerated = 0;
        totalRestarts = 0;
        totalFailures = 0;

        for (int i = 0; i < boardList.size(); i++) {
            int[] initialBoard = boardList.get(i).clone();
            int initialHeuristic = calculateHeuristic(initialBoard);

            int[] currentBoard = boardList.get(i).clone();
            int currentHeuristic = calculateHeuristic(currentBoard);

            int boardRestarts = 0;
            int steps = 0;
            int boardSuccessors = 0;
            boolean solved = false;

            while (boardRestarts < MAX_RESTARTS) {
                if (currentHeuristic == 0) {
                    solved = true;
                    break;
                }
                List<int[]> successors = getSuccessors(currentBoard);
                boardSuccessors += successors.size();

                int bestSuccessorHeuristic = Integer.MAX_VALUE;
                List<int[]> bestSuccessors = new ArrayList<>();

                for (int j = 0; j < successors.size(); j++) {
                    int[] successor = successors.get(j);
                    int heuristic = calculateHeuristic(successor);

                    if (heuristic < bestSuccessorHeuristic) {
                        bestSuccessorHeuristic = heuristic;
                        bestSuccessors.clear();
                        bestSuccessors.add(successor);
                    }
                    else if (heuristic == bestSuccessorHeuristic){
                        bestSuccessors.add(successor);
                    }

                }
                if (bestSuccessorHeuristic < currentHeuristic) {
                    int randomIndex = random.nextInt(bestSuccessors.size());
                    currentBoard = bestSuccessors.get(randomIndex);
                    currentHeuristic = bestSuccessorHeuristic;
                    steps++;
                } else {
                    boardRestarts++;
                    currentBoard = randomizeQueenMap();
                    currentHeuristic = calculateHeuristic(currentBoard);
                }

            }
            if (!solved) {
                totalFailures++;
            }

            totalSuccessorsGenerated += boardSuccessors;
            totalRestarts += boardRestarts;

            if (randomBoardStates == 1) {
                visualizer("Random Restart Hill Climb", initialBoard, initialHeuristic,
                        currentBoard, currentHeuristic, steps, boardRestarts, solved);
            }
        }
    }

    public static void firstChoice() {
        totalSuccessorsGenerated = 0;
        totalRestarts = 0;
        totalFailures = 0;

        for (int i = 0; i < boardList.size(); i++) {
            int[] initialBoard = boardList.get(i).clone();
            int initialHeuristic = calculateHeuristic(initialBoard);

            int[] currentBoard = initialBoard.clone();
            int currentHeuristic = initialHeuristic;

            int boardRestarts = 0;
            int steps = 0;
            int boardSuccessors = 0;
            boolean solved = false;

            while (boardRestarts < MAX_RESTARTS) {
                if (currentHeuristic == 0) {
                    solved = true;
                    break;
                }

                boolean[][] checkedNeighbors = new boolean[8][8];
                int uniqueNeighborsChecked = 0;
                boolean foundBetter = false;

                while (uniqueNeighborsChecked < 56){
                    int col = random.nextInt(8);
                    int newRow = random.nextInt(8);

                    if (newRow == currentBoard[col] || checkedNeighbors[col][newRow]) {
                        continue;
                    }

                    checkedNeighbors[col][newRow] = true;
                    uniqueNeighborsChecked++;

                    int[] singleNeighborOption = currentBoard.clone();
                    singleNeighborOption[col] = newRow;
                    boardSuccessors++;

                    int neighborHeuristic = calculateHeuristic(singleNeighborOption);

                    if (neighborHeuristic < currentHeuristic){
                        currentBoard = singleNeighborOption;
                        currentHeuristic = neighborHeuristic;
                        steps++;
                        foundBetter = true;
                        break;

                    }

                }
                if (!foundBetter){
                    boardRestarts++;
                    currentBoard = randomizeQueenMap();
                    currentHeuristic = calculateHeuristic(currentBoard);
                }

            }
            if (!solved){
                totalFailures++;
            }
            totalSuccessorsGenerated += boardSuccessors;
            totalRestarts += boardRestarts;

            if (randomBoardStates == 1) {
                visualizer("First Choice Hill Climb", initialBoard, initialHeuristic,
                        currentBoard, currentHeuristic, steps, boardRestarts, solved);
            }
        }
    }

    public static int calculateHeuristic(int[] board) {
        int conflicts = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = i + 1; j < board.length; j++) {
                //row conflict
                if (board[i] == board[j]) {
                    conflicts++;
                }
                //diagonal conflict
                else if (Math.abs(board[i] - board[j]) == Math.abs(i - j)) {
                    conflicts++;
                }
            }
        }
        return conflicts;
    }

    public static List<int[]> getSuccessors(int[] board) {
        List<int[]> successors = new ArrayList<>();

        for (int col = 0; col < 8; col++) {
            for (int row = 0; row < 8; row++) {
                if (row != board[col]) {
                    int[] successor = board.clone();
                    successor[col] = row;
                    successors.add(successor);
                }
            }
        }
        return successors;
    }

    public static void randomRestartStatistics(int totalSuccessors, int totalRestarts, int totalFailures) {

        int avgSuccessors = totalSuccessors / randomBoardStates;
        int avgRestarts = totalRestarts / randomBoardStates;

        System.out.println("Hill-climbing with random restart: average search cost/number of " +
                "successors: " + avgSuccessors + ", average number of restarts required: " +
                avgRestarts + ", total number of failed searches: " + totalFailures);


    }

    public static void firstChoiceStatistics(int totalSuccessors, int totalRestarts, int totalFailures) {
        int avgSuccessors = totalSuccessors / randomBoardStates;
        int avgRestarts = totalRestarts / randomBoardStates;

        System.out.println("First-choice hill climbing: average search cost/number of " +
                "successors: " + avgSuccessors + ", average number of restarts required: " +
                avgRestarts + ", total number of failed searches: " + totalFailures);
    }

    public static int[] randomizeQueenMap() {
        int[] queenMap = new int[8];
        for (int col = 0; col < 8; col++) {
            queenMap[col] = random.nextInt(8);
        }
        return queenMap;
    }

}