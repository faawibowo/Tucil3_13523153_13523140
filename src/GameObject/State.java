package GameObject;
import java.util.*;
import java.awt.Point;


public class State {
    public Map<Character,Car> cars;
    public State parent;
    public char carId;
    public int direction; // 0=left, 1=right, 2=up, 3=down.
    public int cost;


    //board size
    public static int length;
    public static int width;
    public static int exitRow; //exit row
    public static int exitCol; //exit column

    public State(State parent, char carId, int direction, int cost, Point exit) {
        cars = new HashMap<Character,Car>();
        this.parent = parent;
        this.carId = carId;
        this.direction = direction;
        this.cost = cost;
    }

    public static void initBoard(int l, int w, int r, int c) {
        length = l;
        width = w;
        exitRow = r;
        exitCol = c;
    }

    public char[][] buildBoard() {
        char[][] board = new char[length][width];
        for (int i = 0; i < length; i++) {
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

    public State moveLeft(char carId, int steps) {
        Car car = cars.get(carId);
        if (car != null) {
            Car newCar = car.moveLeft(steps);
            State newState = new State(this, carId, 0, this.cost + steps, null);
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
            State newState = new State(this, carId, 1, this.cost + steps, null);
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
            State newState = new State(this, carId, 2, this.cost + steps, null);
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
            State newState = new State(this, carId, 3, this.cost + steps, null);
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

