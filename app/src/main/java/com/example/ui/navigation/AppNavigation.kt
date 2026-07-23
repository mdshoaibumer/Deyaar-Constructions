package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.di.AppContainer
import com.example.ui.screens.auth.PinScreen
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.client.ClientListScreen
import com.example.ui.screens.client.ClientViewModel
import com.example.ui.screens.client.ClientViewModelFactory
import com.example.ui.screens.client.ClientAddEditScreen
import com.example.ui.screens.client.ClientAddEditViewModel
import com.example.ui.screens.client.ClientAddEditViewModelFactory
import com.example.ui.screens.client.ClientDetailsScreen
import com.example.ui.screens.client.ClientDetailsViewModel
import com.example.ui.screens.client.ClientDetailsViewModelFactory
import com.example.ui.screens.project.ProjectAddEditScreen
import com.example.ui.screens.project.ProjectAddEditViewModel
import com.example.ui.screens.project.ProjectAddEditViewModelFactory
import com.example.ui.screens.project.ProjectDetailsScreen
import com.example.ui.screens.project.ProjectDetailsViewModel
import com.example.ui.screens.project.ProjectDetailsViewModelFactory
import com.example.ui.screens.project.ProjectListScreen
import com.example.ui.screens.project.ProjectListViewModel
import com.example.ui.screens.project.ProjectListViewModelFactory
import com.example.ui.screens.sitediary.SiteDiaryListScreen
import com.example.ui.screens.sitediary.SiteDiaryListViewModel
import com.example.ui.screens.sitediary.SiteDiaryListViewModelFactory
import com.example.ui.screens.sitediary.SiteDiaryDetailsScreen
import com.example.ui.screens.sitediary.SiteDiaryDetailsViewModel
import com.example.ui.screens.sitediary.SiteDiaryDetailsViewModelFactory
import com.example.ui.screens.finance.FinanceLedgerScreen
import com.example.ui.screens.finance.FinanceLedgerViewModel
import com.example.ui.screens.finance.FinanceLedgerViewModelFactory
import com.example.ui.screens.finance.PaymentDashboardScreen
import com.example.ui.screens.finance.PaymentDashboardViewModel
import com.example.ui.screens.finance.PaymentDashboardViewModelFactory
import com.example.ui.screens.finance.TransactionAddEditScreen
import com.example.ui.screens.finance.TransactionAddEditViewModel
import com.example.ui.screens.finance.TransactionAddEditViewModelFactory
import com.example.ui.screens.finance.PaymentRemindersScreen
import com.example.ui.screens.finance.PaymentRemindersViewModel
import com.example.ui.screens.finance.PaymentRemindersViewModelFactory
import com.example.ui.screens.resource.ResourceDashboardScreen
import com.example.ui.screens.resource.ResourceDashboardViewModel
import com.example.ui.screens.resource.ResourceDashboardViewModelFactory
import com.example.ui.screens.resource.MaterialListScreen
import com.example.ui.screens.resource.MaterialListViewModel
import com.example.ui.screens.resource.MaterialListViewModelFactory
import com.example.ui.screens.resource.MaterialAddEditScreen
import com.example.ui.screens.resource.MaterialAddEditViewModel
import com.example.ui.screens.resource.MaterialAddEditViewModelFactory
import com.example.ui.screens.resource.WorkerListScreen
import com.example.ui.screens.resource.WorkerListViewModel
import com.example.ui.screens.resource.WorkerListViewModelFactory
import com.example.ui.screens.resource.WorkerAddEditScreen
import com.example.ui.screens.resource.WorkerAddEditViewModel
import com.example.ui.screens.resource.WorkerAddEditViewModelFactory
import com.example.ui.screens.resource.SupplierListScreen
import com.example.ui.screens.resource.SupplierListViewModel
import com.example.ui.screens.resource.SupplierListViewModelFactory
import com.example.ui.screens.resource.SupplierAddEditScreen
import com.example.ui.screens.resource.SupplierAddEditViewModel
import com.example.ui.screens.resource.SupplierAddEditViewModelFactory
import com.example.ui.screens.resource.PayrollScreen
import com.example.ui.screens.resource.PayrollViewModel
import com.example.ui.screens.resource.PayrollViewModelFactory
import com.example.ui.screens.resource.MaterialUsageScreen
import com.example.ui.screens.resource.MaterialUsageViewModel
import com.example.ui.screens.resource.MaterialUsageViewModelFactory
import com.example.ui.screens.resource.WorkerPaymentHistoryScreen
import com.example.ui.screens.resource.WorkerPaymentHistoryViewModel
import com.example.ui.screens.resource.WorkerPaymentHistoryViewModelFactory
import com.example.ui.screens.documentation.DocumentationDashboardScreen
import com.example.ui.screens.documentation.DocumentationViewModel
import com.example.ui.screens.documentation.DocumentationViewModelFactory
import com.example.ui.screens.documentation.CameraScreen
import com.example.ui.screens.documentation.PhotoDetailsScreen
import com.example.ui.screens.documentation.DocumentDetailsScreen
import com.example.ui.screens.attendance.AttendanceDailyScreen
import com.example.ui.screens.attendance.AttendanceHistoryScreen
import com.example.ui.screens.attendance.AttendanceHistoryViewModel
import com.example.ui.screens.attendance.AttendanceHistoryViewModelFactory
import com.example.ui.screens.attendance.AttendanceViewModel
import com.example.ui.screens.attendance.AttendanceViewModelFactory
import com.example.ui.screens.reports.ReportsScreen
import com.example.ui.screens.reports.ReportsViewModel
import com.example.ui.screens.reports.ReportsViewModelFactory



@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = {
            androidx.compose.animation.fadeIn(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { it / 6 },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.9f,
                    stiffness = 500f
                )
            )
        },
        exitTransition = {
            androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(200)
            ) + androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { -it / 6 },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.9f,
                    stiffness = 500f
                )
            )
        },
        popEnterTransition = {
            androidx.compose.animation.fadeIn(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { -it / 6 },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.9f,
                    stiffness = 500f
                )
            )
        },
        popExitTransition = {
            androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(200)
            ) + androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { it / 6 },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.9f,
                    stiffness = 500f
                )
            )
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = {
                navController.navigate(Screen.AuthGraph.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        navigation(startDestination = Screen.PinVerify.route, route = Screen.AuthGraph.route) {
            composable(Screen.PinVerify.route) {
                PinScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onUseBiometric = { }
                )
            }
            composable(Screen.PinSetup.route) {
                PinScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.MainGraph.route) {
                            popUpTo(Screen.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onUseBiometric = { }
                )
            }
        }

        navigation(startDestination = Screen.Dashboard.route, route = Screen.MainGraph.route) {
            composable(Screen.Dashboard.route) {
                val viewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.Factory(appContainer.getDashboardStatsUseCase)
                )
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToClients = { navController.navigate(Screen.Clients.route) },
                    onNavigateToProjects = { navController.navigate(Screen.Projects.route) },
                    onNavigateToResources = { navController.navigate(Screen.ResourceDashboard.route) },
                    onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                    onNavigateToPayments = { navController.navigate(Screen.PaymentDashboard.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                    onNavigateToSitePhotos = { navController.navigate(Screen.Projects.route) }
                )
            }

            composable(Screen.Projects.route) {
                val viewModel: ProjectListViewModel = viewModel(
                    factory = ProjectListViewModelFactory(
                        appContainer.getProjectsUseCase
                    )
                )
                ProjectListScreen(
                    viewModel = viewModel,
                    onNavigateToAddProject = { navController.navigate(Screen.ProjectAdd.route) },
                    onNavigateToProjectDetails = { projectId -> navController.navigate(Screen.ProjectDetails.createRoute(projectId)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ProjectAdd.route) {
                val viewModel: ProjectAddEditViewModel = viewModel(
                    factory = ProjectAddEditViewModelFactory(
                        null,
                        appContainer.getProjectByIdUseCase,
                        appContainer.saveProjectUseCase,
                        appContainer.saveProjectTimelineEventUseCase,
                        appContainer.saveMilestonesUseCase,
                        appContainer.getClientsUseCase
                    )
                )
                ProjectAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.ProjectEdit.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")
                val viewModel: ProjectAddEditViewModel = viewModel(
                    factory = ProjectAddEditViewModelFactory(
                        projectId,
                        appContainer.getProjectByIdUseCase,
                        appContainer.saveProjectUseCase,
                        appContainer.saveProjectTimelineEventUseCase,
                        appContainer.saveMilestonesUseCase,
                        appContainer.getClientsUseCase
                    )
                )
                ProjectAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ProjectDetails.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val viewModel: ProjectDetailsViewModel = viewModel(
                    factory = ProjectDetailsViewModelFactory(
                        projectId,
                        appContainer.getProjectByIdUseCase,
                        appContainer.getClientByIdUseCase,
                        appContainer.getProjectMilestonesUseCase,
                        appContainer.getProjectTimelineUseCase,
                        appContainer.saveMilestoneUseCase
                    )
                )
                ProjectDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditProject = { id -> navController.navigate(Screen.ProjectEdit.createRoute(id)) },
                    onNavigateToSiteDiaries = { id -> navController.navigate(Screen.SiteDiaryList.createRoute(id)) },
                    onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) },
                    onNavigateToDocumentation = { navController.navigate(Screen.DocumentationDashboard.createRoute(projectId!!)) }
                )
            }
            
            composable(Screen.Clients.route) {
                val viewModel: ClientViewModel = viewModel(
                    factory = ClientViewModelFactory(
                        appContainer.getClientsUseCase,
                        appContainer.deleteClientUseCase,
                        appContainer.saveClientUseCase
                    )
                )
                ClientListScreen(
                    viewModel = viewModel,
                    onNavigateToAddClient = { navController.navigate(Screen.ClientAdd.route) },
                    onNavigateToClientDetails = { clientId -> navController.navigate(Screen.ClientDetails.createRoute(clientId)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.ClientAdd.route) {
                val viewModel: ClientAddEditViewModel = viewModel(
                    factory = ClientAddEditViewModelFactory(
                        null,
                        appContainer.getClientByIdUseCase,
                        appContainer.saveClientUseCase,
                        appContainer.validateClientPhoneUseCase
                    )
                )
                ClientAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.ClientEdit.route,
                arguments = listOf(navArgument("clientId") { type = NavType.StringType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getString("clientId")
                val viewModel: ClientAddEditViewModel = viewModel(
                    factory = ClientAddEditViewModelFactory(
                        clientId,
                        appContainer.getClientByIdUseCase,
                        appContainer.saveClientUseCase,
                        appContainer.validateClientPhoneUseCase
                    )
                )
                ClientAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.ClientDetails.route,
                arguments = listOf(navArgument("clientId") { type = NavType.StringType })
            ) { backStackEntry ->
                val clientId = backStackEntry.arguments?.getString("clientId") ?: return@composable
                val viewModel: ClientDetailsViewModel = viewModel(
                    factory = ClientDetailsViewModelFactory(
                        clientId,
                        appContainer.getClientByIdUseCase,
                        appContainer.deleteClientUseCase
                    )
                )
                ClientDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditClient = { id -> navController.navigate(Screen.ClientEdit.createRoute(id)) }
                )
            }
            
            composable(
                route = Screen.SiteDiaryList.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val viewModel: SiteDiaryListViewModel = viewModel(
                    factory = SiteDiaryListViewModelFactory(
                        projectId,
                        appContainer.getProjectByIdUseCase,
                        appContainer.getSiteDiariesForProjectUseCase,
                        appContainer.getOrCreateSiteDiaryForDateUseCase
                    )
                )
                SiteDiaryListScreen(
                    viewModel = viewModel,
                    onNavigateToDiaryDetails = { diaryId -> navController.navigate(Screen.SiteDiaryDetails.createRoute(diaryId)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.SiteDiaryDetails.route,
                arguments = listOf(navArgument("diaryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val diaryId = backStackEntry.arguments?.getString("diaryId") ?: return@composable
                val viewModel: SiteDiaryDetailsViewModel = viewModel(
                    factory = SiteDiaryDetailsViewModelFactory(
                        diaryId,
                        appContainer.getProjectByIdUseCase,
                        appContainer.getSiteDiaryDetailsUseCase,
                        appContainer.saveSiteDiaryDetailsUseCase
                    )
                )
                SiteDiaryDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.FinanceLedger.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val viewModel: FinanceLedgerViewModel = viewModel(
                    factory = FinanceLedgerViewModelFactory(
                        projectId,
                        appContainer.getProjectByIdUseCase,
                        appContainer.getTransactionsForProjectUseCase
                    )
                )
                FinanceLedgerScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAddTransaction = { navController.navigate(Screen.TransactionAdd.createRoute(projectId)) },
                    onNavigateToEditTransaction = { transactionId -> navController.navigate(Screen.TransactionEdit.createRoute(projectId, transactionId)) }
                )
            }
            
            composable(
                route = Screen.TransactionAdd.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val viewModel: TransactionAddEditViewModel = viewModel(
                    factory = TransactionAddEditViewModelFactory(
                        null,
                        projectId,
                        appContainer.getTransactionByIdUseCase,
                        appContainer.saveTransactionUseCase,
                        appContainer.deleteTransactionUseCase
                    )
                )
                TransactionAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.TransactionEdit.route,
                arguments = listOf(
                    navArgument("projectId") { type = NavType.StringType },
                    navArgument("transactionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: return@composable
                val viewModel: TransactionAddEditViewModel = viewModel(
                    factory = TransactionAddEditViewModelFactory(
                        transactionId,
                        projectId,
                        appContainer.getTransactionByIdUseCase,
                        appContainer.saveTransactionUseCase,
                        appContainer.deleteTransactionUseCase
                    )
                )
                TransactionAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Finance.route) {
                val viewModel: com.example.ui.screens.reports.ReportsViewModel = viewModel(
                    factory = com.example.ui.screens.reports.ReportsViewModelFactory(
                        appContainer.transactionRepository,
                        appContainer.projectRepository,
                        appContainer.resourceRepository
                    )
                )
                com.example.ui.screens.reports.ReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                com.example.ui.screens.settings.SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToPinSetup = {
                        navController.navigate(Screen.PinSetup.route)
                    }
                )
            }
            
            composable(Screen.PaymentDashboard.route) {
                val viewModel: PaymentDashboardViewModel = viewModel(
                    factory = PaymentDashboardViewModelFactory(
                        appContainer.transactionRepository,
                        appContainer.projectRepository
                    )
                )
                PaymentDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.PaymentReminders.route) {
                val viewModel: PaymentRemindersViewModel = viewModel(
                    factory = PaymentRemindersViewModelFactory(
                        appContainer.projectRepository,
                        appContainer.transactionRepository
                    )
                )
                PaymentRemindersScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.DocumentationDashboard.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                val viewModel: DocumentationViewModel = viewModel(
                    factory = DocumentationViewModelFactory(projectId, appContainer.documentationRepository)
                )
                DocumentationDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToCamera = { navController.navigate(Screen.Camera.createRoute(projectId)) },
                    onNavigateToUpload = { /* TODO */ },
                    onNavigateToPhotoDetail = { id -> navController.navigate(Screen.PhotoDetails.createRoute(id)) },
                    onNavigateToDocumentDetail = { id -> navController.navigate(Screen.DocumentDetails.createRoute(id)) }
                )
            }

            composable(
                route = Screen.Camera.route,
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable
                CameraScreen(
                    projectId = projectId,
                    repository = appContainer.documentationRepository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.PhotoDetails.route,
                arguments = listOf(navArgument("photoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val photoId = backStackEntry.arguments?.getString("photoId") ?: return@composable
                PhotoDetailsScreen(
                    photoId = photoId,
                    repository = appContainer.documentationRepository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.DocumentDetails.route,
                arguments = listOf(navArgument("documentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
                DocumentDetailsScreen(
                    documentId = documentId,
                    repository = appContainer.documentationRepository,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ResourceDashboard.route) {
                val viewModel: ResourceDashboardViewModel = viewModel(
                    factory = ResourceDashboardViewModelFactory(appContainer.resourceRepository)
                )
                ResourceDashboardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToMaterials = { navController.navigate(Screen.MaterialList.route) },
                    onNavigateToWorkers = { navController.navigate(Screen.WorkerList.route) },
                    onNavigateToSuppliers = { navController.navigate(Screen.SupplierList.route) },
                    onNavigateToAttendance = { navController.navigate(Screen.AttendanceDaily.route) },
                    onNavigateToPayroll = { navController.navigate(Screen.Payroll.route) }
                )
            }

            composable(Screen.MaterialList.route) {
                val viewModel: MaterialListViewModel = viewModel(
                    factory = MaterialListViewModelFactory(appContainer.resourceRepository)
                )
                MaterialListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.MaterialAdd.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.MaterialEdit.createRoute(id)) }
                )
            }

            composable(Screen.MaterialAdd.route) {
                val viewModel: MaterialAddEditViewModel = viewModel(
                    factory = MaterialAddEditViewModelFactory(null, appContainer.resourceRepository)
                )
                MaterialAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.MaterialEdit.route,
                arguments = listOf(navArgument("materialId") { type = NavType.StringType })
            ) { backStackEntry ->
                val materialId = backStackEntry.arguments?.getString("materialId")
                val viewModel: MaterialAddEditViewModel = viewModel(
                    factory = MaterialAddEditViewModelFactory(materialId, appContainer.resourceRepository)
                )
                MaterialAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.WorkerList.route) {
                val viewModel: WorkerListViewModel = viewModel(
                    factory = WorkerListViewModelFactory(appContainer.resourceRepository)
                )
                WorkerListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.WorkerAdd.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.WorkerEdit.createRoute(id)) }
                )
            }

            composable(Screen.WorkerAdd.route) {
                val viewModel: WorkerAddEditViewModel = viewModel(
                    factory = WorkerAddEditViewModelFactory(null, appContainer.resourceRepository)
                )
                WorkerAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.WorkerEdit.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId")
                val viewModel: WorkerAddEditViewModel = viewModel(
                    factory = WorkerAddEditViewModelFactory(workerId, appContainer.resourceRepository)
                )
                WorkerAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SupplierList.route) {
                val viewModel: SupplierListViewModel = viewModel(
                    factory = SupplierListViewModelFactory(appContainer.resourceRepository)
                )
                SupplierListScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAdd = { navController.navigate(Screen.SupplierAdd.route) },
                    onNavigateToEdit = { id -> navController.navigate(Screen.SupplierEdit.createRoute(id)) }
                )
            }

            composable(Screen.SupplierAdd.route) {
                val viewModel: SupplierAddEditViewModel = viewModel(
                    factory = SupplierAddEditViewModelFactory(null, appContainer.resourceRepository)
                )
                SupplierAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.SupplierEdit.route,
                arguments = listOf(navArgument("supplierId") { type = NavType.StringType })
            ) { backStackEntry ->
                val supplierId = backStackEntry.arguments?.getString("supplierId")
                val viewModel: SupplierAddEditViewModel = viewModel(
                    factory = SupplierAddEditViewModelFactory(supplierId, appContainer.resourceRepository)
                )
                SupplierAddEditScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Attendance
            composable(Screen.AttendanceDaily.route) {
                val viewModel: AttendanceViewModel = viewModel(
                    factory = AttendanceViewModelFactory(appContainer.resourceRepository)
                )
                AttendanceDailyScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AttendanceHistory.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: return@composable
                val viewModel: AttendanceHistoryViewModel = viewModel(
                    factory = AttendanceHistoryViewModelFactory(workerId, appContainer.resourceRepository)
                )
                AttendanceHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Payroll.route) {
                val viewModel: PayrollViewModel = viewModel(
                    factory = PayrollViewModelFactory(appContainer.resourceRepository, appContainer.transactionRepository)
                )
                PayrollScreen(viewModel = viewModel, onNavigateBack = { navController.popBackStack() })
            }

            // Material Usage
            composable(Screen.MaterialUsage.route) {
                val viewModel: MaterialUsageViewModel = viewModel(
                    factory = MaterialUsageViewModelFactory(null, appContainer.resourceRepository)
                )
                MaterialUsageScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Worker Payment History
            composable(
                route = Screen.WorkerPaymentHistory.route,
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workerId = backStackEntry.arguments?.getString("workerId") ?: return@composable
                val viewModel: WorkerPaymentHistoryViewModel = viewModel(
                    factory = WorkerPaymentHistoryViewModelFactory(workerId, appContainer.resourceRepository, appContainer.transactionRepository)
                )
                WorkerPaymentHistoryScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Reports
            composable(Screen.Reports.route) {
                val viewModel: ReportsViewModel = viewModel(
                    factory = ReportsViewModelFactory(
                        appContainer.transactionRepository,
                        appContainer.projectRepository,
                        appContainer.resourceRepository
                    )
                )
                ReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Global Search
            composable(Screen.Search.route) {
                val viewModel: com.example.ui.screens.search.SearchViewModel = viewModel(
                    factory = com.example.ui.screens.search.SearchViewModelFactory(appContainer.globalSearchUseCase)
                )
                com.example.ui.screens.search.SearchScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToResult = { route -> navController.navigate(route) }
                )
            }
        }
    }
}

@Composable
fun PlaceholderScreen(title: String, onClick: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Button(onClick = onClick) {
            Text(text = title)
        }
    }
}
