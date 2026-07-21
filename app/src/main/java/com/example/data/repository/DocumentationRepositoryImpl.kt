package com.example.data.repository

import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.PhotoDao
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.PhotoEntity
import com.example.domain.model.Document
import com.example.domain.model.Photo
import com.example.domain.repository.DocumentationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentationRepositoryImpl(
    private val photoDao: PhotoDao,
    private val documentDao: DocumentDao
) : DocumentationRepository {

    override fun getPhotosForProject(projectId: String): Flow<List<Photo>> =
        photoDao.getPhotosForProject(projectId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getPhotoById(id: String): Photo? =
        photoDao.getPhotoById(id)?.toDomain()

    override suspend fun savePhoto(photo: Photo) {
        photoDao.insertPhoto(photo.toEntity())
    }

    override suspend fun deletePhoto(id: String) {
        photoDao.deletePhoto(id)
    }

    override fun getDocumentsForProject(projectId: String): Flow<List<Document>> =
        documentDao.getDocumentsForProject(projectId).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getDocumentById(id: String): Document? =
        documentDao.getDocumentById(id)?.toDomain()

    override suspend fun saveDocument(document: Document) {
        documentDao.insertDocument(document.toEntity())
    }

    override suspend fun deleteDocument(id: String) {
        documentDao.deleteDocument(id)
    }

    private fun PhotoEntity.toDomain() = Photo(
        id = id, projectId = projectId, uri = uri, description = description,
        tags = tags, category = category,
        date = date, capturedBy = capturedBy, location = location,
        linkedSiteDiaryId = linkedSiteDiaryId, linkedMilestoneId = linkedMilestoneId
    )

    private fun Photo.toEntity() = PhotoEntity(
        id = id, projectId = projectId, uri = uri, description = description,
        tags = tags, category = category,
        date = date, capturedBy = capturedBy, location = location,
        linkedSiteDiaryId = linkedSiteDiaryId, linkedMilestoneId = linkedMilestoneId
    )

    private fun DocumentEntity.toDomain() = Document(
        id = id, projectId = projectId, title = title, category = category, uri = uri,
        description = description, tags = tags,
        createdAt = createdAt, updatedAt = updatedAt
    )

    private fun Document.toEntity() = DocumentEntity(
        id = id, projectId = projectId, title = title, category = category, uri = uri,
        description = description, tags = tags,
        createdAt = createdAt, updatedAt = updatedAt
    )
}
