# decryption-tool
This tool is used to decrypt the file encrypted by sunbird.

### Supported OS 
* Mac
* Ubuntu
* Windows
### System Requirements:
### Prerequisites:
* Python 3

# How to run
## Let us assume that B received files from A.
```
   bash decrypt.sh
   # File to decrypt: path of the encrypted csv file that received. Tool will detect whether file is encryptes using security level TEXT_KEY_ENCRYPTED_DATASET or PUBLIC_KEY_ENCRYPTED_DATASET. Based on that it will promt the further steps to decrypt the file. 
   
   # If security level is TEXT_KEY_ENCRYPTED_DATASET
   # AES key: AES key, that used to encrypt the csv file.
   
   # If seccurity level is PUBLIC_KEY_ENCRYPTED_DATASET
   # Private key path: Path od the private.pem file.
   # Private key passphrase: Passphrase which is used to generate public private key pair
```