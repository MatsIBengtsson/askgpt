import sys
import os


def load_file(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        return set(line.strip() for line in file)


def get_and_check_file_parameters():
    if len(sys.argv) != 3:
        print("Usage: python filter_files.py <original_files.txt> <modified_files.txt>")
        sys.exit(1)
    str_original_files_path = sys.argv[1]
    str_modified_files_path = sys.argv[2]
    if not os.path.exists(str_original_files_path):
        print(f"Error: The file '{str_original_files_path}' does not exist.")
        sys.exit(1)
    if not os.path.exists(str_modified_files_path):
        print(f"Error: The file '{str_modified_files_path}' does not exist.")
        sys.exit(1)
    return str_modified_files_path, str_original_files_path


def main():
    str_modified_files_path, str_original_files_path = get_and_check_file_parameters()
    set_original_files = load_file(str_original_files_path)
    set_modified_files = load_file(str_modified_files_path)
    with open('.\\NoticeData\\files_to_update.txt', 'w', encoding='utf-8') as fil_files_to_update:
        for str_modified_file in set_modified_files:
            if str_modified_file in set_original_files:
                fil_files_to_update.write(str_modified_file + '\n')


if __name__ == "__main__":
    main()
