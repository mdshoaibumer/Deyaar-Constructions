import re

with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'r') as f:
    content = f.read()

content = re.sub(r'@Entity\(\n    indices = \[Index\("projectId"\)\],\n    ', r'@Entity(\n    ', content)

with open('app/src/main/java/com/example/data/local/entity/SiteDiaryEntity.kt', 'w') as f:
    f.write(content)

