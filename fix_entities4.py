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
    
    # Remove the added indices
    content = re.sub(r'    indices = \[Index\("projectId"\)\],\n    ', '', content)
    
    # Remove one of the duplicate imports
    content = content.replace('import androidx.room.Index\nimport androidx.room.Index', 'import androidx.room.Index')
    
    with open(dir_path + f, 'w') as file:
        file.write(content)

