package com.it10x.foodappgstav7_16.ui.settings

import com.it10x.foodappgstav7_16.data.PrinterConfig
import com.it10x.foodappgstav7_16.data.PrinterRole

data class PrinterSettingsState(
    val printers: Map<PrinterRole, PrinterConfig> = emptyMap()
)
