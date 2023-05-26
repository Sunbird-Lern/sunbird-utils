import base64
import shutil
import sys
from os import remove

from utils import *

filename = sys.argv[1]
privateKeyPath = sys.argv[2]
privateKeyPassphrase = sys.argv[3]

# Read the encrypted file (fIn) 
with open(filename, 'rb') as fIn:
    # Read the first line of the file that contains the security level
    securityLevel = fIn.readline().decode('UTF-8')

    # Read the second line of the file that contains the password encrypted using the
    # equivalent public key and decrypt to retrieve the password
    cipherText = fIn.readline().decode('UTF-8')
    password = decryptDataUsingPrivateKey(base64.b64decode(cipherText), privateKeyPath, privateKeyPassphrase)
    print("password-" + str(password))

    # Write the remaining content of the encrypted file to an intermediate file
    with open(filename + '.cryptout', 'wb') as cipherOut:
        shutil.copyfileobj(fIn, cipherOut)

    # Read the encrypted zip, decrypt it and write to a filename called retrieved.zip 
    with open(filename + '.cryptout', 'rb') as cipherIn:
        with open(filename, "wb") as fOut:
            decryptFileContentsWithAESKey(cipherIn, fOut, password)

remove(filename + '.cryptout')
