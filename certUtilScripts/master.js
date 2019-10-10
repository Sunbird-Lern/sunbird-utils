const config = require("./config.js");
var fs = require('fs');

var badge = {
    "id": config.domainUrl + "/" + config.slug + "/" + config.badge.batchId + "/Badge.json",
    "type": "BadgeClass",
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "name": config.badge.name,
    "description": config.badge.description,
    "image": config.badge.image,
    "criteria": {
        "type": [
            "Criteria"
        ],
        "id": config.domainUrl + "/" + config.slug + "/" + config.badge.batchId,
        "narrative": config.badge.narrative
    },
    "issuer": config.domainUrl + "/" + config.slug + "/Issuer.json"
}
var issuer = {
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "type": "Issuer",
    "id": config.domainUrl + "/" + config.slug + "/Issuer.json",
    "name": config.issuer.name,
    "url": config.issuer.url,
    "publicKey": config.domainUrl + "/" + config.slug + "/keys/" + process.env.KEY_ID + "_publickey.json"
}


var publicKey = {
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "type": "CryptographicKey",
    "id": config.domainUrl + "/" + config.slug + "/keys/" + process.env.KEY_ID + "_publickey.json",
    "owner": config.domainUrl + "/" + config.slug + "/Issuer.json",
    "publicKeyPem": config.publicKeyPem
}

var outDirName = "./out";

var methods = {
    createPublicKeyJson: function () {
        let publicDir = outDirName + "/keys"
        if (!fs.existsSync(publicDir)) {
            fs.mkdirSync(publicDir);
        }
        if (writeToFile(publicDir + "/" + process.env.KEY_ID + "_publicKey.json", publicKey)) {
            console.log("publicKey json is created")
        }
    },
    createIssuerJson: function () {
        let issuerDir = outDirName
        if (!fs.existsSync(issuerDir)) {
            fs.mkdirSync(issuerDir);
        }
        if (writeToFile(issuerDir + "/" + "Issuer.json", issuer)) {
            console.log("issuer json is created")
        }
    },
    createBadgeJson: function () {
        let batchDir = outDirName + "/" + config.badge.batchId
        if (!fs.existsSync(batchDir)) {
            if (!fs.existsSync(outDirName)) {
                fs.mkdirSync(outDirName);
            }
            fs.mkdirSync(batchDir);
        }
        if (writeToFile(batchDir + "/Badge.json", badge)) {
            console.log("Badge json is created")
        }
    }
}

const writeToFile = (fileName, keys) => {
    fs.writeFile(fileName, JSON.stringify(keys), function (err) {
        if (err) {
            return console.log(err);
        }
    });
    return true;
}

module.exports = methods