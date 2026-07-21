package com.example.domain.model

data class Photo(
    val id: String,
    val projectId: String,
    val uri: String,
    val description: String?,
    val tags: List<String>,
    val category: String,
    val date: Long,
    val capturedBy: String?,
    val location: String?,
    val linkedSiteDiaryId: String?,
    val linkedMilestoneId: String?
)

data class Document(
    val id: String,
    val projectId: String,
    val title: String,
    val category: String,
    val uri: String,
    val description: String?,
    val tags: List<String>,
    val createdAt: Long,
    val updatedAt: Long
)
