import sys

for byte in sys.argv[1:]:
    sys.stdout.write(chr(int(byte, 16)))

