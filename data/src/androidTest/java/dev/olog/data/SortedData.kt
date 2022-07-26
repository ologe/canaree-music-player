package dev.olog.data

import android.provider.MediaStore

data class SortedData(
    val id: String,
    val value: String,
    val value2: String? = null,
) {

    companion object {
        val title = listOf(
            SortedData("2", "hello"),
            SortedData("3", "héz"),
            SortedData("4", "hèa"),
            SortedData("5", "hTy"),
            SortedData("6", "Haa"),
        )

        val titleSortedAsc = listOf(
            SortedData("6", "Haa"),
            SortedData("4", "hèa"),
            SortedData("2", "hello"),
            SortedData("3", "héz"),
            SortedData("5", "hTy"),
        )

        val titleSortedDesc = listOf(
            SortedData("5", "hTy"),
            SortedData("3", "héz"),
            SortedData("2", "hello"),
            SortedData("4", "hèa"),
            SortedData("6", "Haa"),
        )

        val artist = listOf(
            SortedData("1", MediaStore.UNKNOWN_STRING),
            SortedData("2", "hello", "hTy"),
            SortedData("3", "hello", "hèa"),
            SortedData("4", "hello", "héz"),
            SortedData("5", "héz"),
            SortedData("6", "hèa"),
            SortedData("7", "hTy"),
            SortedData("8", "Haa"),
        )

        val artistSortedAsc = listOf(
            SortedData("8", "Haa"),
            SortedData("6", "hèa"),
            SortedData("3", "hello", "hèa"),
            SortedData("4", "hello", "héz"),
            SortedData("2", "hello", "hTy"),
            SortedData("5", "héz"),
            SortedData("7", "hTy"),
            SortedData("1", MediaStore.UNKNOWN_STRING),
        )

        val artistSortedDesc = listOf(
            SortedData("7", "hTy"),
            SortedData("5", "héz"),
            SortedData("2", "hello", "hTy"),
            SortedData("4", "hello", "héz"),
            SortedData("3", "hello", "hèa"),
            SortedData("6", "hèa"),
            SortedData("8", "Haa"),
            SortedData("1", MediaStore.UNKNOWN_STRING),
        )

        val album = artist.map { it.copy(id = it.id.repeat(2)) }
        val albumSortedAsc = artistSortedAsc.map { it.copy(id = it.id.repeat(2)) }
        val albumSortedDesc = artistSortedDesc.map { it.copy(id = it.id.repeat(2)) }

        val duration = listOf(
            SortedData("1", "10000"),
            SortedData("2", "2000", "hTy"),
            SortedData("3", "2000", "hèa"),
            SortedData("4", "2000", "héz"),
            SortedData("5", "500"),
        )

        val durationSortedAsc = listOf(
            SortedData("5", "500"),
            SortedData("3", "2000", "hèa"),
            SortedData("4", "2000", "héz"),
            SortedData("2", "2000", "hTy"),
            SortedData("1", "10000"),
        )

        val durationSortedDesc = listOf(
            SortedData("1", "10000"),
            SortedData("2", "2000", "hTy"),
            SortedData("4", "2000", "héz"),
            SortedData("3", "2000", "hèa"),
            SortedData("5", "500"),
        )

        val date = listOf(
            SortedData("1", "10000"),
            SortedData("2", "2000", "hTy"),
            SortedData("3", "2000", "hèa"),
            SortedData("4", "2000", "héz"),
            SortedData("5", "500"),
        )

        // for date first value is inverted
        val dateSortedAsc = listOf(
            SortedData("1", "10000"),
            SortedData("3", "2000", "hèa"),
            SortedData("4", "2000", "héz"),
            SortedData("2", "2000", "hTy"),
            SortedData("5", "500"),
        )

        // for date first value is inverted
        val dateSortedDesc = listOf(
            SortedData("5", "500"),
            SortedData("2", "2000", "hTy"),
            SortedData("4", "2000", "héz"),
            SortedData("3", "2000", "hèa"),
            SortedData("1", "10000"),
        )

    }

}