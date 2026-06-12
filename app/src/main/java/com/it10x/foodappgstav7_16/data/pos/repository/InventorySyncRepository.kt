package com.it10x.foodappgstav7_16.data.pos.repository

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.it10x.foodappgstav7_16.data.pos.AppDatabase
import com.it10x.foodappgstav7_16.data.pos.entities.InventorySyncEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class InventorySyncRepository(
    private val db: AppDatabase
) {

    private val firestore =
        FirebaseFirestore.getInstance()



    suspend fun syncInventoryFromSales() = withContext(Dispatchers.IO) {

        try {

            // =====================================================
            // 1. LOAD DATA FROM ROOM
            // =====================================================

            val recipes =
                db.productRecipeDao().getAll()

            val paidItems =
                db.orderProductDao().getPaidItems()

            val syncedIds =
                db.inventorySyncDao()
                    .getSyncedIds()
                    .toSet()

            val pendingItems =
                paidItems.filter {
                    !syncedIds.contains(it.id)
                }

            if (pendingItems.isEmpty()) {
                Log.d("INV_SYNC", "No pending inventory updates")
                return@withContext
            }

            // =====================================================
            // 2. CALCULATE USAGE MAP
            // =====================================================

            val inventoryUsage =
                mutableMapOf<String, Double>()

            pendingItems.forEach { orderItem ->

                val productRecipes =
                    recipes.filter {
                        it.productId ==
                                orderItem.productId
                    }

                productRecipes.forEach { recipe ->

                    val usedQty =
                        recipe.quantity *
                                orderItem.quantity

                    inventoryUsage.merge(
                        recipe.inventoryItemId,
                        usedQty,
                        Double::plus
                    )
                }
            }

            // =====================================================
            // 3. FIRESTORE BATCH UPDATE
            // =====================================================

            val batch =
                firestore.batch()

            inventoryUsage.forEach { (inventoryId, qty) ->

                val ref =
                    firestore
                        .collection("inventoryItems")
                        .document(inventoryId)

                batch.update(
                    ref,
                    "currentStock",
                    FieldValue.increment(-qty)
                )
            }

            batch.commit().await()

            // =====================================================
            // 4. MARK ITEMS AS SYNCED
            // =====================================================

            db.inventorySyncDao().insertAll(
                pendingItems.map {

                    InventorySyncEntity(
                        orderItemId = it.id,
                        syncedAt = System.currentTimeMillis()
                    )
                }
            )

            Log.d(
                "INV_SYNC",
                "Inventory sync successful"
            )

        } catch (e: Exception) {

            Log.e(
                "INV_SYNC",
                "Inventory sync failed: ${e.message}",
                e
            )

            throw e
        }
    }
}