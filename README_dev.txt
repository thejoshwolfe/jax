
first read README.txt, then this document.

Phases of compiling (see Main.comprehend():
1. Tokenization - generates an array of tokens
2. Parsing - generates and Abstract Syntax Tree
3. Lexiconization (or Lexical Analysis for you traditionalists) - fills in the AST with contextual info
4. Code Generation - generates Jasmin code


We have Codegen!
we can now compile "test.jax" into "test.jasmin" into "test.class".
   see HelloWorld.jasmin for jasmin's syntax.
answer key:
   I'm using "test_goal.java" -> "test_goal.class" -> "test_goal.javap" as a guideline to verify we're doin it right.
   to compile the answer key:
       javac test_goal.java
       javap -c test_goal > test_goal.javap
   javap's syntax is different, but the .class files should be nearly identical.
Difference:
   I see javac turns my int literal (7) into a "bipush" instruction, whereas I'm using a "ldc" instruction. TODO: What's the difference?
Call.java:
   Simple java file that tests the functionality of test.class by calling method foo() and verifying that it returns 7.


The contents of net.wolfesoftware.java.jax.ast would ideally be generated from the specification coded out in "jax.jape".
-For an explanation and examples of the .jape syntax, see "jax.jape".
-For example code in the language specified by "simple.jape", see "simple.jax"


If you'd like to help out, search for "TODO" throughout the code (and even this document). Also, most occurrences of the following code

default:
    throw new RuntimeException();

are indicative of the need for more cases.
