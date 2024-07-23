CREATE TABLE IF NOT EXISTS sunbird.user_declarations(
   userid text,
   orgid text,
   persona text,
   userinfo map<text,text>,
   status text,
   errortype text,
   createdon timestamp,
   createdby text,
   updatedon timestamp,
   updatedby text,
   primary key(userid,orgid,persona)
);