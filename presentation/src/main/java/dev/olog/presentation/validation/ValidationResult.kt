package dev.olog.presentation.validation

sealed interface ValidationResult {
    object Valid : ValidationResult
    class Invalid(val message: CharSequence): ValidationResult
}