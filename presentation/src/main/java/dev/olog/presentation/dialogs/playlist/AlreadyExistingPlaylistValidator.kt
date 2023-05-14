package dev.olog.presentation.dialogs.playlist

import dev.olog.presentation.validation.ValidationResult
import dev.olog.presentation.validation.Validator

class AlreadyExistingPlaylistValidator(
    private val existingPlaylists: Collection<String>,
    private val message: String,
) : Validator {

    override fun validate(text: String): ValidationResult {
        if (text in existingPlaylists) {
            return ValidationResult.Invalid(message)
        }
        return ValidationResult.Valid
    }
}