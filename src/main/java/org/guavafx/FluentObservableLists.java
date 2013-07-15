package org.guavafx;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javafx.collections.ObservableList;

public class FluentObservableLists
{
	public static FluentObservableList filter( ObservableList wrappedList, Predicate predicate )
	{
		return new FluentObservableList( ObservableLists.filter( wrappedList, predicate ) );
	}

	public static FluentObservableList transform( ObservableList wrappedList, Function function )
	{
		return new FluentObservableList( ObservableLists.transform(wrappedList, function) );
	}
}
