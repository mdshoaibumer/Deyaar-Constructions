import re

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'r') as f:
    content = f.read()

# Fix QuickActionsPlaceholder
old_qa = """@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsPlaceholder(onNavigateToSiteDiaries: () -> Unit = {},
    onNavigateToFinanceLedger: () -> Unit = {},
    onNavigateToDocumentation: (String) -> Unit = {}) {"""

new_qa = """@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsPlaceholder(onNavigateToSiteDiaries: () -> Unit = {},
    onNavigateToFinanceLedger: () -> Unit = {},
    onNavigateToDocumentation: () -> Unit = {}) {"""
content = content.replace(old_qa, new_qa)

# Fix duplicate buttons
old_buttons = """            AssistChip(
                onClick = onNavigateToDocumentation,
                label = { Text("Documentation & Photos") }
            )
            val actions = listOf(
                "Add Expense", "Add Labour", "Add Material",
            )
            AssistChip(onClick = onNavigateToDocumentation, label = { Text("Documentation & Photos") })
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AssistChip(onClick = onNavigateToSiteDiaries, label = { Text("Site Diaries") })
            AssistChip(onClick = onNavigateToFinanceLedger, label = { Text("Finance Ledger") })
        }"""
        
new_buttons = """            AssistChip(
                onClick = onNavigateToDocumentation,
                label = { Text("Documentation & Photos") },
                leadingIcon = { Icon(Icons.Outlined.Folder, contentDescription = null) }
            )
            AssistChip(
                onClick = onNavigateToFinanceLedger,
                label = { Text("Financial Ledger") },
                leadingIcon = { Icon(Icons.Outlined.AccountBalance, contentDescription = null) }
            )
            AssistChip(
                onClick = onNavigateToSiteDiaries,
                label = { Text("Daily Site Diaries") },
                leadingIcon = { Icon(Icons.Outlined.Book, contentDescription = null) }
            )
        }"""
content = content.replace(old_buttons, new_buttons)

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'w') as f:
    f.write(content)

