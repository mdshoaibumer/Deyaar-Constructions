#!/bin/bash
sed -i 's/expenseDao: ExpenseDao/transactionDao: TransactionDao/' app/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
sed -i 's/import com.example.data.local.dao.ExpenseDao/import com.example.data.local.dao.TransactionDao/' app/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
sed -i 's/private val expenseDao: ExpenseDao/private val transactionDao: TransactionDao/' app/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
sed -i 's/expenseDao.getTotalExpenses()/transactionDao.getGlobalTotalExpenses()/' app/src/main/java/com/example/data/repository/DashboardRepositoryImpl.kt
