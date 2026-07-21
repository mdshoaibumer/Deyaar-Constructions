import os
import re

files = [
    'AttendanceEntity.kt',
    'ExpenseEntity.kt',
    'MilestoneEntity.kt',
    'ProjectTimelineEventEntity.kt',
    'ResourceAllocationEntity.kt',
    'TransactionEntity.kt'
]

dir_path = 'app/src/main/java/com/example/data/local/entity/'

for f in files:
    with open(dir_path + f, 'r') as file:
        content = file.read()
    
    # Remove any line that has indices =
    content = re.sub(r'\s*indices\s*=\s*\[[^\]]+\]\s*,?', '', content)
    
    # Remove any import of Index
    content = re.sub(r'import androidx\.room\.Index\n*', '', content)
    
    with open(dir_path + f, 'w') as file:
        file.write(content)

