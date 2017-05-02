Molumby, Megan N00942101
Construction of Language Translators~COP 4620
Dr. Eggen
Date Submitted: 3 Mar 2017
Due Date: 4 Mar 2017

i.Purpose of Project
Using the grammar definition in the appendix
Compiler Construction- Principles and Practice by Kenneth C. Louden, construct
a recursive descent parser, using the following grammer rules: 

Program -> DeclarationList .
DeclarationList -> Declaration DeclarationListPrime .
Declaration -> int  id DeclarationLeftFactorLeftFactor
	| float id DeclarationLeftFactorLeftFactor
	| void id DeclarationLeftFactorLeftFactor .
VarDeclaration -> void id DeclarationLeftFactorLeftFactor
 	| float id DeclarationLeftFactorLeftFactor
 	| int id DeclarationLeftFactorLeftFactor .
DeclarationLeftFactorLeftFactor -> ( Params ) CompoundStatement 
	| VarDeclarationLeftFactor .
Params -> void ParamsLeftFactor 
	| int ParamLeftFactor ParamListPrime
	| float ParamLeftFactor ParamListPrime .
CompoundStatement -> lbrace LocalDeclarations StatementList rbrace .
ParamsLeftFactor -> id ParamLeftFactor ParamListPrime
	| .
ParamLeftFactor -> lbrkt rbrkt 
	| .
ParamListPrime -> comma Param ParamListPrime
	| .
Param -> int id ParamLeftFactor
	| void id ParamLeftFactor
	| float id ParamLeftFactor .
LocalDeclarations -> VarDeclaration LocalDeclarations
	| .
StatementList -> ; Statement StatementList
	| lbrkt Statement StatementList
	| while Statement StatementList
	| id Statement StatementList
	| num Statement StatementList
	|if Statement StatementList
	|return Statement StatementList
	| ( Statement StatementList
	| numf Statement StatementList
	| .
VarDeclarationLeftFactor -> ;
	| lbrkt num rbrkt ; .
Statement -> ExpressionStatement
	| CompoundStatement
	| SelectionStatement
	| IterationStatement
	| ReturnStatement .
ExpressionStatement -> Expression ;
	| ; .
SelectionStatement -> if ( Expression ) Statement SelectionStatementLeftFactor .
IterationStatement -> while ( Expression ) Statement .
ReturnStatement -> return ReturnStatementLeftFactor .
ReturnStatementLeftFactor -> ;
	| Expression ; .
Expression ->  id ExpressionLeftFactor
	| ( Expression ) TermPrime AddExpressionPrime RelationalOperation
	| int TermPrime AddExpressionPrime RelationalOperation
	| float TermPrime AddExpressionPrime RelationalOperation .
ExpressionLeftFactor -> VariableLeftFactor ExpressionLeftFactorLeftFactor
 	| ( Args ) TermPrime AddExpressionPrime RelationalOperation .
ExpressionLeftFactorLeftFactor -> equals Expression
	| TermPrime AddExpressionPrime RelationalOperation .
VariableLeftFactor -> lbrkt Expression rbrkt
	| .
RelationalOperation -> lessthanequal Factor AddExpressionPrime
	| lessthan Factor AddExpressionPrime
	| greaterthanequal Factor AddExpressionPrime
	| notequalto Factor AddExpressionPrime
	| greaterthan Factor AddExpressionPrime
	| equivalentto Factor AddExpressionPrime
	| .
AddExpressionPrime ->  AdditionOperation Term AddExpressionPrime
	| .
AdditionOperation -> +
	| minus  .
Term -> Factor TermPrime .
TermPrime -> MultiplicationOperation Factor TermPrime
	| .
MultiplicationOperation -> * 
	| / .
Factor -> ( Expression )
	| num
	| numf
	| id X .
X -> FactorLeftFactor
  | VariableLeftFactor .
Args -> )
	| ArgsList .
ArgsListPrime -> comma Expression ArgsListPrime
	| .
SelectionStatementLeftFactor -> else Statement
	| .
DeclarationListPrime -> Declaration DeclarationListPrime
	| .

ii. Source File(s)
        prj2.java
        HashTable.java
        DataItem.java
	LexicalAnalyzer.java

iii.Input file(s)
        given at command line OR
        testfile.txt
iv.Output files:
        None
v.Instructions to execute project

        In command line:
                >sh n00942101
                >make
                >p2 testfile*
        *Where p2 is a script

