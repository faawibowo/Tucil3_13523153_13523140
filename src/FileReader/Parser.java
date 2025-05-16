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
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        String[] dims = reader.readLine().trim().split("\\s+");
        if (dims.length != 2)
            throw new IllegalArgumentException("Baris pertama harus dua angka: height width");
        int height = Integer.parseInt(dims[0]);
        int width = Integer.parseInt(dims[1]);

        String pcLine = reader.readLine();
        if (pcLine == null)
            throw new IllegalArgumentException("File kurang baris untuk piece count");
        
        int countPieces = Integer.parseInt(pcLine.trim());
        if (countPieces < 1)
            throw new IllegalArgumentException("Jumlah pieces harus >= 1");
        System.out.println("height: " + height + ", width: " + width + ", countPieces: " + countPieces);

        char[][] rawBoard = new char[height][width];
        int exitRow = -1, exitCol = -1;
        List<String> lines = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            // raw line (untuk deteksi K di luar bounds)
            lines.add(line);
        }
        reader.close();

        // Validasi jumlah baris
        if (lines.size() < height)
            throw new IllegalArgumentException(
                    "Baris papan kurang: diharapkan " + height + ", tapi file hanya punya " + lines.size());

        for (int i = 0; i < height; i++) {
            String trimmed = lines.get(i).stripLeading();
            if (trimmed.length() < width) {
                throw new IllegalArgumentException(
                        "Panjang baris ke-" + i + " < width (" + width + ")");
            }
            // Ambil width karakter terakhir
            String rowData = trimmed.substring(trimmed.length() - width);
            for (int j = 0; j < width; j++) {
                char c = rowData.charAt(j);
                rawBoard[i][j] = c;
                if (c == 'K') {
                    // catat exit dalam bounds
                    exitRow = i;
                    exitCol = j;
                }
            }
        }

        for (int fileR = 0; fileR < lines.size(); fileR++) {
            String full = lines.get(fileR);
            for (int fileC = 0; fileC < full.length(); fileC++) {
                if (full.charAt(fileC) == 'K') {
                    // clamp relatif baris
                    int relR = clamp(fileR, 0, height - 1);
                    int relC = clamp(fileC - Math.max(0, full.stripLeading().length() - width),
                            0, width - 1);
                    exitRow = relR;
                    exitCol = relC;
                }
            }
        }

        // wajib ada exit dan P
        if (exitRow < 0)
            throw new IllegalArgumentException("Tidak ditemukan exit ‘K’ di file");
        boolean foundP = false;


        State.initBoard(height, width, exitRow, exitCol);
        State state = new State(null, ' ', 0, 0);


        Map<Character, List<int[]>> positions = new HashMap<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                char c = rawBoard[i][j];
                if (c != '.' && c != 'K') {
                    positions.computeIfAbsent(c, k -> new ArrayList<>())
                            .add(new int[] { i, j });
                    if (c == 'P')
                        foundP = true;
                }
            }
        }
        if (!foundP)
            throw new IllegalArgumentException("Harus ada main car 'P' di papan");

        int countCar = 0;
        for (Map.Entry<Character, List<int[]>> e : positions.entrySet()) {
            
            char id = e.getKey();
            List<int[]> pts = e.getValue();
            int minR = pts.stream().mapToInt(p -> p[0]).min().getAsInt();
            int minC = pts.stream().mapToInt(p -> p[1]).min().getAsInt();
            boolean horizontal = pts.stream().allMatch(p -> p[0] == minR);
            int length = pts.size();
            boolean isMain = (id == 'P');

            Car car = new Car(minR, minC, horizontal, length, id, isMain);
            countCar++;
            state.cars.put(id, car);
        }
        System.out.println("countCar: " + countCar);
        if (countCar-1 != countPieces)
            throw new IllegalArgumentException("Jumlah pieces tidak sesuai dengan yang diharapkan");

        return state;
    }
}
