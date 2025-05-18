package GameObject;
import java.util.*;
import util.ColorUtil;

public class State {
    public Map<Character,Car> cars;
    public State parent;
    public char carId;
    public int direction; // 0=left, 1=right, 2=up, 3=down.
    public int cost;
    public int heuristic;

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

    public State(State parent, char carId, int direction, int cost, int heuristic) {
        cars = new HashMap<Character, Car>();
        this.parent = parent;
        this.carId = carId;
        this.direction = direction;
        this.cost = cost;
        this.heuristic = heuristic;
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
            if(car.col < 0 || car.col >= width || car.row < 0 || car.row >= height) {
                continue; // Skip cars that are out of bounds
            }
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
                if (newBoard[i][j]==carId){
                    System.out.print(ColorUtil.colorize(newBoard[i][j], ColorUtil.YELLOW) + " ");
                }
                else{
                    System.out.print(newBoard[i][j] + " ");
                }
                
            }
            System.out.println();
        }
        System.out.println("--------------------------");
        

    }

    public State moveLeft(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveLeft(steps);
            State newState = new State(this, carId, 0, this.cost+1);
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
            State newState = new State(this, carId, 1, this.cost+1);
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
            State newState = new State(this, carId, 2, this.cost + 1);
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
            State newState = new State(this, carId, 3, this.cost + 1);
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

    public void displayState() {
        if (parent != null) {
            parent.displayState();
            if (direction == 0) {
                System.out.println("Move car " + carId + " to the left");
            } else if (direction == 1) {
                System.out.println("Move car " + carId + " to the right");
            } else if (direction == 2) {
                System.out.println("Move car " + carId + " up");
            } else if (direction == 3) {
                System.out.println("Move car " + carId + " down");
            }
            this.printBoard();
        }
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        State other = (State) obj;
        for (Map.Entry<Character, Car> entry : cars.entrySet()) {
            char carId = entry.getKey();
            Car car1 = entry.getValue();
            Car car2 = other.cars.get(carId);
            if (car2 == null || !car1.equals(car2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cars);
    }


}

