# How to run
## Let us assume that B received files from A.
```
   sh decrypt.sh
   # Decryption level 3 or 4: Enter the level used to encrypt the file (3 or 4).
   
   # If selected level as 3
   # File to decrypt: path of the encrypted csv file that received.
   # AES key: AES key, that used to encrypt the csv file.
   
   # If selected level as 4
   # File to decrypt: path of the encrypted csv file that received.
   # Private key path: Path od the private.pem file.
   # Private key passphrase: Passphrase which is used to generate public private key pair
```