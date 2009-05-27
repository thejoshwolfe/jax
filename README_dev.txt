
first read README.txt, then this document.

Phases of compiling (see Main.comprehend():
1. Tokenization - generates an array of tokens
2. Parsing - generates and Abstract Syntax Tree
3. Lexiconization (or Lexical Analysis for you traditionalists) - fills in the AST with contextual info
4. Code Generation - generates Jasmin code

See test.README_tests.txt for testing info. 


The contents of net.wolfesoftware.java.jax.ast would ideally be generated from the specification coded out in "jax.jape".
EDIT: there's also additional fields in the classes (like FunctionDefinition.returnType) that isn't (maybe can't be?) specified in the .jape.


If you'd like to help out, search for "TODO" throughout the code (and even this document). Also, most switch-cases throughout need more cases.
