package com.example.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.DashboardRepositoryImpl
import com.example.data.repository.ClientRepositoryImpl
import com.example.data.repository.ProjectRepositoryImpl
import com.example.domain.repository.DashboardRepository
import com.example.domain.repository.ClientRepository
import com.example.domain.repository.ProjectRepository
import com.example.domain.usecase.dashboard.GetDashboardStatsUseCase
import com.example.domain.usecase.client.*
import com.example.domain.usecase.project.*
import com.example.domain.usecase.sitediary.*
import com.example.domain.usecase.finance.*
import com.example.data.repository.SiteDiaryRepositoryImpl
import com.example.domain.repository.SiteDiaryRepository
import com.example.data.repository.TransactionRepositoryImpl
import com.example.domain.repository.TransactionRepository
import com.example.domain.repository.ResourceRepository
import com.example.domain.repository.DocumentationRepository
import com.example.data.repository.DocumentationRepositoryImpl
import com.example.data.repository.ResourceRepositoryImpl

class AppContainer(private val context: Context) {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .addMigrations(AppDatabase.MIGRATION_7_8, AppDatabase.MIGRATION_8_9)
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
    }

    val siteDiaryRepository: SiteDiaryRepository by lazy {
        SiteDiaryRepositoryImpl(database.siteDiaryDao())
    }
    
    val getSiteDiariesForProjectUseCase: GetSiteDiariesForProjectUseCase by lazy {
        GetSiteDiariesForProjectUseCase(siteDiaryRepository)
    }
    
    val getSiteDiaryDetailsUseCase: GetSiteDiaryDetailsUseCase by lazy {
        GetSiteDiaryDetailsUseCase(siteDiaryRepository)
    }
    
    val saveSiteDiaryDetailsUseCase: SaveSiteDiaryDetailsUseCase by lazy {
        SaveSiteDiaryDetailsUseCase(siteDiaryRepository)
    }
    
    val getOrCreateSiteDiaryForDateUseCase: GetOrCreateSiteDiaryForDateUseCase by lazy {
        GetOrCreateSiteDiaryForDateUseCase(siteDiaryRepository)
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepositoryImpl(
            clientDao = database.clientDao(),
            projectDao = database.projectDao(),
            transactionDao = database.transactionDao(),
            attendanceDao = database.attendanceDao()
        )
    }

    val getDashboardStatsUseCase: GetDashboardStatsUseCase by lazy {
        GetDashboardStatsUseCase(dashboardRepository)
    }

    val clientRepository: ClientRepository by lazy {
        ClientRepositoryImpl(database.clientDao())
    }

    val getClientsUseCase: GetClientsUseCase by lazy {
        GetClientsUseCase(clientRepository)
    }

    val getClientByIdUseCase: GetClientByIdUseCase by lazy {
        GetClientByIdUseCase(clientRepository)
    }

    val saveClientUseCase: SaveClientUseCase by lazy {
        SaveClientUseCase(clientRepository)
    }

    val deleteClientUseCase: DeleteClientUseCase by lazy {
        DeleteClientUseCase(clientRepository)
    }

    val validateClientPhoneUseCase: ValidateClientPhoneUseCase by lazy {
        ValidateClientPhoneUseCase(clientRepository)
    }

    val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(database.projectDao())
    }

    val getProjectsUseCase: GetProjectsUseCase by lazy {
        GetProjectsUseCase(projectRepository)
    }

    val getProjectByIdUseCase: GetProjectByIdUseCase by lazy {
        GetProjectByIdUseCase(projectRepository)
    }

    val saveProjectUseCase: SaveProjectUseCase by lazy {
        SaveProjectUseCase(projectRepository)
    }

    val deleteProjectUseCase: DeleteProjectUseCase by lazy {
        DeleteProjectUseCase(projectRepository)
    }

    val getProjectMilestonesUseCase: GetProjectMilestonesUseCase by lazy {
        GetProjectMilestonesUseCase(projectRepository)
    }

    val saveMilestoneUseCase: SaveMilestoneUseCase by lazy {
        SaveMilestoneUseCase(projectRepository)
    }

    val saveMilestonesUseCase: SaveMilestonesUseCase by lazy {
        SaveMilestonesUseCase(projectRepository)
    }

    val getProjectTimelineUseCase: GetProjectTimelineUseCase by lazy {
        GetProjectTimelineUseCase(projectRepository)
    }

    val saveProjectTimelineEventUseCase: SaveProjectTimelineEventUseCase by lazy {
        SaveProjectTimelineEventUseCase(projectRepository)
    }

    val transactionRepository: TransactionRepository by lazy {
        TransactionRepositoryImpl(database.transactionDao())
    }

    val getTransactionsForProjectUseCase: GetTransactionsForProjectUseCase by lazy {
        GetTransactionsForProjectUseCase(transactionRepository)
    }

    val getTransactionByIdUseCase: GetTransactionByIdUseCase by lazy {
        GetTransactionByIdUseCase(transactionRepository)
    }

    val saveTransactionUseCase: SaveTransactionUseCase by lazy {
        SaveTransactionUseCase(transactionRepository)
    }

    val getGlobalFinancialStatsUseCase: GetGlobalFinancialStatsUseCase by lazy {
        GetGlobalFinancialStatsUseCase(transactionRepository)
    }

    val documentationRepository: DocumentationRepository by lazy {
        DocumentationRepositoryImpl(
            photoDao = database.photoDao(),
            documentDao = database.documentDao()
        )
    }

    val resourceRepository: ResourceRepository by lazy {
        ResourceRepositoryImpl(
            database = database,
            materialDao = database.materialDao(),
            workerDao = database.workerDao(),
            supplierDao = database.supplierDao(),
            resourceAllocationDao = database.resourceAllocationDao(),
            attendanceDao = database.attendanceDao()
        )
    }

    val deleteTransactionUseCase: DeleteTransactionUseCase by lazy {
        DeleteTransactionUseCase(transactionRepository)
    }
}
