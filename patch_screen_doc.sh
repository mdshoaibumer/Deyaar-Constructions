#!/bin/bash
sed -i '/data object Settings : Screen("settings")/a\
    \
    // Documentation\
    object DocumentationDashboard {\
        const val route = "documentation_dashboard/{projectId}"\
        fun createRoute(projectId: String) = "documentation_dashboard/$projectId"\
    }\
    object Camera {\
        const val route = "camera/{projectId}"\
        fun createRoute(projectId: String) = "camera/$projectId"\
    }\
    object PhotoDetails {\
        const val route = "photo_details/{photoId}"\
        fun createRoute(photoId: String) = "photo_details/$photoId"\
    }\
    object DocumentDetails {\
        const val route = "document_details/{documentId}"\
        fun createRoute(documentId: String) = "document_details/$documentId"\
    }\
' app/src/main/java/com/example/ui/navigation/Screen.kt
