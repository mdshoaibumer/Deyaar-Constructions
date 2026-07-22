package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    
    // Auth
    data object AuthGraph : Screen("auth_graph")
    data object PinVerify : Screen("pin_verify")
    data object PinSetup : Screen("pin_setup")

    // Main
    data object MainGraph : Screen("main_graph")
    data object Dashboard : Screen("dashboard")
    data object Projects : Screen("projects")
    data object ProjectAdd : Screen("project_add")
    object ProjectEdit {
        const val route = "project_edit/{projectId}"
        fun createRoute(projectId: String) = "project_edit/$projectId"
    }
    object ProjectDetails {
        const val route = "project_details/{projectId}"
        fun createRoute(projectId: String) = "project_details/$projectId"
    }
    
    // Clients
    data object Clients : Screen("clients")
    data object ClientAdd : Screen("client_add")
    object ClientEdit {
        const val route = "client_edit/{clientId}"
        fun createRoute(clientId: String) = "client_edit/$clientId"
    }
    object ClientDetails {
        const val route = "client_details/{clientId}"
        fun createRoute(clientId: String) = "client_details/$clientId"
    }
    
    object SiteDiaryList {
        const val route = "site_diary_list/{projectId}"
        fun createRoute(projectId: String) = "site_diary_list/$projectId"
    }

    object SiteDiaryDetails {
        const val route = "site_diary_details/{diaryId}"
        fun createRoute(diaryId: String) = "site_diary_details/$diaryId"
    }
    
    object FinanceLedger {
        const val route = "finance_ledger/{projectId}"
        fun createRoute(projectId: String) = "finance_ledger/$projectId"
    }

    object TransactionAdd {
        const val route = "transaction_add/{projectId}"
        fun createRoute(projectId: String) = "transaction_add/$projectId"
    }

    object TransactionEdit {
        const val route = "transaction_edit/{projectId}/{transactionId}"
        fun createRoute(projectId: String, transactionId: String) = "transaction_edit/$projectId/$transactionId"
    }
    
    data object Finance : Screen("finance")
    data object Settings : Screen("settings")
    
    // Documentation
    object DocumentationDashboard {
        const val route = "documentation_dashboard/{projectId}"
        fun createRoute(projectId: String) = "documentation_dashboard/$projectId"
    }
    object Camera {
        const val route = "camera/{projectId}"
        fun createRoute(projectId: String) = "camera/$projectId"
    }
    object PhotoDetails {
        const val route = "photo_details/{photoId}"
        fun createRoute(photoId: String) = "photo_details/$photoId"
    }
    object DocumentDetails {
        const val route = "document_details/{documentId}"
        fun createRoute(documentId: String) = "document_details/$documentId"
    }

    
    // Resources
    data object ResourceDashboard : Screen("resource_dashboard")
    data object MaterialList : Screen("material_list")
    data object MaterialAdd : Screen("material_add")
    object MaterialEdit {
        const val route = "material_edit/{materialId}"
        fun createRoute(materialId: String) = "material_edit/$materialId"
    }
    data object WorkerList : Screen("worker_list")
    data object WorkerAdd : Screen("worker_add")
    object WorkerEdit {
        const val route = "worker_edit/{workerId}"
        fun createRoute(workerId: String) = "worker_edit/$workerId"
    }
    data object SupplierList : Screen("supplier_list")
    data object SupplierAdd : Screen("supplier_add")
    object SupplierEdit {
        const val route = "supplier_edit/{supplierId}"
        fun createRoute(supplierId: String) = "supplier_edit/$supplierId"
    }

    // Attendance
    data object AttendanceDaily : Screen("attendance_daily")
    object AttendanceHistory {
        const val route = "attendance_history/{workerId}"
        fun createRoute(workerId: String) = "attendance_history/$workerId"
    }

    // Material Usage
    data object MaterialUsage : Screen("material_usage")

    // Worker Payment History
    object WorkerPaymentHistory {
        const val route = "worker_payment_history/{workerId}"
        fun createRoute(workerId: String) = "worker_payment_history/$workerId"
    }

    // Reports
    data object Reports : Screen("reports")
}
