import java.io.IOException;

import GameObject.*;
import FileReader.*;
import Algorithm.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize the board with length, width, exit row, and exit column
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the file name to load the state: ");
        String filePath = scan.nextLine();
        State loadedState = null;
        try {
            loadedState = Parser.loadState(filePath);
            loadedState.printBoard();
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid file format: " + e.getMessage());
        }

        scan.close();

        UCS solution = new UCS();
        boolean solved = solution.solve(loadedState);
        System.out.println("solution found: " + solved);
        System.out.println("nodes explored: " + solution.nodesExplored);



        // // Create a new state with some cars
        // State state = new State(null, ' ', 0, 0);

        // Car car1 = new Car(0, 0, true, 2, 'A', true);
        // Car car2 = new Car(1, 0, false, 3, 'B', false);
        // Car car3 = new Car(3, 2, true, 2, 'C', false);
        // Car car4 = new Car(4, 3, false, 2, 'D', false);
        // Car car5 = new Car(5, 4, false, 2, 'P', false);

        // // Add cars to the state
        // state.cars.put('A', car1);
        // state.cars.put('B', car2);
        // state.cars.put('C', car3);
        // state.cars.put('D', car4);
        // state.cars.put('P', car5);

        // char[][] board = state.buildBoard();
        // for (int i = 0; i < board.length; i++) {
        //     for (int j = 0; j < board[i].length; j++) {
        //         System.out.print(board[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        // System.out.println("test board print:");
        // state.printBoard();

        // // State newState = new State(state, 'A', 1, 0, null);
        // // newState.cars.put('A', car1.moveRight(4));
        // // for (Car car : state.cars.values()) {
        // //     if (car.id != 'A') {
        // //         System.out.println("masuk");
        // //         newState.cars.put(car.id, car);
        // //     }
        // // }
        // // char[][] newBoard = newState.buildBoard();
        // // System.out.println("After moving car A to the right:");
        // // for (int i = 0; i < newBoard.length; i++) {
        // //     for (int j = 0; j < newBoard[i].length; j++) {
        // //         System.out.print(newBoard[i][j] + " ");
        // //     }
        // //     System.out.println();
        // // }

        // State newState = state.moveRight('A', 3);
        // char[][] newBoard = newState.buildBoard();
        // System.out.println("After moving car A to the right:");
        // for (int i = 0; i < newBoard.length; i++) {
        //     for (int j = 0; j < newBoard[i].length; j++) {
        //         System.out.print(newBoard[i][j] + " ");
        //     }
        //     System.out.println();
        // }


        // // test moveUp
        // newState = newState.moveUp('B', 1);
        // newBoard = newState.buildBoard();
        // System.out.println("After moving car B up:");
        // for (int i = 0; i < newBoard.length; i++) {
        //     for (int j = 0; j < newBoard[i].length; j++) {
        //         System.out.print(newBoard[i][j] + " ");
        //     }
        //     System.out.println();
        // }

        // System.out.println("Car A can move right: " + newState.cars.get('A').canMoveRight(newBoard));
        // System.out.println("Car A can move left: " + newState.cars.get('A').canMoveLeft(newBoard));
        // System.out.println("Car B can move up: " + newState.cars.get('B').canMoveUp(newBoard));
        // System.out.println("Car B can move down: " + newState.cars.get('B').canMoveDown(newBoard));

        // System.out.println("Car C can move right: " + newState.cars.get('C').canMoveRight(newBoard));
        // System.out.println("Car C can move left: " + newState.cars.get('C').canMoveLeft(newBoard));
        // System.out.println("Car D can move up: " + newState.cars.get('D').canMoveUp(newBoard));
        // System.out.println("Car D can move down: " + newState.cars.get('D').canMoveDown(newBoard));
    }
}
