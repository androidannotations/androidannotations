package com.googlecode.androidannotations.test15.ebean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.googlecode.androidannotations.annotations.EBean;

@EBean
public class SomeComplexGenericList<A> extends SomeSingleton implements
		SomeOtherInferface<A, SomeInterface> {

	private List<SomeInterface> list = new ArrayList<SomeInterface>();

	@Override
	public boolean add(SomeInterface arg0) {
		return list.add(arg0);
	}

	@Override
	public int size() {

		return list.size();
	}

	@Override
	public void add(int arg0, SomeInterface arg1) {

	}

	@Override
	public boolean addAll(Collection<? extends SomeInterface> arg0) {

		return false;
	}

	@Override
	public boolean addAll(int arg0, Collection<? extends SomeInterface> arg1) {

		return false;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean contains(Object arg0) {

		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {

		return false;
	}

	@Override
	public SomeInterface get(int arg0) {

		return null;
	}

	@Override
	public int indexOf(Object arg0) {

		return 0;
	}

	@Override
	public boolean isEmpty() {

		return false;
	}

	@Override
	public Iterator<SomeInterface> iterator() {

		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object arg0) {

		return 0;
	}

	@Override
	public ListIterator<SomeInterface> listIterator() {

		return list.listIterator();
	}

	@Override
	public ListIterator<SomeInterface> listIterator(int arg0) {

		return list.listIterator(arg0);
	}

	@Override
	public SomeInterface remove(int arg0) {

		return null;
	}

	@Override
	public boolean remove(Object arg0) {

		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {

		return false;
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {

		return false;
	}

	@Override
	public SomeInterface set(int arg0, SomeInterface arg1) {

		return null;
	}

	@Override
	public List<SomeInterface> subList(int arg0, int arg1) {

		return null;
	}

	@Override
	public Object[] toArray() {

		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {

		return null;
	}

}
