import re

with open('e:/construction-manager/all_code.txt', 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

# Naive split by file, assuming "app/src/" or similar marks files
# Wait, all_code.txt might just be a dump. Let's look at the first few lines to see how it separates files.
