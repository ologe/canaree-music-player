package dev.olog.presentation.validation

class NonEmptyValidator(
    private val message: String,
) : Validator {

    override fun validate(text: String): ValidationResult {
        if (text.isNotBlank()) {
            return ValidationResult.Valid
        }
        return ValidationResult.Invalid(message)
    }

}