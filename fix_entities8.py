with open('app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt', 'r') as f:
    content = f.read()

content = content.replace('val cost: Double?,', 'val cost: Double,')

with open('app/src/main/java/com/example/data/local/entity/ResourceAllocationEntity.kt', 'w') as f:
    f.write(content)

