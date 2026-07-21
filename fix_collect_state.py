import os
import re

search_dir = 'e:/construction-manager/app/src/main/java/com/example/ui'

for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            if 'collectAsState(' in content or 'collectAsState()' in content:
                # Replace collectAsState with collectAsStateWithLifecycle
                new_content = re.sub(r'collectAsState\((.*?)\)', r'collectAsStateWithLifecycle(\1)', content)
                new_content = new_content.replace('collectAsState()', 'collectAsStateWithLifecycle()')
                
                # Add import if missing
                if 'import androidx.lifecycle.compose.collectAsStateWithLifecycle' not in new_content:
                    # Find the last import and add it after
                    # Or just add it after the package declaration if there are no imports
                    import_statement = "import androidx.lifecycle.compose.collectAsStateWithLifecycle\n"
                    # Remove old import
                    new_content = new_content.replace('import androidx.compose.runtime.collectAsState\n', '')
                    
                    if 'import ' in new_content:
                        new_content = new_content.replace('import ', import_statement + 'import ', 1)
                    else:
                        lines = new_content.split('\n')
                        lines.insert(1, import_statement)
                        new_content = '\n'.join(lines)
                
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Updated {filepath}")
