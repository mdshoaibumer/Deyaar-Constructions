import os
import re

search_dir = 'e:/construction-manager/app/src/main/java/com/example/ui'

replacements = [
    (r'\bpadding\(16\.dp\)', 'padding(Dimens.spaceMedium)'),
    (r'\bpadding\(horizontal = 16\.dp\)', 'padding(horizontal = Dimens.spaceMedium)'),
    (r'\bpadding\(vertical = 16\.dp\)', 'padding(vertical = Dimens.spaceMedium)'),
    (r'\bpadding\(start = 16\.dp\)', 'padding(start = Dimens.spaceMedium)'),
    (r'\bpadding\(end = 16\.dp\)', 'padding(end = Dimens.spaceMedium)'),
    (r'\bpadding\(top = 16\.dp\)', 'padding(top = Dimens.spaceMedium)'),
    (r'\bpadding\(bottom = 16\.dp\)', 'padding(bottom = Dimens.spaceMedium)'),
    (r'\bheight\(16\.dp\)', 'height(Dimens.spaceMedium)'),
    (r'\bwidth\(16\.dp\)', 'width(Dimens.spaceMedium)'),
    (r'\bspacedBy\(16\.dp\)', 'spacedBy(Dimens.spaceMedium)'),
    (r'\bpadding\(8\.dp\)', 'padding(Dimens.spaceSmall)'),
    (r'\bpadding\(horizontal = 8\.dp\)', 'padding(horizontal = Dimens.spaceSmall)'),
    (r'\bpadding\(vertical = 8\.dp\)', 'padding(vertical = Dimens.spaceSmall)'),
    (r'\bpadding\(start = 8\.dp\)', 'padding(start = Dimens.spaceSmall)'),
    (r'\bpadding\(end = 8\.dp\)', 'padding(end = Dimens.spaceSmall)'),
    (r'\bpadding\(top = 8\.dp\)', 'padding(top = Dimens.spaceSmall)'),
    (r'\bpadding\(bottom = 8\.dp\)', 'padding(bottom = Dimens.spaceSmall)'),
    (r'\bheight\(8\.dp\)', 'height(Dimens.spaceSmall)'),
    (r'\bwidth\(8\.dp\)', 'width(Dimens.spaceSmall)'),
    (r'\bspacedBy\(8\.dp\)', 'spacedBy(Dimens.spaceSmall)'),
    (r'\bpadding\(24\.dp\)', 'padding(Dimens.spaceLarge)'),
    (r'\bheight\(24\.dp\)', 'height(Dimens.spaceLarge)'),
    (r'\bwidth\(24\.dp\)', 'width(Dimens.spaceLarge)'),
    (r'\bspacedBy\(24\.dp\)', 'spacedBy(Dimens.spaceLarge)'),
    (r'\bpadding\(4\.dp\)', 'padding(Dimens.spaceMicro)'),
    (r'\bheight\(4\.dp\)', 'height(Dimens.spaceMicro)'),
    (r'\bwidth\(4\.dp\)', 'width(Dimens.spaceMicro)'),
    (r'\bpadding\(12\.dp\)', 'padding(12.dp)'), # leave 12 alone or use a specific one? 12.dp is radiusMedium usually, or we can just leave it.
    (r'\bspacedBy\(12\.dp\)', 'spacedBy(12.dp)'),
]

for root, dirs, files in os.walk(search_dir):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements:
                new_content = re.sub(old, new, new_content)
                
            if new_content != content:
                if 'import com.example.ui.theme.Dimens' not in new_content:
                    if 'import androidx.compose.' in new_content:
                        new_content = new_content.replace('import androidx.compose.', 'import com.example.ui.theme.Dimens\nimport androidx.compose.', 1)
                
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Updated {filepath}")
