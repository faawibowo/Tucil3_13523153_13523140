package FileReader;

import GameObject.Car;
import GameObject.State;

import java.io.*;
import java.util.*;

public class Parser {
    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    public static State loadState(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.isDirectory()) {
            file = new File("test" + File.separator + filename);
            if (!file.exists() || file.isDirectory()) {
                throw new FileNotFoundException("File not found: " + filename);
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(file));

        String line = reader.readLine();
        if (line == null)
            throw new IllegalArgumentException("File kosong");
        String[] dims = line.trim().split("\\s+");
        if (dims.length != 2)
            throw new IllegalArgumentException("Baris pertama harus dua angka: height width");
        int height = Integer.parseInt(dims[0]);
        int width = Integer.parseInt(dims[1]);

        line = reader.readLine();
        if (line == null)
            throw new IllegalArgumentException("File kurang baris untuk piece count");
        int pieceCount = Integer.parseInt(line.trim());
        if (pieceCount < 1)
            throw new IllegalArgumentException("Jumlah pieces harus >= 1");

        List<String> lines = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        if (lines.size() < height)
            throw new IllegalArgumentException(
                    "Baris papan kurang: diharapkan " + height + ", tapi file punya " + lines.size());

        int boardStart = -1;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).stripLeading().length() >= width) {
                boardStart = i;
                break;
            }
        }
        if (boardStart < 0 || boardStart + height > lines.size())
            throw new IllegalArgumentException("Tidak ditemukan blok papan yang valid");

        char[][] rawBoard = new char[height][width];
        boolean[][] occupied = new boolean[height][width];
        int exitRow = -1, exitCol = -1;
        boolean foundP = false;

        for (int r = 0; r < height; r++) {
            String rawLine = lines.get(boardStart + r);
            String trimmed = rawLine.stripLeading();
            int tlen = trimmed.length();
            String rowData;
            // Case: exact width
            if (tlen == width) {
                rowData = trimmed;
            }
            // Case: K at left outside
            else if (tlen == width + 1 && trimmed.charAt(0) == 'K') {
                exitRow = r;
                exitCol = 0;
                rowData = trimmed.substring(1);
            }
            // Case: K at right outside
            else if (tlen == width + 1 && trimmed.charAt(tlen - 1) == 'K') {
                exitRow = r;
                exitCol = width - 1;
                rowData = trimmed.substring(0, width);
            } else {
                throw new IllegalArgumentException(
                        "Baris ke-" + r + " harus panjang " + width + " atau " + (width + 1)
                                + ", tapi panjangnya " + tlen);
            }

            // Fill board row
            for (int c = 0; c < width; c++) {
                char ch = rowData.charAt(c);
                rawBoard[r][c] = ch;
                if (ch == 'P')
                    foundP = true;
                if (ch != '.') {
                    if (occupied[r][c])
                        throw new IllegalArgumentException(
                                "Overlap pada piece di posisi (" + r + "," + c + ")");
                    occupied[r][c] = true;
                }
            }
        }

        int indentBoard = lines.get(boardStart).length() - lines.get(boardStart).stripLeading().length();
        for (int i = 0; i < lines.size(); i++) {
            String rawLine2 = lines.get(i);
            for (int k = 0; k < rawLine2.length(); k++) {
                if (rawLine2.charAt(k) == 'K') {
                    if (i < boardStart) {
                        exitRow = 0;
                    }
                    else if (i >= boardStart + height) {
                        exitRow = height - 1;
                    }
                    int relC = clamp(k - indentBoard, 0, width - 1);
                    exitCol = relC;
                }
            }
        }

        if (exitRow < 0)
            throw new IllegalArgumentException("Tidak ditemukan exit 'K' di file");

        if (!foundP)
            throw new IllegalArgumentException("Harus ada main car 'P' di papan");

        Map<Character, List<int[]>> positions = new HashMap<>();
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                char ch = rawBoard[r][c];
                if (ch != '.') {
                    positions.computeIfAbsent(ch, k -> new ArrayList<>()).add(new int[] { r, c });
                }
            }
        }
        int distinct = positions.size() - (positions.containsKey('P') ? 1 : 0);
        if (distinct != pieceCount)
            throw new IllegalArgumentException(
                    "Jumlah pieces tidak sesuai, diharapkan " + pieceCount + ", tapi ditemukan " + distinct);

        State.initBoard(height, width, exitRow, exitCol);
        State state = new State(null, ' ', 0, 0);
        for (Map.Entry<Character, List<int[]>> e : positions.entrySet()) {
            char id = e.getKey();
            List<int[]> pts = e.getValue();
            int minR = pts.stream().mapToInt(p -> p[0]).min().getAsInt();
            int minC = pts.stream().mapToInt(p -> p[1]).min().getAsInt();
            boolean horiz = pts.stream().allMatch(p -> p[0] == minR);
            int len = pts.size();
            boolean main = (id == 'P');
            Car car = new Car(minR, minC, horiz, len, id, main);
            state.cars.put(id, car);
        }
        return state;
    }
}
