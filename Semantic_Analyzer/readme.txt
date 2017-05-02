Molumby, Megan N00942101
Construction of Language Translators~COP 4620
Dr. Eggen
Date Submitted: 3 APR 2017
Due Date: 4 APR  2017

i.Purpose of Project
Construct a semantic analyzer, following the simplifid C- grammer rules.
Compiler Construction- Principles and Practice by Kenneth C. Louden
The following is a brief but not all inclusive list of semantic checks: 
main function must be declared last
return only simple data types
functions declared int or float  must have a return value of the
   correct type.
void functions may or may not have a return, but must not return a
   value.
parameters and arguments agree in number
parameters and arguments agree in type
operand agreement
operand/operator agreement
array index agreement
variable declaration (all variables must be declared ... scope)
variable declaration (all variables declared once ... scope)
void functions cannot have a return value
each program must have one main function
return only simple structures
id's should not be type void
each function should be defined (actually a linker error)
ii. Source File(s)
        prj3.java
        HashTable.java
        DataItem.java
	LexicalAnalyzer.java
	Parser.java
	Symbol.java

iii.Input file(s)
        given at command line OR
        testfile.txt
iv.Output files:
        None
v.Instructions to execute project

        In command line:
                >sh n00942101
                >make
                >p3 testfile*
        *Where p3 is a script

