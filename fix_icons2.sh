#!/bin/bash
sed -i '/import androidx.compose.material.icons.filled.Add/a\
import androidx.compose.material.icons.outlined.Folder\
import androidx.compose.material.icons.outlined.AccountBalance\
import androidx.compose.material.icons.outlined.Book' app/src/main/java/com/example/ui/screens/project/ProjectDetailsScreen.kt
