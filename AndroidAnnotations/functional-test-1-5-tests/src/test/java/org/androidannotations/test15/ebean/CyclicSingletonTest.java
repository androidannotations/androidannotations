package org.androidannotations.test15.ebean;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.androidannotations.test15.AndroidAnnotationsTestRunner;
import org.androidannotations.test15.EmptyActivityWithoutLayout_;

@RunWith(AndroidAnnotationsTestRunner.class)
public class CyclicSingletonTest {

	@Test
	public void cyclic_singleton() {
		EmptyActivityWithoutLayout_ context = new EmptyActivityWithoutLayout_();
		SomeCyclicSingletonA_ singletonA = SomeCyclicSingletonA_.getInstance_(context);
		SomeCyclicSingletonB_ singletonB = SomeCyclicSingletonB_.getInstance_(context);
		assertThat(singletonA.singletonB).isSameAs(singletonB);
		assertThat(singletonB.singletonA).isSameAs(singletonA);
	}

}
