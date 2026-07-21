#!/bin/bash
sed -i 's/items(stats.recentProjects) { project ->/items(stats.recentProjects, key = { it.id }) { project ->/g' app/src/main/java/com/example/ui/screens/dashboard/DashboardScreen.kt
sed -i 's/items(uiState.materials) { material ->/items(uiState.materials, key = { it.id }) { material ->/g' app/src/main/java/com/example/ui/screens/resource/MaterialListScreen.kt
sed -i 's/items(uiState.workers) { worker ->/items(uiState.workers, key = { it.id }) { worker ->/g' app/src/main/java/com/example/ui/screens/resource/WorkerListScreen.kt
sed -i 's/items(uiState.suppliers) { supplier ->/items(uiState.suppliers, key = { it.id }) { supplier ->/g' app/src/main/java/com/example/ui/screens/resource/SupplierListScreen.kt
sed -i 's/items(photos) { photo ->/items(photos, key = { it.id }) { photo ->/g' app/src/main/java/com/example/ui/screens/documentation/DocumentationDashboardScreen.kt
sed -i 's/items(documents) { doc ->/items(documents, key = { it.id }) { doc ->/g' app/src/main/java/com/example/ui/screens/documentation/DocumentationDashboardScreen.kt
sed -i 's/items(combinedEvents) { event ->/items(combinedEvents, key = { it.id }) { event ->/g' app/src/main/java/com/example/ui/screens/documentation/DocumentationDashboardScreen.kt
