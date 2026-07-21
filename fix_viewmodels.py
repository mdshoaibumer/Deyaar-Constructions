import os
import re

def fix_viewmodel(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Find the save function (e.g. saveClient(), saveProject(), saveTransaction())
    # which contains viewModelScope.launch
    
    # We will do a generic approach: look for viewModelScope.launch
    # and if it calls a saveXXXUseCase or deleteXXXUseCase, we wrap its internals in try/catch.
    
    lines = content.split('\n')
    out_lines = []
    
    i = 0
    while i < len(lines):
        line = lines[i]
        if 'viewModelScope.launch {' in line:
            # Check if this launch block has a save/delete UseCase call inside
            # We'll peek ahead to find the matching closing bracket
            bracket_count = 0
            has_write = False
            j = i
            
            while j < len(lines):
                bracket_count += lines[j].count('{')
                bracket_count -= lines[j].count('}')
                
                if 'save' in lines[j] and 'UseCase(' in lines[j]:
                    has_write = True
                if 'delete' in lines[j] and 'UseCase(' in lines[j]:
                    has_write = True
                
                if bracket_count == 0 and j > i:
                    break
                j += 1
                
            if has_write and j < len(lines):
                # We need to wrap
                out_lines.append(line)
                out_lines.append('            try {')
                
                # Now process all lines from i+1 to j-1, adding an indent
                for k in range(i+1, j):
                    # add indent if line is not empty
                    l = lines[k]
                    if l.strip():
                        out_lines.append('    ' + l)
                    else:
                        out_lines.append(l)
                
                # Close the try and add catch
                out_lines.append('            } catch (e: Exception) {')
                # Find the name of the state update method, usually _uiState.update
                # But sometimes it's different. We will just use _uiState.update if it exists
                if '_uiState.update' in content:
                    out_lines.append('                _uiState.update { it.copy(error = e.message ?: "An error occurred") }')
                else:
                    out_lines.append('                // e.printStackTrace()')
                out_lines.append('            }')
                out_lines.append(lines[j]) # the closing bracket
                
                i = j + 1
                continue
                
        out_lines.append(line)
        i += 1
        
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write('\n'.join(out_lines))

search_dir = 'e:/construction-manager/app/src/main/java/com/example/ui/screens'
for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith('ViewModel.kt') and 'AddEdit' in file:
            fix_viewmodel(os.path.join(root, file))

# Also fix lists for delete
for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith('ListViewModel.kt'):
            fix_viewmodel(os.path.join(root, file))

print("Fixed ViewModels.")
