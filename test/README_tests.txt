DEPENDENCIES - the following must be in your PATH variable or however you get to that stuff in unix (bash path?):
java
javac

See the javadoc and comments in:
    net.wolfesoftware.java.jax.test.codegen.CodeGenTest
    net.wolfesoftware.java.jax.test.parser.ParserTest

For a demo of Jax:
    1. Run the CodeGenTest with clean turned off. See comments in the class about turning clean off.
    2. You can find .jasmin and .class files along side the .jax and Call.java files.
    3. You can run the Call classes by cd'ing into the dir and "java <Test>Call".

