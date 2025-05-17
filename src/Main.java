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
        for (Car car : loadedState.cars.values()) {
            System.out.println("Car ID: " + car.id + ", Row: " + car.row + ", Col: " + car.col);
        }
        System.out.println("Exit Row: " + State.exitRow);
        System.out.println("Exit Col: " + State.exitCol);

        scan.close();

        UCS solution = new UCS();
        boolean solved = solution.solve(loadedState);
        if (solved) {
            System.out.println("Solution found!");
            System.out.println("solution found: " + solved);
            System.out.println("nodes explored: " + solution.nodesExplored);
            System.out.printf("Runtime: %.3f ms%n", solution.getRuntime());
        } else {
            System.out.println("No solution found.");
        }

    }
}
