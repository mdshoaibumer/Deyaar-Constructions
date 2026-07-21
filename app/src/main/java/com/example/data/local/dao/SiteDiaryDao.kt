package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.local.entity.ExpenseSummaryEntity
import com.example.data.local.entity.LabourSummaryEntity
import com.example.data.local.entity.MaterialSummaryEntity
import com.example.data.local.entity.SiteDiaryEntity
import com.example.data.local.entity.SiteIssueEntity
import com.example.data.local.entity.WorkItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDiaryDao {
    @Query("SELECT * FROM site_diaries WHERE projectId = :projectId ORDER BY date DESC")
    fun getSiteDiariesForProject(projectId: String): Flow<List<SiteDiaryEntity>>

    @Query("SELECT * FROM site_diaries WHERE id = :id")
    fun getSiteDiaryByIdFlow(id: String): Flow<SiteDiaryEntity?>

    @Query("SELECT * FROM site_diaries WHERE id = :id")
    suspend fun getSiteDiaryById(id: String): SiteDiaryEntity?

    @Query("SELECT * FROM site_diaries WHERE projectId = :projectId AND date >= :startOfDay AND date <= :endOfDay LIMIT 1")
    suspend fun getSiteDiaryForProjectAndDate(projectId: String, startOfDay: Long, endOfDay: Long): SiteDiaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiteDiary(diary: SiteDiaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLabourSummary(summary: LabourSummaryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterialSummaries(summaries: List<MaterialSummaryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenseSummaries(summaries: List<ExpenseSummaryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkItems(items: List<WorkItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSiteIssues(issues: List<SiteIssueEntity>)

    @Query("SELECT * FROM labour_summaries WHERE siteDiaryId = :diaryId")
    fun getLabourSummaryForDiary(diaryId: String): Flow<LabourSummaryEntity?>

    @Query("SELECT * FROM material_summaries WHERE siteDiaryId = :diaryId")
    fun getMaterialsForDiary(diaryId: String): Flow<List<MaterialSummaryEntity>>

    @Query("SELECT * FROM expense_summaries WHERE siteDiaryId = :diaryId")
    fun getExpensesForDiary(diaryId: String): Flow<List<ExpenseSummaryEntity>>

    @Query("SELECT * FROM work_items WHERE siteDiaryId = :diaryId")
    fun getWorkItemsForDiary(diaryId: String): Flow<List<WorkItemEntity>>

    @Query("SELECT * FROM site_issues WHERE siteDiaryId = :diaryId")
    fun getIssuesForDiary(diaryId: String): Flow<List<SiteIssueEntity>>
    
    @Query("DELETE FROM material_summaries WHERE siteDiaryId = :diaryId")
    suspend fun deleteMaterialsForDiary(diaryId: String)
    
    @Query("DELETE FROM expense_summaries WHERE siteDiaryId = :diaryId")
    suspend fun deleteExpensesForDiary(diaryId: String)
    
    @Query("DELETE FROM work_items WHERE siteDiaryId = :diaryId")
    suspend fun deleteWorkItemsForDiary(diaryId: String)
    
    @Query("DELETE FROM site_issues WHERE siteDiaryId = :diaryId")
    suspend fun deleteIssuesForDiary(diaryId: String)

    @Transaction
    suspend fun saveSiteDiaryDetailsTransaction(
        diary: SiteDiaryEntity,
        labourSummary: LabourSummaryEntity?,
        materials: List<MaterialSummaryEntity>,
        expenses: List<ExpenseSummaryEntity>,
        workItems: List<WorkItemEntity>,
        issues: List<SiteIssueEntity>
    ) {
        insertSiteDiary(diary)
        
        if (labourSummary != null) {
            insertLabourSummary(labourSummary)
        }
        
        deleteMaterialsForDiary(diary.id)
        insertMaterialSummaries(materials)
        
        deleteExpensesForDiary(diary.id)
        insertExpenseSummaries(expenses)
        
        deleteWorkItemsForDiary(diary.id)
        insertWorkItems(workItems)
        
        deleteIssuesForDiary(diary.id)
        insertSiteIssues(issues)
    }
}
