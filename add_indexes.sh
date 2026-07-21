#!/bin/bash
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/TransactionEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/ProjectTimelineEventEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/MilestoneEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/ExpenseEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt
sed -i 's/@Entity(/@Entity(\n    indices = \[Index("projectId")],\n    /' app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt
sed -i 's/@Entity(tableName = "photos")/@Entity(\n    tableName = "photos",\n    indices = \[Index("projectId")]\n)/' app/src/main/java/com/example/data/local/entity/PhotoEntity.kt
sed -i 's/@Entity(tableName = "documents")/@Entity(\n    tableName = "documents",\n    indices = \[Index("projectId")]\n)/' app/src/main/java/com/example/data/local/entity/DocumentEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/TransactionEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/ProjectTimelineEventEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/MilestoneEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/ExpenseEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/PhotoEntity.kt
sed -i '/import androidx.room.Entity/a\
import androidx.room.Index' app/src/main/java/com/example/data/local/entity/DocumentEntity.kt
