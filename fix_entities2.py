import re

with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'r') as f:
    content = f.read()

# Remove all
content = re.sub(r'@Entity\(\n    indices = \[Index\("projectId"\)\],\n    ', r'@Entity(\n    ', content)

# Add only to the first one (SiteDiaryEntity)
content = content.replace('tableName = "site_diaries"', 'tableName = "site_diaries",\n    indices = [Index("projectId")]')


with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt', 'r') as f:
    content = f.read()
    content = content.replace('indices = [Index("projectId")]', 'indices = [Index("projectId"), Index("workerId")]')
with open('app/src/main/java/com/example/data/local/entity/AttendanceEntity.kt', 'w') as f:
    f.write(content)
