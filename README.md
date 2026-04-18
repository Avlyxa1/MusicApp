# MusicApp

MusicApp adalah aplikasi pemutar musik sederhana berbasis Android yang memungkinkan pengguna untuk memutar, menjeda, dan mengelola daftar lagu dengan tampilan antarmuka yang intuitif.

## Fitur

- Menampilkan daftar lagu menggunakan RecyclerView
- Memutar musik (play)
- Menjeda musik (pause)
- Lanjut ke lagu berikutnya dan sebelumnya
- Menampilkan judul lagu, artis, dan thumbnail
- Player screen dengan informasi lagu yang sedang diputar
- Navigasi antara daftar lagu dan player
- Kontrol pemutaran audio dasar

## Teknologi yang Digunakan

- Kotlin
- Android Studio
- RecyclerView
- MediaPlayer
- ViewModel (opsional untuk manajemen data)
- Fragment

## Struktur Aplikasi

- MainActivity: Menampilkan daftar lagu
- PlayerFragment: Menampilkan pemutar musik
- SongAdapter: Adapter untuk RecyclerView
- Song Model: Representasi data lagu
- MusicViewModel: Mengelola data lagu yang dipilih

## Cara Menjalankan Project

1. Clone repository ini:
