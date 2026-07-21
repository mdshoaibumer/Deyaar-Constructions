#!/bin/bash
cat << 'INNER' > app/src/main/java/com/example/data/repository/SiteDiaryRepositoryImpl.kt
package com.example.data.repository

import com.example.data.local.dao.SiteDiaryDao
import com.example.data.local.entity.*
import com.example.domain.model.*
import com.example.domain.repository.SiteDiaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.TimeZone

class SiteDiaryRepositoryImpl(
    private val dao: SiteDiaryDao
) : SiteDiaryRepository {

    override fun getSiteDiariesForProject(projectId: String): Flow<List<SiteDiary>> {
        return dao.getSiteDiariesForProject(projectId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSiteDiaryById(id: String): SiteDiary? {
        return dao.getSiteDiaryById(id)?.toDomain()
    }

    override fun getSiteDiaryDetails(id: String): Flow<SiteDiaryDetails?> {
        val diaryFlow = dao.getSiteDiaryByIdFlow(id)
        val labourFlow = dao.getLabourSummaryForDiary(id)
        val materialsFlow = dao.getMaterialsForDiary(id)
        val expensesFlow = dao.getExpensesForDiary(id)
        val workItemsFlow = dao.getWorkItemsForDiary(id)
        val issuesFlow = dao.getIssuesForDiary(id)
        
        return combine(
            diaryFlow, labourFlow, materialsFlow, expensesFlow, workItemsFlow, issuesFlow
        ) { values ->
            val diary = values[0] as SiteDiaryEntity?
            val labour = values[1] as LabourSummaryEntity?
            val materials = values[2] as List<MaterialSummaryEntity>
            val expenses = values[3] as List<ExpenseSummaryEntity>
            val workItems = values[4] as List<WorkItemEntity>
            val issues = values[5] as List<SiteIssueEntity>
            
            if (diary == null) null
            else SiteDiaryDetails(
                diary = diary.toDomain(),
                labourSummary = labour?.toDomain(),
                materials = materials.map { it.toDomain() },
                expenses = expenses.map { it.toDomain() },
                workItems = workItems.map { it.toDomain() },
                issues = issues.map { it.toDomain() }
            )
        }
    }

    override suspend fun getSiteDiaryForProjectAndDate(projectId: String, date: Long): SiteDiary? {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        cal.timeInMillis = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        val endOfDay = cal.timeInMillis
        
        return dao.getSiteDiaryForProjectAndDate(projectId, startOfDay, endOfDay)?.toDomain()
    }

    override suspend fun saveSiteDiary(diary: SiteDiary) {
        dao.insertSiteDiary(diary.toEntity())
    }

    override suspend fun saveSiteDiaryDetails(details: SiteDiaryDetails) {
        dao.insertSiteDiary(details.diary.toEntity())
        
        if (details.labourSummary != null) {
            dao.insertLabourSummary(details.labourSummary.toEntity())
        }
        
        dao.deleteMaterialsForDiary(details.diary.id)
        dao.insertMaterialSummaries(details.materials.map { it.toEntity() })
        
        dao.deleteExpensesForDiary(details.diary.id)
        dao.insertExpenseSummaries(details.expenses.map { it.toEntity() })
        
        dao.deleteWorkItemsForDiary(details.diary.id)
        dao.insertWorkItems(details.workItems.map { it.toEntity() })
        
        dao.deleteIssuesForDiary(details.diary.id)
        dao.insertSiteIssues(details.issues.map { it.toEntity() })
    }

    // Mappers
    private fun SiteDiaryEntity.toDomain() = SiteDiary(
        id = id,
        projectId = projectId,
        date = date,
        startTime = startTime,
        endTime = endTime,
        weather = weather,
        temperature = temperature,
        overallProgress = overallProgress,
        workSummary = workSummary,
        engineerNotes = engineerNotes,
        safetyObservations = safetyObservations,
        nextDayPlan = nextDayPlan,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun SiteDiary.toEntity() = SiteDiaryEntity(
        id = id,
        projectId = projectId,
        date = date,
        startTime = startTime,
        endTime = endTime,
        weather = weather,
        temperature = temperature,
        overallProgress = overallProgress,
        workSummary = workSummary,
        engineerNotes = engineerNotes,
        safetyObservations = safetyObservations,
        nextDayPlan = nextDayPlan,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun LabourSummary.toEntity() = LabourSummaryEntity(
        id = id,
        siteDiaryId = siteDiaryId,
        masons = masons,
        helpers = helpers,
        carpenters = carpenters,
        steelWorkers = steelWorkers,
        painters = painters,
        electricians = electricians,
        plumbers = plumbers,
        machineOperators = machineOperators,
        other = other,
        totalWages = totalWages,
        attendanceSummary = attendanceSummary
    )
    
    private fun LabourSummaryEntity.toDomain() = LabourSummary(
        id = id,
        siteDiaryId = siteDiaryId,
        masons = masons,
        helpers = helpers,
        carpenters = carpenters,
        steelWorkers = steelWorkers,
        painters = painters,
        electricians = electricians,
        plumbers = plumbers,
        machineOperators = machineOperators,
        other = other,
        totalWages = totalWages,
        attendanceSummary = attendanceSummary
    )

    private fun MaterialSummary.toEntity() = MaterialSummaryEntity(
        id = id,
        siteDiaryId = siteDiaryId,
        materialName = materialName,
        type = type.name,
        supplier = supplier,
        quantity = quantity,
        unit = unit,
        remarks = remarks,
        lowStockWarning = lowStockWarning
    )

    private fun MaterialSummaryEntity.toDomain() = MaterialSummary(
        id = id,
        siteDiaryId = siteDiaryId,
        materialName = materialName,
        type = MaterialType.valueOf(type),
        supplier = supplier,
        quantity = quantity,
        unit = unit,
        remarks = remarks,
        lowStockWarning = lowStockWarning
    )

    private fun ExpenseSummary.toEntity() = ExpenseSummaryEntity(
        id = id,
        siteDiaryId = siteDiaryId,
        category = category.name,
        amount = amount,
        remarks = remarks
    )
    
    private fun ExpenseSummaryEntity.toDomain() = ExpenseSummary(
        id = id,
        siteDiaryId = siteDiaryId,
        category = ExpenseCategory.valueOf(category),
        amount = amount,
        remarks = remarks
    )

    private fun WorkItem.toEntity() = WorkItemEntity(
        id = id,
        siteDiaryId = siteDiaryId,
        description = description,
        percentageComplete = percentageComplete,
        remarks = remarks
    )
    
    private fun WorkItemEntity.toDomain() = WorkItem(
        id = id,
        siteDiaryId = siteDiaryId,
        description = description,
        percentageComplete = percentageComplete,
        remarks = remarks
    )

    private fun SiteIssue.toEntity() = SiteIssueEntity(
        id = id,
        siteDiaryId = siteDiaryId,
        type = type.name,
        description = description,
        resolved = resolved
    )
    
    private fun SiteIssueEntity.toDomain() = SiteIssue(
        id = id,
        siteDiaryId = siteDiaryId,
        type = IssueType.valueOf(type),
        description = description,
        resolved = resolved
    )
}
INNER
