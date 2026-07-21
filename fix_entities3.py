import re
with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'r') as f:
    content = f.read()

content = content.replace('tableName = "labour_summaries",', 'tableName = "labour_summaries",\n    indices = [Index("siteDiaryId")],')
content = content.replace('tableName = "material_deliveries",', 'tableName = "material_deliveries",\n    indices = [Index("siteDiaryId")],')
content = content.replace('tableName = "site_expenses",', 'tableName = "site_expenses",\n    indices = [Index("siteDiaryId")],')
content = content.replace('tableName = "progress_activities",', 'tableName = "progress_activities",\n    indices = [Index("siteDiaryId")],')
content = content.replace('tableName = "site_issues",', 'tableName = "site_issues",\n    indices = [Index("siteDiaryId")],')

with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'w') as f:
    f.write(content)
