
The contents of net.wolfesoftware.java.jax.parser.elements would ideally be generated from the specification coded out in "simple.jape".
-For an explanation and examples of the .jape syntax, see "jax.jape".
-For example code in the language specified by "simple.jape", see "simple.jax"


I CAN HAS CODEGEN??
attempting to compile "test.jax" into "test.jasmin" into "test.class".
   see HelloWorld.jasmin for jasmin's syntax.
answer key:
   I'm using "test_goal.java" -> "test_goal.class" -> "test_goal.javap" as a guideline to verify we're doin it right.
   to compile the answer key:
       javac test_goal.java
       javap -c test_goal > test_goal.javap
   javap's syntax is different, but the .class files should be nearly identical.
call_it:
   TODO: write a java class that calls the method test.foo() to make sure the .class file works.
