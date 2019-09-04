const config = require("./config.js");
var fs = require('fs');

var badge = {
    "id": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/" + config.badge.batchId + "/Badge.json",
    "type": "BadgeClass",
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "name": config.badge.name,
    "description": config.badge.description,
    "image": config.badge.image,
    "criteria": {
        "type": [
            "Criteria"
        ],
        "id": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/" + config.badge.batchId,
        "narrative": config.badge.narrative
    },
    "issuer": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/Issuer.json"
}
var issuer = {
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "type": "Issuer",
    "id": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/Issuer.json",
    "name": config.issuer.name,
    "url": config.issuer.url,
    "publicKey": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/" + process.env.KEY_ID + "_publickey.json"
}


var publicKey = {
    "@context": config.domainUrl + "/" + config.slug + "/v1/context.json",
    "type": "CryptographicKey",
    "id": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/" + process.env.KEY_ID + "_publickey.json",
    "owner": config.domainUrl + "/" + config.slug + "/" + process.env.ROOT_ORG_ID + "/Issuer.json",
    "publicKeyPem": config.publicKeyPem
}

var outDirName = "./out";

var methods = {
    createPublicKeyJson: function () {
        let publicDir = outDirName + "/" + process.env.ROOT_ORG_ID
        if (!fs.existsSync(publicDir)) {
            fs.mkdirSync(publicDir);
        }
        if (writeToFile(outDirName + "/" + process.env.KEY_ID + "_publicKey.json", publicKey)) {
            console.log("publicKey json is created")
        }
    },
    createIssuerJson: function () {
        let issuerDir = outDirName + "/" + process.env.ROOT_ORG_ID
        if (!fs.existsSync(issuerDir)) {
            fs.mkdirSync(issuerDir);
        }
        if (writeToFile(issuerDir + "/" + "Issuer.json", issuer)) {
            console.log("issuer json is created")
        }
    },
    createBadgeJson: function () {
        let batchDir = outDirName + "/" + process.env.ROOT_ORG_ID + "/" + config.badge.batchId
        if (!fs.existsSync(batchDir)) {
            if (!fs.existsSync(outDirName + "/" + process.env.ROOT_ORG_ID)) {
                fs.mkdirSync(outDirName + "/" + process.env.ROOT_ORG_ID);
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