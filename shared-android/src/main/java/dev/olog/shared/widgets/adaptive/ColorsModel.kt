package dev.olog.shared.widgets.adaptive

sealed class ProcessorColors(
    val background: Int,
    val primaryText: Int,
    val secondaryText: Int
)

sealed class PaletteColors(
    val accent: Int
)

class ValidProcessorColors(
    background: Int,
    primaryText: Int,
    secondaryText: Int
) : ProcessorColors(background, primaryText, secondaryText)

object InvalidProcessColors : ProcessorColors(0, 0, 0)


class ValidPaletteColors(
    accent: Int
) : PaletteColors(accent)

object InvalidPaletteColors : PaletteColors(0)