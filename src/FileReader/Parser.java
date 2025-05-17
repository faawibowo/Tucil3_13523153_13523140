package FileReader;

import GameObject.Car;
import GameObject.State;

import java.io.*;
import java.util.*;

public class Parser {
    /** Clamp value into the inclusive range [min..max] */
    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    /**
     * Load State from file with validation:
     * - First line: height width
     * - Second line: piece count (excluding main car 'P')
     * - Next lines: board must have height rows of at least width characters
     * - Lines before/after board can contain 'K' (exit)
     * - Must contain exactly one exit 'K' and at least one main car 'P'
     * - No overlapping pieces
     */
    public static State loadState(String filename) throws IOException {
        String path = "test\\" + filename;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            //Read dimensions
            String[] dims = reader.readLine().trim().split("\\s+");
            if (dims.length != 2) {
                throw new IllegalArgumentException("Baris pertama harus dua angka: height width");
            }
            int height = Integer.parseInt(dims[0]);
            int width = Integer.parseInt(dims[1]);

            //Read piece count
            String pcLine = reader.readLine();
            if (pcLine == null) {
                throw new IllegalArgumentException("File kurang baris untuk piece count");
            }
            int pieceCount = Integer.parseInt(pcLine.trim());
            if (pieceCount < 1) {
                throw new IllegalArgumentException("Jumlah pieces harus >= 1");
            }

            List<String> lines = new ArrayList<>();
            String raw;
            while ((raw = reader.readLine()) != null) {
                lines.add(raw);
            }
            if (lines.size() < height) {
                throw new IllegalArgumentException(
                        "Baris papan kurang: diharapkan " + height + ", tapi file hanya punya " + lines.size());
            }

            int boardStart = -1;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).stripLeading().length() >= width) {
                    boardStart = i;
                    break;
                }
            }
            if (boardStart < 0 || boardStart + height > lines.size()) {
                throw new IllegalArgumentException("Tidak ditemukan blok papan yang valid");
            }

            char[][] rawBoard = new char[height][width];
            boolean[][] occupied = new boolean[height][width];
            for (int i = 0; i < height; i++) {
                Arrays.fill(rawBoard[i], '.');
            }

            int exitRow = -1, exitCol = -1;
            boolean foundP = false;

            //Parse board rows
            for (int r = 0; r < height; r++) {
                String rawLine = lines.get(boardStart + r);
                String trimmed = rawLine.stripLeading();
                if (trimmed.length() < width) {
                    throw new IllegalArgumentException(
                            "Baris ke-" + r + " harus panjang minimal " + width +
                                    ", tapi panjangnya " + trimmed.length());
                }
                String rowData = trimmed.substring(0, width);
                for (int c = 0; c < width; c++) {
                    char ch = rowData.charAt(c);
                    if (ch != '.' && ch != 'K' && !Character.isLetter(ch)) {
                        throw new IllegalArgumentException(
                                "Karakter tidak valid '" + ch + "' di (" + r + "," + c + ")");
                    }
                    if (ch == 'K') {
                        exitRow = r;
                        exitCol = c;
                        continue;
                    }
                    rawBoard[r][c] = ch;
                    if (ch == 'P') {
                        foundP = true;
                    }
                    if (ch != '.') {
                        if (occupied[r][c]) {
                            throw new IllegalArgumentException(
                                    "Overlap pada piece di posisi (" + r + "," + c + ")");
                        }
                        occupied[r][c] = true;
                    }
                }
            }

            // Locate any 'K' outside board rows
            String firstBoardRaw = lines.get(boardStart);
            int boardIndent = firstBoardRaw.length() - firstBoardRaw.stripLeading().length();
            for (int i = 0; i < lines.size(); i++) {
                String rawLine2 = lines.get(i);
                for (int j = 0; j < rawLine2.length(); j++) {
                    if (rawLine2.charAt(j) == 'K') {
                        int relRow = clamp(i - boardStart, 0, height - 1);
                        int relCol = clamp(j - boardIndent, 0, width - 1);
                        exitRow = relRow;
                        exitCol = relCol;
                    }
                }
            }

            //Validate exit and main car
            if (exitRow < 0) {
                throw new IllegalArgumentException("Tidak ditemukan exit 'K' di file");
            }
            if (!foundP) {
                throw new IllegalArgumentException("Harus ada main car 'P' di papan");
            }

            //Collect pieces
            Map<Character, List<int[]>> positions = new HashMap<>();
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    char ch = rawBoard[r][c];
                    if (ch != '.') {
                        positions.computeIfAbsent(ch, k -> new ArrayList<>())
                                .add(new int[] { r, c });
                    }
                }
            }

            int distinctPieces = positions.size() - (positions.containsKey('P') ? 1 : 0);
            if (distinctPieces != pieceCount) {
                throw new IllegalArgumentException(
                        "Jumlah pieces tidak sesuai: diharapkan " + pieceCount +
                                ", tapi ditemukan " + distinctPieces);
            }

            // Init State
            State.initBoard(height, width, exitRow, exitCol);
            State state = new State(null, ' ', 0, 0);


            for (Map.Entry<Character, List<int[]>> e : positions.entrySet()) {
                char id = e.getKey();
                List<int[]> pts = e.getValue();
                int minR = pts.stream().mapToInt(p -> p[0]).min().getAsInt();
                int minC = pts.stream().mapToInt(p -> p[1]).min().getAsInt();
                boolean horizontal = pts.stream().allMatch(p -> p[0] == minR);
                int length = pts.size();
                boolean isMain = (id == 'P');

                Car car = new Car(minR, minC, horizontal, length, id, isMain);
                state.cars.put(id, car);
            }

            return state;
        }
    }
}
