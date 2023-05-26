import base64
import shutil
import sys
from os import remove

from utils import *

filename = sys.argv[1]
password = sys.argv[2]

# Read the encrypted file (fIn)
with open(filename, 'rb') as fIn:
    # Read the first line of the file that contains the security level
    securityLevel = fIn.readline().decode('UTF-8')

    passwordLength = len(password)

    cipherText = fIn.readline().decode('UTF-8')
    print("passargentered-" + cipherText)
    password = generate_32_character_string(password)
    aesKey = decryptDataUsingAESKey(base64.b64decode(cipherText), password.upper())
    print("aesKey-" + str(aesKey))

    # Write the remaining content of the encrypted file to an intermediate file
    with open(filename + '.cryptout', 'wb') as cipherOut:
        shutil.copyfileobj(fIn, cipherOut)

    # Read the encrypted zip, decrypt it and write to a filename called retrieved.zip 
    with open(filename + '.cryptout', 'rb') as cipherIn:
        with open(filename, "wb") as fOut:
            decryptFileContentsWithAESKey(cipherIn, fOut, aesKey)

remove(filename + '.cryptout')