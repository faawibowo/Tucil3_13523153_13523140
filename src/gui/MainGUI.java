package gui;

import FileReader.Parser;
import Algorithm.GBFS;
import Algorithm.UCS;

import javax.swing.*;
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
    private JComboBox<String> algCombo;
    private JComboBox<String> heuristicCombo;
    private JPanel boardPanel;
    private JLabel statusLabel;
    private JTextArea logArea;
    private JScrollPane logScrollPane;

    private List<GameObject.State> pathStates;
    private int currentStep;
    private javax.swing.Timer animator;
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

        String[] algs = { "UCS", "GBFS" };
        algCombo = new JComboBox<>(algs);
        algCombo.setSelectedIndex(0);

        String[] heuristics = { "Exit distance only", "Blockers count only", "Both heuristics" };
        heuristicCombo = new JComboBox<>(heuristics);
        heuristicCombo.setEnabled(false);
        algCombo.addActionListener(e -> {
            boolean useGBFS = "GBFS".equals(algCombo.getSelectedItem());
            heuristicCombo.setEnabled(useGBFS);
        });

        statusLabel = new JLabel("Load a puzzle file to begin");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(100, 100, 100));

        boardPanel = new JPanel(null);
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
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    private void layoutComponents() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        controlPanel.setBackground(BACKGROUND_COLOR);
        controlPanel.add(new JLabel("Algorithm:"));
        controlPanel.add(algCombo);
        controlPanel.add(new JLabel("Heuristic:"));
        controlPanel.add(heuristicCombo);
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
                String path = chooser.getSelectedFile().getAbsolutePath();
                String name = chooser.getSelectedFile().getName();
                initialState = Parser.loadState(path);
                assignPieceColors(initialState);
                renderBoard(initialState.buildBoard(), '\0');
                logArea.setText("Loaded " + name + "\n");
                solveButton.setEnabled(true);
                resetButton.setEnabled(false);
                updateStatus("Puzzle loaded successfully. Click 'Solve'.");
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                updateStatus("Error loading puzzle");
            }
        }
    }

    private void onSolve() {
        solveButton.setEnabled(false);
        resetButton.setEnabled(true);
        updateStatus("Solving puzzle...");

        new SwingWorker<Void, String>() {
            private List<GameObject.State> solution;
            private boolean found;
            private int nodesExplored;
            private double runtime;

            @Override
            protected Void doInBackground() {
                String algo = (String) algCombo.getSelectedItem();
                publish("Solving with " + algo + "...");
                if ("UCS".equals(algo)) {
                    UCS solver = new UCS();
                    found = solver.solve(initialState);
                    nodesExplored = solver.nodesExplored;
                    runtime = solver.getRuntime();
                    solution = buildPath(solver.finalState);
                } else {
                    int choice = heuristicCombo.getSelectedIndex();
                    GBFS solver = new GBFS(choice);
                    found = solver.solve(initialState);
                    nodesExplored = solver.nodesExplored;
                    runtime = solver.getRuntime();
                    solution = buildPath(solver.finalState);
                }
                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                chunks.forEach(line -> logArea.append(line + "\n"));
            }

            @Override
            protected void done() {
                logArea.append("Found: " + found + " | Nodes: " + nodesExplored + "\n");
                logArea.append(String.format("Runtime: %.3f ms\n\n", runtime));
                if (found && solution != null) {
                    pathStates = solution;
                    startAnimation();
                } else {
                    logArea.append("No solution found.\n");
                    updateStatus("No solution");
                    solveButton.setEnabled(true);
                }
            }
        }.execute();
    }

    private List<GameObject.State> buildPath(GameObject.State end) {
        LinkedList<GameObject.State> list = new LinkedList<>();
        GameObject.State cur = end;
        while (cur != null) {
            list.addFirst(cur);
            cur = cur.parent;
        }
        return list;
    }

    private void startAnimation() {
        currentStep = 0;
        animator = new javax.swing.Timer(animationSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < pathStates.size()) {
                    State s = pathStates.get(currentStep);
                    renderBoard(s.buildBoard(), s.carId);
                    logArea.append(describeMove(s, currentStep) + "\n");
                    updateStatus("Step " + currentStep + " of " + (pathStates.size() - 1));
                    currentStep++;
                    logArea.setCaretPosition(logArea.getDocument().getLength());
                } else {
                    animator.stop();
                    logArea.append("Solution complete! " + (pathStates.size() - 1) + " moves total.\n");
                    updateStatus("Complete");
                    solveButton.setEnabled(true);
                }
            }
        });
        animator.setInitialDelay(500);
        animator.start();
    }

    private String describeMove(State s, int step) {
        if (step == 0)
            return "Initial state";
        StringBuilder sb = new StringBuilder();
        sb.append("Step ").append(step).append(": Move car ").append(s.carId);
        switch (s.direction) {
            case 0:
                sb.append(" ← left");
                break;
            case 1:
                sb.append(" → right");
                break;
            case 2:
                sb.append(" ↑ up");
                break;
            case 3:
                sb.append(" ↓ down");
                break;
        }
        return sb.toString();
    }

    private void renderBoard(char[][] board, char highlightId) {
        boardPanel.removeAll();
        int h = board.length, w = board[0].length;
        Car mainCar = initialState.cars.get('P');
        boolean horiz = mainCar.isHorizontal;
        int exitRow = State.exitRow, exitCol = State.exitCol;

        int newCols = horiz ? w + 1 : w;
        int newRows = horiz ? h : h + 1;
        int shiftRight = horiz && exitCol == 0 ? 1 : 0;
        int shiftDown = !horiz && exitRow == 0 ? 1 : 0;

        boardPanel.setPreferredSize(new Dimension(
                newCols * (CELL_SIZE + CELL_GAP) + CELL_GAP + 20,
                newRows * (CELL_SIZE + CELL_GAP) + CELL_GAP + 20));

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
            Color carColor = pieceColors.getOrDefault(cellChar, Color.LIGHT_GRAY);
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
                JLabel target = new JLabel("♦");
                target.setFont(new Font("Segoe UI", Font.BOLD, 9));
                target.setForeground(getLabelColor(carColor));
                target.setHorizontalAlignment(SwingConstants.RIGHT);
                target.setVerticalAlignment(SwingConstants.TOP);
                cell.add(target, BorderLayout.NORTH);
            }
        }
        return cell;
    }

    private Color getLabelColor(Color bg) {
        double lum = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255;
        return lum > 0.6 ? Color.BLACK : Color.WHITE;
    }

    private void assignPieceColors(State state) {
        pieceColors.clear();
        RandomColorGenerator gen = new RandomColorGenerator();
        for (Character id : state.cars.keySet()) {
            pieceColors.put(id, gen.nextColor());
        }
    }

    private void updateStatus(String msg) {
        statusLabel.setText(msg);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(MainGUI::new);
    }
}

class RandomColorGenerator {
    private float hue = 0, sat = 0.8f, bri = 0.95f;

    public Color nextColor() {
        Color c = Color.getHSBColor(hue, sat, bri);
        hue = (hue + 0.618033988749895f) % 1.0f;
        return c;
    }
}
