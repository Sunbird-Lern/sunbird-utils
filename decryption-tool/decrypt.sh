#!/bin/sh
# To install the dependencies
pip3 install -r ./src/requirements.txt

# Sample command to run the script
#sh decrypt.sh
# shellcheck disable=SC2039
# shellcheck disable=SC2162
read -p 'Decryption level 3 or 4: ' level
# shellcheck disable=SC2039
if [ "$level" == 3 ]
then
   #File to decrypt: sample.csv.dat
   #Private key path: private.pem
   #Private key passphrase:

   # shellcheck disable=SC2039
   # shellcheck disable=SC2162
   read -p 'File to decrypt: ' filename
   # shellcheck disable=SC2162
   # shellcheck disable=SC2039
   read -sp 'AES key: ' aesKey
   echo
   python3 ./src/decrypt-l3.py "${filename}" "${aesKey}"
else
   #File to decrypt: sample.csv.dat
   #Private key path: private.pem
   #Private key passphrase:

   # shellcheck disable=SC2039
   # shellcheck disable=SC2162
   read -p 'File to decrypt: ' filename
   # shellcheck disable=SC2039
   read -p 'Private key path: ' privateKeyPath
   # shellcheck disable=SC2039
   read -sp 'Private key passphrase: ' privateKeyPassphrase
   echo
   python3 ./src/decrypt.py "${filename}" "${privateKeyPath}" "${privateKeyPassphrase}"
fi
