package org.sunbird.common.hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.sunbird.notification.utils.JsonUtil;

public class HashGeneratorUtil {
  private static List<Integer> primes = null;
  private static int MAX_NUMBER = 300;
  private static int numPrimes = 7;

  private static List<Integer> getPrimes() {
    List<Integer> list = new ArrayList<>();
    boolean prime[] = new boolean[MAX_NUMBER + 1];
    Arrays.fill(prime, true);
    for (int p = 2; p * p <= MAX_NUMBER; p++) {
      if (prime[p] == true) {
        for (int i = p * p; i <= MAX_NUMBER; i += p) prime[i] = false;
      }
    }
    for (int i = numPrimes; i <= MAX_NUMBER; i++) {
      if (prime[i] == true) {
        list.add(i);
      }
    }
    return list;
  }

  public static long getHashCode(String jsonString) {
    if (primes == null) {
      primes = getPrimes();
    }
    jsonString = jsonString.trim();
    int brackets = 0;
    long hash = numPrimes;
    long colons = 1;
    long commas = 1;
    for (int i = 0; i < jsonString.length(); i++) {
      if (jsonString.charAt(i) == '{' || jsonString.charAt(i) == '[') {
        brackets++;
      } else if (jsonString.charAt(i) == '}' || jsonString.charAt(i) == ']') {
        brackets--;
      } else if (jsonString.charAt(i) == ':') {
        colons++;
      } else if (jsonString.charAt(i) == ',') {
        commas++;
      } else if (jsonString.charAt(i) != ' ') {
        if (brackets < primes.size()) {
          hash = hash + jsonString.charAt(i) * primes.get(brackets);
        } else {
          return 0;
        }
      }
    }
    return hash + colons * 17 + commas * 19;
  }

  public static String getHashCodeAsString(Map<String, Object> request) {
    if (request != null) {
      String responseAsString = JsonUtil.toJson(request);
      return String.valueOf(HashGeneratorUtil.getHashCode(responseAsString));
    } else {
      return null;
    }
  }
}
