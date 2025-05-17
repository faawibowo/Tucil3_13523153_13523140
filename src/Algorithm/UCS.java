package Algorithm;

import GameObject.*;
import java.util.*;

public class UCS {
    public Queue<State> queue;
    public Set<State> visited;
    public State finalState;
    public int nodesExplored;
    public long time;

    public UCS(){
        queue = new LinkedList<>();
        visited = new HashSet<>();
        nodesExplored = 0;
    }

    public boolean solve(State initialState) {
        long startTime = System.nanoTime();
        System.out.println("generating solution....");
        boolean found = false;
        queue.add(initialState);
        while (!queue.isEmpty() && !found) {
            State currentState = queue.poll();
            Car mainCar = currentState.cars.get('P');
            if (mainCar.isHorizontal){
                if(mainCar.col<=State.exitCol && State.exitCol <=mainCar.col+mainCar.length-1){
                    found = true;
                    System.out.println("found exit");
                    finalState = currentState;
                    Car carP = currentState.cars.get('P');
                    carP.col = -1;
                    carP.row = -1;
                    currentState.displayState();
                    break;
                }
            }
            else{
                if(mainCar.row<=State.exitRow && State.exitRow <=mainCar.row+mainCar.length-1){
                    found = true;
                    System.out.println("found exit");
                    finalState = currentState;
                    Car carP = currentState.cars.get('P');
                    carP.col = -1;
                    carP.row = -1;
                    currentState.displayState();
                    break;
                }
            }
            char[][] board = currentState.buildBoard(); 
            for(Car car: currentState.cars.values()){
                if(car.isHorizontal){
                    int moveLeft = car.canMoveLeft(board);
                    int moveRight = car.canMoveRight(board);
                    if(moveLeft>0){
                        for (int i=0;i<moveLeft;i++){
                            State moveState = currentState.moveLeft(car.id, i+1);
                            if (visited.add(moveState)) {
                                nodesExplored++;
                                queue.add(moveState);
                            }
                        }
                    }
                    if(moveRight>0){
                        for (int i = 0; i < moveRight; i++) {
                            State moveState = currentState.moveRight(car.id, i+1);
                            if (visited.add(moveState)) {
                                nodesExplored++;
                                queue.add(moveState);
                            }
                        }
                    }
                }
                else{
                    int moveUp = car.canMoveUp(board);
                    int moveDown = car.canMoveDown(board);
                    if (moveUp>0){
                        for (int i = 0; i < moveUp; i++) {
                            State moveState = currentState.moveUp(car.id, i+1);
                            if (visited.add(moveState)) {
                                nodesExplored++;
                                queue.add(moveState);
                            }
                        }
                    }
                    if (moveDown>0){
                        for (int i = 0; i < moveDown; i++) {
                            State moveState = currentState.moveDown(car.id, i+1);
                            if (visited.add(moveState)) {
                                nodesExplored++;
                                queue.add(moveState);
                            }
                        }
                    }
                }
            }           
        }
        time = System.nanoTime() - startTime;
        return found;
    }

    public double getRuntime() {
        return time / 1_000_000.0; // Convert nanoseconds to milliseconds
    }

}
