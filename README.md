GuavaFX
=======

A functional library for JavaFX's [ObservableList] [1] class, inspired by [Google Guava] [2].

Allows you to _transform_, _filter_ and _concat_ ObservableLists, as well as some additional utility methods.

GuavaFX was initially written by @dainnilsson as a part of [LoadUI][3].

```java
FluentObservableList.from( anObservableList ).filter( aGuavaPredicate ).transform( aGuavaFunction );
```


[1]: http://docs.oracle.com/javafx/2/api/javafx/collections/ObservableList.html        "Observable List JavaDoc"
[2]: https://code.google.com/p/guava-libraries/        "Google Guava home"
[3]: https://github.com/SmartBear/loadui        "LoadUI project at Github"
