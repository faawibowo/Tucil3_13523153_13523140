package GameObject;
import java.util.*;

public class State {
    public Map<Character,Car> cars;
    public State parent;
    public char carId;
    public int direction; // 0=left, 1=right, 2=up, 3=down.
    public int cost;


    //board size
    public static int height;
    public static int width;
    public static int exitRow; //exit row
    public static int exitCol; //exit column

    public State(State parent, char carId, int direction, int cost) {
        cars = new HashMap<Character,Car>();
        this.parent = parent;
        this.carId = carId;
        this.direction = direction;
        this.cost = cost;
    }

    public static void initBoard(int h, int w, int r, int c) {
        height = h;
        width = w;
        exitRow = r;
        exitCol = c;
    }

    public char[][] buildBoard() {
        char[][] board = new char[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                board[i][j] = '.';
            }
        }
        for (Car car : cars.values()) {
            if (car.isHorizontal) {
                for (int i = 0; i < car.length; i++) {
                    board[car.row][car.col + i] = car.id;
                }
            } else {
                for (int i = 0; i < car.length; i++) {
                    board[car.row + i][car.col] = car.id;
                }
            }
        }
        return board;
    }

    public void printBoard() {
        char[][] board = buildBoard();
        Car mainCar = cars.get('P');
        char[][] newBoard;
        if (mainCar.isHorizontal){
            newBoard = new char[height][width+1];
            if (exitCol == 0){
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width+1; j++) {
                        if (i == exitRow && j == exitCol) {
                            newBoard[i][j] = 'K';
                        } else if (j == 0) {
                            newBoard[i][j] = ' ';
                        }
                        else {
                            newBoard[i][j] = board[i][j-1];
                        }
                    }
                }
            }
            else{
                for (int i = 0; i < height; i++) {
                    for (int j = 0; j < width + 1; j++) {
                        if (i == exitRow && j == exitCol+1) {
                            newBoard[i][j] = 'K';
                        } else if (j == width) {
                            newBoard[i][j] = ' ';
                        } else {
                            newBoard[i][j] = board[i][j];
                        }
                    }
                }
            }
        }
        else{
            newBoard = new char[height+1][width];
            if (exitRow == 0) {
                for (int i = 0; i < height+1; i++) {
                    for (int j = 0; j < width; j++) {
                        System.out.println("i: " + i + " j: " + j);
                        if (i == exitRow && j == exitCol) {
                            newBoard[i][j] = 'K';
                        } else if (i == 0) {
                            newBoard[i][j] = ' ';
                        } else {
                            newBoard[i][j] = board[i-1][j];
                        }
                    }
                }
            } else {
                for (int i = 0; i < height+1; i++) {
                    for (int j = 0; j < width; j++) {
                        if (i == exitRow+1 && j == exitCol) {
                            newBoard[i][j] = 'K';
                        } else if (i == height) {
                            newBoard[i][j] = ' ';
                        } else {
                            newBoard[i][j] = board[i][j];
                        }
                    }
                }
            }
        }
        for (int i = 0; i < newBoard.length; i++) {
            for (int j = 0; j < newBoard[0].length; j++) {
                System.out.print(newBoard[i][j] + " ");
            }
            System.out.println();
        }
        

    }

    public State moveLeft(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveLeft(steps);
            State newState = new State(this, carId, 0, this.cost + steps);
            newState.cars.put(carId, newCar);
            for (Car car2 : cars.values()) {
                if (car2.id != carId) {
                    newState.cars.put(car2.id, car2);
                }
            }
            return newState;
        }
        return null;
    }

    public State moveRight(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveRight(steps);
            State newState = new State(this, carId, 1, this.cost + steps);
            newState.cars.put(carId, newCar);
            for (Car car2 : cars.values()) {
                if (car2.id != carId) {
                    newState.cars.put(car2.id, car2);
                }
            }
            return newState;
        }
        return null;
    }

    public State moveUp(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveUp(steps);
            State newState = new State(this, carId, 2, this.cost + steps);
            newState.cars.put(carId, newCar);
            for (Car car2 : cars.values()) {
                if (car2.id != carId) {
                    newState.cars.put(car2.id, car2);
                }
            }
            return newState;
        }
        return null;
    }

    public State moveDown(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveDown(steps);
            State newState = new State(this, carId, 3, this.cost + steps);
            newState.cars.put(carId, newCar);
            for (Car car2 : cars.values()) {
                if (car2.id != carId) {
                    newState.cars.put(car2.id, car2);
                }
            }
            return newState;
        }
        return null;
    }


}

