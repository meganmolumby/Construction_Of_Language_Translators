/** Name: Megan Molumby
**  N#:N00942101
**  Class:COP 4620 Construction of Language Translators
**  Project 2
** Due Date: 3/2/2017
** PROBLEM: construction of parser for compiler.
** Last Update: 3/2/2017
 * BEST COPY !! AS OF 2 March
**/

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.regex.Pattern;
import java.lang.String;


public class prj2 {

    public static void main(String[] args)throws IOException {
        LexicalAnalyzer la = new LexicalAnalyzer();
        int counter = 0;
        //full acceptable input array
        ArrayList<DataItem> parseString = new ArrayList<>();
        //scanner to scan in c program
        Scanner scan = new Scanner(new File(args[0]));
        //writes output to file
        String input;
        //special class to handle input.
        DataItem aDataItem;
        //variable for transferring value of the depth of nested comments.
        int nestedCarryover;
        //Creates Keyword Hashtable for checking if token is a keyword
        String[] keywordArray = new String[]{"else", "if", "int", "return", "void", "while", "float"};
        int sizeKwList = la.getPrime(7);
        //stored by hashing
        HashTable keywordList = new HashTable(sizeKwList);

        int depth = 0;
        Pattern p = Pattern.compile("\\s");

        for (int i = 0; i < keywordArray.length; i++) {

            aDataItem = new DataItem(keywordArray[i]);//creates DataItem from string inputs

            keywordList.insertQuad(aDataItem);//inserts into table
        }

        while (scan.hasNext()) {
            //Scanner takes next input till end of file.
            //if line is blank or white space skip it
            if (scan.hasNext(p)) {
                scan.skip(p);
            }

            input = scan.nextLine();
            input = input.trim();//trims outer whitespace
            if (!input.equals("")) {//if the line is not blank
                aDataItem = new DataItem(input);
                aDataItem.setDepth(depth);//sets depth

                //While the input is a comment, proceed to next line.
                if (la.charCategorization(aDataItem, keywordList, parseString)) {//returns true if still in a comment.
                    //take old line's nested value to attribute to new line.
                    while (aDataItem.getNested() > 0) {
                        nestedCarryover = aDataItem.getNested();
                        depth = aDataItem.getDepth();//saves depth from previous
                        //if not at end of file
                        if (scan.hasNext()) {
                            input = scan.nextLine();
                            aDataItem = new DataItem(input);
                            aDataItem.setNested(nestedCarryover);
                            //carries depth to new item
                            aDataItem.setDepth(depth);
                            la.in_comment(aDataItem);//continue in comment state machine
                        } else {
                            System.out.println("Error: " + aDataItem.getKey());
                            break;
                        }
                    }
                }
                if (!la.endOfLine(aDataItem)) {//if there is still data on the line.
                    //add one to start fresh look at end of comment
                    aDataItem.setCounter(aDataItem.getCounter() + 1);
                    la.charCategorization(aDataItem, keywordList, parseString);
                }
                depth = aDataItem.getDepth();
            }
        }

        //---------------------------------------------------
        //Add $ signaling end of input
        aDataItem = new DataItem("$");
        parseString.add(aDataItem);


        program(parseString, counter);


    }
    public static void program(ArrayList<DataItem> parseString, int counter){
            counter = declarationList(parseString, counter);
        if(parseString.get(counter).getKey().equals("$")) {
             System.out.println("ACCEPT");
        }
    }
    public static int declarationList(ArrayList<DataItem> parseString, int counter){
        counter = declaration(parseString, counter);
        counter = declarationListPrime(parseString, counter);
        return counter;
    }
    public static int declaration(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("void")
                ||parseString.get(counter).getKey().equals("float")){
            counter++;
            if(parseString.get(counter).getType() == "id"){
                counter++;
               counter = declarationLeftFactorLeftFactor(parseString, counter);
            }


        }
        else{
            reject(parseString, counter);
        }
        return counter;
    }
    public static int varDeclaration(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("void")||parseString.get(counter).getKey().equals("int")
                ||parseString.get(counter).getKey().equals("float")) {
            counter++;
            if (parseString.get(counter).getType() == "id") {
                counter++;
                counter = varDeclarationLeftFactor(parseString, counter);
            }
        }
        else{
            reject(parseString, counter);
        }
        return counter;
    }
    public static int declarationLeftFactorLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(";" )|| parseString.get(counter).getKey().equals("[")){
            counter = varDeclarationLeftFactor(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals("(")){
            counter++;
            counter = params(parseString,counter);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                counter = compoundStatement(parseString, counter);
            }
            else{
                reject(parseString, counter);
            }
        }
        else{
            reject(parseString, counter);
        }
        return counter;
    }
    public static int params(ArrayList<DataItem> parseString, int counter) {
        if (parseString.get(counter).getKey().equals("void")) {
            counter++;
            counter = paramsLeftFactor(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("float")){
         counter++;
         if(parseString.get(counter).getType() == "id"){
            counter++;
            counter = paramLeftFactor(parseString, counter);
            counter = paramListPrime(parseString, counter);
         }
        }
        else{reject(parseString, counter);}
        return counter;
    }
    public static int paramsLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getType() == "id"){
            counter++;
            counter = paramLeftFactor(parseString, counter);
            counter = paramListPrime(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals(")")){
            return counter;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int paramListPrime(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(",")){
            counter++;
            counter = param(parseString, counter);
            counter = paramListPrime(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals(")")){
            return counter;
        }
        else{ reject(parseString, counter); }
        return counter;
    }
    public static int param(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("int")
                || parseString.get(counter).getKey().equals("void") || parseString.get(counter).getKey().equals("float")){
            counter++;
            if(parseString.get(counter).getType() == "id"){
                counter++;
                counter = paramLeftFactor(parseString, counter);
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int paramLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("[")){
            counter++;
            if(parseString.get(counter).getKey().equals("]")){
                counter++;
            }
        }else if(parseString.get(counter).getKey().equals(",") || parseString.get(counter).getKey().equals("int")
                ||parseString.get(counter).getKey().equals("void") ||parseString.get(counter).getKey().equals("float")
                ||parseString.get(counter).getKey().equals(")")){
            return counter;
        }else{ reject(parseString, counter); }
        return counter;    
     }
    public static int compoundStatement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("{")){
            counter++;
            counter = localDeclarations(parseString, counter);
            counter = statementList(parseString, counter);
            if(parseString.get(counter).getKey().equals("}")){
                counter++;
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int localDeclarations(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("int")
                || parseString.get(counter).getKey().equals("void") || parseString.get(counter).getKey().equals("float")){
            counter = varDeclaration(parseString, counter);
            counter = localDeclarations(parseString, counter);

        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while")|| parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id" || parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"
                || parseString.get(counter).getKey().equals("}")){
            return counter;

        }
        else{ reject(parseString, counter); }
        return counter;
    }
    public static int statementList(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while") || parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id"|| parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"){
            counter = statement(parseString, counter);
            counter = statementList(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals("}")){
            return counter;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int varDeclarationLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(";")){
            counter++;
        }
        else if(parseString.get(counter).getKey().equals("[")){
            counter++;
            if(parseString.get(counter).getType() == "int"){
                counter++;
                if(parseString.get(counter).getKey().equals("]")) {
                    counter++;
                    if (parseString.get(counter).getKey().equals(";")) {
                        counter++;
                    } else {
                        reject(parseString, counter);
                    }
                }else{
                    reject(parseString, counter);
                }
            }else{
                reject(parseString, counter);
            }



        }else{
            reject(parseString, counter);
        }
        return counter;

    }
    public static int statement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getType() == "id" || parseString.get(counter).getKey().equals("(")){
            counter = expressionStatement(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("{")){
            counter = compoundStatement(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("if")){
            counter = selectionStatement(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("while")){
            counter = iterationStatement(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("return")){
            counter = returnStatement(parseString, counter);
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int expressionStatement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getType() == "id" ){
            counter = expression(parseString, counter);
            if(parseString.get(counter).getKey().equals(";")){
                counter++;
            }else{ reject(parseString, counter); }
        }else if(parseString.get(counter).getKey().equals(";")){
            counter++;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int selectionStatement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("if")){
            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                counter = expression(parseString, counter);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    counter = statement(parseString, counter);
                    counter = selectionStatementLeftFactor(parseString, counter);
                }else{ reject(parseString, counter); }
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int iterationStatement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("while")){
            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                counter = expression(parseString, counter);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    counter = statement(parseString, counter);
                }else{ reject(parseString, counter); }
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int returnStatement(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("return")){
            counter++;
            counter = returnStatementLeftFactor(parseString, counter);
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int returnStatementLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(";")){
           counter++;
        }else{
            counter = expression(parseString, counter);
            if(parseString.get(counter).getKey().equals(";")){
                counter++;
            }
            else{
                reject(parseString, counter);
            }
        }
        return counter;
    }
    public static int expression(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getType() == "id"){
            counter++;
            counter = expressionLeftFactor(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("(")){
            counter++;
            counter = expression(parseString, counter);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                counter = termPrime(parseString, counter);
                counter = addExpressionPrime(parseString, counter);
                counter = relationalOperation(parseString, counter);
            }
            else{ reject(parseString, counter);}
        } else if(parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"){
           counter++;
            counter = termPrime(parseString, counter);
            counter = addExpressionPrime(parseString, counter);
            counter = relationalOperation(parseString, counter);

        } else{ reject(parseString, counter); }
        return counter;
    }
    public static int expressionLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("[") ||parseString.get(counter).getKey().equals("=")
                ||parseString.get(counter).getKey().equals("*") ||parseString.get(counter).getKey().equals("/")
                ||parseString.get(counter).getKey().equals(",") ||parseString.get(counter).getKey().equals("]")
                ||parseString.get(counter).getKey().equals(")") || parseString.get(counter).getKey().equals(";")
                ||parseString.get(counter).getKey().equals("!=")||parseString.get(counter).getKey().equals("==")
                ||parseString.get(counter).getKey().equals(">=")||parseString.get(counter).getKey().equals(">")
                ||parseString.get(counter).getKey().equals("<")||parseString.get(counter).getKey().equals("<=")
                ||parseString.get(counter).getKey().equals("-")||parseString.get(counter).getKey().equals("+")){
            counter = variableLeftFactor(parseString, counter);
            counter = expressionLeftFactorLeftFactor(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("(")){
            counter++;
            counter = args(parseString, counter);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                counter = termPrime(parseString, counter);
                counter = addExpressionPrime(parseString, counter);
                counter = relationalOperation(parseString, counter);
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int expressionLeftFactorLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("=")){
            counter++;
            counter = expression(parseString, counter);
        }else if(parseString.get(counter).getKey().equals("*") || parseString.get(counter).getKey().equals("/")
                || parseString.get(counter).getKey().equals("+")|| parseString.get(counter).getKey().equals("-")
                || parseString.get(counter).getKey().equals("<=") || parseString.get(counter).getKey().equals("<")
                || parseString.get(counter).getKey().equals(">") || parseString.get(counter).getKey().equals(">=")
                || parseString.get(counter).getKey().equals("==") || parseString.get(counter).getKey().equals("!=")
                || parseString.get(counter).getKey().equals(";") || parseString.get(counter).getKey().equals(")")
                || parseString.get(counter).getKey().equals("]") || parseString.get(counter).getKey().equals(",") ){
           counter =  termPrime(parseString, counter);
            counter = addExpressionPrime(parseString, counter);
            counter = relationalOperation(parseString, counter);
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int variableLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("[")){
            counter++;
            counter = expression(parseString, counter);
            if(parseString.get(counter).getKey().equals("]")){
                counter++;
            }else{ reject(parseString, counter); }
        }else if(parseString.get(counter).getKey().equals("=")
                || parseString.get(counter).getKey().equals("*") || parseString.get(counter).getKey().equals("/")
                || parseString.get(counter).getKey().equals(",") || parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(")")|| parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("!=")  || parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("-")|| parseString.get(counter).getKey().equals("+")
                || parseString.get(counter).getKey().equals(",") ){
            return counter;

        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int relationalOperation(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals("!=")){
            counter++;
            counter = factor(parseString, counter);
            counter = addExpressionPrime(parseString, counter);
        }else if(parseString.get(counter).getKey().equals(";")||parseString.get(counter).getKey().equals(")")
                ||parseString.get(counter).getKey().equals("]") ||parseString.get(counter).getKey().equals(",")){
            return counter;
        }
        else{ reject(parseString, counter); }
        return counter;
    }
    public static int addExpressionPrime(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("+") || parseString.get(counter).getKey().equals("-")){
            counter = additionOperation(parseString, counter);
            counter = term(parseString, counter);
            counter = addExpressionPrime(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals("!=")|| parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals(")")|| parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(",")){
            return counter;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int additionOperation(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("+") || parseString.get(counter).getKey().equals("-")){
            counter++;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int term(ArrayList<DataItem> parseString, int counter){//MAY BE ERROR
        counter = factor(parseString, counter);
        counter = termPrime(parseString, counter);
        return counter;
    }
    public static int termPrime(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("*")|| parseString.get(counter).getKey().equals("/")){
            counter = multiplicationOperation(parseString, counter);
            counter = factor(parseString, counter);
            counter = termPrime(parseString, counter);
        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals(")") || parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(",")|| parseString.get(counter).getKey().equals("!=")
                || parseString.get(counter).getKey().equals("==")|| parseString.get(counter).getKey().equals(">=")
                || parseString.get(counter).getKey().equals(">")|| parseString.get(counter).getKey().equals("<")
                || parseString.get(counter).getKey().equals("<=")|| parseString.get(counter).getKey().equals("-")
                || parseString.get(counter).getKey().equals("+")){
                return counter;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int multiplicationOperation(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("*")
                || parseString.get(counter).getKey().equals("/")){
            counter++;
        }else{ reject(parseString, counter); }
        return counter;

    }
    public static int factor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("(")){
            counter++;
            counter = expression(parseString, counter);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
            }
        }else if(parseString.get(counter).getType() =="int" || parseString.get(counter).getType() == "float"){
            counter++;
        }else if(parseString.get(counter).getType() == "id"){
            counter++;
            if(parseString.get(counter).getType().equals("(")){
                counter = factorLeftFactor(parseString, counter);
            }
            else{
                counter = variableLeftFactor(parseString, counter);//MUSHKELA KIBERA!
            }
        } else{ reject(parseString, counter); }
        return counter;
    }
    public static int factorLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("(")){
            counter++;
            counter = args(parseString, counter);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
            }else{ reject(parseString, counter); }
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int args(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(")")){
            return counter;
        }else{  counter = argsList(parseString, counter);}
        return counter;
    }
    public static int argsList(ArrayList<DataItem> parseString, int counter){
        counter = expression(parseString, counter);
        counter = argsListPrime(parseString, counter);
        return counter;
    }
    public static int argsListPrime(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals(",")){
            counter++;
            counter = expression(parseString, counter);
            counter = argsListPrime(parseString, counter);
        }else if(parseString.get(counter).getKey().equals(")")){
            return counter;
        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int selectionStatementLeftFactor(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("else")){
            counter++;
            counter = statement(parseString, counter);
        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while")|| parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id"|| parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"
                || parseString.get(counter).getKey().equals("}")){
                return counter;

        }else{ reject(parseString, counter); }
        return counter;
    }
    public static int declarationListPrime(ArrayList<DataItem> parseString, int counter){
        if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("float")
                || parseString.get(counter).getKey().equals("void")){
            counter = declaration(parseString, counter);
            counter = declarationListPrime(parseString, counter);
        }
        else if(parseString.get(counter).getKey().equals("$")){
            return counter;
        }
        else{
           reject(parseString, counter);
        }
      return counter;
    }
    public static void reject(ArrayList<DataItem> parseString, int counter){
        System.out.println("REJECT");
        System.exit(0);
    }





}
