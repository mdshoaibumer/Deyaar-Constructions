package com.example.ui.screens.client

import com.example.domain.model.Client
import com.example.domain.model.ClientCategory
import com.example.domain.repository.ClientRepository
import com.example.domain.usecase.client.GetClientByIdUseCase
import com.example.domain.usecase.client.SaveClientUseCase
import com.example.domain.usecase.client.ValidateClientPhoneUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClientAddEditViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var mockRepository: ClientRepository
    private lateinit var getClientByIdUseCase: GetClientByIdUseCase
    private lateinit var saveClientUseCase: SaveClientUseCase
    private lateinit var validateClientPhoneUseCase: ValidateClientPhoneUseCase

    private val savedClients = mutableListOf<Client>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        savedClients.clear()
        
        mockRepository = object : ClientRepository {
            override fun getAllClients(): Flow<List<Client>> {
                return flowOf(savedClients)
            }

            override suspend fun getClientById(id: String): Client? {
                return savedClients.find { it.id == id }
            }

            override suspend fun saveClient(client: Client) {
                val index = savedClients.indexOfFirst { it.id == client.id }
                if (index != -1) {
                    savedClients[index] = client
                } else {
                    savedClients.add(client)
                }
            }

            override suspend fun deleteClient(id: String) {
                savedClients.removeIf { it.id == id }
            }
        }

        getClientByIdUseCase = GetClientByIdUseCase(mockRepository)
        saveClientUseCase = SaveClientUseCase(mockRepository)
        validateClientPhoneUseCase = ValidateClientPhoneUseCase(mockRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `save with valid data updates isSaved state`() = runTest(testDispatcher) {
        val viewModel = ClientAddEditViewModel(
            null,
            getClientByIdUseCase,
            saveClientUseCase,
            validateClientPhoneUseCase
        )

        viewModel.onEvent(ClientAddEditEvent.NameChanged("John Doe"))
        viewModel.onEvent(ClientAddEditEvent.PhoneChanged("9876543210"))
        
        viewModel.onEvent(ClientAddEditEvent.Save)
        
        testScheduler.advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isSaved)
        assertEquals(1, savedClients.size)
        assertEquals("John Doe", savedClients.first().name)
    }

    @Test
    fun `save with missing name shows error`() = runTest(testDispatcher) {
        val viewModel = ClientAddEditViewModel(
            null,
            getClientByIdUseCase,
            saveClientUseCase,
            validateClientPhoneUseCase
        )

        viewModel.onEvent(ClientAddEditEvent.PhoneChanged("9876543210"))
        viewModel.onEvent(ClientAddEditEvent.Save)
        
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals("Name is required", viewModel.uiState.value.error)
        assertEquals(0, savedClients.size)
    }

    @Test
    fun `save with duplicate phone shows error`() = runTest(testDispatcher) {
        savedClients.add(
            Client(
                id = "1",
                name = "Jane Doe",
                phone = "9876543210",
                email = null,
                address = null,
                notes = null,
                createdAt = 0,
                updatedAt = 0
            )
        )

        val viewModel = ClientAddEditViewModel(
            null,
            getClientByIdUseCase,
            saveClientUseCase,
            validateClientPhoneUseCase
        )

        viewModel.onEvent(ClientAddEditEvent.NameChanged("John Doe"))
        viewModel.onEvent(ClientAddEditEvent.PhoneChanged("9876543210"))
        viewModel.onEvent(ClientAddEditEvent.Save)
        
        testScheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isSaved)
        assertEquals("A client with this phone number already exists", viewModel.uiState.value.error)
        assertEquals(1, savedClients.size) // No new client added
    }
}
