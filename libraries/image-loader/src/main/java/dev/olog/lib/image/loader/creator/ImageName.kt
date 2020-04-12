package dev.olog.lib.image.loader.creator

/**
 * File name structure -> groupId_progressive(albumsIdSeparatedByUnderscores).webp
 *  eg: 10_1(15_20_25)
 */
/*
    regex tested for:

    valid
    10_1()
    10_1(1)
    10_1(1_2_3)
    10_1(10)
    10_1(10_11)

    invalid
    ()
    _()
    10
    10_1
    10_1(
    10_1)
    10_11_10_11)
    10_()
    10_(1)
    10_(1_11)
 */
internal class ImageName(
    private val fileName: String
) {

    init {
        require(Regex("^.+_\\d+\\(\\d+(_\\d+)*\\)\\.webp\$").matches(fileName))
    }

    val albums: List<Long>
        get() {
            val indexOfStart = fileName.indexOf("(")
            val indexOfEnd = fileName.indexOf(")")
            val images = fileName.substring(indexOfStart + 1, indexOfEnd)
                .split("_")

            if (images.isEmpty() || images[0].isBlank()) {
                return emptyList()
            }

            return images.map { it.toLong() }
        }

    val progressive: Long
        get() {
            val indexOfStart = fileName.indexOf("_")
            val indexOfEnd = fileName.indexOf("(")
            return fileName.substring(indexOfStart + 1, indexOfEnd).toLong()
        }

}