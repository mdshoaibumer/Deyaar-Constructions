package com.example.ui.navigation

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for Screen route definitions and navigation patterns.
 * Verifies all route strings are correctly formed and argument extraction works.
 */
class NavigationTest {

    @Test
    fun screen_dashboard_hasCorrectRoute() {
        assertEquals("dashboard", Screen.Dashboard.route)
    }

    @Test
    fun screen_projects_hasCorrectRoute() {
        assertEquals("projects", Screen.Projects.route)
    }

    @Test
    fun screen_clients_hasCorrectRoute() {
        assertEquals("clients", Screen.Clients.route)
    }

    @Test
    fun screen_settings_hasCorrectRoute() {
        assertEquals("settings", Screen.Settings.route)
    }

    @Test
    fun screen_projectDetails_createRoute_insertsId() {
        val route = Screen.ProjectDetails.createRoute("proj_123")
        assertEquals("project_details/proj_123", route)
    }

    @Test
    fun screen_projectEdit_createRoute_insertsId() {
        val route = Screen.ProjectEdit.createRoute("proj_456")
        assertEquals("project_edit/proj_456", route)
    }

    @Test
    fun screen_clientDetails_createRoute_insertsId() {
        val route = Screen.ClientDetails.createRoute("client_789")
        assertEquals("client_details/client_789", route)
    }

    @Test
    fun screen_clientEdit_createRoute_insertsId() {
        val route = Screen.ClientEdit.createRoute("client_012")
        assertEquals("client_edit/client_012", route)
    }

    @Test
    fun screen_siteDiaryList_createRoute_insertsProjectId() {
        val route = Screen.SiteDiaryList.createRoute("proj_abc")
        assertEquals("site_diary_list/proj_abc", route)
    }

    @Test
    fun screen_siteDiaryDetails_createRoute_insertsDiaryId() {
        val route = Screen.SiteDiaryDetails.createRoute("diary_xyz")
        assertEquals("site_diary_details/diary_xyz", route)
    }

    @Test
    fun screen_financeLedger_createRoute_insertsProjectId() {
        val route = Screen.FinanceLedger.createRoute("proj_finance")
        assertEquals("finance_ledger/proj_finance", route)
    }

    @Test
    fun screen_transactionAdd_createRoute_insertsProjectId() {
        val route = Screen.TransactionAdd.createRoute("proj_txn")
        assertEquals("transaction_add/proj_txn", route)
    }

    @Test
    fun screen_transactionEdit_createRoute_insertsBothIds() {
        val route = Screen.TransactionEdit.createRoute("proj_001", "txn_002")
        assertEquals("transaction_edit/proj_001/txn_002", route)
    }

    @Test
    fun screen_documentationDashboard_createRoute_insertsProjectId() {
        val route = Screen.DocumentationDashboard.createRoute("proj_docs")
        assertEquals("documentation_dashboard/proj_docs", route)
    }

    @Test
    fun screen_camera_createRoute_insertsProjectId() {
        val route = Screen.Camera.createRoute("proj_cam")
        assertEquals("camera/proj_cam", route)
    }

    @Test
    fun screen_photoDetails_createRoute_insertsPhotoId() {
        val route = Screen.PhotoDetails.createRoute("photo_001")
        assertEquals("photo_details/photo_001", route)
    }

    @Test
    fun screen_documentDetails_createRoute_insertsDocId() {
        val route = Screen.DocumentDetails.createRoute("doc_001")
        assertEquals("document_details/doc_001", route)
    }

    @Test
    fun screen_materialEdit_createRoute_insertsMaterialId() {
        val route = Screen.MaterialEdit.createRoute("mat_steel")
        assertEquals("material_edit/mat_steel", route)
    }

    @Test
    fun screen_workerEdit_createRoute_insertsWorkerId() {
        val route = Screen.WorkerEdit.createRoute("worker_raj")
        assertEquals("worker_edit/worker_raj", route)
    }

    @Test
    fun screen_supplierEdit_createRoute_insertsSupplierId() {
        val route = Screen.SupplierEdit.createRoute("sup_ultra")
        assertEquals("supplier_edit/sup_ultra", route)
    }

    @Test
    fun screen_attendanceHistory_createRoute_insertsWorkerId() {
        val route = Screen.AttendanceHistory.createRoute("worker_001")
        assertEquals("attendance_history/worker_001", route)
    }

    @Test
    fun screen_workerPaymentHistory_createRoute_insertsWorkerId() {
        val route = Screen.WorkerPaymentHistory.createRoute("worker_pay")
        assertEquals("worker_payment_history/worker_pay", route)
    }

    @Test
    fun screen_routes_doNotContainSpaces() {
        val routes = listOf(
            Screen.Splash.route, Screen.Dashboard.route, Screen.Projects.route,
            Screen.ProjectAdd.route, Screen.Clients.route, Screen.ClientAdd.route,
            Screen.Finance.route, Screen.Settings.route, Screen.PaymentDashboard.route,
            Screen.ResourceDashboard.route, Screen.MaterialList.route, Screen.MaterialAdd.route,
            Screen.WorkerList.route, Screen.WorkerAdd.route, Screen.SupplierList.route,
            Screen.SupplierAdd.route, Screen.AttendanceDaily.route, Screen.MaterialUsage.route,
            Screen.Payroll.route, Screen.Reports.route, Screen.Search.route,
            Screen.PaymentReminders.route
        )
        routes.forEach { route ->
            assertFalse("Route '$route' contains space", route.contains(" "))
        }
    }

    @Test
    fun screen_routes_allStartWithLowercase() {
        val routes = listOf(
            Screen.Dashboard.route, Screen.Projects.route, Screen.Clients.route,
            Screen.Settings.route, Screen.Finance.route
        )
        routes.forEach { route ->
            assertTrue("Route '$route' should start with lowercase", route[0].isLowerCase())
        }
    }

    @Test
    fun authGraph_hasCorrectRoute() {
        assertEquals("auth_graph", Screen.AuthGraph.route)
    }

    @Test
    fun mainGraph_hasCorrectRoute() {
        assertEquals("main_graph", Screen.MainGraph.route)
    }
}
