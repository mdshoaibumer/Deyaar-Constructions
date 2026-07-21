package com.example.data.mapper

import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.MilestoneEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectTimelineEventEntity
import com.example.domain.model.Client
import com.example.domain.model.Milestone
import com.example.domain.model.Project
import com.example.domain.model.ProjectTimelineEvent

fun ProjectEntity.toDomain() = Project(
    id = id,
    projectNumber = projectNumber,
    name = name,
    clientId = clientId,
    category = category,
    address = address,
    location = location,
    contractValuePaise = contractValuePaise,
    estimatedBudgetPaise = estimatedBudgetPaise,
    advanceReceivedPaise = advanceReceivedPaise,
    expectedProfitPaise = expectedProfitPaise,
    startDate = startDate,
    expectedCompletionDate = expectedCompletionDate,
    actualCompletionDate = actualCompletionDate,
    status = status,
    priority = priority,
    engineerInCharge = engineerInCharge,
    notes = notes,
    progress = progress,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Project.toEntity() = ProjectEntity(
    id = id,
    projectNumber = projectNumber,
    name = name,
    clientId = clientId,
    category = category,
    address = address,
    location = location,
    contractValuePaise = contractValuePaise,
    estimatedBudgetPaise = estimatedBudgetPaise,
    advanceReceivedPaise = advanceReceivedPaise,
    expectedProfitPaise = expectedProfitPaise,
    startDate = startDate,
    expectedCompletionDate = expectedCompletionDate,
    actualCompletionDate = actualCompletionDate,
    status = status,
    priority = priority,
    engineerInCharge = engineerInCharge,
    notes = notes,
    progress = progress,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MilestoneEntity.toDomain() = Milestone(
    id = id,
    projectId = projectId,
    name = name,
    isCompleted = isCompleted,
    completionDate = completionDate,
    notes = notes,
    orderIndex = orderIndex
)

fun Milestone.toEntity() = MilestoneEntity(
    id = id,
    projectId = projectId,
    name = name,
    isCompleted = isCompleted,
    completionDate = completionDate,
    notes = notes,
    orderIndex = orderIndex
)

fun ProjectTimelineEventEntity.toDomain() = ProjectTimelineEvent(
    id = id,
    projectId = projectId,
    title = title,
    description = description,
    timestamp = timestamp,
    type = type
)

fun ProjectTimelineEvent.toEntity() = ProjectTimelineEventEntity(
    id = id,
    projectId = projectId,
    title = title,
    description = description,
    timestamp = timestamp,
    type = type
)

fun ClientEntity.toDomain() = Client(
    id = id,
    name = name,
    companyName = companyName,
    phone = phone,
    altPhone = altPhone,
    whatsapp = whatsapp,
    email = email,
    gstNumber = gstNumber,
    panNumber = panNumber,
    address = address,
    city = city,
    state = state,
    pincode = pincode,
    mapsLocation = mapsLocation,
    category = category,
    notes = notes,
    photoPath = photoPath,
    isActive = isActive,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Client.toEntity() = ClientEntity(
    id = id,
    name = name,
    companyName = companyName,
    phone = phone,
    altPhone = altPhone,
    whatsapp = whatsapp,
    email = email,
    gstNumber = gstNumber,
    panNumber = panNumber,
    address = address,
    city = city,
    state = state,
    pincode = pincode,
    mapsLocation = mapsLocation,
    category = category,
    notes = notes,
    photoPath = photoPath,
    isActive = isActive,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt
)
