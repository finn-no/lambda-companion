
# Deprecation Notice
This project is no longer being maintained, and its usage should be replaced with Vavr (http://www.vavr.io)


# Purpose

This project adds some concepts that didn't ship with java 8 lambda project like Either<L,R> or Pair<L,R> etc...

# Include from maven

    <dependency>
        <groupId>no.finn.lambda</groupId>
        <artifactId>lambda-companion</artifactId>
        <version>0.27</version>
    </dependency>

# Either cheatsheet

## Why Either?

The ```Either``` class provides a way to enforce failure handling in the same fashion as ```Optional``` is a way to enforce ```null``` values handling.

## How do I use an Either?

Convention dictates that ```Left``` is used for failure path and ```Right``` is used for success / happy path.

## How do I instantiate an Either?

For a failure:

    final Either<String, String> var = Either.left("My failure text");

For a success:

    final Either<String, String> var = Either.right("My text");

## How can I access the Right value?

There is no way to directly access the value inside an ```Either``` in the same way as ```Optional.get()```.
This is made on purpose to avoid code in the style of ```if(either.isRight()) { either.getRight(); }``` which promotes not handling the failure cases (left side).

    final Either<String, String> var = ...;
    final String myValue = either.fold(failure -> handleFailure(failure), Function.identity())

## How can I transform the value on only one side?

For the ```Left``` side:

    final Either<String, String> var = ...;
    final Either<Integer, String> transformed = var.left().map(Integer::valueOf);

For the ```Right``` side:

    final Either<String, String> var = ...;
    final Either<String, Integer> transformed = var.right().map(Integer::valueOf);

## How can I log the failure before transforming it to a default value?

    final Logger LOGGER = ...;
    final Either<Exception, String> either = ...;
      
    final String myValue = either.left().peek(LOGGER::warn)
                                 .fold(failure -> handleFailure(failure), Function.identity());

## I have several Either, how can I log the failures of all of them?

    final Logger LOGGER = ...;
    final Either<Exception, String> either1 = ...;
    final Either<Exception, String> either2 = ...;
    final Either<Exception, String> either3 = ...;
      
    Arrays.asList(either1, either2, either3).stream().forEach(either -> either.forEach(LOGGER::warn));

## How can I conditionally chain functions on the happy path?

    private final Either<Exception, String> getUserInput() {
       ...
    }
      
    private final Either<Exception, Integer> processUserInput(final String userInput) {
       ...
    }
      
    private final Either<Exception, Integer> saveUserInput(final Integer userInput) {
       ...
    }
      
    // processUserInput() only gets executed if getUserInput() succeeded, and saveUserInput() only gets executed if processUserInput() succeeded
    final Either<Exception, Integer> chained = getUserInput().joinRight(this::processUserInput).joinRight(this::saveUserInput);

## How can I combine several Either to use all of their Right values at once?

    private final Integer processValues(final String val1, final String val2, final String val3) {
       ...
    }
      
    final Either<Exception, String> either1 = ...;
    final Either<Exception, String> either2 = ...;
    final Either<Exception, String> either3 = ...;

    Either<Exception, Integer> result = either1.joinRight(val1 -> either2.joinRight(val2 -> either3.right().map(val3 -> processValues(val1, val2, val3))));

# StreamableOptional cheatsheet

## Why StreamableOptional

It happens (rather often) to end with a ```Stream``` containing ```Optional```-s and to be willing to only retain ```Present``` instances.


In Java 8 there are 2 options:

* the bad: filtering with ```isPresent()``` and unwrapping values with ```get()```: ```stream().filter(Optional::isPresent).map(Optional::get)```
* and the ugly: flat-mapping ```Optional``` values to ```Stream``` of one or no value: ```stream().flatMap(opt -> opt.map(Stream::of).orElseGet(Optional::empty))```

StreamableOptional is a cleaner shorthand for the second solution above (provided that you use ```StreamableOptional``` instead
of ```Optional``` ): ```stream.flatMap(StreamableOptional::stream)```.

## How do I retain only present instances

    Arrays.asList(StreamableOptional.of(1), StreamableOptional.empty(), StreamableOptional.of(3))
      .stream()
      .flatMap(StreamableOptional::stream)
      .forEach(System.out::println);
      
    // yields :
    // 1
    // 2

## I already have a stream of traditional Java 8 Optional, how does that help me?

It might not be the ideal setup, however you still have two possibilities:

* try to replace usages of ```Optional``` with ```StreamableOptional``` upstream of your code if you can;
* else : ```stream.map(StreamableOptional::ofOptional).flatMap(StreamableOptional::stream)``` or ```stream.map(opt -> StreamableOptional.ofOptional(opt).stream())```

## How do I get back a traditional Java 8 Optional?

This might indicate a code smell since:

* you probably used ```StreamableOptional``` to flatMap values into a ```Stream``` (and thus have no more use for a ```StreamableOptional```)
* you can use the same operations on ```StreamableOptional``` as on ```Optional```

However, this case can happen when you interact with an API that requires an ```Optional```. Then simply use the ```toOptional()``` method.

    public apiMethodRequiringAnOptional(Optional<Whatever> maybe);
      
    // call it
    apiMethodRequiringAnOptional(myStreamableOptional.toOptional());

# Try cheatsheet

## Why Try?

The ```Try``` class provides convenient ways to handle computations which might fail. 

Unlike ```Either``` it is right-biased,
simplifying the use of familiar higher order functions such as map, flatMap and forEach. 

In proper functional languages, 
```Try``` is a monad where as ```Either``` is not, (but this implementation does not satisfy the monadic laws
because of how flatMap borrows inspiration from its counterpart in ```java.util.Optional```)

**Warning: Do not use ```Try``` on ```Autoclosable``` resources and expect them to close (try-with-resources) - ```Try``` does not close resources!**

## An example of using Try

    private void playWithTry() {
        //a chain of Exception-prone operations
        Try.of(Integer::valueOf, "3f")
            .map(x -> x * 2)
      
        byte[] mybytes = {0b101, 0b001};
                String myString = Try.of(Float::valueOf, "3F")
                        .map(f -> f * 2)
                        .peek(f -> System.out.println("Float? : " + f))
                        .peekFailure(f -> System.out.println("Exception1? : " + f.getThrowable()))
        
                        .map(f -> f / 0)
                        .peek(f -> System.out.println("Float? : " + f))
                        .peekFailure(f -> System.out.println("Exception2? : " + f.getThrowable()))
        
                        .map(f -> mybytes)
                        .peek(b -> System.out.println("bytes? " + b))
                        .peekFailure(f -> System.out.println("Exception3? : " + f.getThrowable()))
        
                        .map(bytes -> new String(bytes, 0, 10, "UTF-128"))
                        .peek(s -> System.out.println("String? " + s))
                        .peekFailure(f -> System.out.println("Exception4? : " + f.getThrowable()))
                        .recover(s -> s, f -> f.getMessage());
                
        //Try from a two argument Function
        Try<Integer> myTry = Try.of((a, b) -> a / b, 3, 0);
        //Try to Option
        Optional<Integer> oInt = myTry.toOptional();
        //Try to Either
        Either<Throwable, Integer> integerEither = myTry.toEither();

        //flatMap - a map without nested Try as the result
        Integer divByZero = Try.of(Integer::new, 3)
                .flatMap(i -> div(i, 0))
                .recover(Function.identity(), a -> 0);
      
        //orElse when Failure
        Integer shouldEqZero = Try.of((a, b) -> a / b, 1, 0).orElse(0);
      
        //orElseGet when Failure (the fallback Supplier is lazy)
        Integer shouldEqZero = Try.of((a, b) -> a / b, 1, 0).orElseGet(() -> 0);
      
        //forEach only runs when success
        new Success<>(3)
                .forEach(i -> System.out.println("a number : " + i));
                
        //escape the Try structure and enter a regular try-catch flow
        Try<Object> myTry = Try.of(() -> {
            throw new IOException("floppy drive too busy");
        });
        try {
            myTry.orElseThrow(e -> new AWTException("hah!"));
        } catch (AWTException e) {
            //handle and/or propagate
        }
    }
      
    private Try<Integer> div(Integer a, Integer b) {
        return Try.of((x,y) -> x / y, a, b);
    }
      
    //this function must end with a value
    private String handleFailure(Exception e) {
        e.printStackTrace();
        return "I'm afraid this didnt work because of " + e.getMessage();
    }
