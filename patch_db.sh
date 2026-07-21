#!/bin/bash
sed -i '/import com.example.data.local.entity.TransactionEntity/a\
import com.example.data.local.entity.MaterialEntity\
import com.example.data.local.entity.WorkerEntity\
import com.example.data.local.entity.SupplierEntity\
import com.example.data.local.entity.ResourceAllocationEntity\
import com.example.data.local.entity.AttendanceEntity\
import com.example.data.local.dao.MaterialDao\
import com.example.data.local.dao.WorkerDao\
import com.example.data.local.dao.SupplierDao\
import com.example.data.local.dao.ResourceAllocationDao\
import com.example.data.local.dao.AttendanceDao' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i '/TransactionEntity::class/a\
        ,MaterialEntity::class,\
        WorkerEntity::class,\
        SupplierEntity::class,\
        ResourceAllocationEntity::class,\
        AttendanceEntity::class' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i 's/version = 4/version = 5/' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i '/abstract fun transactionDao(): TransactionDao/a\
    abstract fun materialDao(): MaterialDao\
    abstract fun workerDao(): WorkerDao\
    abstract fun supplierDao(): SupplierDao\
    abstract fun resourceAllocationDao(): ResourceAllocationDao\
    abstract fun attendanceDao(): AttendanceDao' app/src/main/java/com/example/data/local/AppDatabase.kt
