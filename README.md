# Ice Sliding Puzzle Solver (Java)

Program CLI untuk tugas Tucil 3 IF2211 — mencari jalur permainan Ice Sliding Puzzle dengan Uniform Cost Search (UCS), Greedy Best-First Search (GBFS), dan A\*, dengan heuristik H1, H2, dan H3.

# Requirement

- JDK 17+

# Cara mengcompile di Linux

```bash
chmod +x bin/javacompile.sh bin/run-java.sh
./bin/javacompile.sh
```

# Cara menjalankan di Linux

```bash
./bin/run-java.sh
```

# Penggunaan

1. Masukkan file `.txt` (cth: `test/simple.txt`).
2. Pilih algoritma: `UCS`, `GBFS`, atau `A*`.
3. Untuk GBFS atau A\*, pilih heuristik: (`H1`, `H2`, `H3`).
4. Opsi playback: banyak terminal tidak bisa mengirim tombol panah seperti di spesifikasi referensi PDF; pemetaan dipakai:
   - `a` atau `>` — satu langkah maju
   - `d` atau `<` — satu langkah mundur
   - `w` — lompat ke step (prompt angka); setara lompat ke step X seperti contoh tugas
   - `q` — keluar
5. Opsi penyimpanan solusi atau log iterasi ke berkas `.txt`.

Program memvalidasi dimensi, simbol, keberadaan tepat satu `Z` dan satu `O`, serta urutan digit tanpa celah.

# File Input

- `test/simple.txt` — papan kecil tanpa digit terurut.
- `test/with_digits.txt` — contoh dengan digit yang harus dikunjungi berurutan.

# Author

- 13524138 / Ahmad Rinofaros Muchtar

# Algoritma yang digunakan

- UCS: antrian prioritas berdasarkan cost kumulatif `g(n)`.
- GBFS: prioritas berdasarkan heuristik `h(n)` saja.
- A\*: prioritas berdasarkan `f(n) = g(n) + h(n)`.
