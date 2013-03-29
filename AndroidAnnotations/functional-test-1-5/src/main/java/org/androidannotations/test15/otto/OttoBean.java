package org.androidannotations.test15.otto;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Trace;

import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

@EBean
public class OttoBean {
	
	@Trace
	@Background
	@Subscribe
	public void onEvent(Event event) {

	}
	
	@Produce
	public Event produceEvent() {
		return new Event();
	}

}
