Jax (JAva-like eXpression language)
started 2009 January by Josh Wolfe (thejoshwolfe at gmail)

Currently in development (meaning the example code below might not compile yet).

Jax is a programming language which compiles into Java Bytecode. It is a modified version of Java 
that treats everything inside a method body as an expression.


Example Java/Jax comparison:

Java:

    public int readAbsNumber(Scanner input) {
        int number;
        try {
            number = input.nextInt();
        } catch (InputMismatchException e) {
            number = 0;
        }
        return number < 0 ? -number : number;
    }

Jax:

    public int readAbsNumber(Scanner input) {
        int number = try input.nextInt() catch (InputMismatchException e) 0;
        return if (number < 0) -number else number;
    }

The whole point of the try...catch statement in the Java code was to get a value for "number".
In the Jax code, the try...catch expression evaluates to an int, and is then stored in "number".
try...catch, if...else, and all other "statements" in Java evaluate as expressions in Jax.
The return type of an expression can be "void", meaning you can't assign or use the value anywhere.


Current state of Jax:

Since we are in a very early state of development, there are many restrictions on the language,
but they are gradually being overcome as the project expands.

See README_dev.txt for development info.
