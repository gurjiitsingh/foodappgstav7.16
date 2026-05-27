package com.it10x.foodappgstav7_16.utils

import com.it10x.foodappgstav7_16.data.online.models.OrderMasterData

fun OrderMasterData.createdAtMillis(): Long {
    return createdAt?.toDate()?.time ?: createdAtMillis
}