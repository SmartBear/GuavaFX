/*
 * Copyright 2013 SmartBear Software
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package org.guavafx;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Sets.newHashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.scene.Node;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Utility class for dealing with JavaFX ObservableLists.
 * 
 * @author dain.nilsson
 * @author henrik.olsson
 */
public class ObservableLists
{
	/**
	 * Creates a readonly ObservableList containing all OSGi published services
	 * for the given Class type. The list is dynamically updated to reflect
	 * changes in the services.
	 * 
	 * @param type
	 * @return
	 */

	/**
	 * Creates an ObservableList of transformed elements from a given list.
	 * 
	 * @param original
	 * @param function
	 * @return
	 */
	public static <F, T> ObservableList<T> transform( final ObservableList<F> original, final Function<F, T> function )
	{
		final ListeningList<F, T> listeningList = new ListeningList<>( original );

		final LoadingCache<F, T> cache = CacheBuilder.newBuilder().weakKeys().softValues().build( new CacheLoader<F, T>()
		{
			@Override
			public T load( F key ) throws Exception
			{
				return function.apply( key );
			}
		} );

		final InvalidationListener listener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				listeningList.list.setAll( Lists.newArrayList( Iterables.transform( original, cache ) ) );
			}
		};
		listeningList.addListener( listener );
		listener.invalidated( original );

		return listeningList.readOnlyList;
	}

	/**
	 * Creates an ObservableList which contains all elements in the original list
	 * which satisfy the given condition.
	 * 
	 * @param original
	 * @param condition
	 * @return
	 */
	public static <E> ObservableList<E> filter( final ObservableList<E> original, final Predicate<E> condition )
	{
		final ListeningList<E, E> listeningList = new ListeningList<>( original );
		listeningList.addListener( new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				listeningList.list.setAll( Lists.newArrayList( Iterables.filter( original, condition ) ) );
			}
		} );
		listeningList.list.setAll( Lists.newArrayList( Iterables.filter( original, condition ) ) );

		return listeningList.readOnlyList;
	}

	/**
	 * Returns an unmodifiable view of the given ObservableList, where all
	 * modifications are guaranteed to be done in the FX thread.
	 * 
	 * @param originalList
	 * @return
	 */
	public static <E> ObservableList<E> fx( final ObservableList<E> original )
	{
		final ListeningList<E, E> listeningList = new ListeningList<>( original );
		final InvalidationListener listener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				final ImmutableList<E> elements = ImmutableList.copyOf( original );
				if( Platform.isFxApplicationThread() )
				{
					listeningList.list.setAll( elements );
				}
				else
				{
					Platform.runLater( new Runnable()
					{
						@Override
						public void run()
						{
							listeningList.list.setAll( elements );
						}
					} );
				}
			}
		};
		listeningList.addListener( listener );
		listener.invalidated( original );

		return listeningList.readOnlyList;
	}

	/**
	 * Returns an unmodifiable view of the given ObservableList, where all
	 * modifications are optimized.
	 * 
	 * @param originalList
	 * @return
	 */
	public static <E> ObservableList<E> optimize( final ObservableList<E> originalList )
	{
		final ListeningList<E, E> listeningList = new ListeningList<>( originalList );
		final InvalidationListener listener = new InvalidationListener()
		{
			private int syncNumber = 0;

			private void synchronize( int sync )
			{
				if( syncNumber == sync )
				{
					listeningList.list.retainAll( originalList );

					Ordering<E> ordering = Ordering.explicit( originalList );
					FXCollections.sort( listeningList.list, ordering );

					List<E> elementsToAdd = Lists.newArrayList( Iterables.filter( originalList,
							not( in( listeningList.list ) ) ) );
					Collections.sort( elementsToAdd, ordering );
					for( E element : elementsToAdd )
					{
						listeningList.list.add( originalList.indexOf( element ), element );
					}
				}
			}

			@Override
			public void invalidated( Observable _ )
			{
				final int nextSync = ++syncNumber;
				Platform.runLater( new Runnable()
				{
					@Override
					public void run()
					{
						synchronize( nextSync );
					}
				} );
			}
		};
		listeningList.addListener( listener );
		listeningList.list.setAll( originalList );

		return listeningList.readOnlyList;
	}

	public static <E, T extends Iterable<E>> ObservableList<E> fromExpression( Callable<T> expression,
			ObservableList<? extends Observable> observables )
	{
		return new ExpressionList<E>( expression, observables ).readOnlyList;
	}

	private static final LoadingCache<List<?>, ListChangeListener<?>> contentListeners = CacheBuilder.newBuilder()
			.weakKeys().weakValues().build( new CacheLoader<List<?>, ListChangeListener<?>>()
			{
				@Override
				@SuppressWarnings( "rawtypes" )
				public ListChangeListener<?> load( final List<?> list ) throws Exception
				{
					return new ListChangeListener()
					{
						@Override
						@SuppressWarnings( "unchecked" )
						public void onChanged( ListChangeListener.Change change )
						{
							while( change.next() )
							{
								list.removeAll( change.getRemoved() );
								list.addAll( change.getAddedSubList() );
							}
						}
					};
				}
			} );

	/**
	 * Returns a new ObservableList that contains all the elements of the given
	 * lists, and keeps the new list in sync with any changes to the original
	 * lists. The order of the elements are guaranteed to correspond to the order
	 * of the elements in the sublists -- but as a consequence, this method is
	 * inefficient and recreates the whole list on any change.
	 * 
	 * @param listsToConcat
	 * @return
	 */
	public static final <T> ObservableList<T> concat(
			final ObservableList<? extends ObservableList<? extends T>> listsToConcat )
	{
		return fromExpression( new Callable<Iterable<T>>()
		{
			@Override
			public Iterable<T> call() throws Exception
			{
				return Iterables.concat( listsToConcat );
			}
		}, listsToConcat );
	}

	@SafeVarargs
	public static final <T> ObservableList<T> concat( ObservableList<? extends T> first,
			ObservableList<? extends T> second, ObservableList<? extends T>... rest )
	{
		return concat( FXCollections.observableList( Lists.asList( first, second, rest ) ) );
	}

	/**
	 * Returns a new ObservableList that contains all the elements of the given
	 * list, with elementToAppend added last. The order of the elements are
	 * guaranteed to correspond to the order of the elements in the sublists --
	 * but as a consequence, this method is inefficient and recreates the whole
	 * list on any change.
	 * 
	 * @return
	 */

	public static final <T> ObservableList<T> appendElement( final ObservableList<? extends T> inputList,
			final T elementToAppend )
	{
		final ListeningList<T, T> listeningList = new ListeningList<>( inputList );
		final InvalidationListener listener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				listeningList.list.setAll( inputList );
				listeningList.list.add( elementToAppend );
			}
		};
		listeningList.addListener( listener );
		listener.invalidated( inputList );

		return listeningList.readOnlyList;
	}

	/**
	 * Returns a new ObservableList that contains all the elements of the given
	 * list, with elementToPrepend added first. The order of the elements are
	 * guaranteed to correspond to the order of the elements in the sublists --
	 * but as a consequence, this method is inefficient and recreates the whole
	 * list on any change.
	 * 
	 * @return
	 */

	public static final <T> ObservableList<T> prependElement( final ObservableList<? extends T> inputList,
			final T elementToPrepend )
	{
		final ListeningList<T, T> listeningList = new ListeningList<>( inputList );
		final InvalidationListener listener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable _ )
			{
				listeningList.list.add( elementToPrepend );
				listeningList.list.addAll( inputList );
			}
		};
		listeningList.addListener( listener );
		listener.invalidated( inputList );

		return listeningList.readOnlyList;
	}

	/**
	 * Like Bindings.bindContent(), but doesn't take ordering into account. This
	 * means that list1 can be reordered after binding, and the content will
	 * still stay in sync with list2.
	 * 
	 * @param list1
	 * @param list2
	 */
	@SuppressWarnings( "unchecked" )
	public static <E> void bindContentUnordered( final List<E> list1, ObservableList<? extends E> list2 )
	{
		list2.addListener( ( ListChangeListener<? super E> )contentListeners.getUnchecked( list1 ) );

		if( list1 instanceof ObservableList )
		{
			( ( ObservableList<E> )list1 ).setAll( list2 );
		}
		else
		{
			list1.clear();
			list1.addAll( list2 );
		}
	}

	public static <E> void bindSorted( final List<E> list1, final ObservableList<? extends E> list2,
			final Comparator<? super E> comparator, Observable... observables )
	{
		final InvalidationListener invalidationListener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable arg0 )
			{
				if( list1 instanceof ObservableList )
				{
					FXCollections.sort( ( ObservableList<E> )list1, comparator );
				}
				else
				{
					Collections.sort( list1, comparator );
				}
			}
		};
		list2.addListener( invalidationListener );
		bindContentUnordered( list1, list2 );

		@SuppressWarnings( "unchecked" )
		final ListChangeListener<E> contentSyncer = ( ListChangeListener<E> )contentListeners.getUnchecked( list1 );
		final ListChangeListener<E> sortingListener = new ListChangeListener<E>()
		{
			@Override
			public void onChanged( ListChangeListener.Change<? extends E> change )
			{
				contentSyncer.onChanged( change );
				invalidationListener.invalidated( list2 );
			}
		};
		contentListeners.put( list1, sortingListener );
		list2.removeListener( contentSyncer );
		list2.addListener( sortingListener );
		for( Observable observable : observables )
		{
			observable.addListener( invalidationListener );
		}
		invalidationListener.invalidated( list2 );
	}

	@SuppressWarnings( "unchecked" )
	public static <E> void unbindContent( List<? super E> list1, ObservableList<E> list2 )
	{
		list2.removeListener( ( ListChangeListener<? super E> )contentListeners.getUnchecked( list1 ) );
	}

	private static final Cache<Object, Map<String, Object>> osgiProperties = CacheBuilder.newBuilder().weakKeys()
			.build();

	/**
	 * Gets the OSGi properties of an OSGi service that has been imported using
	 * the ofServices() method.
	 * 
	 * @param service
	 * @return
	 */
	public static Map<String, Object> getOsgiProperties( Object service )
	{
		Map<String, Object> properties = osgiProperties.getIfPresent( service );
		if( properties == null )
		{
			throw new NoSuchElementException();
		}

		return properties;
	}

	@SuppressWarnings( "serial" )
	private static class ListeningList<F, T> extends ArrayList<T>
	{
		private final ObservableList<? extends F> originalList;
		private final ObservableList<T> list;
		private final ObservableList<T> readOnlyList;
		private final Set<Object> hardrefs = new HashSet<>();

		private ListeningList( ObservableList<? extends F> originalList )
		{
			this.originalList = originalList;
			list = FXCollections.observableList( this );
			readOnlyList = FXCollections.unmodifiableObservableList( list );
		}

		public ListeningList<F, T> addListener( InvalidationListener listener )
		{
			originalList.addListener( new WeakInvalidationListener( listener ) );
			hardrefs.add( listener );

			return this;
		}

		public ListeningList<F, T> addListener( ListChangeListener<F> listener )
		{
			originalList.addListener( new WeakListChangeListener<>( listener ) );
			hardrefs.add( listener );

			return this;
		}
	}

	@SuppressWarnings( "serial" )
	private static class ExpressionList<E> extends ArrayList<E>
	{
		private final ListChangeListener<Observable> observablesListListener = new ListChangeListener<Observable>()
		{
			@Override
			public void onChanged( ListChangeListener.Change<? extends Observable> change )
			{
				while( change.next() )
				{
					for( Observable elem : change.getAddedSubList() )
					{
						elem.addListener( weakObservableListener );
					}
					for( Observable elem : change.getRemoved() )
					{
						elem.removeListener( weakObservableListener );
					}
				}
			}
		};
		private final ListChangeListener<Observable> weakObservablesListListener = new WeakListChangeListener<>(
				observablesListListener );

		private final InvalidationListener observableListener = new InvalidationListener()
		{
			@Override
			public void invalidated( Observable arg0 )
			{
				if( Platform.isFxApplicationThread() )
				{
					try
					{
						list.setAll( Lists.newArrayList( expression.call() ) );
					}
					catch( Exception e )
					{
						throw new RuntimeException( e );
					}
				}
				else
					Platform.runLater( new Runnable()
					{
						@Override
						public void run()
						{
							try
							{
								list.setAll( Lists.newArrayList( expression.call() ) );
							}
							catch( Exception e )
							{
								throw new RuntimeException( e );
							}
						}
					} );
			}
		};

		private final WeakInvalidationListener weakObservableListener = new WeakInvalidationListener( observableListener );

		private final ObservableList<E> list;
		private final ObservableList<E> readOnlyList;
		private final Callable<? extends Iterable<E>> expression;
		@SuppressWarnings( "unused" )
		private ObservableList<? extends Observable> observables; // Needs to be a field to avoid GC.

		private ExpressionList( Callable<? extends Iterable<E>> expression,
				ObservableList<? extends Observable> observables )
		{
			this.expression = expression;
			this.observables = observables;

			list = FXCollections.observableList( this );
			readOnlyList = FXCollections.unmodifiableObservableList( list );

			observables.addListener( weakObservablesListListener );
			observables.addListener( weakObservableListener );
			for( Observable observable : observables )
			{
				observable.addListener( weakObservableListener );
			}

			observableListener.invalidated( observables );
		}
	}


	public static <E> Set<E> getActuallyRemoved( ListChangeListener.Change<E> c )
	{
		if( c.wasRemoved() )
		{
			return ImmutableSet
					.copyOf( Sets.difference( newHashSet( c.getRemoved() ), newHashSet( c.getAddedSubList() ) ) );
		}
		return ImmutableSet.of();
	}

	public static void releaseElementsWhenRemoved( ObservableList<? extends Node> list )
	{
		list.addListener( new ListChangeListener<Node>()
		{
			@Override
			public void onChanged( javafx.collections.ListChangeListener.Change<? extends Node> c )
			{
				while( c.next() )
				{
					for( Node node : getActuallyRemoved( c ) )
					{
						NodeUtils.releaseRecursive( node );
					}
				}
			}
		} );
	}
}
