import shutil
import sys

from utils import *

filename = sys.argv[1]
privateKeyPath = sys.argv[2]
privateKeyPassphrase = sys.argv[3]

# Read the encrypted file (fIn) 
with open(filename, 'rb') as fIn:
    # Read the first line of the file that contains the password encrypted using the 
    # equivalent public key and decrypt to retrieve the password
    cipherText = fIn.readline().decode('UTF-8')
    password = decryptDataUsingPrivateKey(cipherText, privateKeyPath, privateKeyPassphrase)

    # Write the remaining content of the encrypted file to an intermediate file
    with open(filename + '.crypt', 'wb') as cipherOut:
        shutil.copyfileobj(fIn, cipherOut)

    # Read the encrypted zip, decrypt it and write to a filename called retrieved.zip 
    with open(filename + '.crypt', 'rb') as cipherIn:
        with open(filename + 'retrieved.zip', "wb") as fOut:
            decryptFileWithAESKey(filename + '.crypt', cipherIn, fOut, password)

# Uncomment the uncompressFile invocation if you want to retrieve the contents of the zip.
# I have left this commented as it will override the input file.
uncompressFile(filename + 'retrieved.zip')
remove(filename + '.crypt')
remove(filename + 'retrieved.zip')
