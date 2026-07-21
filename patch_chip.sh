#!/bin/bash
sed -i '/label = { Text("Daily Site Diary") }/a\
            )\
            AssistChip(\
                onClick = onNavigateToFinanceLedger,\
                label = { Text("Financial Ledger") }' app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt
