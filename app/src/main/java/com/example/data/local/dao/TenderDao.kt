package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AuditReportEntity
import com.example.data.local.entity.TenderDocumentEntity
import com.example.data.local.entity.TenderProjectEntity
import com.example.data.local.entity.UserRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TenderDao {
    @Query("SELECT * FROM tender_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<TenderProjectEntity>>

    @Query("SELECT * FROM tender_projects WHERE id = :projectId")
    suspend fun getProjectById(projectId: String): TenderProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: TenderProjectEntity)

    @Update
    suspend fun updateProject(project: TenderProjectEntity)

    @Query("DELETE FROM tender_projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: String)

    // Documents
    @Query("SELECT * FROM tender_documents WHERE projectId = :projectId")
    fun getDocumentsForProject(projectId: String): Flow<List<TenderDocumentEntity>>

    @Query("SELECT * FROM tender_documents WHERE projectId = :projectId")
    suspend fun getDocumentsForProjectSync(projectId: String): List<TenderDocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: TenderDocumentEntity)

    @Query("DELETE FROM tender_documents WHERE id = :documentId")
    suspend fun deleteDocument(documentId: String)

    @Query("DELETE FROM tender_documents WHERE projectId = :projectId")
    suspend fun deleteDocumentsForProject(projectId: String)

    // User Rules
    @Query("SELECT * FROM user_rules")
    fun getAllUserRules(): Flow<List<UserRuleEntity>>

    @Query("SELECT * FROM user_rules WHERE isEnabled = 1")
    suspend fun getActiveUserRules(): List<UserRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRule(rule: UserRuleEntity)

    @Query("DELETE FROM user_rules WHERE id = :ruleId")
    suspend fun deleteUserRule(ruleId: String)

    // Reports
    @Query("SELECT * FROM audit_reports ORDER BY createdAt DESC")
    fun getAllReports(): Flow<List<AuditReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: AuditReportEntity)

    @Query("DELETE FROM audit_reports WHERE id = :reportId")
    suspend fun deleteReport(reportId: String)
}
