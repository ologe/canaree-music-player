package dev.olog.msc.presentation.edit

enum class UpdateResult{
    OK,
    ERROR,
    CANNOT_READ,
    READ_ONLY,
    FILE_NOT_FOUND,
    EMPTY_TITLE,
    ILLEGAL_YEAR,
    ILLEGAL_DISC_NUMBER,
    ILLEGAL_TRACK_NUMBER
}
