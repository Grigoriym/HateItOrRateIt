package com.grappim.hateitorrateit.data.db.wrapper

import android.content.Context
import androidx.room.Room
import com.grappim.hateitorrateit.commons.IoDispatcher
import com.grappim.hateitorrateit.data.db.BuildConfig
import com.grappim.hateitorrateit.data.db.HateItOrRateItDatabase
import com.grappim.hateitorrateit.data.db.converters.DateTimeConverter
import com.grappim.hateitorrateit.data.db.dao.DatabaseDao
import com.grappim.hateitorrateit.data.db.dao.ProductsDao
import com.grappim.hateitorrateit.data.db.utils.TransactionController
import com.grappim.hateitorrateit.data.db.utils.TransactionControllerImpl
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseWrapperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    dateTimeConverter: DateTimeConverter,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : DatabaseWrapper {

    private val db: HateItOrRateItDatabase

    init {
        db = Room.databaseBuilder(
            context,
            HateItOrRateItDatabase::class.java,
            "hateitorrateit_${BuildConfig.BUILD_TYPE}.db"
        )
            .addTypeConverter(dateTimeConverter)
            .build()
    }

    override val productsDao: ProductsDao get() = db.productsDao()

    override val databaseDao: DatabaseDao get() = db.databaseDao()

    override val transactionController: TransactionController get() = TransactionControllerImpl(db)

    override suspend fun clearAllTables() = withContext(ioDispatcher) {
        db.clearAllTables()
    }
}
