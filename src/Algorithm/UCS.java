package Algorithm;

import GameObject.*;
import java.util.*;

public class UCS {
    public Queue<State> queue;
    public Set<State> visited;
    public int nodesExplored;

    public UCS(){
        queue = new LinkedList<>();
        visited = new HashSet<>();
        nodesExplored = 0;
    }

    public boolean solve(State initialState) {
        boolean found = false;
        queue.add(initialState);
        while (!queue.isEmpty() && !found) {
            State currentState = queue.poll();
            Car mainCar = currentState.cars.get('P');
            if (mainCar.isHorizontal){
                if(mainCar.col<=State.exitCol && State.exitCol <=mainCar.col+mainCar.length-1){
                    found = true;
                    System.out.println("found exit");
                    currentState.displayState();
                    break;
                }
            }
            else{
                if(mainCar.row<=State.exitRow && State.exitRow <=mainCar.row+mainCar.length-1){
                    found = true;
                    System.out.println("exit row: " + State.exitRow);
                    System.out.println("main car row: " + mainCar.row);
                    System.out.println("main car row + width - 1: " + (mainCar.row+State.width-1));
                    System.out.println("found exit");
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
                            nodesExplored++;
                            queue.add(currentState.moveLeft(car.id, i+1));
                        }
                    }
                    if(moveRight>0){
                        for (int i = 0; i < moveRight; i++) {
                            nodesExplored++;
                            queue.add(currentState.moveRight(car.id, i+1));
                        }
                    }
                }
                else{
                    int moveUp = car.canMoveUp(board);
                    int moveDown = car.canMoveDown(board);
                    if (moveUp>0){
                        for (int i = 0; i < moveUp; i++) {
                            nodesExplored++;
                            queue.add(currentState.moveUp(car.id, i+1));
                        }
                    }
                    if (moveDown>0){
                        for (int i = 0; i < moveDown; i++) {
                            nodesExplored++;
                            queue.add(currentState.moveDown(car.id, i+1));
                        }
                    }
                }
            }           
        }
        return found;
    }

}
