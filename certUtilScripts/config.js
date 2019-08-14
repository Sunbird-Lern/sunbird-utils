const config = {
    "issuer": {
        "name": "" || process.env.ISSUER_NAME,
        "url": "" || process.env.ISSUER_URL,
        "publicKey": [""] || [process.env.PUBLICKEY_URL]
    },
    "badge": {
        "name": "" || process.env.BADGE_NAME, 
        "description": "" || process.env.BADGE_DESCRIPTION,
        "image": "" || process.env.BADGE_IMAGE,
        "criteria": "" || process.env.BADGE_CRITERIA,
        "batchId":"" || process.env.BATCH_ID
    },
    "domainUrl": "" || process.env.PROTO + process.env.DOMAIN
}

module.exports = config;