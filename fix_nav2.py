import re

# Fix AppNavigation.kt
with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'r') as f:
    content = f.read()

content = content.replace('                    onNavigateToDocumentation = { id -> navController.navigate(Screen.DocumentationDashboard.createRoute(id)) },\n', '')

with open('app/src/main/java/com/example/ui/navigation/AppNavigation.kt', 'w') as f:
    f.write(content)

# Fix ProjectDetailsScreen.kt
with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('    onNavigateToDocumentation: (String) -> Unit = {}', '    onNavigateToDocumentation: () -> Unit = {}')

old_qa = """                item {
                    QuickActionsPlaceholder(
                        onNavigateToSiteDiaries = { onNavigateToSiteDiaries(project.id) },
                        onNavigateToFinanceLedger = { onNavigateToFinanceLedger(project.id) },
                        onNavigateToDocumentation = { onNavigateToDocumentation(project.id) }
                    )
                }"""

new_qa = """                item {
                    QuickActionsPlaceholder(
                        onNavigateToSiteDiaries = { onNavigateToSiteDiaries(project.id) },
                        onNavigateToFinanceLedger = { onNavigateToFinanceLedger(project.id) },
                        onNavigateToDocumentation = onNavigateToDocumentation
                    )
                }"""
content = content.replace(old_qa, new_qa)

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'w') as f:
    f.write(content)
