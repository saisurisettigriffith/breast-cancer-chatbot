import os

def print_folder(path, out_file, skip_name):
    # List entries sorted so output is deterministic
    entries = sorted(os.listdir(path))
    
    # First, write all files at this level
    for name in entries:
        # Skip the output file itself
        if name == skip_name or name == 'WORKINGJAVACODEBASE.txt' or name.startswith('src/test') or name.endswith('.class') or name.startswith('.'):
            continue

        full_path = os.path.join(path, name)
        if os.path.isfile(full_path):
            filename, ext = os.path.splitext(name)
            ext = ext.lstrip('.')  # remove the leading dot
            try:
                with open(full_path, 'r', encoding='utf-8') as f:
                    content = f.read()
            except Exception as e:
                content = f"<Could not read file: {e}>"
            print(f'"{filename}.{ext}: "', file=out_file)
            print(f'"{content}"\n', file=out_file)
    
    # Then, recurse into each subdirectory
    for name in entries:
        if name == skip_name:
            continue

        full_path = os.path.join(path, name)
        if os.path.isdir(full_path):
            print(f'"{name} - Start :"', file=out_file)
            print_folder(full_path, out_file, skip_name)
            print(f'"{name} - End"\n', file=out_file)

if __name__ == '__main__':
    start_dir = '/Users/saisurisetti/Desktop/breast-cancer-chatbot/src/main/java/com/example/breastcancer'
    out_dir = os.getcwd()
    output_filename = 'COMBINEDOUTPUTJAVA.txt'
    output_path = os.path.join(out_dir, output_filename)
    with open(output_path, 'w', encoding='utf-8') as out:
        print_folder(start_dir, out, output_filename)
    print(f"Directory listing written to {output_path}")