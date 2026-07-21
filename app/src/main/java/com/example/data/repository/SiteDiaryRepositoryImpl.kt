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
        
        val part1 = combine(diaryFlow, labourFlow, materialsFlow) { diary, labour, materials ->
            Triple(diary, labour, materials)
        }
        
        val part2 = combine(expensesFlow, workItemsFlow, issuesFlow) { expenses, workItems, issues ->
            Triple(expenses, workItems, issues)
        }
        
        return combine(part1, part2) { (diary, labour, materials), (expenses, workItems, issues) ->
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
        dao.saveSiteDiaryDetailsTransaction(
            diary = details.diary.toEntity(),
            labourSummary = details.labourSummary?.toEntity(),
            materials = details.materials.map { it.toEntity() },
            expenses = details.expenses.map { it.toEntity() },
            workItems = details.workItems.map { it.toEntity() },
            issues = details.issues.map { it.toEntity() }
        )
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
        totalWagesPaise = totalWagesPaise,
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
        totalWagesPaise = totalWagesPaise,
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
        amountPaise = amountPaise,
        remarks = remarks
    )
    
    private fun ExpenseSummaryEntity.toDomain() = ExpenseSummary(
        id = id,
        siteDiaryId = siteDiaryId,
        category = ExpenseCategory.valueOf(category),
        amountPaise = amountPaise,
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
