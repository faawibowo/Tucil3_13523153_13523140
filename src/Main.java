import java.io.IOException;
import GameObject.*;
import FileReader.*;
import Algorithm.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Load input state from file
        System.out.print("Enter the file name to load the state: ");
        String filePath = scan.nextLine();
        State loadedState = null;
        try {
            loadedState = Parser.loadState(filePath);
            loadedState.printBoard();
        } catch (IOException e) {
            System.out.println("Failed to read file: " + e.getMessage());
            scan.close();
            return;
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid file format: " + e.getMessage());
            scan.close();
            return;
        }

        for (Car car : loadedState.cars.values()) {
            System.out.println("Car ID: " + car.id + ", Row: " + car.row + ", Col: " + car.col);
        }
        System.out.println("Exit Row: " + State.exitRow);
        System.out.println("Exit Col: " + State.exitCol);

        // Ask user to select search algorithm
        System.out.println("\nSelect search algorithm:");
        System.out.println("1. UCS");
        System.out.println("2. GBFS");
        System.out.println("3. AStar");
        System.out.print("Enter your choice (1, 2, or 3): ");
        int algChoice = scan.nextInt();
        scan.nextLine(); // Clear the buffer

        boolean solved = false;
        if (algChoice == 1) {
            UCS solution = new UCS();
            solved = solution.solve(loadedState);
            if (solved) {
                System.out.println("Solution found!");
                System.out.println("Nodes explored: " + solution.nodesExplored);
                System.out.printf("Runtime: %.3f ms%n", solution.getRuntime());
            } else {
                System.out.println("No solution found.");
            }
        } else if (algChoice == 2) {
            // Ask for heuristic selection if using GBFS
            System.out.println("\nSelect heuristic option for GBFS:");
            System.out.println("0: Exit distance heuristic only");
            System.out.println("1: Blockers count heuristic only");
            System.out.println("2: Both heuristics");
            System.out.print("Enter your heuristic choice (0, 1, or 2): ");
            int heuristicChoice = scan.nextInt();
            scan.nextLine(); // Clear the buffer

            GBFS solution = new GBFS(heuristicChoice);
            solved = solution.solve(loadedState);
            if (solved) {
                System.out.println("Solution found!");
                System.out.println("Nodes explored: " + solution.nodesExplored);
                System.out.printf("Runtime: %.3f ms%n", solution.getRuntime());
            } else {
                System.out.println("No solution found.");
            }
        } else if (algChoice == 3) {
            // Ask for heuristic selection if using AStar
            System.out.println("\nSelect heuristic option for AStar:");
            System.out.println("0: Exit distance heuristic only");
            System.out.println("1: Blockers count heuristic only");
            System.out.println("2: Both heuristics");
            System.out.print("Enter your heuristic choice (0, 1, or 2): ");
            int heuristicChoice = scan.nextInt();
            scan.nextLine(); // Clear the buffer

            AStar solution = new AStar(heuristicChoice);
            solved = solution.solve(loadedState);
            if (solved) {
                System.out.println("Solution found!");
                System.out.println("Nodes explored: " + solution.nodesExplored);
                System.out.printf("Runtime: %.3f ms%n", solution.getRuntime());
            } else {
                System.out.println("No solution found.");
            }
        } else {
            System.out.println("Invalid selection. Exiting.");
        }

        scan.close();
    }
}