#!/bin/bash
sed -i 's/onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) }/onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) },\
                    onNavigateToDocumentation = { navController.navigate(Screen.DocumentationDashboard.createRoute(projectId!!)) }/' app/src/main/java/com/example/ui/navigation/AppNavigation.kt

sed -i '/import com.example.ui.screens.resource.\*/a\
import com.example.ui.screens.documentation.*\
' app/src/main/java/com/example/ui/navigation/AppNavigation.kt

sed -i '/composable(Screen.ResourceDashboard.route)/i\
            composable(\
                route = Screen.DocumentationDashboard.route,\
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable\
                val viewModel: DocumentationViewModel = viewModel(\
                    factory = DocumentationViewModelFactory(projectId, appContainer.documentationRepository)\
                )\
                DocumentationDashboardScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() },\
                    onNavigateToCamera = { navController.navigate(Screen.Camera.createRoute(projectId)) },\
                    onNavigateToUpload = { /* TODO */ },\
                    onNavigateToPhotoDetail = { id -> navController.navigate(Screen.PhotoDetails.createRoute(id)) },\
                    onNavigateToDocumentDetail = { id -> navController.navigate(Screen.DocumentDetails.createRoute(id)) }\
                )\
            }\
\
            composable(\
                route = Screen.Camera.route,\
                arguments = listOf(navArgument("projectId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val projectId = backStackEntry.arguments?.getString("projectId") ?: return@composable\
                CameraScreen(\
                    projectId = projectId,\
                    repository = appContainer.documentationRepository,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(\
                route = Screen.PhotoDetails.route,\
                arguments = listOf(navArgument("photoId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val photoId = backStackEntry.arguments?.getString("photoId") ?: return@composable\
                PhotoDetailsScreen(\
                    photoId = photoId,\
                    repository = appContainer.documentationRepository,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(\
                route = Screen.DocumentDetails.route,\
                arguments = listOf(navArgument("documentId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable\
                DocumentDetailsScreen(\
                    documentId = documentId,\
                    repository = appContainer.documentationRepository,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
' app/src/main/java/com/example/ui/navigation/AppNavigation.kt
