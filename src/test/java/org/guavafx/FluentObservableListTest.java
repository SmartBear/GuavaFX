package org.guavafx;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.guavafx.FluentObservableLists.filter;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class FluentObservableListTest {

	private final Predicate<Integer> isEven = new Predicate<Integer>()
	{
		@Override
		public boolean apply( Integer input )
		{
			return input.intValue() % 2 == 0;
		}
	};

	private final Function<Integer,Integer> square = new Function<Integer, Integer>()
	{
		@Override
		public Integer apply( Integer input )
		{
			return input * input;
		}
	};

	@Test
    public void basicTest()
    {
		ObservableList<Integer> allElements = FXCollections.observableArrayList();

		ObservableList<Integer> fluentList = filter( allElements, isEven ).transform( square );

		allElements.addAll( 1, 2, 3, 4 );
		allElements.addAll( 5, 6, 7 );

		assertThat( fluentList, equalTo( asList( 4, 16, 36 ) ) );

		allElements.removeAll( 4 );

		assertThat( fluentList, equalTo( asList( 4, 36 ) ) );
    }
}
