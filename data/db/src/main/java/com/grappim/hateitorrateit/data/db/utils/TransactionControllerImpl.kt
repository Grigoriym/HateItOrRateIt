package com.grappim.hateitorrateit.data.db.utils

import androidx.room.RoomDatabase
import androidx.room.withTransaction

class TransactionControllerImpl(
    private val roomDatabase: RoomDatabase,
) : TransactionController {

    override suspend fun runInTransaction(
        block: suspend () -> Unit,
    ) {
        roomDatabase.withTransaction {
            block.invoke()
        }
    }
}
