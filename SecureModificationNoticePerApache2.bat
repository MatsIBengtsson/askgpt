@echo off
setlocal enabledelayedexpansion

REM Specify the commit hash before your changes
REM Get the list of files from the specified commit
REM Get the list of modified files
if exist .\NoticeData\original_files.txt del .\NoticeData\original_files.txt
if exist .\NoticeData\modified_files.txt del .\NoticeData\modified_files.txt

set FORK_POINT=58f3c4debc396382e1a3547ec6459ccda8b23ae1
git ls-tree -r --name-only %FORK_POINT% > .\NoticeData\original_files.txt
git diff --name-only %FORK_POINT% HEAD > .\NoticeData\modified_files.txt

REM Create a list of original files that have been modified
if exist .\NoticeData\files_to_update.txt del .\NoticeData\files_to_update.txt

python FindOriginalFilesNeedingNotice.py .\NoticeData\original_files.txt .\NoticeData\modified_files.txt
python AddModificationNotice.py .\NoticeData\files_to_update.txt

endlocal
