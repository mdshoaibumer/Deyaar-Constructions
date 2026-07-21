package com.example.domain.repository

import com.example.domain.model.SiteDiary
import com.example.domain.model.SiteDiaryDetails
import kotlinx.coroutines.flow.Flow

interface SiteDiaryRepository {
    fun getSiteDiariesForProject(projectId: String): Flow<List<SiteDiary>>
    suspend fun getSiteDiaryById(id: String): SiteDiary?
    fun getSiteDiaryDetails(id: String): Flow<SiteDiaryDetails?>
    suspend fun getSiteDiaryForProjectAndDate(projectId: String, date: Long): SiteDiary?
    suspend fun saveSiteDiary(diary: SiteDiary)
    suspend fun saveSiteDiaryDetails(details: SiteDiaryDetails)
}
