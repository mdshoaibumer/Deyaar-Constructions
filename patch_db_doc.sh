#!/bin/bash
sed -i '/import com.example.data.local.entity.AttendanceEntity/a\
import com.example.data.local.entity.PhotoEntity\
import com.example.data.local.entity.DocumentEntity\
import com.example.data.local.dao.PhotoDao\
import com.example.data.local.dao.DocumentDao' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i '/AttendanceEntity::class/a\
        ,PhotoEntity::class,\
        DocumentEntity::class' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i 's/version = 5/version = 6/' app/src/main/java/com/example/data/local/AppDatabase.kt

sed -i '/abstract fun attendanceDao(): AttendanceDao/a\
    abstract fun photoDao(): PhotoDao\
    abstract fun documentDao(): DocumentDao' app/src/main/java/com/example/data/local/AppDatabase.kt
