#!/bin/bash
sed -i 's/Locale("en", "IN")/java.util.Locale.Builder().setLanguage("en").setRegion("IN").build()/g' app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt
sed -i 's/Locale("en", "IN")/java.util.Locale.Builder().setLanguage("en").setRegion("IN").build()/g' app/src/main/java/com/example/ui/screens/project/ProjectListScreen.kt
sed -i 's/Locale("en", "IN")/java.util.Locale.Builder().setLanguage("en").setRegion("IN").build()/g' app/src/main/java/com/example/ui/screens/finance/FinanceLedgerScreen.kt

sed -i 's/menuAnchor()/menuAnchor(MenuAnchorType.PrimaryNotEditable)/g' app/src/main/java/com/example/ui/screens/project/ProjectAddEditScreen.kt
sed -i 's/menuAnchor()/menuAnchor(MenuAnchorType.PrimaryNotEditable)/g' app/src/main/java/com/example/ui/screens/finance/TransactionAddEditScreen.kt
sed -i 's/menuAnchor()/menuAnchor(MenuAnchorType.PrimaryNotEditable)/g' app/src/main/java/com/example/ui/screens/resource/MaterialAddEditScreen.kt
sed -i 's/menuAnchor()/menuAnchor(MenuAnchorType.PrimaryNotEditable)/g' app/src/main/java/com/example/ui/screens/resource/WorkerAddEditScreen.kt

sed -i '/import androidx.compose.material3.ExperimentalMaterial3Api/a\
import androidx.compose.material3.MenuAnchorType' app/src/main/java/com/example/ui/screens/project/ProjectAddEditScreen.kt
sed -i '/import androidx.compose.material3.ExperimentalMaterial3Api/a\
import androidx.compose.material3.MenuAnchorType' app/src/main/java/com/example/ui/screens/finance/TransactionAddEditScreen.kt
sed -i '/import androidx.compose.material3.ExperimentalMaterial3Api/a\
import androidx.compose.material3.MenuAnchorType' app/src/main/java/com/example/ui/screens/resource/MaterialAddEditScreen.kt
sed -i '/import androidx.compose.material3.ExperimentalMaterial3Api/a\
import androidx.compose.material3.MenuAnchorType' app/src/main/java/com/example/ui/screens/resource/WorkerAddEditScreen.kt
