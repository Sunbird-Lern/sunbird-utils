import hashlib

from Crypto.Cipher import PKCS1_v1_5
from Crypto.PublicKey import RSA
from Crypto.Cipher import AES
from Crypto.Util.Padding import unpad

# Decrypt the data using the private key. The cipherText passed should have been generated using the
# corresponding public key.
def decryptDataUsingPrivateKey(cipherText, privateKeyPath, privateKeyPassphrase):
    with open(privateKeyPath, 'rb') as privateKeyFile:
        privateKey = RSA.importKey(privateKeyFile.read(), passphrase=privateKeyPassphrase)
    decryptor = PKCS1_v1_5.new(privateKey)
    clearText = decryptor.decrypt(cipherText, "Error while decrypting")
    return clearText

def decryptDataUsingAESKey(cipherText, aesKey):
    try:
        iv = "\x00" * AES.block_size
        cipher = AES.new(aesKey.encode(), AES.MODE_CBC, iv.encode())
        return unpad(cipher.decrypt(cipherText), AES.block_size)
    except ValueError:
        # remove output file on error
        print("Error while decrypt the file." + ValueError)

def decryptFileContentsWithAESKey(fIn, fOut, password):
    try:
        iv = "\x00" * AES.block_size
        cipher = AES.new(password, AES.MODE_CBC, iv.encode())
        fOut.write(unpad(cipher.decrypt(fIn.read()), AES.block_size))
    except ValueError:
        # remove output file on error
        print("Error while decrypt the file." + ValueError)

def generate_32_character_string(input_string):
    # Create an MD5 hash object
    md5_hash = hashlib.md5()

    # Convert the input string to bytes and update the hash object
    md5_hash.update(input_string.encode('utf-8'))

    # Get the hexadecimal representation of the hash digest
    hash_digest = md5_hash.hexdigest()

    # Take the first 32 characters of the hash digest
    result = hash_digest[:32]

    return result
