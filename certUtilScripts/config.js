const config = {
    "issuer": {
        "name": "" || process.env.ISSUER_NAME,
        "url": "" || process.env.ISSUER_URL,
    },
    "badge": {
        "name": "" || process.env.BADGE_NAME,
        "description": "" || process.env.BADGE_DESCRIPTION,
        "image": "" || process.env.BADGE_IMAGE,
        "narrative": "" || process.env.CRITERIA_NARRATIVE,
        "batchId": "" || process.env.BATCH_ID
    },
    "domainUrl": "" || process.env.PROTO + process.env.DOMAIN,
    "publicKeyPem": "" || process.env.PUBLIC_KEY_PEM,
    "slug":"certs"
}

module.exports = config;