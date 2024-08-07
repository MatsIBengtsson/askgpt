from dotenv import dotenv_values


def print_env_commands(file_path):
    env_vars = dotenv_values(file_path)
    for key, value in env_vars.items():
        print(f'set {key}={value}')


if __name__ == "__main__":
    print_env_commands(".env")
