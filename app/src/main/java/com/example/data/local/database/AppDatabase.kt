package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.TenderDao
import com.example.data.local.entity.AuditReportEntity
import com.example.data.local.entity.TenderDocumentEntity
import com.example.data.local.entity.TenderProjectEntity
import com.example.data.local.entity.UserRuleEntity

@Database(
    entities = [
        TenderProjectEntity::class,
        TenderDocumentEntity::class,
        UserRuleEntity::class,
        AuditReportEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tenderDao(): TenderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tender_audit_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
