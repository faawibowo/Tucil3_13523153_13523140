package gui;

import FileReader.Parser;
import Algorithm.UCS;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import GameObject.Car;
import GameObject.State;

public class MainGUI extends JFrame {
    private static final Color BACKGROUND_COLOR = new Color(240, 240, 245);
    private static final Color BOARD_COLOR = new Color(220, 220, 225);
    private static final Color EXIT_COLOR = new Color(255, 200, 200);
    private static final Color EXIT_BORDER_COLOR = new Color(200, 50, 50);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font CELL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LOG_FONT = new Font("Consolas", Font.PLAIN, 13);
    private static final int CELL_SIZE = 60;
    private static final int CELL_GAP = 4;
    private static final int ANIMATION_DELAY = 800;

    private State initialState;
    private JButton loadButton, solveButton, resetButton, speedButton;
    private JPanel boardPanel, controlPanel;
    private JTextArea logArea;
    private JScrollPane logScrollPane;
    private JLabel statusLabel;

    private List<GameObject.State> pathStates;
    private int currentStep;
    private Timer animator;
    private int animationSpeed = ANIMATION_DELAY;

    private Map<Character, Color> pieceColors = new HashMap<>();

    public MainGUI() {
        super("Rush Hour Puzzle Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);

        initComponents();
        layoutComponents();
        setupEventHandlers();

        pack();
        setMinimumSize(new Dimension(600, 700));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initComponents() {
        loadButton = createStyledButton("Load Puzzle", new Color(100, 150, 230));
        solveButton = createStyledButton("Solve", new Color(100, 200, 130));
        resetButton = createStyledButton("Reset", new Color(230, 150, 100));
        speedButton = createStyledButton("Speed: Normal", new Color(150, 150, 230));

        solveButton.setEnabled(false);
        resetButton.setEnabled(false);

        statusLabel = new JLabel("Load a puzzle file to begin");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        boardPanel = new JPanel();
        boardPanel.setLayout(null); 
        boardPanel.setBackground(BOARD_COLOR);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(LOG_FONT);
        logArea.setBackground(new Color(250, 250, 250));
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 16, 8, 16));
        return button;
    }

    private void layoutComponents() {
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.add(loadButton);
        controlPanel.add(solveButton);
        controlPanel.add(resetButton);
        controlPanel.add(speedButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        topPanel.add(statusLabel, BorderLayout.SOUTH);
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        add(topPanel, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);

        boardPanel.setPreferredSize(new Dimension(400, 400));
        logScrollPane.setPreferredSize(new Dimension(400, 150));
    }

    private void setupEventHandlers() {
        loadButton.addActionListener(e -> onLoad());
        solveButton.addActionListener(e -> onSolve());
        resetButton.addActionListener(e -> resetAnimation());
        speedButton.addActionListener(e -> toggleSpeed());
    }

    private void toggleSpeed() {
        if (animationSpeed == ANIMATION_DELAY) {
            animationSpeed = ANIMATION_DELAY / 2;
            speedButton.setText("Speed: Fast");
        } else if (animationSpeed == ANIMATION_DELAY / 2) {
            animationSpeed = ANIMATION_DELAY / 4;
            speedButton.setText("Speed: Very Fast");
        } else {
            animationSpeed = ANIMATION_DELAY;
            speedButton.setText("Speed: Normal");
        }

        if (animator != null && animator.isRunning()) {
            animator.setDelay(animationSpeed);
        }
    }

    private void resetAnimation() {
        if (animator != null && animator.isRunning()) {
            animator.stop();
        }

        if (pathStates != null && !pathStates.isEmpty()) {
            currentStep = 0;
            renderBoard(pathStates.get(0).buildBoard(), '\0');
            logArea.append("Animation reset to initial state.\n");
            solveButton.setEnabled(true);
            updateStatus("Ready to solve or load a new puzzle");
        }
    }

    private void onLoad() {
        JFileChooser chooser = new JFileChooser("test");
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String name = chooser.getSelectedFile().getName();
                initialState = Parser.loadState(name);
                assignPieceColors(initialState);
                renderBoard(initialState.buildBoard(), '\0');
                logArea.setText("Loaded " + name + "\n");
                solveButton.setEnabled(true);
                resetButton.setEnabled(false);
                updateStatus("Puzzle loaded successfully. Click 'Solve' to find a solution.");
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                updateStatus("Error loading puzzle");
            }
        }
    }

    private void assignPieceColors(State state) {
        pieceColors.clear();
        RandomColorGenerator gen = new RandomColorGenerator();
        for (Character id : state.cars.keySet()) {
            pieceColors.put(id, gen.nextColor());
        }
    }

    private void onSolve() {
        solveButton.setEnabled(false);
        resetButton.setEnabled(true);
        updateStatus("Solving puzzle...");


        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private java.util.List<GameObject.State> solution;
            private boolean found;
            private int nodesExplored;
            private double runtime;

            @Override
            protected Void doInBackground() {
                publish("Solving with UCS algorithm...");
                UCS ucs = new UCS();
                found = ucs.solve(initialState);
                nodesExplored = ucs.nodesExplored;
                runtime = ucs.getRuntime();

                if (found) {
                    GameObject.State node = ucs.finalState;
                    java.util.LinkedList<GameObject.State> list = new java.util.LinkedList<>();
                    while (node != null) {
                        list.addFirst(node);
                        node = node.parent;
                    }
                    solution = list;
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String chunk : chunks) {
                    logArea.append(chunk + "\n");
                }
            }

            @Override
            protected void done() {
                logArea.append("Found solution: " + found + "\n");
                logArea.append("Nodes explored: " + nodesExplored + "\n");
                logArea.append(String.format("Runtime: %.3f ms\n\n", runtime));

                if (found && solution != null) {
                    pathStates = solution;
                    currentStep = 0;

                    animator = new Timer(animationSpeed, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (currentStep < pathStates.size()) {
                                GameObject.State s = pathStates.get(currentStep);
                                renderBoard(s.buildBoard(), s.carId);
                                logArea.append(describeMove(s, currentStep) + "\n");
                                updateStatus("Step " + currentStep + " of " + (pathStates.size() - 1));
                                currentStep++;

                                logArea.setCaretPosition(logArea.getDocument().getLength());
                            } else {
                                animator.stop();
                                logArea.append("Solution complete! " + (pathStates.size() - 1) + " moves total.\n");
                                updateStatus("Solution complete - " + (pathStates.size() - 1) + " moves");
                                solveButton.setEnabled(true);
                            }
                        }
                    });
                    animator.setInitialDelay(500);
                    animator.start();
                } else {
                    logArea.append("No solution found.\n");
                    updateStatus("No solution found");
                    solveButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private String describeMove(State s, int step) {
        if (step == 0)
            return "Initial state";

        StringBuilder sb = new StringBuilder();
        sb.append("Step ").append(step).append(": ");

        switch (s.direction) {
            case 0:
                sb.append("Move car ").append(s.carId).append(" ← left");
                break;
            case 1:
                sb.append("Move car ").append(s.carId).append(" → right");
                break;
            case 2:
                sb.append("Move car ").append(s.carId).append(" ↑ up");
                break;
            case 3:
                sb.append("Move car ").append(s.carId).append(" ↓ down");
                break;
        }

        return sb.toString();
    }

    private void renderBoard(char[][] board, char highlightId) {
        boardPanel.removeAll();

        int h = board.length;
        int w = board[0].length;
        Car mainCar = initialState.cars.get('P');
        boolean horiz = mainCar.isHorizontal;
    
        int exitRow = State.exitRow;
        int exitCol = State.exitCol;

    
        int newCols = horiz ? w + 1 : w;
        int newRows = horiz ? h : h + 1;

        int shiftRight = horiz && exitCol == 0 ? 1 : 0;
        int shiftDown = !horiz && exitRow == 0 ? 1 : 0;

        int panelW = newCols * (CELL_SIZE + CELL_GAP) + CELL_GAP;
        int panelH = newRows * (CELL_SIZE + CELL_GAP) + CELL_GAP;
        boardPanel.setPreferredSize(new Dimension(panelW + 20, panelH + 20));

        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                int x = (j + shiftRight) * (CELL_SIZE + CELL_GAP) + CELL_GAP;
                int y = (i + shiftDown) * (CELL_SIZE + CELL_GAP) + CELL_GAP;
                JPanel cell = createBoardCell(board[i][j], highlightId);
                cell.setBounds(x, y, CELL_SIZE, CELL_SIZE);
                boardPanel.add(cell);
            }
        }

        int exitX, exitY;
        String arrow;
        if (horiz) {
            exitX = (exitCol + (exitCol == 0 ? 0 : 1)) * (CELL_SIZE + CELL_GAP) + CELL_GAP;
            exitY = exitRow * (CELL_SIZE + CELL_GAP) + CELL_GAP;
            arrow = exitCol == 0 ? "←" : "→";
        } else {
            exitX = exitCol * (CELL_SIZE + CELL_GAP) + CELL_GAP;
            exitY = (exitRow + (exitRow == 0 ? 0 : 1)) * (CELL_SIZE + CELL_GAP) + CELL_GAP;
            arrow = exitRow == 0 ? "↑" : "↓";
        }

        JPanel exitCell = new JPanel(new BorderLayout());
        exitCell.setBackground(EXIT_COLOR);
        exitCell.setBorder(BorderFactory.createLineBorder(EXIT_BORDER_COLOR, 3));
        exitCell.setBounds(exitX, exitY, CELL_SIZE, CELL_SIZE);
        JLabel lbl = new JLabel("EXIT " + arrow, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setForeground(EXIT_BORDER_COLOR);
        exitCell.add(lbl, BorderLayout.CENTER);

        boardPanel.add(exitCell);
        boardPanel.setComponentZOrder(exitCell, 0);

        boardPanel.revalidate();
        boardPanel.repaint();
        pack();
    }       

    private JPanel createBoardCell(char cellChar, char highlightId) {
        JPanel cell = new JPanel(new BorderLayout());
        cell.setOpaque(true);

        if (cellChar == '.') {
            cell.setBackground(BOARD_COLOR);
            cell.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        } else {
            Color carColor = pieceColors.containsKey(cellChar) ? pieceColors.get(cellChar) : Color.LIGHT_GRAY;

            cell.setBackground(carColor);
            if (cellChar == highlightId) {
                cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(50, 50, 50), 3),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            } else {
                cell.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(carColor.darker(), 2),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }


            JLabel carLabel = new JLabel(String.valueOf(cellChar));
            carLabel.setFont(CELL_FONT);
            carLabel.setForeground(getLabelColor(carColor));
            carLabel.setHorizontalAlignment(SwingConstants.CENTER);
            cell.add(carLabel, BorderLayout.CENTER);

            if (cellChar == 'P') {
                JLabel targetIndicator = new JLabel("♦");
                targetIndicator.setFont(new Font("Segoe UI", Font.BOLD, 9));
                targetIndicator.setForeground(getLabelColor(carColor));
                targetIndicator.setHorizontalAlignment(SwingConstants.RIGHT);
                targetIndicator.setVerticalAlignment(SwingConstants.TOP);
                cell.add(targetIndicator, BorderLayout.NORTH);
            }
        }

        return cell;
    }

    private Color getLabelColor(Color background) {
        double luminance = (0.299 * background.getRed() +
                0.587 * background.getGreen() +
                0.114 * background.getBlue()) / 255;

        return luminance > 0.6 ? Color.BLACK : Color.WHITE;
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(MainGUI::new);
    }
}

class RandomColorGenerator {
    private float hue = 0;
    private float saturation = 0.8f;
    private float brightness = 0.95f;

    public Color nextColor() {
        Color c = Color.getHSBColor(hue, saturation, brightness);


        hue += 0.618033988749895f;
        hue %= 1.0f;

        return c;
    }
}