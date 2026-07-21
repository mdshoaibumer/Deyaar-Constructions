#!/bin/bash
sed -i '/import com.example.domain.repository.TransactionRepository/a\
import com.example.domain.repository.ResourceRepository\
import com.example.data.repository.ResourceRepositoryImpl' app/src/main/java/com/example/di/AppContainer.kt

sed -i '/val deleteTransactionUseCase: DeleteTransactionUseCase by lazy/i\
    val resourceRepository: ResourceRepository by lazy {\
        ResourceRepositoryImpl(\
            materialDao = database.materialDao(),\
            workerDao = database.workerDao(),\
            supplierDao = database.supplierDao(),\
            resourceAllocationDao = database.resourceAllocationDao(),\
            attendanceDao = database.attendanceDao()\
        )\
    }\
' app/src/main/java/com/example/di/AppContainer.kt
