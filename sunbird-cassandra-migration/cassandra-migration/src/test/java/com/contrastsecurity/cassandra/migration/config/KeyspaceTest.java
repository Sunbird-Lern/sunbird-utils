package com.contrastsecurity.cassandra.migration.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class KeyspaceTest {
  @Test
  public void shouldDefaultToNoKeyspaceButCanBeOverridden() {
    assertThat(new Keyspace().getName(), is(nullValue()));
    System.setProperty(Keyspace.KeyspaceProperty.NAME.getName(), "myspace");
    assertThat(new Keyspace().getName(), is("myspace"));
  }

  @Test
  public void shouldHaveDefaultClusterObject() {
    assertThat(new Keyspace().getCluster(), is(notNullValue()));
  }
}
