package dev.olog.presentation.fragment_detail

import dev.olog.presentation.model.DisplayableItem

class DetailDataDiff(
        private val oldData: Map<DetailDataController.DataType, List<DisplayableItem>>,
        private val newData: Map<DetailDataController.DataType, List<DisplayableItem>>
)