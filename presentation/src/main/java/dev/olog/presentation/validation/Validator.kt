package dev.olog.presentation.validation

interface Validator {

    fun validate(text: String): ValidationResult

}