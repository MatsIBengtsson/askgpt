import os
import sys

str_base_modification_notice = (f"/* This file has been modified by Mats Bengtsson.\n"
                                f"Original file is part of the Nerdy Things AskGPT project.\n*/\n")
dict_extension_adjusted_notices = {}

dict_notice_syntax = {
    'default': ('/*', '*/'),
    '.gitignore': ('#', '#'),
    '.xml': ('<!--', '-->'),
    '.md': ('<!--', '-->'),
    '.kts': ('/*', '*/'),
    '.kt': ('/*', '*/'),
    '.html': ('<!--', '-->')}


def get_notice_template(str_file_extension):
    if str_file_extension in dict_extension_adjusted_notices:
        return dict_extension_adjusted_notices[str_file_extension]
    try:
        str_start_comment, str_end_comment = dict_notice_syntax[str_file_extension]
    except Exception as e:
        raise Exception(f"The needed file extension ({str_file_extension}) has no current known syntax definition")
    str_modified_comment_template = str_base_modification_notice.replace('/*', str_start_comment).replace(
        '*/', str_end_comment)
    if str_start_comment == str_end_comment:
        lst_str_rows = str_modified_comment_template.split('\n')
        str_modified_comment_template = '\n'.join(
            [f"{str_start_comment} {str_line}" if not (str_line.startswith(str_start_comment) or str_line == '')
             else str_line
             for str_line in lst_str_rows])
    dict_extension_adjusted_notices[str_file_extension] = str_modified_comment_template
    return str_modified_comment_template


def extract_file_extension(str_current_file_path):
    str_file_name_no_path = os.path.basename(str_current_file_path).lower()
    _, str_file_extension = os.path.splitext(str_file_name_no_path)
    if str_file_name_no_path in ['.gitignore']:
        str_file_extension = str_file_name_no_path
    return str_file_extension


def add_modification_notice(str_current_file_path, fil_added_notice_log):
    if not os.path.exists(str_current_file_path):
        return
    str_file_extension = extract_file_extension(str_current_file_path)
    str_adjusted_modification_notice = get_notice_template(str_file_extension)
    with open(str_current_file_path, 'r+', encoding='utf-8') as fil_needing_notice:
        str_content = fil_needing_notice.read()
        # Check if the modification notice is already present
        if str_adjusted_modification_notice.strip() not in str_content:
            fil_needing_notice.seek(0, 0)
            if str_file_extension == '.xml':
                str_content = add_notice_to_xml(str_adjusted_modification_notice, str_content)
            elif str_file_extension == '.html':
                str_content = add_notice_to_html(str_adjusted_modification_notice, str_content)
            else:
                str_content = str_adjusted_modification_notice + str_content
            fil_needing_notice.write(str_content)
            fil_needing_notice.truncate()
            fil_added_notice_log.write(f"{str_current_file_path}\n")


def add_notice_to_html(str_adjusted_modification_notice, str_content):
    lst_content_parts = str_content.split('>', 1)
    if len(lst_content_parts) > 1:
        if lst_content_parts[0].strip().lower().startswith('<!doctype html'):
            str_part_two = lst_content_parts[1]
            if str_part_two.startswith('\n'):
                str_part_two = str_part_two[1:]
            str_content = (lst_content_parts[0] + '>\n' + str_adjusted_modification_notice +
                           str_part_two)
        else:
            str_content = (str_adjusted_modification_notice + str_content)
    else:
        str_content = str_adjusted_modification_notice + str_content
    return str_content


def add_notice_to_xml(str_adjusted_modification_notice, str_content):
    lst_content_parts = str_content.split('?>', 1)
    if len(lst_content_parts) > 1:
        if lst_content_parts[0].strip().startswith('<?xml'):
            str_part_two = lst_content_parts[1]
            if str_part_two.startswith('\n'):
                str_part_two = str_part_two[1:]
            str_content = (lst_content_parts[0] + '?>\n' + str_adjusted_modification_notice +
                           str_part_two)
        else:
            str_content = (str_adjusted_modification_notice + str_content)
    else:
        str_content = str_adjusted_modification_notice + str_content
    return str_content


def main():
    if len(sys.argv) != 2:
        print("Usage: python add_modification_notice.py <files_to_add_notice_to.txt>")
        sys.exit(1)

    with open('.\\NoticeData\\last_added_notices.txt', 'w', encoding='utf-8') as fil_added_notice_log:
        with open(sys.argv[1], 'r', encoding='utf-8') as fil_file_list:
            lst_str_files = fil_file_list.readlines()
        for str_file in lst_str_files:
            add_modification_notice(str_file.strip(), fil_added_notice_log)
        a=1

if __name__ == "__main__":
    main()
