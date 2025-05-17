package gui;

import FileReader.Parser;
import GameObject.State;
import Algorithm.UCS;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MainGUI extends JFrame{
    private State initialState;
    private JButton loadButton, solveButton;
    private JPanel boardPanel;
    private JTextArea logArea;

    private List<State> pathStates;
    private int currentStep;
    private Timer animator;

    private Map<Character, Color> pieceColors = new HashMap<>();
    private Color exitColor = Color.RED;

    public MainGUI() {
        super("Car Puzzle Solver");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel();
        loadButton = new JButton("Load TXT");
        solveButton = new JButton("Solve UCS");
        solveButton.setEnabled(false);
        top.add(loadButton);
        top.add(solveButton);
        add(top, BorderLayout.NORTH);

        boardPanel = new JPanel();
        add(boardPanel, BorderLayout.CENTER);

        logArea = new JTextArea(10, 40);
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(logArea), BorderLayout.SOUTH);

        loadButton.addActionListener(e -> onLoad());
        solveButton.addActionListener(e -> onSolve());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
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
            } catch (IOException | IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        logArea.append("Solving with UCS...\n");
        UCS ucs = new UCS();
        boolean found = ucs.solve(initialState);
        logArea.append("Found exit: " + found + "\n");
        logArea.append("Nodes explored: " + ucs.nodesExplored + "\n");
        logArea.append(String.format("Runtime: %.3f ms\n", ucs.getRuntime()));

        State node = ucs.finalState;
        LinkedList<State> list = new LinkedList<>();
        while (node != null) {
            list.addFirst(node);
            node = node.parent;
        }
        pathStates = list;
        currentStep = 0;

        animator = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentStep < pathStates.size()) {
                    State s = pathStates.get(currentStep);
                    renderBoard(s.buildBoard(), s.carId);
                    logArea.append(describeMove(s, currentStep) + "\n");
                    currentStep++;
                } else {
                    animator.stop();
                    logArea.append("Animation complete.\n");
                    solveButton.setEnabled(true);
                }
            }
        });
        animator.setInitialDelay(0);
        animator.start();
    }

    private String describeMove(State s, int step) {
        if (step == 0)
            return "Initial state";
        switch (s.direction) {
            case 0:
                return "Move car " + s.carId + " left";
            case 1:
                return "Move car " + s.carId + " right";
            case 2:
                return "Move car " + s.carId + " up";
            case 3:
                return "Move car " + s.carId + " down";
            default:
                return "";
        }
    }

    private void renderBoard(char[][] board, char highlightId) {
        boardPanel.removeAll();
        int h = board.length;
        int w = board[0].length;
        boolean horizExit = initialState.cars.get('P').isHorizontal;
        int rows = horizExit ? h : h + 1;
        int cols = horizExit ? w + 1 : w;
        boardPanel.setLayout(new GridLayout(rows, cols, 2, 2));

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JLabel lbl;
                char ch = '.';
                boolean isExit = false;
                if (i < h && j < w) {
                    ch = board[i][j];
                } else if (horizExit && j == w && i == State.exitRow) {
                    ch = 'K';
                    isExit = true;
                } else if (!horizExit && i == h && j == State.exitCol) {
                    ch = 'K';
                    isExit = true;
                }
                lbl = new JLabel(String.valueOf(ch));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                if (isExit)
                    lbl.setBackground(exitColor);
                else if (pieceColors.containsKey(ch))
                    lbl.setBackground(pieceColors.get(ch));
                else
                    lbl.setBackground(Color.LIGHT_GRAY);
                if (isExit)
                    lbl.setBorder(BorderFactory.createLineBorder(exitColor.darker(), 3));
                else if (ch == highlightId && ch != '.')
                    lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                else
                    lbl.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                boardPanel.add(lbl);
            }
        }
        boardPanel.revalidate();
        boardPanel.repaint();
        pack();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainGUI::new);
    }
}

class RandomColorGenerator {
    private float hue = 0;

    public Color nextColor() {
        Color c = Color.getHSBColor(hue, 0.5f, 0.95f);
        hue += 0.15f;
        if (hue > 1)
            hue -= 1;
        return c;
    }
}
