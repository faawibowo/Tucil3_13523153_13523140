package Algorithm;
import GameObject.*;
import java.util.*;

/*
 * Greedy Best First Search Algorithm
 * heuristic: The closer the primary car is to the exit door (measured in cell between primary car and the exit)
 */

 /*
  * Special cases:
  1. Car right next to exit
  2.
  */

  /*
   * General Idea: 
   * 1. Init: 
   *    - recieve a current state of a board
   *    - initiate cost:
   *        1) Get distance between primary car and exit (getExitDistance())
   *        2) Cost = getExitDistance() + getBlockers
   * 2. Final state: If getExitDistance() = 0
   * 3. Loop
   *    You have a state. Each state has a map of cars. For each car that`
   * 
   * Other notes:
   * Use set to know visited state
   * prio queue for gbfs (Use BFS technique for the algorithm)
   * Car is horizontal or vertical
   */

public class GBFS {
    private PriorityQueue<State> prioQueue;
    public Set<State> visited;
    public State finalState;
    public int nodesExplored;
    public long time;
    boolean useExitDistH;
    boolean useObsCountH;


    public GBFS(int heuristicValue) {
        prioQueue = new PriorityQueue<State>(Comparator.comparingInt((State s) -> s.heuristic)); // Always process the state with the least cost first
        visited = new HashSet<>();
        nodesExplored = 0;
        if (heuristicValue == 0) {
            useExitDistH = true;
        }
        else if (heuristicValue == 1) {
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
                    Car carP = finalState.cars.get('P');
                    carP.col = -1;
                    carP.row = -1;
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
                    Car carP = finalState.cars.get('P');
                    carP.col = -1;
                    carP.row = -1;
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
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
                                }
                                else if (useExitDistH) {
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol);
                                }
                                else if (useObsCountH) {
                                    movedState.heuristic = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
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
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
                                }
                                else if (useExitDistH) {
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol);
                                }
                                else if (useObsCountH) {
                                    movedState.heuristic = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
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
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
                                }
                                else if (useExitDistH) {
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol);
                                }
                                else if (useObsCountH) {
                                    movedState.heuristic = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
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
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol) + getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
                                }
                                else if (useExitDistH) {
                                    movedState.heuristic = getExitDist(movedState.cars.get('P'), State.exitRow, State.exitCol);
                                }
                                else if (useObsCountH) {
                                    movedState.heuristic = getBlockersAmount(movedState.cars.get('P'), State.exitRow, State.exitCol, board);
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
