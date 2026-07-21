import re

with open('e:/construction-manager/all_code.txt', 'r', encoding='utf-8', errors='ignore') as f:
    content = f.read()

keywords = [
    'BiometricPrompt', 'EncryptedSharedPreferences', 'DataStore', 'ZipOutputStream', 
    'Cipher', 'KeyGenParameterSpec', 'contentDescription', 'cleartextTrafficPermitted',
    'Modifier.clickable', 'LazyColumn', 'remember', 'Flow', 'StateFlow', 'Dispatchers.IO',
    'Repository', 'UseCase', 'ViewModel', '@Test'
]

print("=== KEYWORD COUNTS ===")
for kw in keywords:
    count = len(re.findall(r'\b' + re.escape(kw) + r'\b', content))
    if count == 0:
        count = content.count(kw)
    print(f"{kw}: {count}")

print("\n=== MISSING/POTENTIAL ISSUES ===")
c_null = content.count('contentDescription = null')
if c_null > 0:
    print(f"Found 'contentDescription = null' {c_null} times.")

c_main = content.count('Dispatchers.Main')
c_io = content.count('Dispatchers.IO')
if c_main > 0 and c_io == 0:
    print("Might be missing Dispatchers.IO for background work.")

c_runblocking = content.count('runBlocking')
if c_runblocking > 0:
    print(f"Found runBlocking {c_runblocking} times.")

c_global = content.count('GlobalScope')
if c_global > 0:
    print(f"Found GlobalScope {c_global} times.")

c_exported = content.count('android:exported="true"')
if c_exported > 0:
    print(f"Found exported activities/components {c_exported} times.")
