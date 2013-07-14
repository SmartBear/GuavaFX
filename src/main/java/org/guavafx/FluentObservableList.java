package org.guavafx;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javafx.collections.ObservableList;

/**
 * An immutable wrapper of ObservableList, whose only purpose is to provide a fluent API for the methods provided by
 * ObservableLists.java.
 *
 * @author henrik.olsson
 */

public class FluentObservableList {

    private ObservableList wrappedList;

    private FluentObservableList( ObservableList wrappedList )
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
}
