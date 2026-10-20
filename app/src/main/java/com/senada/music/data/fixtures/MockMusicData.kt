package com.senada.music.data.fixtures

import com.senada.music.data.model.Folder
import com.senada.music.data.model.Song

/**
 * Fixture lagu deterministik sesuai handoff dokumen desain (rU68D.3).
 * Digunakan untuk Compose Preview, screenshot verifikasi visual, dan pengujian unit.
 */
object MockMusicData {
    val sampleSongs: List<Song> = listOf(
        Song(
            id = 1L,
            title = "Arah Pulang",
            artist = "Awan Sore",
            durationMs = (3 * 60 + 46) * 1000L,
            dateAdded = 1700000004L,
            contentUriString = "content://media/external/audio/media/1",
            folderName = "Music",
            bucketId = "music_bucket",
            volumePath = "Penyimpanan internal / Music"
        ),
        Song(
            id = 2L,
            title = "Di Antara Rerumputan dan Langit",
            artist = "Nara",
            durationMs = (4 * 60 + 8) * 1000L,
            dateAdded = 1700000002L,
            contentUriString = "content://media/external/audio/media/2",
            folderName = "Download",
            bucketId = "download_bucket",
            volumePath = "Penyimpanan internal / Download"
        ),
        Song(
            id = 3L,
            title = "Hujan Pelan",
            artist = "Laras Senja",
            durationMs = (3 * 60 + 32) * 1000L,
            dateAdded = 1700000006L,
            contentUriString = "content://media/external/audio/media/3",
            folderName = "Music",
            bucketId = "music_bucket",
            volumePath = "Penyimpanan internal / Music"
        ),
        Song(
            id = 4L,
            title = "Langkah Kecil",
            artist = "Penyanyi tidak diketahui",
            durationMs = (2 * 60 + 54) * 1000L,
            dateAdded = 1700000003L,
            contentUriString = "content://media/external/audio/media/4",
            folderName = "Download",
            bucketId = "download_bucket",
            volumePath = "Penyimpanan internal / Download"
        ),
        Song(
            id = 5L,
            title = "Ruang Teduh",
            artist = "Nara",
            durationMs = (5 * 60 + 1) * 1000L,
            dateAdded = 1700000007L,
            contentUriString = "content://media/external/audio/media/5",
            folderName = "Music",
            bucketId = "music_bucket",
            volumePath = "Penyimpanan internal / Music"
        ),
        Song(
            id = 6L,
            title = "Senja di Jendela",
            artist = "Laras Senja",
            durationMs = (4 * 60 + 12) * 1000L,
            dateAdded = 1700000008L, // Terakhir ditambahkan (paling baru)
            contentUriString = "content://media/external/audio/media/6",
            folderName = "Music",
            bucketId = "music_bucket",
            volumePath = "Penyimpanan internal / Music"
        ),
        Song(
            id = 7L,
            title = "Sisa Cahaya",
            artist = "Awan Sore",
            durationMs = (3 * 60 + 18) * 1000L,
            dateAdded = 1700000005L,
            contentUriString = "content://media/external/audio/media/7",
            folderName = "Download",
            bucketId = "download_bucket",
            volumePath = "Penyimpanan internal / Download"
        ),
        Song(
            id = 8L,
            title = "Usai Hujan",
            artist = "Nara",
            durationMs = (4 * 60 + 24) * 1000L,
            dateAdded = 1700000001L,
            contentUriString = "content://media/external/audio/media/8",
            folderName = "Music",
            bucketId = "music_bucket",
            volumePath = "Penyimpanan internal / Music"
        )
    )

    val sampleFolders: List<Folder> = listOf(
        Folder(
            bucketId = "music_bucket",
            name = "Music",
            volumePath = "Penyimpanan internal / Music",
            songs = sampleSongs.filter { it.folderName == "Music" }
        ),
        Folder(
            bucketId = "download_bucket",
            name = "Download",
            volumePath = "Penyimpanan internal / Download",
            songs = sampleSongs.filter { it.folderName == "Download" }
        )
    )
}
