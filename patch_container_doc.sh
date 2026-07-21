#!/bin/bash
sed -i '/import com.example.domain.repository.ResourceRepository/a\
import com.example.domain.repository.DocumentationRepository\
import com.example.data.repository.DocumentationRepositoryImpl' app/src/main/java/com/example/di/AppContainer.kt

sed -i '/val resourceRepository: ResourceRepository by lazy {/i\
    val documentationRepository: DocumentationRepository by lazy {\
        DocumentationRepositoryImpl(\
            photoDao = database.photoDao(),\
            documentDao = database.documentDao()\
        )\
    }\
' app/src/main/java/com/example/di/AppContainer.kt
