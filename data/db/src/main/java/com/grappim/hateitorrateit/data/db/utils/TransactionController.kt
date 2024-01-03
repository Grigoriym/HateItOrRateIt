package com.grappim.hateitorrateit.data.db.utils

interface TransactionController {
    suspend fun runInTransaction(block: suspend () -> Unit)
}
