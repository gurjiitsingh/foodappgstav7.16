package com.it10x.foodappgstav7_16.fiscal

import com.it10x.foodappgstav7_16.fiskaly.GermanyFiscalService
import com.it10x.foodappgstav7_16.fiskaly.IndiaFiscalService
import com.it10x.foodappgstav7_16.fiskaly.SpainFiscalService
import com.it10x.foodappgstav7_16.fiskaly.FiskalyRepository

fun getFiscalService(
    countryCode: String,
    fiskalyRepository: FiskalyRepository? = null
): FiscalService {

    return when (countryCode) {
        "DE" -> GermanyFiscalService(fiskalyRepository!!)
        "IN" -> IndiaFiscalService()
        "ES" -> SpainFiscalService()
        else -> IndiaFiscalService()
    }
}