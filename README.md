fructose
=========
[![Build Status](https://travis-ci.org/horthy/fructose.svg?branch=master)](https://travis-ci.org/horthy/fructose)

A questionably useful, borderline obnoxious collection of convenience methods for building higher-order functions in Java 8.


Examples
--------
    
###### Ever wanted to filter a stream of predicates? The wait is over!

```java
import com.github.horthy.fructose.Predicates;

Stream.of(
    x -> x > 5,
    x -> x < 10,
    x -> x % 2 == 0,
    x -> x == 12
 ).filter(Predicates.testing(12))
 // x -> x > 5
 // x -> x == 12, 
 // x -> x % 2 == 0
    
```
    
Even chain them together!   
    
```java
import com.github.horthy.fructose.Predicates;

Stream.of(
    x -> x > 5,
    x -> x < 10,
    x -> x % 2 == 0,
    x -> x == 12
 ).filter(Predicates.testing(9)) // (x -> x > 5), (x -> x < 10)
  .filter(Predicates.testing(12) // (x -> x > 5)
  .anyMatch(Predicates.testing(6)) // true
    
```

###### Ever wanted to sort a stream of comparators? Now you can!

```java
import com.github.horthy.fructose.Comparators;

Comparator<String> compareLength = Comparator.comparing(String::length);
Comparator<String> naturalOrder = Comparator.naturalOrder();
    
Stream.of(
        compareByLength,                     // bc < abc
        naturalOrder                         // abc < bc
).sorted(Comparators.comparing("abc", "bc")) // naturalOrder < compareByLength
// [naturalOrder, compareByLength]
```

###### Functions of functions? You Betcha!

```java 

import com.github.horthy.fructose.Functions;

Stream.of(
        x -> x * x,
        x -> x + 1,
        x -> x / 2
).map(Functions.applying(6)) // [36, 7, 3]

```


###### Consume consumers!

```java

import com.github.horthy.fructose.Consumers;

List<Integer> list = Lists.newArrayList();

Stream.of(
    x -> list.add(x),
    x -> list.add(x + 5)
).forEach(Consumers.accepting(5));
//  list == [5, 10]  
```


###### Supply suppliers!

```java

import com.github.horthy.fructose.Predicates;
import com.github.horthy.fructose.Maps;

Stream.generate(
    Suppliers.supplying(() -> 5)
).limit(5)
.mapToInt(Supplier::get)
.sum() // 25
```


###### Map Suppliers:

```java 

import com.github.horthy.fructose.Consumers;

Supplier<Date> dates = () -> new Date();
Supplier<Long> epochs = Suppliers.mapping(dates, Date::getTime);

```

Even create a `Function<Supplier<T>, Supplier<R>>` for `Stream#map`:

```java 

import com.github.horthy.fructose.Suppliers;


Stream.of(
    () -> new Date(),
    () -> new Date().clearTime(),
    () -> new Date().minus(30), 
).map(Suppliers.mapping(Date::getTime))

// () -> new Date().getTime()
// () -> new Date().clearTime().getTime()
// () -> new Date().minus(30).getTime()
```

###### Map Consumers!

```java 

import com.github.horthy.fructose.Consumers;

Consumer<String> stringConsumer = s -> System.out.println(s);
Consumer<Date> dateConsumer = Consumers.mapping(stringConsumer, Date::toString);

```

Even create a `Function<Consumer<R>, Consumer<T>>` for `Stream#map`:

```java 

import com.github.horthy.fructose.Consumers;


Stream.of(
    (String s) -> System.out.println(s),
    (String s) -> System.out.println("Today's date is " + s)
).map(Consumers.mapping(Date::toString))
        .forEach(Consumers.accepting(new Date());
// Output:
// Sun Jun 12 13:42:46 PDT 2016
// Today's date is Sun Jun 12 13:42:46 PDT 2016
```

###### Mapping a stream of maps? Yessiree

```java

import com.github.horthy.fructose.Maps;

Stream.of(
        ImmutableMap.of("foo", "bar", "spam", "eggs"),
        ImmutableMap.of("foo", "baz"),
        ImmutableMap.of("spam", "eggs")
).map(Maps.getting("foo"))      // ["bar", "baz", null]
        .filter(x -> x != null)
        .collect(toList())      // ["bar", "baz"]

```

###### Filter Map entries by key!

```java

import com.github.horthy.fructose.Predicates;
import com.github.horthy.fructose.Maps;

ImmutableMap.of("foo", "bar", "spam", "eggs", "web", "scale")
    .entrySet()
    .stream()
    .filter(Predicates.keys(k -> k.length() == 3)
    .collect(Maps.collectEntries())    // {"foo": "bar", "web": "scale"}
        
```

And by value!

```java

import com.github.horthy.fructose.Predicates;
import com.github.horthy.fructose.Maps;

ImmutableMap.of("foo", "bar", "spam", "eggs", "web", "scale")
    .entrySet()
    .stream()
    .filter(Predicates.values(v -> v.startsWith("e"))
    .collect(Maps.collectEntries()) // {"spam": "eggs"}
        
```

###### Decorate functions, suppliers, and consumers!

```java

import com.github.horthy.fructose.Decorators;

Function<String, Character> getFifthChar = s -> s.charAt(4);

Function<String,Character> decorated = Decorators.decorating(getFifthChar)
        .before(str -> System.out.println("getting fifth char of " + str))
        .success((str, c) -> System.out.println(str + " had fifth character " + t))
        .failure((str, t) -> System.out.println("couldn't get fifth char of " + str + " exception was " + t.getMessage()))
        .build();
        
char c = decorated.apply("abcdefg"); // c == 'e'
// Output:
// getting fifth char of abcdefg
// abcdefg had fifth character e

char c = decorated.apply("abc"); // java.lang.StringIndexOutOfBoundsException
// Output:
// getting fifth char of abc
// couldn't get fifth char of abc exception was String index out of range: 4
```

`before`, `success` and `failure` are all optional. `Decorators.decorating(getFifthChar).build().apply("abcdefg")` is perfectly valid.

FAQ
---

> Whyyyyyyy!?

There are a few real-world applications below the FAQ section.

> Is this a joke?

Mostly.

> I have a yak that needs shaving.

I'm your guy.

Actual practical examples
-------------------------

You didn't think I was serious did you?

Notes
-----

- Maven central coming soon.

- There's a fun asymmetry between `Consumers#mapping` and `Suppliers#mapping` -- each take a `Function<T,R>`, 
but the order of type arguments of the resulting function is different -- compare
    - `Function<Supplier<T>, Supplier<R>>`
    - `Function<Consumer<R>, Consumer<T>>`
    
Granted, `java.lang.Consumer` doesn't really fit into a pure-FP stateless programming model, but I'm
sure there's an interesting formalism behind this somewhere.


Contributing
------------

I mean, if you really think its a good idea...

License
-------

MIT
