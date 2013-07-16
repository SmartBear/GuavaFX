GuavaFX
=======

A functional library for [JavaFX][5]'s [ObservableList] [1] class, inspired by [Google Guava] [2].

GuavaFX allows you to _transform_, _filter_ and _concat_ an ObservableList, keeping the returned ObservableList constantly in sync.


### Concept
```java
filter( anObservableList, aPredicate ).transform( aFunction );
```

### Example
```java
import static org.guavafx.FluentObservableLists.*
import static javafx.beans.binding.Bindings.*

ObservableList<TweetNode> tweetNodes = filter( tweets, contains( "#javafx" ) ).
                                       transform( toTweetNode );
bindContent( tweetsListView.getItems(), tweetNodes );
```
As a comparison, [here][4] is a plain JavaFX implementation.

### Start using
This is what you need in your pom.xml file to start using GuavaFX:
```XML
<pluginRepositories>
   <pluginRepository>
      <id>loaduiRepository</id>
      <url>http://www.soapui.org/repository/maven2/</url>
   </pluginRepository>
</pluginRepositories>
```
```XML
<dependency>
    <groupId>org.loadui</groupId>
    <artifactId>guavaFx</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Credits
GuavaFX was initially created by @dainnilsson as a part of [LoadUI][3]. Today, it is being extended
and maintained by the LoadUI team.

[1]: http://docs.oracle.com/javafx/2/api/javafx/collections/ObservableList.html        "Observable List JavaDoc"
[2]: https://code.google.com/p/guava-libraries/        "Google Guava home"
[3]: https://github.com/SmartBear/loadui        "LoadUI project at Github"
[4]: https://github.com/SmartBear/GuavaFX/wiki/Concrete-Example-with-plain-JavaFX "Concrete Example in plain JavaFX"
[5]: http://www.oracle.com/technetwork/java/javafx/overview/index.html "JavaFX website"
