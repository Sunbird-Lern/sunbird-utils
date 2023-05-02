import shutil
import sys

from utils import *

filename = sys.argv[1]
password = sys.argv[2]

with open(filename, 'rb') as cipherIn:
    with open(filename + 'retrieved.zip', "wb") as fOut:
        decryptFileWithAESKey(filename , cipherIn, fOut, password)

# Uncomment the uncompressFile invocation if you want to retrieve the contents of the zip.
# I have left this commented as it will override the input file.
uncompressFile(filename + 'retrieved.zip')
# remove(filename + '.crypt')
remove(filename + 'retrieved.zip')
