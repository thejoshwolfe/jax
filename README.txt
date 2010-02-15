Jax (JAva-like eXpression language)
started 2009 January by Josh Wolfe (thejoshwolfe@gmail.com)

Currently in development (meaning the example code below might not compile yet).

Jax is a programming language which compiles into Java Bytecode. It is a modified version of Java 
with that combines Ruby-like block semantics and powerful C- and Lisp-like macros. The fundamental
difference between Java and Jax is that Jax treats blocks and statements as expressions. Just as
the ?: operator is the expression equivalent of the if-else statement, all Java statements are Jax
expression constructs. For example, a try-catch could be the source of an assignment (see example).
With the arbitrary nestability of these semantics, inlining functions becomes nearly trivial, and
the ability to use macros become feasible. Macros are inlined and so are able to modify their 
parameters (resembling pass-by-reference parameters) and are able to return from their containing
function. This yields a good portion of the power of C macros and yet keeps namespaces cleanly 
separated like Lisp macros.



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



Macros:

One serious selling point of Jax is its ability to support macros. Macros are (or rather will be) 
implemented as inline functions. 

Benefits of macros:
    - efficiency. of course.
    - pass-by-reference parameters. There is no real invocation of a macro, which means the body 
      of a macro can use the original variables themselves instead of the value of them at 
      invocation time.
    - cross-scoped control statements. A macro can return from the enclosing function.
Drawbacks of macros:
    - currntly, all macros must be private, because it is non-trivial to store them in .class files.
      In order for macros to be really useful, this limitation will have to be overcome.

see ideas.txt for examples of proposed macro functionality



Current state of Jax:

There is a suite of test cases in the test/ directory. All of them work (excluding "goal" test 
cases), so you can get an idea of the current state of the feature set from them. Test cases with
"fancy" in their names are good demonstrations of currently supported features.



How the Jax compiler works:

See README_dev.txt for development info.

