#!/bin/bash
sed -i '/val deleteTransactionUseCase/i\
    val getGlobalFinancialStatsUseCase: GetGlobalFinancialStatsUseCase by lazy {\
        GetGlobalFinancialStatsUseCase(transactionRepository)\
    }\
' app/src/main/java/com/example/di/AppContainer.kt
