GuavaFX
=======

A functional library for JavaFX's [ObservableList] [1] class, inspired by [Google Guava] [2].

GuavaFX allows you to _transform_, _filter_ and _concat_ an ObservableList, keeping the returned ObservableList constantly in sync.

The code was initially written by @dainnilsson as a part of [LoadUI][3].

### Abstract Usage Example
```java
import static org.guavafx.FluentObservableList.*

filter( anObservableList, aPredicate ).transform( aGuavaFunction );
```

### Concrete Usage Example
```java
import static org.guavafx.FluentObservableList.*
import static javafx.beans.binding.Bindings.*

ObservableList<TweetNode> tweetNodes = filter( tweets, contains( "#javafx" ) ).
                                       transform( toTweetNode );
bindContent( tweetsListView, tweetNodes );
```


[1]: http://docs.oracle.com/javafx/2/api/javafx/collections/ObservableList.html        "Observable List JavaDoc"
[2]: https://code.google.com/p/guava-libraries/        "Google Guava home"
[3]: https://github.com/SmartBear/loadui        "LoadUI project at Github"
