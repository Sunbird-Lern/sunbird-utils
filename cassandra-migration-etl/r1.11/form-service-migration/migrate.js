// Form service migration script
// Accepted arguments
// 1. contactPoints: IP with port of DB
// 2. username: username for DB // optional
// 3. password: password for DB // optional
// example: node migration.js 127.0.0.1:9200 username password

const cassandra = require('cassandra-driver');
let cassandra_client_options = { contactPoints: [process.argv[2]] };
if(process.argv[3] && process.argv[4]){
  cassandra_client_options.authProvider = new cassandra.auth.PlainTextAuthProvider(process.argv[3], process.argv[4]);
}
console.log('connecting to DB with', process.argv[2], process.argv[3], process.argv[4]);
const client = new cassandra.Client(cassandra_client_options);

let transformed_data = [];
let dest_obj = {
  root_org: undefined,
  framework: undefined,
  type: undefined,
  subtype: undefined,
  component: undefined,
  action: undefined,
  created_on: new Date(),
  last_modified_on: undefined,
  data: undefined
}

const query = 'SELECT * FROM sunbird.tenant_preference';
client.execute(query)
  .then(result => {
    console.log('no. of records to migrate:', result.rows.length);
    result.rows.forEach((row) => {
      let temp = Object.assign({}, dest_obj);
      temp.root_org = row.orgid;
      if (row.key && row.key.length) {
        temp.type = row.key.split(".")[0]
        temp.subtype = row.key.split(".")[1]
        temp.action = row.key.split(".")[2]
      } else {
        temp.type = "*";
        temp.subtype = "*";
        temp.action = "*";
      }
      temp.component = "*"; //default, since it's new field
      if (row.data) {
        if (typeof row.data === "string")
          try {
            row.data = JSON.parse(row.data);
          } catch (e) {
            console.log('JSON parse error! :');
            console.log('row.key', row.key);
            console.log('row.orgid', row.orgid);
            console.log('row.data', row.data);
          }
        if (typeof row.data === "object") {
          Object.keys(row.data).forEach((key) => {
            if (key === "default") {
              temp.framework = "*";
            } else {
              temp.framework = key;
            }
            temp.data = row.data[key];
            transformed_data.push(temp);
          })
        } else {
          temp.data = row.data;
          temp.framework = "*";
          transformed_data.push(temp);
        }
      }
    })
  })
  .then(async _ => {
    // create keyspace if not exist
    await client.execute('CREATE KEYSPACE IF NOT EXISTS qmzbm_form_service WITH REPLICATION = { \'class\' : \'SimpleStrategy\', \'replication_factor\' : 1 }')
    // create table if not exist
    await client.execute(`CREATE TABLE IF NOT EXISTS qmzbm_form_service.form_data (
      root_org text,
      framework text,
      type text,
      subtype text,
      action text,
      component text,
      created_on timestamp,
      data text,
      last_modified_on timestamp,
      PRIMARY KEY ((root_org, framework, type, subtype, action, component)))   
    `);
    for(const data of transformed_data) {
      let query = "INSERT INTO qmzbm_form_service.form_data(root_org, framework, type, subtype, action, component, created_on, last_modified_on, data) values(?,?,?,?,?,?,?,?,?)"
      await client.execute(query, [data.root_org, data.framework, data.type, data.subtype, data.action, data.component, data.created_on, data.last_modified_on, JSON.stringify(data.data)], { prepare: true });
    }
    console.log('no. of records migrated after denorm:', transformed_data.length);
    process.exit(1);
  })
  .catch(error => {
    console.log(error);
    process.exit(1);
  })

process.on('unhandledRejection', (reason, p) => {
  console.log('Unhandled Rejection at: Promise', p, 'reason:', reason);
  // application specific logging, throwing an error, or other logic here
});
