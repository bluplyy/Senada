package com.senada.music

import com.senada.music.data.fixtures.MockMusicData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicDataTest {

    @Test
    fun testHomeSortingByDateAddedDescending() {
        val songs = MockMusicData.sampleSongs
        val recentSongs = songs.sortedByDescending { it.dateAdded }

        assertEquals(8, recentSongs.size)
        // Lagu paling baru adalah "Senja di Jendela" dengan dateAdded terbesar (1700000008L)
        assertEquals("Senja di Jendela", recentSongs.first().title)
        // Lagu paling awal adalah "Usai Hujan" dengan dateAdded terkecil (1700000001L)
        assertEquals("Usai Hujan", recentSongs.last().title)
    }

    @Test
    fun testSongsScreenSortingAlphabetical() {
        val songs = MockMusicData.sampleSongs
        val sortedSongs = songs.sortedBy { it.title.lowercase() }

        assertEquals(8, sortedSongs.size)
        assertEquals("Arah Pulang", sortedSongs.first().title)
        assertEquals("Usai Hujan", sortedSongs.last().title)
    }

    @Test
    fun testFolderGrouping() {
        val songs = MockMusicData.sampleSongs
        val groupedFolders = songs.groupBy { it.bucketId }
            .map { (_, fSongs) ->
                val first = fSongs.first()
                fSongs.size to first.folderName
            }

        assertEquals(2, groupedFolders.size)
        // Music: 5 lagu, Download: 3 lagu sesuai handoff rU68D.3
        assertTrue(groupedFolders.contains(5 to "Music"))
        assertTrue(groupedFolders.contains(3 to "Download"))
    }

    @Test
    fun testSearchFilteringFoundAndEmpty() {
        val songs = MockMusicData.sampleSongs

        // Kueri "Laras" (Frame 06 & 14) -> Hujan Pelan & Senja di Jendela
        val queryLaras = "Laras"
        val resultsLaras = songs.filter {
            it.title.contains(queryLaras, ignoreCase = true) ||
            it.artist.contains(queryLaras, ignoreCase = true)
        }.sortedBy { it.title.lowercase() }

        assertEquals(2, resultsLaras.size)
        assertEquals("Hujan Pelan", resultsLaras[0].title)
        assertEquals("Senja di Jendela", resultsLaras[1].title)

        // Kueri "Pelangi" (Frame 07) -> tidak ditemukan
        val queryPelangi = "Pelangi"
        val resultsPelangi = songs.filter {
            it.title.contains(queryPelangi, ignoreCase = true) ||
            it.artist.contains(queryPelangi, ignoreCase = true)
        }

        assertTrue(resultsPelangi.isEmpty())
    }

    @Test
    fun testDurationEdgeCases() {
        val sample = MockMusicData.sampleSongs.first()
        val zeroSong = sample.copy(id = 999L, durationMs = 0L)
        val negativeSong = sample.copy(id = 1000L, durationMs = -500L)
        val shortSong = sample.copy(id = 1001L, durationMs = 45000L) // 45 detik -> 0:45
        val hourSong = sample.copy(id = 1002L, durationMs = 3665000L) // 1 jam 1 menit 5 detik -> 61:05

        assertEquals("0:00", zeroSong.formattedDuration())
        assertEquals("0:00", negativeSong.formattedDuration())
        assertEquals("0:45", shortSong.formattedDuration())
        assertEquals("61:05", hourSong.formattedDuration())
    }

    @Test
    fun testUnknownArtistDisplay() {
        val emptyArtistSong = MockMusicData.sampleSongs.first().copy(artist = "")
        val whitespaceArtistSong = MockMusicData.sampleSongs.first().copy(artist = "   ")
        val knownArtistSong = MockMusicData.sampleSongs.first().copy(artist = "Nara")

        assertEquals("Penyanyi tidak diketahui", emptyArtistSong.displayArtist)
        assertEquals("Penyanyi tidak diketahui", whitespaceArtistSong.displayArtist)
        assertEquals("Nara", knownArtistSong.displayArtist)
    }
}
