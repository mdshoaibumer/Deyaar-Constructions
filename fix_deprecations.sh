#!/bin/bash
sed -i 's/fallbackToDestructiveMigration()/fallbackToDestructiveMigration(dropAllTables = true)/g' app/src/main/java/com/example/di/AppContainer.kt
sed -i 's/Icons.Filled.ArrowBack/Icons.AutoMirrored.Filled.ArrowBack/g' app/src/main/java/com/example/ui/screens/client/ClientListScreen.kt
sed -i 's/Icons.Default.ArrowBack/Icons.AutoMirrored.Filled.ArrowBack/g' app/src/main/java/com/example/ui/screens/client/ClientListScreen.kt
sed -i 's/Icons.Filled.ArrowBack/Icons.AutoMirrored.Filled.ArrowBack/g' app/src/main/java/com/example/ui/screens/project/ProjectListScreen.kt
sed -i 's/Icons.Default.ArrowBack/Icons.AutoMirrored.Filled.ArrowBack/g' app/src/main/java/com/example/ui/screens/project/ProjectListScreen.kt
