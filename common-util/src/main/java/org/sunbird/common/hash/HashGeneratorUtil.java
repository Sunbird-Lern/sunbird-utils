package org.sunbird.common.hash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashGeneratorUtil {
  private static List<Integer> a = null;
  private static int n = 300;

  private static List<Integer> getPrimes() {
    List<Integer> list = new ArrayList<>();
    boolean prime[] = new boolean[n + 1];
    Arrays.fill(prime, true);
    for (int p = 2; p * p <= n; p++) {
      if (prime[p] == true) {
        for (int i = p * p; i <= n; i += p) prime[i] = false;
      }
    }
    for (int i = 7; i <= n; i++) {
      if (prime[i] == true) {
        list.add(i);
      }
    }
    return list;
  }

  public static long getHashCode(String s1) {
    if (a == null) {
      a = getPrimes();
    }
    s1 = s1.trim();
    int brackets = 0;
    long hash = 7;
    long collons = 1;
    long commas = 1;
    for (int i = 0; i < s1.length(); i++) {
      if (s1.charAt(i) == '{' || s1.charAt(i) == '[') {
        brackets++;
      } else if (s1.charAt(i) == '}' || s1.charAt(i) == ']') {
        brackets--;
      } else if (s1.charAt(i) == ':') {
        collons++;
      } else if (s1.charAt(i) == ',') {
        commas++;
      } else if (s1.charAt(i) != ' ') {
        if (brackets < a.size()) {
          hash = hash + s1.charAt(i) * a.get(brackets);
        } else {
          return 0;
        }
      }
    }
    return hash + collons * 17 + commas * 19;
  }
}
