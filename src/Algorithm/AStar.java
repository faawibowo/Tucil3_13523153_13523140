package Algorithm;
import GameObject.*;
import java.util.*;

/*
 * Basically the same as AStar but with the addition for a normal cost 
*/

public class AStar {
    private PriorityQueue<State> prioQueue;
    public Set<State> visited;
    public State finalState;
    public int nodesExplored;
    public long time;
    boolean useExitDistH;
    boolean useObsCountH;


    public AStar(int AScoreValue) {
        prioQueue = new PriorityQueue<State>(Comparator.comparingInt((State s) -> s.AScore)); // Always process the state with the least cost first
        visited = new HashSet<>();
        nodesExplored = 0;
        if (AScoreValue == 0) {
            useExitDistH = true;
        }
        else if (AScoreValue == 1) {
            useObsCountH = true;
        }
        else {
            useExitDistH = true;
            useObsCountH = true;
        }
    }

    public boolean solve(State initialState) {
        long startTime = System.nanoTime();
        System.out.println("generating solution....");
        boolean found = false; 
        prioQueue.add(initialState);

        while (!found) {
        // detect if finish
            State currState = prioQueue.poll();
            if (currState == null) break;
            Car mainCar = currState.cars.get('P'); // retrieve primary car
            if (mainCar.isHorizontal) {
                if (mainCar.col + mainCar.length - 1 == State.exitCol || mainCar.col == State.exitCol) {
                    found = true;
                    time = System.nanoTime() - startTime;
                    System.out.println("Exit found");
                    finalState = currState;
                    finalState.displayState();
                    break;
                }
            }
            else
            {
                if (mainCar.row + mainCar.length - 1 == State.exitRow || mainCar.row == State.exitRow) {
                    found = true;
                    time = System.nanoTime() - startTime;
                    System.out.println("Exit found");
                    finalState = currState;
                    finalState.displayState();
                    break;
                }
            }
            // move every car
            char[][] board = currState.buildBoard();
            for (Car car : currState.cars.values()) {
                if (car.isHorizontal) {
                    int emptyCellsLeft = car.canMoveLeft(board); 
                    int emptyCellsRight = car.canMoveRight(board);
                    if (emptyCellsLeft > 0) {
                        for (int i = 0; i < emptyCellsLeft; i++) {
                            State movedState = currState.moveLeft(car.id, i + 1);
                            if (visited.add(movedState)) {
                                nodesExplored++;
                                if (useExitDistH && useObsCountH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                else if (useExitDistH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + movedState.cost;
                                }
                                else if (useObsCountH) {
                                    movedState.AScore = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                prioQueue.add(movedState);
                            }
                        }
                    }
                    if (emptyCellsRight > 0) {
                        for (int i = 0; i < emptyCellsRight; i++) {
                            State movedState = currState.moveRight(car.id, i + 1);
                            if (visited.add(movedState)) {
                                nodesExplored++;
                                if (useExitDistH && useObsCountH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                else if (useExitDistH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + movedState.cost;
                                }
                                else if (useObsCountH) {
                                    movedState.AScore = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                prioQueue.add(movedState);
                            }
                        }
                    }
                }
                else
                {
                    int emptyCellsTop = car.canMoveUp(board); 
                    int emptyCellsBottom = car.canMoveDown(board);
                    if (emptyCellsTop > 0) {
                        for (int i = 0; i < emptyCellsTop; i++) {
                            State movedState = currState.moveUp(car.id, i + 1);
                            if (visited.add(movedState)) {
                                nodesExplored++;
                                if (useExitDistH && useObsCountH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                else if (useExitDistH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + movedState.cost;
                                }
                                else if (useObsCountH) {
                                    movedState.AScore = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                prioQueue.add(movedState);
                            }
                        }
                    }
                    else
                    {
                        if (emptyCellsBottom > 0) {
                            for (int i = 0; i < emptyCellsBottom; i++) {
                                State movedState = currState.moveDown(car.id, i + 1);
                                if (visited.add(movedState)) {
                                    nodesExplored++;
                                if (useExitDistH && useObsCountH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                else if (useExitDistH) {
                                    movedState.AScore = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + movedState.cost;
                                }
                                else if (useObsCountH) {
                                    movedState.AScore = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board) + movedState.cost;
                                }
                                prioQueue.add(movedState);
                                }
                            }
                        }
                    }
                }
            }
        }
        return found;
    }

    public int getExitDist(Car car, int exitRow, int exitCol) {
        int dist = 0; // in cells
        if (car.isHorizontal) {
            if (exitRow >= car.row + car.length - 1) {
                dist = exitRow - (car.row + car.length - 1);
            }
            else {
                dist = car.row - exitRow;
            }
        }
        else {
            if (exitCol >= car.col + car.length - 1) {
                dist = exitCol - (car.col + car.length - 1);
            }
            else {
                dist = car.col - exitCol;
            }
        }
        return dist;
    }

    public int getBlockersAmount(Car car, int exitRow, int exitCol, char[][] board) { 
        int amount = 0; // amount of cars blocking the exit from the primary car

        Set<Character> blockers = new HashSet<>(); // set to car or character?

        if (car.isHorizontal) {
            if (exitCol >= car.col + car.length - 1) { // if exit on right
                for (int i = car.col + car.length; i <= exitRow; i++) {
                    if (board[car.row][i] != '.' && blockers.add(car.id)) {
                        amount++;
                    }
                }
            }
            else {
                for (int i = car.col - 1; i >= exitCol; i--) {
                    if (board[car.row][i] != '.' && blockers.add(car.id)) {
                        amount++;
                    }
                }
            }
            
        }
        else
        {
            if (exitRow >= car.row + car.length - 1) { // if exit on right
                for (int i = car.row + car.length; i <= exitRow; i++) {
                    if (board[car.row][i] != '.' && blockers.add(car.id)) {
                        amount++;
                    }
                }
            }
            else {
                for (int i = car.row - 1; i >= exitRow; i--) {
                    if (board[car.row][i] != '.' && blockers.add(car.id)) {
                        amount++;
                    }
                }
            }
        }
        return amount;
    }

    public double getRuntime() {
        return time / 1_000_000.0; // Convert nanoseconds to milliseconds
    }
}
