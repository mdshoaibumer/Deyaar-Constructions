import re

# Fix ProjectDetailsScreen.kt
with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('onNavigateToDocumentation: () -> Unit = {}', 'onNavigateToDocumentation: (String) -> Unit = {}')

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'w') as f:
    f.write(content)

# Fix AppNavigation.kt
with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

nav_str = """                ProjectDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditProject = { id -> navController.navigate(Screen.ProjectEdit.createRoute(id)) },
                    onNavigateToSiteDiaries = { id -> navController.navigate(Screen.SiteDiaryList.createRoute(id)) },
                    onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) }"""
                    
new_nav_str = """                ProjectDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEditProject = { id -> navController.navigate(Screen.ProjectEdit.createRoute(id)) },
                    onNavigateToSiteDiaries = { id -> navController.navigate(Screen.SiteDiaryList.createRoute(id)) },
                    onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) },
                    onNavigateToDocumentation = { id -> navController.navigate(Screen.DocumentationDashboard.createRoute(id)) }"""

content = content.replace(nav_str, new_nav_str)

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)

