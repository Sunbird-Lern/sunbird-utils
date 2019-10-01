#Certificate utilities
This is a utility to create badge, issuer and public key jsons.

### Setup
> npm install

### Procedure
The outTemplate folder has some static jsons files in an expected hierarchy. These refer to the domain name that needs to be replaced. 

### Steps
1.Update the environment variables (take a copy of setVars.sh.sample)
> source ./setVars.sh <br>

After setting the environment variables, replace the domain name in the json files. This will create an 'out' directory.
> ./createStaticJson.sh

2. Run individually other scripts, as needed

> node createBadge.js

> node createIssuer.js

> node createPublicKey.js

1. Finally copy contents of the 'out' directory to container.

The structure must look like this <br>
container_name <br>
- v1 <br>
   - context.json
   - extensions
        - AssessedEvidence
            - ...other files...
- <ROOT_ORG_ID>
    - <BATCH_ID>
        - Badge.json
        - ...all certificate files go here...
    - Issuer.json
    - 1_publicKey.json
    - 2_publicKey.json