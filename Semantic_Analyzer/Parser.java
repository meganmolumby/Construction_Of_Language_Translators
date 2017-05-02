
import java.util.ArrayList;

/**
 * Created by Megan on 3/23/2017.
 *Should be best copy as of 3 APril 2017
 */

public class Parser {
//if time modify so global counter works and global Arraylist assign parse string.
    int counter = 0;
    HashTable globalSymbolTable;
    int parameterCounter = 0;
    public void program(ArrayList<DataItem> parseString, HashTable table){
        Symbol temp = new Symbol("");


        globalSymbolTable = table;

        declarationList(parseString,temp, globalSymbolTable);
        //Checks to see if a main exists in fail
        temp.setKey("main");
        if(globalSymbolTable.quadFind(temp) == null){
            reject(parseString);
        }
        //if main is not last declared reject
        if(!globalSymbolTable.getMostRecentDeclaration().getKey().equals("main")){
            reject(parseString);
        }
        if(parseString.get(counter).getKey().equals("$")) {
            System.out.println("ACCEPT");

        }

    }
    public HashTable declarationList(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
            currentScope = declaration(parseString, currentScope);
            currentScope = declarationListPrime(parseString, temp, currentScope);

        return currentScope;
    }
    public HashTable declaration(ArrayList<DataItem> parseString, HashTable currentScope){


        if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("void")
                ||parseString.get(counter).getKey().equals("float")){

            String functionDefinitionType = parseString.get(counter).getKey();
            Symbol temp = new Symbol("");
            temp.setDepth(parseString.get(counter).getDepth());

            switch(functionDefinitionType){
                case "int": temp.setType("int");
                    break;
                case "float": temp.setType("float");
                    break;
                case "void": temp.setType("void");
                    break;
                default: break;
            }
            counter++;
            if(parseString.get(counter).getType() == "id"){
                temp.setKey(parseString.get(counter).getKey());
                if(checkIfAlreadyDeclared(temp, globalSymbolTable)){
                    reject(parseString);//reject because the id name is already used within a scope.
                }
                counter++;
                currentScope = declarationLeftFactorLeftFactor(parseString, temp, currentScope);
            }


        }
        else{
            reject(parseString);
        }
        return currentScope;
    }
    //function declaration
    public HashTable varDeclaration(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        temp = new Symbol("");
        if(parseString.get(counter).getKey().equals("void")||parseString.get(counter).getKey().equals("int")
                ||parseString.get(counter).getKey().equals("float")) {
            temp.setType(parseString.get(counter).getKey());
            counter++;
            if (parseString.get(counter).getType() == "id") {
                //Semantic Check: if id is of type void, invalid
                if(temp.getType().equals("void")){
                    reject(parseString);
                }
                temp.setKey(parseString.get(counter).getKey());
                counter++;
               currentScope = varDeclarationLeftFactor(parseString,temp, currentScope);
            }
        }
        else{
            reject(parseString);
        }
        return currentScope;
    }
    public  HashTable declarationLeftFactorLeftFactor(ArrayList<DataItem> parseString,Symbol temp, HashTable currentScope){

        if(parseString.get(counter).getKey().equals(";" )|| parseString.get(counter).getKey().equals("[")){
            varDeclarationLeftFactor(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals("(")){
            temp.setKind("func");
            //insert function header into global symbol table
            globalSymbolTable.insertQuad(temp);
            globalSymbolTable.setMostRecentDeclaration(temp);
            //create a new scope
            HashTable tempHash = currentScope;
            currentScope = new HashTable(parseString.size()-counter);
            currentScope.setParent(tempHash);//sets pointer back to parent
           /* if(currentScope != globalSymbolTable){

            }*/
            temp.setFunctionChild(currentScope);
            //set functions return type
            currentScope.setReturnType(temp.getType());
            counter++;
            currentScope = params(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){

                counter++;
               currentScope = compoundStatement(parseString, temp, currentScope);
            }
            else{
                reject(parseString);
            }
        }
        else {
            reject(parseString);
        }
        return currentScope;
    }
    public  HashTable params(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope) {
         temp = new Symbol("");
        temp.setKind("par");
        if (parseString.get(counter).getKey().equals("void")) {
            temp.setType("void");
            counter++;
            currentScope = paramsLeftFactor(parseString, temp, currentScope);//HAVE NOT CHECKED
        }else if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("float")){
            temp.setType(parseString.get(counter).getKey());
            counter++;
            if(parseString.get(counter).getType() == "id"){
                temp.setKey(parseString.get(counter).getKey());
                counter++;
                currentScope = paramLeftFactor(parseString, temp, currentScope);//checks if array type
                currentScope.insertQuad(temp);//inserts current and prepares for more or none// wHY IS THIS NOT INSERTING!?!
                currentScope.getParameterList().add(temp);
                currentScope = paramListPrime(parseString, temp, currentScope);
            }
        }
        else{reject(parseString);}
        return currentScope;
    }
    public  HashTable paramsLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

        if(parseString.get(counter).getType() == "id"){
            temp.setDepth(parseString.get(counter).getDepth());
            temp.setKey(parseString.get(counter).getKey());
            if(checkIfAlreadyDeclared(temp, currentScope)){
                reject(parseString);
            }
            currentScope.insertQuad(temp);
            counter++;
            currentScope =  paramLeftFactor(parseString, temp, currentScope);
            currentScope = paramListPrime(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals(")")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable paramListPrime(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(",")){
            counter++;

            currentScope =  param(parseString, temp, currentScope);//checked
            currentScope =  paramListPrime(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals(")")){
            return currentScope;
        }
        else{ reject(parseString); }
        return currentScope;
    }
    public HashTable param(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        temp = new Symbol("");
        temp.setKind("par");
        if(parseString.get(counter).getKey().equals("int")
                || parseString.get(counter).getKey().equals("void") || parseString.get(counter).getKey().equals("float")){
            temp.setType(parseString.get(counter).getKey());//sets type
            counter++;
            if(parseString.get(counter).getType() == "id"){
                //adds identifier to symbol
                temp.setKey(parseString.get(counter).getKey());
                counter++;
                currentScope =  paramLeftFactor(parseString, temp, currentScope);//checked
                if(checkIfAlreadyDeclared(temp, currentScope)){
                        reject(parseString);
                }else{
                    currentScope.insertQuad(temp);
                    currentScope.getParameterList().add(temp);
                }

            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable paramLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("[")){
            counter++;
            if(parseString.get(counter).getKey().equals("]")){
                counter++;
                temp.setIsArray(true);
            }
        }else if(parseString.get(counter).getKey().equals(",") || parseString.get(counter).getKey().equals("int")
                ||parseString.get(counter).getKey().equals("void") ||parseString.get(counter).getKey().equals("float")
                ||parseString.get(counter).getKey().equals(")")){

            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable compoundStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("{")){
            //; fixes {},
            if(parseString.get(counter-1).getKey().equals(";")|| currentScope.getInConditional()){

                HashTable tempHash = currentScope;
                currentScope = new HashTable(parseString.size()-counter);
                //set functions return type
                currentScope.setReturnType("void");
                if(currentScope != globalSymbolTable){
                    currentScope.setParent(tempHash);//sets pointer back to parent

                }

            }
            //IF a conditional statement set

            counter++;

            currentScope =  localDeclarations(parseString, temp, currentScope);
            currentScope = statementList(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals("}")){
                //Semantic Check for if no return value is given and not of type void
                if((!currentScope.getCalledAReturnValue() && currentScope.getReturnType() != "void")){
                    //semantic check for if function in global symbol table  of void.
                    if(currentScope.getParent() != null) {
                        reject(parseString);
                    }
                }
                counter++;
                if(currentScope.getParent() != null){
                    return currentScope.getParent();
                }

            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable localDeclarations(ArrayList<DataItem> parseString,Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("int")
                || parseString.get(counter).getKey().equals("void") || parseString.get(counter).getKey().equals("float")){
            currentScope =  varDeclaration(parseString, temp, currentScope);
            currentScope =  localDeclarations(parseString, temp, currentScope);

        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while")|| parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id" || parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"
                || parseString.get(counter).getKey().equals("}")){
            return currentScope;

        }
        else{ reject(parseString); }
        return currentScope;
    }
    public HashTable statementList(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while") || parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id"|| parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"){
            currentScope =  statement(parseString, temp, currentScope);
            currentScope =  statementList(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals("}")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable varDeclarationLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(";")){
            temp.setKind("var");
            if(checkIfAlreadyDeclared(temp, currentScope)){
                reject(parseString);
            }
            currentScope.insertQuad(temp);
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
                        temp.setKind("array");
                        temp.setIsArray(true);
                        currentScope.insertQuad(temp);
                    } else {
                        reject(parseString);
                    }
                }else{
                    reject(parseString);
                }
            }else{
                reject(parseString);
            }



        }else{
            reject(parseString);
        }
        return currentScope;

    }
    public HashTable statement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getType() == "id" || parseString.get(counter).getKey().equals("(")){
            currentScope =  expressionStatement(parseString,temp,currentScope);
        }else if(parseString.get(counter).getKey().equals("{")){
            currentScope =  compoundStatement(parseString, temp,currentScope);
        }else if(parseString.get(counter).getKey().equals("if")){
            currentScope =  selectionStatement(parseString, temp,currentScope);
        }else if(parseString.get(counter).getKey().equals("while")){
            currentScope =  iterationStatement(parseString,temp,currentScope);
        }else if(parseString.get(counter).getKey().equals("return")){
            currentScope =  returnStatement(parseString, temp, currentScope);
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable expressionStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getType() == "id" ){
            currentScope =   expression(parseString,temp, currentScope);
            if(parseString.get(counter).getKey().equals(";")){
                counter++;
            }else{ reject(parseString); }
        }else if(parseString.get(counter).getKey().equals(";")){
            counter++;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable selectionStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){


        if(parseString.get(counter).getKey().equals("if")){

            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                currentScope = expression(parseString, temp, currentScope);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    currentScope.setInConditional(true);
                    ///THIS IS WHERE A SIGNIFIER OF NONE FUNCTION TYPE
                    currentScope =  statement(parseString,temp,currentScope);
                    currentScope.setInConditional(false);
                    currentScope =  selectionStatementLeftFactor(parseString,temp, currentScope);
                }else{ reject(parseString); }
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable iterationStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("while")){
            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                currentScope =   expression(parseString,temp, currentScope);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    currentScope.setInConditional(true);
                    currentScope =  statement(parseString, temp, currentScope);
                    currentScope.setInConditional(false);
                }else{ reject(parseString); }
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable returnStatement(ArrayList<DataItem> parseString, Symbol temp,HashTable currentScope){
        if(parseString.get(counter).getKey().equals("return")){
            counter++;
            currentScope =  returnStatementLeftFactor(parseString, temp,currentScope);
        }else{ reject(parseString); }
        return currentScope;
    }
    public  HashTable  returnStatementLeftFactor(ArrayList<DataItem> parseString, Symbol temp,HashTable currentScope){

        if(parseString.get(counter).getKey().equals(";")){
            //check if return type matches function type
            if(currentScope.getReturnType() == "void"){
                counter++;

            }else{
                reject(parseString);
            }

        }else{
            // any return value
            currentScope =  expression(parseString, temp, currentScope);

            if(parseString.get(counter).getKey().equals(";")){

                counter++;
            }
            else{
                reject(parseString);
            }
        }
        return currentScope;
    }
    public  HashTable expression(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        Symbol compareTemp = temp;
        temp = new Symbol("");
        boolean returnValue;
        if(parseString.get(counter - 1).getKey().equals("return")){
            temp.setIsReturnValue(true);

        }

        if(parseString.get(counter).getType() == "id"){
            temp.setKey(parseString.get(counter).getKey());

            //check if not already declared, if not already declared then reject
            if(checkIfNotDeclared(temp, currentScope)){
                //make temp value = value found
                returnValue = temp.isReturnValue();
                temp = getItemFromTree(temp, currentScope);
                temp.setIsReturnValue(returnValue);
                //CHECK FOR PARAMETER MATCH
               if(compareTemp.getParameterCounter() != -1){
                   if (!compareParameterValues(compareTemp, temp)) {
                       reject(parseString);
                   }
               }
                //declared now it can be utilized
                counter++;
                currentScope =  expressionLeftFactor(parseString, temp, currentScope);
                if(temp.isReturnValue()){
                    currentScope.setCalledAReturnValue(true);
                    //Semantic Check: if returning a complex data type->reject
                    if(temp.getKind().equals("array")){
                        reject(parseString);
                    }
                }
            }else{
                reject(parseString);
            }


        }else if(parseString.get(counter).getKey().equals("(")){
            counter++;
            currentScope =  expression(parseString,temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                currentScope =   termPrime(parseString, temp, currentScope);
                currentScope = addExpressionPrime(parseString, temp, currentScope);
                currentScope =  relationalOperation(parseString, temp, currentScope);
                if(temp.isReturnValue()){
                    currentScope.setCalledAReturnValue(true);
                }
            }
            else{ reject(parseString);}
        } else if(parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"){
            temp.setKey(parseString.get(counter).getKey());
            temp.setType(parseString.get(counter).getType());
            //Semantic Check for number and type of parameters
            if(compareTemp.getParameterCounter() != -1) {
                //compare parameter function
                if (!compareParameterValues(compareTemp, temp)) {
                    reject(parseString);
                }

            }

            counter++;
            currentScope =   termPrime(parseString, temp, currentScope);//mul/div operations
            currentScope =  addExpressionPrime(parseString,  temp, currentScope);//addsubops
            currentScope =  relationalOperation(parseString, temp, currentScope);//relopps
            if(temp.isReturnValue()){
                currentScope.setCalledAReturnValue(true);
            }
        } else{ reject(parseString); }
        //checking if return value; is == to function return type
        if(temp != null){
            if(parseString.get(counter).getKey().equals(";") && temp.isReturnValue() == true){
                currentScope.setCalledAReturnValue(true);
                if(!temp.getType().equals(currentScope.getReturnType())){
                reject(parseString);
                }
            }
        }
        if(compareTemp.isArray()){
            if(!compareTemp.getType().equals(temp.getType())){
                reject(parseString);
            }
        }
        return  currentScope;

    }
    public HashTable  expressionLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("[") ||parseString.get(counter).getKey().equals("=")
                ||parseString.get(counter).getKey().equals("*") ||parseString.get(counter).getKey().equals("/")
                ||parseString.get(counter).getKey().equals(",") ||parseString.get(counter).getKey().equals("]")
                ||parseString.get(counter).getKey().equals(")") || parseString.get(counter).getKey().equals(";")
                ||parseString.get(counter).getKey().equals("!=")||parseString.get(counter).getKey().equals("==")
                ||parseString.get(counter).getKey().equals(">=")||parseString.get(counter).getKey().equals(">")
                ||parseString.get(counter).getKey().equals("<")||parseString.get(counter).getKey().equals("<=")
                ||parseString.get(counter).getKey().equals("-")||parseString.get(counter).getKey().equals("+")){
            currentScope =   variableLeftFactor(parseString,temp, currentScope);//Array[value] check
            currentScope =   expressionLeftFactorLeftFactor(parseString,temp,currentScope);//operation/compare expression

            //function call, don't need to worry about for prj3 for return type
        }else if(parseString.get(counter).getKey().equals("(")){
            counter++;

            currentScope =  args(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
             //if parameter count is not equal to the number of parameters
             if(parameterCounter+1 != temp.getFunctionChild().getParameterList().size()){
                 reject(parseString);

             }
             parameterCounter = 0;


                currentScope =  termPrime(parseString, temp, currentScope);
                currentScope =  addExpressionPrime(parseString, temp, currentScope);
                currentScope =  relationalOperation(parseString,temp, currentScope);
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable expressionLeftFactorLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("=")){
            counter++;//a = expression;
            currentScope =  expression(parseString, temp, currentScope);
        }else if(parseString.get(counter).getKey().equals("*") || parseString.get(counter).getKey().equals("/")
                || parseString.get(counter).getKey().equals("+")|| parseString.get(counter).getKey().equals("-")
                || parseString.get(counter).getKey().equals("<=") || parseString.get(counter).getKey().equals("<")
                || parseString.get(counter).getKey().equals(">") || parseString.get(counter).getKey().equals(">=")
                || parseString.get(counter).getKey().equals("==") || parseString.get(counter).getKey().equals("!=")
                || parseString.get(counter).getKey().equals(";") || parseString.get(counter).getKey().equals(")")
                || parseString.get(counter).getKey().equals("]") || parseString.get(counter).getKey().equals(",") ){
            currentScope =  termPrime(parseString, temp, currentScope);//multiply or divide expression?
            currentScope = addExpressionPrime(parseString,  temp, currentScope);//addition or subtraction expression.
            currentScope = relationalOperation(parseString, temp, currentScope);//relational operation expression check
        }else{ reject(parseString); }
        return currentScope;
    }
    //Array [expression]
    public  HashTable variableLeftFactor(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("[")){
            counter++;
            temp.setIsArray(false);//may remove
            temp.setKind("array");
            currentScope =  expression(parseString, temp, currentScope);
            if(!temp.getType().equals("int")){
                reject(parseString);
            }
            if(parseString.get(counter).getKey().equals("]")){

                counter++;
            }else{ reject(parseString); }
        }else if(parseString.get(counter).getKey().equals("=")
                || parseString.get(counter).getKey().equals("*") || parseString.get(counter).getKey().equals("/")
                || parseString.get(counter).getKey().equals(",") || parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(")")|| parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("!=")  || parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("-")|| parseString.get(counter).getKey().equals("+")
                || parseString.get(counter).getKey().equals(",") ){


            return currentScope;

        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable relationalOperation(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals("!=")){
            counter++;
           currentScope = factor(parseString, temp, currentScope);
            currentScope =  addExpressionPrime(parseString, temp, currentScope);
        }else if(parseString.get(counter).getKey().equals(";")||parseString.get(counter).getKey().equals(")")
                ||parseString.get(counter).getKey().equals("]") ||parseString.get(counter).getKey().equals(",")){
            return currentScope;//check return type here
        }
        else{ reject(parseString); }
        return currentScope;
    }
    public HashTable addExpressionPrime(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope ){
        if(parseString.get(counter).getKey().equals("+") || parseString.get(counter).getKey().equals("-")){
        additionOperation(parseString, temp, currentScope);
            currentScope =  term(parseString, temp, currentScope);
            currentScope =  addExpressionPrime(parseString,  temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals("<=")
                || parseString.get(counter).getKey().equals("<") || parseString.get(counter).getKey().equals(">")
                || parseString.get(counter).getKey().equals(">=")|| parseString.get(counter).getKey().equals("==")
                || parseString.get(counter).getKey().equals("!=")|| parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals(")")|| parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(",")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable additionOperation(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("+") || parseString.get(counter).getKey().equals("-")){
            counter++;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable term(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){//MAY BE ERROR
        currentScope =  factor(parseString, temp, currentScope);
        currentScope =  termPrime(parseString, temp, currentScope);//mulp/div
        return currentScope;
    }
    public HashTable termPrime(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope){//multiply divide check
        if(parseString.get(counter).getKey().equals("*")|| parseString.get(counter).getKey().equals("/")){
            currentScope =  multiplicationOperation(parseString,  temp, currentScope);
            currentScope =  factor(parseString, temp, currentScope);
            currentScope =  termPrime(parseString, temp, currentScope);
        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals(")") || parseString.get(counter).getKey().equals("]")
                || parseString.get(counter).getKey().equals(",")|| parseString.get(counter).getKey().equals("!=")
                || parseString.get(counter).getKey().equals("==")|| parseString.get(counter).getKey().equals(">=")
                || parseString.get(counter).getKey().equals(">")|| parseString.get(counter).getKey().equals("<")
                || parseString.get(counter).getKey().equals("<=")|| parseString.get(counter).getKey().equals("-")
                || parseString.get(counter).getKey().equals("+")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable multiplicationOperation(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("*")
                || parseString.get(counter).getKey().equals("/")){
            counter++;
        }else{ reject(parseString); }
        return currentScope;

    }
    public HashTable factor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("(")){//something in (expression)
            counter++;
            currentScope =  expression(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
            }
        }else if(parseString.get(counter).getType() =="int" || parseString.get(counter).getType() == "float"){//straight number or decimal

            if((!temp.getType().equals(parseString.get(counter).getType())) || temp.isArray()){
                reject(parseString);
            }
            counter++;
        }else if(parseString.get(counter).getType() == "id"){//variable check
            //check if id is it has not been declared
            if(checkIfNotDeclared(parseString.get(counter),currentScope)){//returns true if it is declared before
                //get Symbol
                Symbol compareTemp;
                if(currentScope.quadFind(parseString.get(counter)) == null){
                    compareTemp = (Symbol) globalSymbolTable.quadFind(parseString.get(counter));
                }else{
                    compareTemp =(Symbol) currentScope.quadFind(parseString.get(counter));
                }

                //checking if types can be add/sub/mul/div
                if(!temp.getType().equals(compareTemp.getType())){
                    reject(parseString);
                }
                counter++;
            }else{
                reject(parseString);
            }
            if(parseString.get(counter).getType().equals("(")){//function call again?
                currentScope = factorLeftFactor(parseString, temp, currentScope);
            }
            else{//checking if array
                currentScope =  variableLeftFactor(parseString,  temp, currentScope);
            }
        } else{ reject(parseString); }
        return currentScope;
    }
    public HashTable  factorLeftFactor(ArrayList<DataItem> parseString,  Symbol temp,HashTable currentScope){
        if(parseString.get(counter).getKey().equals("(")){
            counter++;
            currentScope =  args(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable args(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(")")){
           //check parameter list
           if(temp.getFunctionChild().getParameterList() != null){
               reject(parseString);
           }
            return currentScope;
        }else{
            temp.setParameterCounter(parameterCounter);
            argsList(parseString, temp, currentScope);}
        return currentScope;
    }
    public HashTable argsList(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        currentScope =  expression(parseString,  temp, currentScope);
        currentScope =  argsListPrime(parseString,  temp, currentScope);
        return currentScope;
    }
    public HashTable argsListPrime(ArrayList<DataItem> parseString,  Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(",")){
            counter++;
            parameterCounter++;
            temp.setParameterCounter(parameterCounter);
            currentScope = expression(parseString, temp, currentScope);
            currentScope = argsListPrime(parseString, temp, currentScope);
        }else if(parseString.get(counter).getKey().equals(")")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable selectionStatementLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("else")){
            counter++;
            currentScope.setInConditional(true);
            currentScope =  statement(parseString, temp, currentScope);
            currentScope.setInConditional(false);
        }else if(parseString.get(counter).getKey().equals(";")
                || parseString.get(counter).getKey().equals("{") || parseString.get(counter).getKey().equals("if")
                || parseString.get(counter).getKey().equals("while")|| parseString.get(counter).getKey().equals("return")
                || parseString.get(counter).getType() == "id"|| parseString.get(counter).getKey().equals("(")
                || parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"
                || parseString.get(counter).getKey().equals("}")){
            return currentScope;

        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable declarationListPrime(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("float")
                || parseString.get(counter).getKey().equals("void")){
            currentScope =  declaration(parseString, currentScope);
            currentScope = declarationListPrime(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals("$")){
            return currentScope;
        }
        else{
            reject(parseString);
        }
        return currentScope;
    }
    public  void reject(ArrayList<DataItem> parseString){
        System.out.println("REJECT");
        System.exit(0);
    }
    //checks if already declared in scope, only checks local scope
    public boolean checkIfAlreadyDeclared(DataItem item, HashTable currentScope){
        boolean exists = true;
        if (currentScope.quadFind(item) == null) {
            exists = false;
        }
        return exists;
    }
    //check if it has not been declared, checks all scopes
    public boolean checkIfNotDeclared(DataItem item, HashTable currentScope){
        boolean exists = true;
        if(currentScope == globalSymbolTable){
            if(globalSymbolTable.quadFind(item) == null){
                exists = false;
            }
        }else {
            while (currentScope.getParent() != null) {

                if (currentScope.quadFind(item) != null) {//found! return true
                    exists = true;
                    break;
                }else{
                    currentScope = currentScope.getParent();
                    exists = false;
                }
                if(currentScope.getParent() == null){
                    if(currentScope.quadFind(item) != null){
                        exists = true;
                        break;
                    }
                }


            }
        }
           return exists;
    }
    public Symbol getItemFromTree(DataItem item, HashTable currentScope){
        Symbol temp = new Symbol("");
        if(currentScope == globalSymbolTable){
            if(globalSymbolTable.quadFind(item) == null){
                temp = null;
            }
        }else {
            while (currentScope.getParent() != null) {

                if (currentScope.quadFind(item) != null) {//found! return true
                    temp = (Symbol) currentScope.quadFind(item);
                    break;
                }else{
                    currentScope = currentScope.getParent();
                    temp = null;
                }
                if(currentScope.getParent() == null){
                    if(currentScope.quadFind(item) != null){
                        temp = (Symbol) currentScope.quadFind(item);
                        break;
                    }
                }


            }
        }

        return temp;
    }
    public boolean compareParameterValues(Symbol paramItem, Symbol tempItem){
        boolean isValid = false;
        ArrayList<Symbol> parameterList = paramItem.getFunctionChild().getParameterList();
        if(parameterCounter < parameterList.size()){//too many parameter items
            if(tempItem.getType().equals(parameterList.get(paramItem.getParameterCounter()).getType())){
            isValid = true;
            }
        }else{
            isValid = false;
        }
        return isValid;

    }

}
