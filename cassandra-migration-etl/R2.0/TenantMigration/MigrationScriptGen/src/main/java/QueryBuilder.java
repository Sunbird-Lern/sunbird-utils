import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryBuilder {

  RequestParams requestParams;

  public QueryBuilder(RequestParams requestParams) {
    this.requestParams = requestParams;
  }

  public String createUserUpdateQuery(Map<String, Object> userMap) {
    String updateUserQuery =
        "UPDATE sunbird.user SET channel='"
            + requestParams.getChannel()
            + "',rootOrgId='"
            + requestParams.getRootOrgId()
            + "'WHERE  id='"
            + userMap.get("id")
            + "';";
    return updateUserQuery;
  }

  public List<String> deleteUserOrgQuery(Map<String, Object> map) {

    List<Map<String, Object>> userOrgList = (List) map.get("organisations");
    List<String> queryList = new ArrayList<>();
    for (Map<String, Object> hm : userOrgList) {
      String deleteUserQuery = "DELETE FROM sunbird.user_org Where id='" + hm.get("id") + "';";
      queryList.add(deleteUserQuery);
    }
    return queryList;
  }

  public List<String> createQueryForUserOrg(List<Map<String, Object>> userOrgList) {

    List<String> userOrgQueryList = new ArrayList<>();

    for (Map<String, Object> hm : userOrgList) {
      String userOrgQuery =
          "INSERT INTO sunbird.user_org (id,hashtagid,isdeleted,organisationid,orgjoindate,roles,userid) VALUES ('"
              + hm.get("id")
              + "','"
              + hm.get("hashtagid")
              + "',"
              + hm.get("isdeleted")
              + ",'"
              + hm.get("organisationid")
              + "','"
              + hm.get("orgjoindate")
              + "',"
              + "['PUBLIC']"
              + ",'"
              + hm.get("userid")
              + "');";
      userOrgQueryList.add(userOrgQuery);
    }

    return userOrgQueryList;
  }
}
