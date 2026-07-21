import os

search_dir = 'e:/construction-manager/app/src/main/java'

for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith('RepositoryImpl.kt') or file.endswith('Dao.kt'):
            print(os.path.join(root, file))
