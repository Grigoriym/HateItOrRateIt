package com.grappim.hateitorrateit.data.db.utils

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionControllerImpl @Inject constructor(
    private val db: HateItOrRateItDatabase,
) : TransactionController {

    override suspend fun runInTransaction(
        block: suspend () -> Unit,
    ) {
        db.withTransaction {
            block.invoke()
        }
    }
}
