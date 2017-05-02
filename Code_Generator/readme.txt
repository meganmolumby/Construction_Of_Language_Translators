Molumby, Megan N00942101
Construction of Language Translators~COP 4620
Dr. Eggen
Date Submitted: 12 APR  2017
Due Date: 13 APR  2017

i.Purpose of Project
Generate Intermediate Code, using simples quadruples as in the following example, while still  following the simplifid C- grammer rules.
Compiler Construction- Principles and Practice by Kenneth C. Louden.
void main(void)
{
  int x;
  int y;
  int z;
  int m;
   while(x + 3 * y > 5)
   {
     x = y + m / z;
     m = x - y + z * m / z;
   }
}

----------------------------------------------------

1         func           main           void           0
2         alloc          4                             x
3         alloc          4                             y
4         alloc          4                             z
5         alloc          4                             m
6         mult           3              y              _t0 
7         add            x              _t0            _t1
8         comp           _t1            5              _t2
9         BRLEQ          _t2                           21
10        block
11        div            m              z              _t3
12        add            y              _t3            _t4
13        assign         _t4                           x
14        sub            x              y              _t5
15        times          z              m              _t6
16        div            _t6            z              _t7
17        add            _t5            _t7            _t8
18        assign         _t8                           m
19        end            block
20        BR                                           6
21        end            func           main


ii. Source File(s)
        prj4.java
        HashTable.java
        DataItem.java
	LexicalAnalyzer.java
	Parser.java
	Symbol.java
	Stack.java

iii.Input file(s)
        given at command line OR
        testfile.txt
iv.Output files:
        None
v.Instructions to execute project

        In command line:
                >sh n00942101
                >make
                >p4 testfile*
        *Where p4 is a script

