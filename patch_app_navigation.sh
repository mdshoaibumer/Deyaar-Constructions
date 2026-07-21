#!/bin/bash
sed -i '/import com.example.ui.screens.finance.TransactionAddEditViewModelFactory/a\
import com.example.ui.screens.resource.*\
' app/src/main/java/com/example/ui/navigation/AppNavigation.kt

sed -i 's/onNavigateToProjects = { navController.navigate(Screen.Projects.route) }/onNavigateToProjects = { navController.navigate(Screen.Projects.route) },\
                    onNavigateToResources = { navController.navigate(Screen.ResourceDashboard.route) }/' app/src/main/java/com/example/ui/navigation/AppNavigation.kt

sed -i '/composable(Screen.Settings.route) { PlaceholderScreen("Settings") }/a\
\
            composable(Screen.ResourceDashboard.route) {\
                ResourceDashboardScreen(\
                    onNavigateBack = { navController.popBackStack() },\
                    onNavigateToMaterials = { navController.navigate(Screen.MaterialList.route) },\
                    onNavigateToWorkers = { navController.navigate(Screen.WorkerList.route) },\
                    onNavigateToSuppliers = { navController.navigate(Screen.SupplierList.route) }\
                )\
            }\
\
            composable(Screen.MaterialList.route) {\
                val viewModel: MaterialListViewModel = viewModel(\
                    factory = MaterialListViewModelFactory(appContainer.resourceRepository)\
                )\
                MaterialListScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() },\
                    onNavigateToAdd = { navController.navigate(Screen.MaterialAdd.route) },\
                    onNavigateToEdit = { id -> navController.navigate(Screen.MaterialEdit.createRoute(id)) }\
                )\
            }\
\
            composable(Screen.MaterialAdd.route) {\
                val viewModel: MaterialAddEditViewModel = viewModel(\
                    factory = MaterialAddEditViewModelFactory(null, appContainer.resourceRepository)\
                )\
                MaterialAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(\
                route = Screen.MaterialEdit.route,\
                arguments = listOf(navArgument("materialId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val materialId = backStackEntry.arguments?.getString("materialId")\
                val viewModel: MaterialAddEditViewModel = viewModel(\
                    factory = MaterialAddEditViewModelFactory(materialId, appContainer.resourceRepository)\
                )\
                MaterialAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(Screen.WorkerList.route) {\
                val viewModel: WorkerListViewModel = viewModel(\
                    factory = WorkerListViewModelFactory(appContainer.resourceRepository)\
                )\
                WorkerListScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() },\
                    onNavigateToAdd = { navController.navigate(Screen.WorkerAdd.route) },\
                    onNavigateToEdit = { id -> navController.navigate(Screen.WorkerEdit.createRoute(id)) }\
                )\
            }\
\
            composable(Screen.WorkerAdd.route) {\
                val viewModel: WorkerAddEditViewModel = viewModel(\
                    factory = WorkerAddEditViewModelFactory(null, appContainer.resourceRepository)\
                )\
                WorkerAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(\
                route = Screen.WorkerEdit.route,\
                arguments = listOf(navArgument("workerId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val workerId = backStackEntry.arguments?.getString("workerId")\
                val viewModel: WorkerAddEditViewModel = viewModel(\
                    factory = WorkerAddEditViewModelFactory(workerId, appContainer.resourceRepository)\
                )\
                WorkerAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(Screen.SupplierList.route) {\
                val viewModel: SupplierListViewModel = viewModel(\
                    factory = SupplierListViewModelFactory(appContainer.resourceRepository)\
                )\
                SupplierListScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() },\
                    onNavigateToAdd = { navController.navigate(Screen.SupplierAdd.route) },\
                    onNavigateToEdit = { id -> navController.navigate(Screen.SupplierEdit.createRoute(id)) }\
                )\
            }\
\
            composable(Screen.SupplierAdd.route) {\
                val viewModel: SupplierAddEditViewModel = viewModel(\
                    factory = SupplierAddEditViewModelFactory(null, appContainer.resourceRepository)\
                )\
                SupplierAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }\
\
            composable(\
                route = Screen.SupplierEdit.route,\
                arguments = listOf(navArgument("supplierId") { type = NavType.StringType })\
            ) { backStackEntry ->\
                val supplierId = backStackEntry.arguments?.getString("supplierId")\
                val viewModel: SupplierAddEditViewModel = viewModel(\
                    factory = SupplierAddEditViewModelFactory(supplierId, appContainer.resourceRepository)\
                )\
                SupplierAddEditScreen(\
                    viewModel = viewModel,\
                    onNavigateBack = { navController.popBackStack() }\
                )\
            }' app/src/main/java/com/example/ui/navigation/AppNavigation.kt
