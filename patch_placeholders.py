import re

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'r') as f:
    content = f.read()

# Replace QuickActionsPlaceholder usages
old_usage = """                item {
                    QuickActionsPlaceholder(
                        onNavigateToSiteDiaries = { onNavigateToSiteDiaries(project.id) },
                        onNavigateToFinanceLedger = { onNavigateToFinanceLedger(project.id) }
                    )
                }"""

new_usage = """                item {
                    QuickActionsPlaceholder(
                        onNavigateToSiteDiaries = { onNavigateToSiteDiaries(project.id) },
                        onNavigateToFinanceLedger = { onNavigateToFinanceLedger(project.id) },
                        onNavigateToDocumentation = { onNavigateToDocumentation(project.id) }
                    )
                }"""
content = content.replace(old_usage, new_usage)

old_def = """            actions.forEach { action ->
                AssistChip(
                    onClick = { /* Placeholder */ },
                    label = { Text(action) }
                )
            }"""

new_def = """            AssistChip(onClick = onNavigateToDocumentation, label = { Text("Documentation & Photos") })
        }
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AssistChip(onClick = onNavigateToSiteDiaries, label = { Text("Site Diaries") })
            AssistChip(onClick = onNavigateToFinanceLedger, label = { Text("Finance Ledger") })"""
content = content.replace(old_def, new_def)

# Replace DocumentsPlaceholder
old_docs = """@Composable
fun DocumentsPlaceholder() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Documents & Files", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent documents. Upload blueprints, permits, or contracts here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}"""

content = content.replace(old_docs, "")
content = content.replace("""                item {
                    DocumentsPlaceholder()
                }""", "")

with open('app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt', 'w') as f:
    f.write(content)

