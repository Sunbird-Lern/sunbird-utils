const config = require("./config.js");
var fs = require('fs');

var badge = {
    "id": config.domainUrl + "/" + process.env.CONTAINER_NAME+ "/"+ process.env.ROOT_ORG_ID + "/" + config.badge.batchId + "/badge.json",
    "type": "BadgeClass",
    "@context": config.domainUrl + "/" + process.env.CONTAINER_NAME + "container/v1/context.json",
    "name": config.badge.name,
    "description": config.badge.description,
    "image": config.badge.image,
    "criteria": config.badge.criteria,
    "issuer": config.domainUrl + "/" +process.env.CONTAINER_NAME+ "/"+ process.env.ROOT_ORG_ID + "/issuer.json"
}
var issuer = {
    "@context": config.domainUrl + "/" + process.env.CONTAINER_NAME + "container/v1/context.json",
    "type": "Issuer",
    "id": config.domainUrl + "/" + process.env.CONTAINER_NAME+ "/"+ process.env.ROOT_ORG_ID + "/issuer.json",
    "name": config.issuer.name,
    "url": config.issuer.url,
    "publicKey": config.issuer.publicKey
}


var publicKey = {
    "@context": config.domainUrl + "/" + process.env.CONTAINER_NAME + "container/v1/context.json",
    "type": "CryptographicKey",
    "id": config.domainUrl + "/" + process.env.CONTAINER_NAME+ "/"+ process.env.ROOT_ORG_ID + "_publickey.json",
    "owner": config.domainUrl + "/" +  process.env.CONTAINER_NAME+ "/"+process.env.ROOT_ORG_ID + "_issuer.json",
    "publicKeyPem": ""
}

var outDirName = "./out";

var methods = {
    createPublicKeyJson: function () {
        let publicDir = outDirName + "/" + process.env.ROOT_ORG_ID
        if (!fs.existsSync(publicDir)) {
            fs.mkdirSync(publicDir);
        }
        if (writeToFile(outDirName + "/" + process.env.ROOT_ORG_ID + "_publicKey.json", publicKey)) {
            console.log("publicKey json is created")
        }
    },
    createIssuerJson: function () {
        let issuerDir = outDirName + "/" + process.env.ROOT_ORG_ID
        if (!fs.existsSync(issuerDir)) {
            fs.mkdirSync(issuerDir);
        }
        if (writeToFile(issuerDir + "/" + process.env.ROOT_ORG_ID + "_issuer.json", issuer)) {
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
        if (writeToFile(batchDir + "/badge.json", badge)) {
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