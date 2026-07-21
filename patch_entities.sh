#!/bin/bash
sed -i 's/@Entity(\n    indices = \[Index("projectId")],\n    /@Entity(/g' app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt

sed -i 's/@Entity(tableName = "site_diaries")/@Entity(\n    tableName = "site_diaries",\n    indices = \[Index("projectId")]\n)/' app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt
