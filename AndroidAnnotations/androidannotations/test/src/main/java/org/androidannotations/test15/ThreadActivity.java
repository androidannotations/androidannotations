/**
 * Copyright (C) 2010-2015 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.test15;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.UiThread.Propagation;
import org.androidannotations.test15.ebean.GenericBean;
import org.androidannotations.test15.ebean.SomeBean;
import org.androidannotations.test15.instancestate.MySerializableBean;

import android.app.Activity;
import android.os.Bundle;

@EActivity
public class ThreadActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@UiThread
	void emptyUiMethod() {

	}

	@Background
	void emptyBackgroundMethod() {

	}

	@Background(delay = 1000)
	void emptyDelayedBackgroundMethod() {

	}

	private void add(List<Integer> list, int i, int delay, Semaphore sem) {
		try {
			if (delay > 0) {
				Thread.sleep(delay);
			}
			list.add(i);
			if (sem != null) {
				sem.release();
			}
		} catch (InterruptedException e) {
			// should never happen
		}
	}

	@Background
	void addBackground(List<Integer> list, int i, int delay, Semaphore sem) {
		add(list, i, delay, sem);
	}

	@Background(serial = "test")
	void addSerializedBackground(List<Integer> list, int i, int delay, Semaphore sem) {
		add(list, i, delay, sem);
	}

	@Background(id = "to_cancel")
	void addCancellableBackground(List<Integer> list, int i, int interruptibleDelay) {
		add(list, i, interruptibleDelay, null);
	}

	@Background(id = "to_cancel_serial", serial = "test")
	void addCancellableSerializedBackground(List<Integer> list, int i, int delay) {
		add(list, i, delay, null);
	}

	@UiThread
	void objectUiMethod(Object param) {

	}

	@Background
	void objectBackgroundMethod(Object param) {

	}

	@UiThread
	void genericUiMethod(List<Map<String, List<Set<Void>>>> param) {

	}

	@Background
	void genericBackgroundMethod(List<Map<String, List<Set<MySerializableBean[]>>>> param) {

	}

	@Background
	void genericBackgroundMethod(Set<? extends GenericBean<? extends SomeBean>> param) {

	}

	@UiThread(delay = 1000)
	void emptyUiDelayedMethod() {

	}

	@UiThread(propagation = Propagation.ENQUEUE)
	void emptUiMethodEnqueue() {
	}

	@UiThread(propagation = Propagation.REUSE)
	void emptUiMethodReuse() {
	}

	@UiThread
	void uiThreadedUsingArrayParamtersMethod(MySerializableBean[] array) {
	}

	@UiThread
	void uiThreadedUsingArrayParamtersMethod(MySerializableBean[][] array) {
	}

	@Background
	void backgrounddUsingArrayParamtersMethod(MySerializableBean[] array) {
	}

	@Background
	void backgroundUsingArrayParamtersMethod(MySerializableBean[][] array) {
	}

	@Background
	void backgroundThrowException() {
		throw new RuntimeException();
	}

	@UiThread
	void uiThreadThrowException() {
		throw new RuntimeException();
	}
}
