package com.example.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ClientEntity
import com.example.data.local.entity.ProjectEntity
import com.example.domain.model.ClientCategory
import com.example.domain.model.ProjectCategory
import com.example.domain.model.ProjectPriority
import com.example.domain.model.ProjectStatus
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProjectDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var projectDao: ProjectDao
    private lateinit var clientDao: ClientDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        projectDao = database.projectDao()
        clientDao = database.clientDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createClient(id: String = "client_1") = ClientEntity(
        id = id, name = "Test Client", phone = "9876543210",
        email = "test@test.com", address = "Test Address", notes = null,
        createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis(),
        isActive = true, category = ClientCategory.RESIDENTIAL, isFavorite = false
    )

    private fun createProject(id: String = "proj_1", clientId: String? = "client_1", status: ProjectStatus = ProjectStatus.ACTIVE) = ProjectEntity(
        id = id, projectNumber = "DEY-001", name = "Test Project",
        clientId = clientId, category = ProjectCategory.HOUSE,
        address = "Plot 1", location = null, contractValuePaise = 5000000L,
        estimatedBudgetPaise = 3500000L, advanceReceivedPaise = 1500000L,
        expectedProfitPaise = 1500000L, startDate = System.currentTimeMillis(),
        expectedCompletionDate = System.currentTimeMillis() + 86400000L * 180,
        actualCompletionDate = null, status = status, priority = ProjectPriority.MEDIUM,
        engineerInCharge = "Ramesh K.", notes = null, progress = 45,
        createdAt = System.currentTimeMillis(), updatedAt = System.currentTimeMillis()
    )

    @Test
    fun insertAndGetProject() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject())

        val project = projectDao.getProjectById("proj_1")
        assertNotNull(project)
        assertEquals("Test Project", project!!.name)
        assertEquals(5000000L, project.contractValuePaise)
    }

    @Test
    fun getAllProjects_returnsDescendingCreatedAt() = runTest {
        clientDao.insertClient(createClient())
        val now = System.currentTimeMillis()
        projectDao.insertProject(createProject("proj_1").copy(createdAt = now - 1000))
        projectDao.insertProject(createProject("proj_2").copy(name = "Project 2", createdAt = now))

        val projects = projectDao.getAllProjects().first()
        assertEquals(2, projects.size)
        assertEquals("proj_2", projects[0].id) // Newest first
    }

    @Test
    fun getProjectsForClient_filtersCorrectly() = runTest {
        clientDao.insertClient(createClient("client_1"))
        clientDao.insertClient(createClient("client_2"))
        projectDao.insertProject(createProject("proj_1", "client_1"))
        projectDao.insertProject(createProject("proj_2", "client_2").copy(name = "Other"))

        val projects = projectDao.getProjectsForClient("client_1").first()
        assertEquals(1, projects.size)
        assertEquals("proj_1", projects[0].id)
    }

    @Test
    fun getProjectsCount_returnsCorrectCount() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject("proj_1"))
        projectDao.insertProject(createProject("proj_2"))

        val count = projectDao.getProjectsCount()
        assertEquals(2, count)
    }

    @Test
    fun deleteProject_removesIt() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject())

        projectDao.deleteProject("proj_1")
        val project = projectDao.getProjectById("proj_1")
        assertNull(project)
    }

    @Test
    fun updateProject_changesFields() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject())

        val updated = createProject().copy(name = "Updated Name", progress = 80)
        projectDao.updateProject(updated)

        val project = projectDao.getProjectById("proj_1")
        assertEquals("Updated Name", project!!.name)
        assertEquals(80, project.progress)
    }

    @Test
    fun getCompletedProjectsCount_countsCorrectly() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject("proj_1", status = ProjectStatus.ACTIVE))
        projectDao.insertProject(createProject("proj_2", status = ProjectStatus.COMPLETED))
        projectDao.insertProject(createProject("proj_3", status = ProjectStatus.COMPLETED))

        val count = projectDao.getCompletedProjectsCount().first()
        assertEquals(2, count)
    }

    @Test
    fun getActiveProjectsCount_countsCorrectly() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject("proj_1", status = ProjectStatus.ACTIVE))
        projectDao.insertProject(createProject("proj_2", status = ProjectStatus.PLANNING))
        projectDao.insertProject(createProject("proj_3", status = ProjectStatus.COMPLETED))

        val count = projectDao.getActiveProjectsCount().first()
        assertEquals(2, count) // ACTIVE + PLANNING
    }

    @Test
    fun foreignKey_setNull_onClientDelete() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject())

        clientDao.deleteClient("client_1")

        val project = projectDao.getProjectById("proj_1")
        assertNotNull(project)
        assertNull(project!!.clientId) // SET NULL on delete
    }

    @Test
    fun getTotalContractValue_sumsCorrectly() = runTest {
        clientDao.insertClient(createClient())
        projectDao.insertProject(createProject("proj_1").copy(contractValuePaise = 1000000L))
        projectDao.insertProject(createProject("proj_2").copy(contractValuePaise = 2000000L))

        val total = projectDao.getTotalContractValue().first()
        assertEquals(3000000L, total)
    }
}
