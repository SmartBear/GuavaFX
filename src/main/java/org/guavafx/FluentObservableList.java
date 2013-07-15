package org.guavafx;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * An immutable wrapper of ObservableList, whose only purpose is to provide a fluent API for the methods provided by
 * ObservableLists.java.
 *
 * @author henrik.olsson
 */

public class FluentObservableList implements ObservableList
{
    private ObservableList wrappedList;

    FluentObservableList( ObservableList wrappedList )
    {
        this.wrappedList = wrappedList;
    }

    public FluentObservableList filter( Predicate predicate )
    {
        return new FluentObservableList( ObservableLists.filter( wrappedList, predicate ) );
    }

    public FluentObservableList transform( Function function )
    {
        return new FluentObservableList( ObservableLists.transform(wrappedList, function) );
    }

	/*
		Delegating methods
	 */

	@Override
	public void addListener( ListChangeListener listChangeListener )
	{
		wrappedList.addListener( listChangeListener );
	}

	@Override
	public void removeListener( ListChangeListener listChangeListener )
	{
		wrappedList.removeListener( listChangeListener );
	}

	@Override
	public boolean addAll( Object... objects )
	{
		return wrappedList.addAll( objects );
	}

	@Override
	public boolean setAll( Object... objects )
	{
		return wrappedList.setAll( objects );
	}

	@Override
	public boolean setAll( Collection collection )
	{
		return wrappedList.setAll( collection );
	}

	@Override
	public boolean removeAll( Object... objects )
	{
		return wrappedList.removeAll( objects );
	}

	@Override
	public boolean retainAll( Object... objects )
	{
		return wrappedList.retainAll( objects );
	}

	@Override
	public void remove( int i, int i2 )
	{
		wrappedList.remove( i, i2 );
	}

	@Override
	public int size()
	{
		return wrappedList.size();
	}

	@Override
	public boolean isEmpty()
	{
		return wrappedList.isEmpty();
	}

	@Override
	public boolean contains( Object o )
	{
		return wrappedList.contains( o );
	}

	@Override
	public Iterator iterator()
	{
		return wrappedList.iterator();
	}

	@Override
	public Object[] toArray()
	{
		return wrappedList.toArray();
	}

	@Override
	public Object[] toArray( Object[] a )
	{
		return toArray( a );
	}

	@Override
	public boolean add( Object o )
	{
		return wrappedList.add( o );
	}

	@Override
	public boolean remove( Object o )
	{
		return wrappedList.remove( o );
	}

	@Override
	public boolean containsAll( Collection c )
	{
		return wrappedList.containsAll( c );
	}

	@Override
	public boolean addAll( Collection c )
	{
		return wrappedList.addAll( c );
	}

	@Override
	public boolean addAll( int index, Collection c )
	{
		return wrappedList.addAll( index, c );
	}

	@Override
	public boolean removeAll( Collection c )
	{
		return wrappedList.removeAll( c );
	}

	@Override
	public boolean retainAll( Collection c )
	{
		return wrappedList.retainAll( c );
	}

	@Override
	public void clear()
	{
		wrappedList.clear();
	}

	@Override
	public boolean equals( Object o )
	{
		return wrappedList.equals( o );
	}

	@Override
	public int hashCode()
	{
		return wrappedList.hashCode();
	}

	@Override
	public Object get( int index )
	{
		return wrappedList.get( index );
	}

	@Override
	public Object set( int index, Object element )
	{
		return wrappedList.set( index, element );
	}

	@Override
	public void add( int index, Object element )
	{
		wrappedList.add( index, element );
	}

	@Override
	public Object remove( int index )
	{
		return wrappedList.remove( index );
	}

	@Override
	public int indexOf( Object o )
	{
		return wrappedList.indexOf( o );
	}

	@Override
	public int lastIndexOf( Object o )
	{
		return wrappedList.lastIndexOf( o );
	}

	@Override
	public ListIterator listIterator()
	{
		return wrappedList.listIterator();
	}

	@Override
	public ListIterator listIterator( int index )
	{
		return wrappedList.listIterator( index );
	}

	@Override
	public List subList( int fromIndex, int toIndex )
	{
		return wrappedList.subList( fromIndex, toIndex );
	}

	@Override
	public void addListener( InvalidationListener invalidationListener )
	{
		wrappedList.addListener( invalidationListener );
	}

	@Override
	public void removeListener( InvalidationListener invalidationListener )
	{
		wrappedList.removeListener( invalidationListener );
	}
}
