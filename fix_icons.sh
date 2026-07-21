#!/bin/bash
sed -i '/import androidx.compose.material.icons.filled.Add/a\
import androidx.compose.material.icons.automirrored.filled.ArrowBack' app/src/main/java/com/example/ui/screens/client/ClientListScreen.kt
sed -i '/import androidx.compose.material.icons.filled.Add/a\
import androidx.compose.material.icons.automirrored.filled.ArrowBack' app/src/main/java/com/example/ui/screens/project/ProjectListScreen.kt
sed -i '/import androidx.compose.material.icons.Icons/a\
import androidx.compose.material.icons.automirrored.filled.ArrowBack' app/src/main/java/com/example/ui/screens/client/ClientListScreen.kt
sed -i '/import androidx.compose.material.icons.Icons/a\
import androidx.compose.material.icons.automirrored.filled.ArrowBack' app/src/main/java/com/example/ui/screens/project/ProjectListScreen.kt

