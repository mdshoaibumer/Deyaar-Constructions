package com.example.domain.repository

import com.example.domain.model.Document
import com.example.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface DocumentationRepository {
    fun getPhotosForProject(projectId: String): Flow<List<Photo>>
    suspend fun getPhotoById(id: String): Photo?
    suspend fun savePhoto(photo: Photo)
    suspend fun deletePhoto(id: String)
    
    fun getDocumentsForProject(projectId: String): Flow<List<Document>>
    suspend fun getDocumentById(id: String): Document?
    suspend fun saveDocument(document: Document)
    suspend fun deleteDocument(id: String)
}
