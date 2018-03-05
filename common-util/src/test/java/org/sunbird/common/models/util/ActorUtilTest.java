/**
 * 
 */
package org.sunbird.common.models.util;

import org.junit.Assert;
import org.junit.Test;

import akka.actor.ActorSelection;

/**
 * @author Manzarul
 *
 */
public class ActorUtilTest {

	@Test
	public void getActorSelectionTest() {
		ActorSelection selection = ActorUtility.getActorSelection();
		Assert.assertNotNull(selection);
	}

}
