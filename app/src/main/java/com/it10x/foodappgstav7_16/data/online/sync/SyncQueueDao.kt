package com.it10x.foodappgstav7_16.data.online.sync

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SyncQueueDao {

    @Insert
    suspend fun insert(job: SyncQueueEntity)

    @Query("SELECT * FROM sync_queue WHERE status='PENDING' ORDER BY createdAt ASC LIMIT 1")
    suspend fun getNext(): SyncQueueEntity?

    @Query("UPDATE sync_queue SET status='DONE' WHERE id=:id")
    suspend fun markDone(id: String)

    @Query("DELETE FROM sync_queue WHERE id=:id")
    suspend fun deleteJob(id: String)

    @Query("""
DELETE FROM sync_queue
WHERE status='DONE'
AND createdAt < :cutoff
""")
    suspend fun deleteOldDoneJobs(cutoff: Long)
}