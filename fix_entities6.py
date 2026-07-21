with open('app/src/main/java/com/example/data/local/entity/MilestoneEntity.kt', 'r') as f:
    content = f.read()

content = content.replace('val name: String,', 'val name: String,\n    val orderIndex: Int,')

with open('app/src/main/java/com/example/data/local/entity/MilestoneEntity.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt', 'r') as f:
    content = f.read()

content = content.replace('val resourceId: String,', 'val date: Long,\n    val resourceId: String,')

with open('app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt', 'w') as f:
    f.write(content)

