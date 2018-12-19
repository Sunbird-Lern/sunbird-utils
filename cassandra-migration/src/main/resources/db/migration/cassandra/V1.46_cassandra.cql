CREATE TABLE IF NOT EXISTS sunbird.org_external_identity(provider text, externalId text, orgId text, PRIMARY KEY(provider, externalId));
