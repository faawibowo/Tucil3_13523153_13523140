package GameObject;

public class Car {
    public int row, col;
    public boolean isHorizontal;
    public int length;
    public char id;
    public boolean isMainCar;

    public Car(int row, int col, boolean isHorizontal, int length, char id, boolean isMainCar) {
        this.row = row;
        this.col = col;

        this.isHorizontal = isHorizontal;
        this.length = length;
        this.id = id;
        this.isMainCar = isMainCar;
    }

    public Car(Car other){
        this.row = other.row;
        this.col = other.col;
        this.isHorizontal = other.isHorizontal;
        this.length = other.length;
        this.id = other.id;
        this.isMainCar = other.isMainCar;
    }

    //integer means the number of steps that can be moved
    public int canMoveRight(char[][] board) {
        int steps = 0;
        for (int i = col+length-1; i < board[0].length ; i++){
            if (board[row][i] == '.'){
                steps++;
            }
            else if (board[row][i] == id) {
                continue;
            }
            else {
                break;
            }
        }
        return steps;
    }

    public int canMoveLeft(char[][] board) {
        int steps = 0;
        for (int i = col; i >= 0; i--) {
            if (board[row][i] == '.') {
                steps++;
            }
            else if (board[row][i] == id) {
                continue;
            }
            else {
                break;
            }
        }
        return steps;
    }

    public int canMoveUp(char[][] board) {
        int steps = 0;
        for (int i = row; i > 0; i--) {
            if (board[i][col] == '.') {
                steps++;
            }
            else if (board[i][col] == id) {
                continue;
            }
            else {
                break;
            }
        }
        return steps;
    }

    public int canMoveDown(char[][] board) {
        int steps = 0;
        
        for (int i = row+length-1; i < board.length; i++) {
            if (board[i][col] == '.') {
                steps++;
            }
            else if (board[i][col] == id) {
                continue;
            }
            else {
                break;
            }
        }
        return steps;
    }

    public Car moveRight(int steps) {
        return new Car(row, col + steps, isHorizontal, length, id, isMainCar);
    }

    public Car moveLeft(int steps) {
        return new Car(row, col - steps, isHorizontal, length, id, isMainCar);
    }

    public Car moveUp(int steps) {
        return new Car(row - steps, col, isHorizontal, length, id, isMainCar);
    }

    public Car moveDown(int steps) {
        return new Car(row + steps, col, isHorizontal, length, id, isMainCar);
    }
}
