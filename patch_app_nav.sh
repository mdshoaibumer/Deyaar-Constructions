#!/bin/bash
sed -i 's/onNavigateToSiteDiaries = { id -> navController.navigate(Screen.SiteDiaryList.createRoute(id)) }/onNavigateToSiteDiaries = { id -> navController.navigate(Screen.SiteDiaryList.createRoute(id)) },\n                    onNavigateToFinanceLedger = { id -> navController.navigate(Screen.FinanceLedger.createRoute(id)) }/' app/src/main/java/com/example/ui/navigation/AppNavigation.kt
