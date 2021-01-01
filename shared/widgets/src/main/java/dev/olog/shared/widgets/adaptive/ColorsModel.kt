package dev.olog.shared.widgets.adaptive

sealed class ProcessorColors {
    abstract val background: Int
    abstract val primaryText: Int
    abstract val secondaryText: Int
}

sealed class PaletteColors {
    abstract val accent: Int
}

class ValidProcessorColors(
    override val background: Int,
    override val primaryText: Int,
    override val secondaryText: Int
) : ProcessorColors()

object InvalidProcessColors : ProcessorColors() {
    override val background: Int = 0
    override val primaryText: Int = 0
    override val secondaryText: Int = 0
}


class ValidPaletteColors(
    override val accent: Int
) : PaletteColors()

object InvalidPaletteColors : PaletteColors() {
    override val accent: Int = 0
}