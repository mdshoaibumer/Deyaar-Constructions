#!/bin/bash
sed -i '/fun getTotalExpenseForProject/a\
    fun getGlobalTotalIncome(): Flow<Double>\
    fun getGlobalTotalExpenses(): Flow<Double>' app/src/main/java/com/example/domain/repository/TransactionRepository.kt

cat << 'INNER' >> app/src/main/java/com/example/data/repository/TransactionRepositoryImpl.kt

    override fun getGlobalTotalIncome(): Flow<Double> {
        return dao.getGlobalTotalIncome().map { it ?: 0.0 }
    }

    override fun getGlobalTotalExpenses(): Flow<Double> {
        return dao.getGlobalTotalExpenses().map { it ?: 0.0 }
    }
INNER
