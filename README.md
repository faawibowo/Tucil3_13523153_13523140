# 🚗 Rush Hour Puzzle Solver

Program **Rush Hour Puzzle Solver** merupakan aplikasi Java yang dirancang untuk menyelesaikan permainan puzzle Rush Hour secara otomatis menggunakan algoritma pencarian jalur seperti **Uniform Cost Search (UCS), Greedy Best First Search (GBFS), dan A***. Program ini memvisualisasikan proses penyelesaian secara animatif melalui antarmuka GUI berbasis Java Swing, serta menyediakan alternatif antarmuka berbasis teks (CLI). Pengguna dapat memuat file konfigurasi puzzle, memilih algoritma dan heuristik, lalu melihat solusi yang dihasilkan langkah demi langkah dengan informasi jumlah node yang dieksplorasi serta waktu eksekusi.

Program menyediakan dua antarmuka:
- **Graphical User Interface (GUI)** berbasis Java Swing
- **Command-Line Interface (CLI)** melalui `Main.java`

---

## ✅ Requirements

- Java Development Kit (JDK) versi **11 atau lebih tinggi**
- Tidak memerlukan instalasi eksternal tambahan

---

## ⚙️ Kompilasi Program

### Untuk GUI:
```bash
javac -d bin ^
src\FileReader\Parser.java ^
src\GameObject\State.java src\GameObject\Car.java ^
src\Algorithm\UCS.java src\Algorithm\GBFS.java src\Algorithm\AStar.java ^
src\gui\MainGUI.java src\util\ColorUtil.java
```

### Untuk CLI:
```bash
javac -d bin -cp src src\Main.java
```

---

## ▶️ Menjalankan Program

### Jalankan GUI:
```bash
java -cp bin gui.MainGUI
```

### Jalankan CLI:
```bash
java -cp bin Main
```

---

## 📦 Menjalankan via File `.jar` (GUI)

1. Jalankan program:

   ```bash
   java -jar src/RushHourSolver.jar
   ```

---

## 🧩 Format Input Puzzle

- Baris 1: `tinggi lebar`
- Baris 2: `jumlah piece` (tidak termasuk mobil utama `P`)
- Baris 3 dst: peta puzzle menggunakan karakter mobil (`A`-`Z`, `P` untuk player, `K` untuk exit, `.` untuk kosong)

Contoh:
```
6 6
11
AAB..F
..BCDF
KGPPCD
GHJIII
GHJ...
LLJMM.
```

---

## 👨‍💻 Author

| Nama                        | NIM        |
|-----------------------------|------------|
| Muhammad Farrel Wibowo     | 13523153   |
| Mahesa Fadhillah Andre      | 13523140   |

- **Mata Kuliah**: Strategi Algoritma
- **Institut Teknologi Bandung – 2024**
