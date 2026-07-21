#!/bin/bash
sed -i '/fun getAdvanceReceivedForProject/a\
\
    @Query("SELECT SUM(amount) FROM transactions WHERE type = '"'"'EXPENSE'"'"' AND isDeleted = 0")\
    fun getGlobalTotalExpenses(): Flow<Double?>\
\
    @Query("SELECT SUM(amount) FROM transactions WHERE type = '"'"'INCOME'"'"' AND isDeleted = 0")\
    fun getGlobalTotalIncome(): Flow<Double?>' app/src/main/java/com/example/data/local/dao/TransactionDao.kt
