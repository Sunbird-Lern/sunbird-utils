import shutil
import sys

from utils import *

filename = sys.argv[1]
password = sys.argv[2]

# Read the encrypted file (fIn)
with open(filename, 'rb') as fIn:
    # Read the first line of the file that contains the security level
    securityLevel = fIn.readline().decode('UTF-8')

    # Write the remaining content of the encrypted file to an intermediate file
    with open(filename + '.crypt', 'wb') as cipherOut:
        shutil.copyfileobj(fIn, cipherOut)

    # Read the encrypted zip, decrypt it and write to a filename called retrieved.zip
    with open(filename + '.crypt', 'rb') as cipherIn:
        with open(filename + 'retrieved.zip', "wb") as fOut:
            decryptFileWithAESKey(filename + '.crypt', cipherIn, fOut, password)

# Uncomment the uncompressFile invocation if you want to retrieve the contents of the zip.
# I have left this commented as it will override the input file.
try:
    uncompressFile(filename + 'retrieved.zip')
except Exception:
    print("Error while unzipping the file.")
remove(filename + '.crypt')
remove(filename + 'retrieved.zip')
