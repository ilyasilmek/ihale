package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.DocumentType
import com.example.domain.model.TenderProcedure
import com.example.domain.model.TenderType

@Entity(tableName = "tender_projects")
data class TenderProjectEntity(
    @PrimaryKey val id: String,
    val tenderNumber: String,
    val title: String,
    val tenderType: TenderType,
    val tenderProcedure: TenderProcedure,
    val institutionName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tender_documents")
data class TenderDocumentEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val name: String,
    val documentType: DocumentType,
    val rawContent: String,
    val wordCount: Int,
    val pageCount: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_rules")
data class UserRuleEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val ruleType: String,
    val targetText: String,
    val replacementText: String? = null,
    val isEnabled: Boolean = true
)

@Entity(tableName = "audit_reports")
data class AuditReportEntity(
    @PrimaryKey val id: String,
    val projectId: String,
    val title: String,
    val overallScore: Int,
    val criticalCount: Int,
    val highCount: Int,
    val warningCount: Int,
    val suggestionCount: Int,
    val reportJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
