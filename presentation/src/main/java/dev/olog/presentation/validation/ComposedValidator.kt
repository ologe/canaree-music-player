package dev.olog.presentation.validation

class ComposedValidator(
    private val validators: List<Validator>
) : Validator {

    constructor(vararg validators: Validator) : this(validators.toList())

    override fun validate(text: String): ValidationResult {
        for (validator in validators) {
            val result = validator.validate(text)
            if (result is ValidationResult.Invalid) {
                return result
            }
        }
        return ValidationResult.Valid
    }
}