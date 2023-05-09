#!/bin/sh
# To install the dependencies
python3 -m pip install --upgrade pip
pip3 install -r ./src/requirements.txt

# Sample command to run the script
#sh decrypt.sh
# File to decrypt: sample.csv.dat
# shellcheck disable=SC2039
# shellcheck disable=SC2162
read -p 'File to decrypt: ' filename
level=$(head -1 "${filename}")
# echo "securityLevel=$level"
# shellcheck disable=SC2039
if [ "$level" -eq 3 ]; then
  echo "securityLevel=$level"
  # shellcheck disable=SC2162
  # shellcheck disable=SC2039
  read -sp 'AES key: ' aesKey
  echo
  python3 ./src/decrypt-l3.py "${filename}" "${aesKey}"
elif [ "$level" -eq 4 ]; then
  echo "securityLevel=$level"
  # shellcheck disable=SC2039
  # shellcheck disable=SC2162
  read -p 'Private key path: ' privateKeyPath
  # shellcheck disable=SC2039
  # shellcheck disable=SC2162
  read -sp 'Private key passphrase: ' privateKeyPassphrase
  echo
  python3 ./src/decrypt-l4.py "${filename}" "${privateKeyPath}" "${privateKeyPassphrase}"
else
  echo "Not a valid file. Please provide the valid encrypted file."
fi
