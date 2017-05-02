
import java.util.ArrayList;
import static java.lang.Integer.parseInt;

/**
 * Created by Megan on 3/23/2017.
 *should be best copy as of 9:18pm Wed 4/12/2017
 *
 * Notes: commented out line 39 reject, for prj4
 */

public class Parser {
    String[][] codeGen = new String[100][4];
    Stack expressionStack = new Stack(30);
    int temporaryValue = 0;
    int row = 0;
    int column = 0;
    int backPatch = -1;
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


            for(int i = 0; i<100; i++)
            {      System.out.print(i);
                for(int j = 0; j<4; j++)
                {
                    if(codeGen[i][j] == null){
                        break;
                    }

                    System.out.print(" "+ codeGen[i][j]);
                    int numberOfspaces = 12 -codeGen[i][j].length();
                     String space = String.format("%" + numberOfspaces +"s", "");
                    System.out.print(space);
                }
                System.out.println();
                if(codeGen[i+1][0] == null){
                    break;
                }
            }
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
    public HashTable declarationLeftFactorLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

        if(parseString.get(counter).getKey().equals(";" )|| parseString.get(counter).getKey().equals("[")){
            varDeclarationLeftFactor(parseString, temp, currentScope);
        }
        else if(parseString.get(counter).getKey().equals("(")){
            temp.setKind("func");
            //insert function header into global symbol table
            globalSymbolTable.insertQuad(temp);
            //--------------------CodeGeneration--------------
                codeGen[row][0] = "func";
                codeGen[row][1] = temp.getKey();
                codeGen[row][2] = temp.getType();

            //--------------------CodeGeneration--------------

            globalSymbolTable.setMostRecentDeclaration(temp);
            //create a new scope
            HashTable tempHash = currentScope;
            currentScope = new HashTable(parseString.size()-counter);
            currentScope.setParent(tempHash);//sets pointer back to parent

            temp.setFunctionChild(currentScope);
            //set functions return type
            currentScope.setReturnType(temp.getType());
            counter++;
            currentScope = params(parseString, temp, currentScope);

            if(parseString.get(counter).getKey().equals(")")){

                counter++;

               currentScope = compoundStatement(parseString, temp, currentScope);
                //---------------CodeGeneration------------------------
                codeGen[row][0] = "end";
                codeGen[row][1]= temp.getKind();
                codeGen[row][2] =temp.getKey();
                codeGen[row][3] = "     ";
                row++;

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
    public HashTable params(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope) {
         temp = new Symbol("");
        temp.setKind("par");
        if (parseString.get(counter).getKey().equals("void")) {
            temp.setType("void");
            //--------------------CodeGeneration--------------
            codeGen[row][3] = "0";
            row++;
            //--------------------CodeGeneration--------------
            counter++;
            currentScope = paramsLeftFactor(parseString, temp, currentScope);//HAVE NOT CHECKED
        }else if(parseString.get(counter).getKey().equals("int") || parseString.get(counter).getKey().equals("float")){
            temp.setType(parseString.get(counter).getKey());
            counter++;
            int backPatchParam = row;
            if(parseString.get(counter).getType() == "id"){
                temp.setKey(parseString.get(counter).getKey());
                counter++;
                row++;
                currentScope = paramLeftFactor(parseString, temp, currentScope);//checks if array type
                currentScope.insertQuad(temp);//inserts current and prepares for more or none
                //--------------------CodeGeneration--------------
                codeGen[row][0] = "param";
                codeGen[row][1] = "     ";
                codeGen[row][2] = "     ";
                codeGen[row][3] = "     ";
                row++;
                codeGen[row][0] = "alloc";
                codeGen[row][1] = "4";
                codeGen[row][2] = "     ";
                codeGen[row][3] = temp.getKey();
                row++;
                //--------------------CodeGeneration--------------

                currentScope.getParameterList().add(temp);

                currentScope = paramListPrime(parseString, temp, currentScope);
                //--------------------CodeGeneration--------------
                codeGen[backPatchParam][3] = Integer.toString(currentScope.getParameterList().size());
                //--------------------CodeGeneration--------------
            }

        }
        else{reject(parseString);}

        return currentScope;
    }
    public HashTable paramsLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

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
                    //--------------------CodeGeneration--------------
                    codeGen[row][0] = "param";
                    codeGen[row][1] = "     ";
                    codeGen[row][2] = "     ";
                    codeGen[row][3] = "     ";
                    row++;
                    codeGen[row][0] = "alloc";
                    codeGen[row][1] = "4";
                    codeGen[row][2] = "     ";
                    codeGen[row][3] = temp.getKey();
                    row++;
                    //--------------------CodeGeneration--------------
                    currentScope.insertQuad(temp);
                    currentScope.getParameterList().add(temp);
                }

            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable paramLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
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
    public HashTable compoundStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
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
    public HashTable localDeclarations(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
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
            //--------------------CodeGeneration--------------
            codeGen[row][0] = "alloc";
            codeGen[row][1] = "4";
            codeGen[row][2] = "     ";
            codeGen[row][3] = temp.getKey();
            row++;
            //--------------------CodeGeneration--------------
            currentScope.insertQuad(temp);
            counter++;
        }
        else if(parseString.get(counter).getKey().equals("[")){
            counter++;
            if(parseString.get(counter).getType() == "int"){
                codeGen[row][1] = Integer.toString(4*parseInt(parseString.get(counter).getKey()));
                counter++;
                if(parseString.get(counter).getKey().equals("]")) {
                    counter++;
                    if (parseString.get(counter).getKey().equals(";")) {
                        counter++;
                        temp.setKind("array");
                        temp.setIsArray(true);
                        currentScope.insertQuad(temp);
                        //--------------------CodeGeneration--------------
                        codeGen[row][0] = "alloc";

                        codeGen[row][2] = "     ";
                        codeGen[row][3] = temp.getKey();
                        row++;

                        //--------------------CodeGeneration--------------

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
    public HashTable expressionStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getType() == "id" ){
            currentScope =   expression(parseString,temp, currentScope);
            if(parseString.get(counter).getKey().equals(";")){//evaluta here?
                counter++;
            }else{ reject(parseString); }
        }else if(parseString.get(counter).getKey().equals(";")){
            counter++;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable selectionStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

            int afterIf;
        if(parseString.get(counter).getKey().equals("if")){

            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                currentScope = expression(parseString, temp, currentScope);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    currentScope.setInConditional(true);
                    ///THIS IS WHERE A SIGNIFIER OF NONE FUNCTION TYPE
                    codeGen[backPatch][3] = Integer.toString(row+1);
                    codeGen[row][0] ="B";
                    codeGen[row][1] = "     ";
                    codeGen[row][2] = "      ";
                    backPatch = row;
                    row++;
                    currentScope =  statement(parseString,temp,currentScope);
                    //---------------CodeGeneration------------------------
                    codeGen[row][0] = "end";
                    codeGen[row][1]= "block";
                    codeGen[row][2]= "      ";
                    codeGen[row][3] ="     ";
                    row++;
                    //--------------------CodeGeneration--------------

                    //--------------------CodeGeneration--------------
                    currentScope.setInConditional(false);
                    afterIf =backPatch;
                    codeGen[afterIf][3] = Integer.toString(row);
                    currentScope =  selectionStatementLeftFactor(parseString,temp, currentScope);

                }else{ reject(parseString); }
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable iterationStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        int startWhile = row;
        if(parseString.get(counter).getKey().equals("while")){
            counter++;
            if(parseString.get(counter).getKey().equals("(")){
                counter++;
                currentScope =   expression(parseString,temp, currentScope);
                if(parseString.get(counter).getKey().equals(")")){
                    counter++;
                    currentScope.setInConditional(true);
                    currentScope =  statement(parseString, temp, currentScope);
                    //---------------CodeGeneration------------------------
                    codeGen[row][0] = "end";
                    codeGen[row][1]= "block";
                    codeGen[row][2]= "      ";
                    codeGen[row][3] ="     ";
                    row++;
                    //--------------------CodeGeneration--------------

                    codeGen[row][0] ="BR";
                    codeGen[row][1] = "     ";
                    codeGen[row][2] = "      ";
                    codeGen[row][3] = Integer.toString(startWhile);
                    row++;
                    codeGen[backPatch][3] = Integer.toString(row);
                    //--------------------CodeGeneration--------------
                    currentScope.setInConditional(false);
                }else{ reject(parseString); }
            }else{ reject(parseString); }
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable returnStatement(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("return")){
            counter++;
            currentScope =  returnStatementLeftFactor(parseString, temp,currentScope);
            //---------------CodeGeneration------------------------
            codeGen[row][0] = "return";
            codeGen[row][1]= "      ";
            codeGen[row][2]= "      ";
            codeGen[row][3] = codeGen[row-1][3];
            row++;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable returnStatementLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

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
    public HashTable expression(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){

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
               expressionStack.push(temp);
                currentScope =  expressionLeftFactor(parseString, temp, currentScope);
             /*   if(!temp.getType().equals(compareTemp.getType())){

                    reject(parseString);
                }*/
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
            //-----------------CodeGeneration---------------------
            Symbol paren = new Symbol(parseString.get(counter).getKey());
            expressionStack.push(paren);
            //-----------------CodeGeneration---------------------
            counter++;
            currentScope =  expression(parseString,temp, currentScope);

            if(parseString.get(counter).getKey().equals(")")){
                //-----------------CodeGeneration---------------------
                expressionStack.pop();
                Symbol tempVal = new Symbol("t"+ --temporaryValue);
                temporaryValue++;
                expressionStack.push(tempVal);

                //-----------------CodeGeneration---------------------
                counter++;
                currentScope =   termPrime(parseString, temp, currentScope);
                currentScope = addExpressionPrime(parseString, temp, currentScope);
                currentScope =  relationalOperation(parseString, temp, currentScope);
                if(temp.isReturnValue()){
                    currentScope.setCalledAReturnValue(true);
                    expressionStack.pop();
                }
            }
            else{ reject(parseString);}
        } else if(parseString.get(counter).getType() == "int"|| parseString.get(counter).getType() == "float"){
            temp.setKey(parseString.get(counter).getKey());
            temp.setType(parseString.get(counter).getType());
            //-----------------CodeGeneration---------------------
            expressionStack.push(temp);
            //-----------------CodeGeneration---------------------
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
            //ADD IN LINES OF CODE for expressionsstack popping to add to genereate code!!

            currentScope =  relationalOperation(parseString, temp, currentScope);//relopps
            if(temp.isReturnValue()){
                currentScope.setCalledAReturnValue(true);
            }
        } else{ reject(parseString); }
        //checking if return value; is == to function return type
        if(temp != null){
            if(!temp.getKind().equals("none")) {
                if (parseString.get(counter).getKey().equals(";") && temp.isReturnValue() == true) {
                    currentScope.setCalledAReturnValue(true);
                    if (!temp.getType().equals(currentScope.getReturnType())) {
                        reject(parseString);
                    }
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
    public HashTable expressionLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
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
            Symbol paren = new Symbol(parseString.get(counter).getKey());
            expressionStack.push(paren);
            counter++;
            parameterCounter++;

            currentScope =  args(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                //----------CodeGeneration-------------------------
                expressionStack.pop();
                codeGen[row][0]= "call";
                codeGen[row][1] = expressionStack.pop().getKey();
                codeGen[row][2]=  Integer.toString(parameterCounter);
                Symbol tempValue = new Symbol("t" + Integer.toString(temporaryValue++));
                codeGen[row][3]= tempValue.getKey();
                expressionStack.push(tempValue);
                row++;

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
            Symbol equals = new Symbol("assign");
            expressionStack.push(equals);
            counter++;//a = expression;
            currentScope =  expression(parseString, temp, currentScope);

            //--------------------CodeGeneration--------------
            Symbol tempValSymbol = new Symbol("");
            while(!expressionStack.isEmpty()){
                tempValSymbol = new Symbol("t" + temporaryValue++);
                codeGen[row][3] = tempValSymbol.getKey();
                codeGen[row][2] = expressionStack.pop().getKey();
                codeGen[row][0] = expressionStack.pop().getKey();//operation
                codeGen[row][1] = expressionStack.pop().getKey();
                if(codeGen[row][0].equals("assign")){
                    codeGen[row][3] = codeGen[row][1];
                    codeGen[row][1] = codeGen[row][2];
                    codeGen[row][2] = "     ";
                    temporaryValue--;
                }

                row++;
                if(!expressionStack.isEmpty()) {
                    if(expressionStack.peek().getKey().equals("(")){
                        //expressionStack.push(tempValSymbol);
                        break;
                    }
                    expressionStack.push(tempValSymbol);

                }else{
                    break;
                }
            }
            //----------------CodeGeneration-----------------
        }else if(parseString.get(counter).getKey().equals("*") || parseString.get(counter).getKey().equals("/")
                || parseString.get(counter).getKey().equals("+")|| parseString.get(counter).getKey().equals("-")
                || parseString.get(counter).getKey().equals("<=") || parseString.get(counter).getKey().equals("<")
                || parseString.get(counter).getKey().equals(">") || parseString.get(counter).getKey().equals(">=")
                || parseString.get(counter).getKey().equals("==") || parseString.get(counter).getKey().equals("!=")
                || parseString.get(counter).getKey().equals(";") || parseString.get(counter).getKey().equals(")")
                || parseString.get(counter).getKey().equals("]") || parseString.get(counter).getKey().equals(",") ){
            currentScope =  termPrime(parseString, temp, currentScope);//multiply or divide expression?
            currentScope = addExpressionPrime(parseString,  temp, currentScope);//addition or subtraction expression.
            //--------------------CodeGeneration--------------
           /* if(parseString.get(counter).equals(",")){
                Symbol comma = new Symbol(",");
                expressionStack.push(comma);
            }else{*/
            Symbol tempValSymbol = new Symbol("");
           tempValSymbol = expressionStack.pop();
           if(!expressionStack.isEmpty() && !expressionStack.peek().getKey().equals("[")) {
               expressionStack.push(tempValSymbol);
               while (!expressionStack.isEmpty()) {


                   codeGen[row][2] = expressionStack.pop().getKey();
                   if (expressionStack.peek().getKey().equals("(")) {
                       //IF AN ARGUMENT
                       codeGen[row][3] = codeGen[row][2];
                       codeGen[row][2] = "     ";
                       codeGen[row][1] = "     ";
                       codeGen[row][0] = "arg";
                   } else {
                       tempValSymbol = new Symbol("t" + temporaryValue++);
                       codeGen[row][3] = tempValSymbol.getKey();

                       codeGen[row][0] = expressionStack.pop().getKey();//operation
                       codeGen[row][1] = expressionStack.pop().getKey();
                       if (codeGen[row][0].equals("assign")) {
                           codeGen[row][3] = codeGen[row][1];
                           codeGen[row][1] = codeGen[row][2];
                           codeGen[row][2] = "     ";
                           temporaryValue--;
                       }
                   }
                   row++;
                   if (!expressionStack.isEmpty()) {
                       if (expressionStack.peek().getKey().equals("(") || expressionStack.peek().getKey().equals("[")) {
                           //expressionStack.push(tempValSymbol);
                           break;
                       }
                       expressionStack.push(tempValSymbol);

                   } else {
                       break;
                   }
               }
           }
            //push last temp symbol on
           // expressionStack.push(tempValSymbol);
            //--------------------CodeGeneration--------------
            currentScope = relationalOperation(parseString, temp, currentScope);//relational operation expression check
            //--------------------CodeGeneration--------------
            //if no relops follow skip this part!
           if(!expressionStack.isEmpty() && (!expressionStack.peek().getKey().equals("("))
                   && (!expressionStack.peek().getKey().equals("["))){
               //I BELIEVE THIS SHOULD ONLY HAPPEN ONCE!
               // while(!expressionStack.isEmpty()){
               stackDrain();
               tempValSymbol = new Symbol("t" + temporaryValue++);
               codeGen[row][3] = tempValSymbol.getKey();
               codeGen[row][2] = expressionStack.pop().getKey();
               codeGen[row][0] = "comp";//operation
               codeGen[row + 1][0] = "BR" + expressionStack.pop().getKey();
               codeGen[row][1] = expressionStack.pop().getKey();
               row++;
               //Branch
               codeGen[row][1] = tempValSymbol.getKey();
               codeGen[row][2] = "      ";
               backPatch = row;
               //Block
               row++;
               codeGen[row][0] = "block";
               codeGen[row][1] = " ";
               codeGen[row][2] = " ";
               codeGen[row][3] = " ";
               row++;
             /*   if(!expressionStack.isEmpty()) {
                    expressionStack.push(tempValSymbol);
                }else{
                    break;
                }*/
           }else if(!expressionStack.isEmpty()){
               if (expressionStack.peek().getKey().equals("[")){
                   expressionStack.push(tempValSymbol);
               }
               if(expressionStack.peek().getKey().equals("(")) {
                   Symbol parenTemp = new Symbol(expressionStack.pop().getKey());
                   if(!expressionStack.isEmpty()) {
                       if (((expressionStack.peek().getKey().equals("mult"))
                               || (expressionStack.peek().getKey().equals("subt"))
                               || (expressionStack.peek().getKey().equals("add"))
                               || (expressionStack.peek().getKey().equals("div")))) {
                           expressionStack.push(parenTemp);
                           expressionStack.push(tempValSymbol);
                       }else{
                           expressionStack.push(parenTemp);
                       }
                   }else{
                           expressionStack.push(parenTemp);

                   }

             }
            }
            //--------------------CodeGeneration--------------
        }else{ reject(parseString); }
        return currentScope;
    }
    //Array [expression]
    public HashTable variableLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("[")){
            //-----------------CodeGeneration---------------------
            Symbol bracket = new Symbol(parseString.get(counter).getKey());
            expressionStack.push(bracket);
            //-----------------CodeGeneration---------------------
            counter++;
            temp.setIsArray(true);//CRITICAL MOMENT
            temp.setKind("array");
            currentScope =  expression(parseString, temp, currentScope);
            if(!temp.getType().equals("int")){
                reject(parseString);
            }
            if(parseString.get(counter).getKey().equals("]")){
                //-----------------CodeGeneration---------------------
                //bracket = new Symbol(parseString.get(counter).getKey());
               // expressionStack.push(bracket);
                Symbol tempValue = new Symbol("t"+ temporaryValue++);
                codeGen[row][0]="mult";
                codeGen[row][1]="4";
                codeGen[row][2]= expressionStack.pop().getKey();
                codeGen[row][3] = tempValue.getKey();
                expressionStack.push(tempValue);
                row++;
                //create displacement
                tempValue = new Symbol("t"+ temporaryValue++);
                codeGen[row][0]= "disp";
                codeGen[row][2] = expressionStack.pop().getKey();
                expressionStack.pop();
                codeGen[row][1] = expressionStack.pop().getKey();
                codeGen[row][3] = tempValue.getKey();
                expressionStack.push(tempValue);
                row++;




                //-----------------CodeGeneration---------------------
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
            //--------------------CodeGeneration--------------
           Symbol tempVal = new Symbol("t"+temporaryValue++);
           expressionStack.push(tempVal);
            Symbol relop = new Symbol("");
            String sign = parseString.get(counter).getKey();
            switch(sign){
                case "<=": relop.setKey("LEQ");
                            break;
                case "<": relop.setKey("L");
                            break;
                case ">=": relop.setKey("GEQ");
                            break;
                case "!=": relop.setKey("NEQ");
                            break;
                case "==": relop.setKey("EQ");
                            break;
                case ">": relop.setKey("G");
                            break;
            }
            expressionStack.push(relop);
            //--------------------CodeGeneration--------------
            counter++;
           currentScope = factor(parseString, temp, currentScope);
            currentScope =  addExpressionPrime(parseString, temp, currentScope);
        }else if(parseString.get(counter).getKey().equals(";")||parseString.get(counter).getKey().equals(")")
                ||parseString.get(counter).getKey().equals("]") ||parseString.get(counter).getKey().equals(",")){
           /* if(!expressionStack.isEmpty()) {
                expressionStack.pop();
            }*/
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
    public HashTable additionOperation(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("+") || parseString.get(counter).getKey().equals("-")){
            //--------------------CodeGeneration--------------
            Symbol plus = new Symbol("");
            if(parseString.get(counter).getKey().equals("+")){
                plus = new Symbol("add");
            }else if(parseString.get(counter).getKey().equals("-")){
                plus = new Symbol("subt");
            }

           expressionStack.push(plus);
            //--------------------CodeGeneration--------------
           counter++;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable term(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){//MAY BE ERROR
        currentScope =  factor(parseString, temp, currentScope);
        currentScope =  termPrime(parseString, temp, currentScope);//mulp/div
        return currentScope;
    }
    public HashTable termPrime(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){//multiply divide check
        if(parseString.get(counter).getKey().equals("*")|| parseString.get(counter).getKey().equals("/")){
            currentScope =  multiplicationOperation(parseString,  temp, currentScope);
            currentScope =  factor(parseString, temp, currentScope);//right hand side
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
    public HashTable multiplicationOperation(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("*")
                || parseString.get(counter).getKey().equals("/")){

            //--------------------CodeGeneration--------------
            Symbol mulop = new Symbol("");
            if(parseString.get(counter).getKey().equals("*")){
                mulop = new Symbol("mult");
            }else if(parseString.get(counter).getKey().equals("/")){
                mulop = new Symbol("div");
            }
            expressionStack.push(mulop);
            //--------------------CodeGeneration--------------
            counter++;
        }else{ reject(parseString); }
        return currentScope;

    }
    public HashTable factor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("(")){//something in (expression)
            counter++;
            Symbol paren = new Symbol("(");
            expressionStack.push(paren);
            currentScope =  expression(parseString, temp, currentScope);
            if(parseString.get(counter).getKey().equals(")")){
                counter++;
                Symbol tempValue = new Symbol(expressionStack.pop().getKey());
                expressionStack.pop();
                expressionStack.push(tempValue);
            }
        }else if(parseString.get(counter).getType() =="int" || parseString.get(counter).getType() == "float"){//straight number or decimal

            if((!temp.getType().equals(parseString.get(counter).getType()))){ //|| temp.isArray()) {
                reject(parseString);
            }
            //----------MADE BIG CHANGE TO SYMBOL TABLE!!---------------
            Symbol numberTemp = new Symbol(parseString.get(counter).getKey());
            numberTemp.setType(parseString.get(counter).getType());
           expressionStack.push(numberTemp);
            /// /currentScope.insertQuad(numberTemp);
            ///==========================BIG CHANGE=============COULD AFFFECT ALOT OF STUFF!!?!?!
            counter++;
        }else if(parseString.get(counter).getType() == "id"){//variable check
            //check if id is it has not been declared
            if(checkIfNotDeclared(parseString.get(counter),currentScope)){//returns true if it is declared before
                //get Symbol
                Symbol compareTemp = getItemFromTree(parseString.get(counter), currentScope);
               /* if(currentScope.quadFind(parseString.get(counter)) == null){
                    compareTemp = getItemFromTree(parseString.get(counter), currentScope);
                }/*else{
                    compareTemp =(Symbol) currentScope.quadFind(parseString.get(counter));

                }*/

                //checking if types can be add/sub/mul/div
                if(compareTemp != null) {
                    if (!temp.getType().equals(compareTemp.getType())) {
                       // reject(parseString);
                    }
                }
               expressionStack.push(compareTemp);
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
    public HashTable factorLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
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
    public HashTable argsListPrime(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals(",")){
            counter++;
            parameterCounter++;
            temp.setParameterCounter(parameterCounter);
            currentScope = expression(parseString, temp, currentScope);
            currentScope = argsListPrime(parseString, temp, currentScope);//account for multiple args here
        }else if(parseString.get(counter).getKey().equals(")")){
            return currentScope;
        }else{ reject(parseString); }
        return currentScope;
    }
    public HashTable selectionStatementLeftFactor(ArrayList<DataItem> parseString, Symbol temp, HashTable currentScope){
        if(parseString.get(counter).getKey().equals("else")){
            counter++;
            codeGen[backPatch][3]=Integer.toString(row+1);
            codeGen[row][0] ="B";
            codeGen[row][1] = "     ";
            codeGen[row][2] = "      ";
            backPatch=row;
            row++;
            currentScope.setInConditional(true);
            currentScope =  statement(parseString, temp, currentScope);
            //-------------CODEGENERATION------------------------
            codeGen[row][0] = "end";
            codeGen[row][1]= "block";
            codeGen[row][2]= "      ";
            codeGen[row][3] ="     ";
            row++;
            codeGen[backPatch][3] = Integer.toString(row);
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
       // System.out.println("REJECT" + parseString.get(counter).getKey());
        //System.exit(0);
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
    public void stackDrain() {
        Symbol tempValSymbol = new Symbol("");
        tempValSymbol = new Symbol(expressionStack.pop().getKey());
        if (((expressionStack.peek().getKey().equals("mult"))
                || (expressionStack.peek().getKey().equals("sub"))
                || (expressionStack.peek().getKey().equals("add"))
                || (expressionStack.peek().getKey().equals("div")))) {
           expressionStack.push(tempValSymbol);
            while ((!expressionStack.isEmpty())) {
                tempValSymbol = new Symbol("t" + temporaryValue++);
                codeGen[row][3] = tempValSymbol.getKey();
                codeGen[row][2] = expressionStack.pop().getKey();
                if (((expressionStack.peek().getKey().equals("mult"))
                        || (expressionStack.peek().getKey().equals("sub"))
                        || (expressionStack.peek().getKey().equals("add"))
                        || (expressionStack.peek().getKey().equals("div")))) {
                    //operation
                    codeGen[row][0] = expressionStack.pop().getKey();

                    codeGen[row][1] = expressionStack.pop().getKey();
                    if (codeGen[row][0].equals("assign")) {
                        codeGen[row][3] = codeGen[row][1];
                        codeGen[row][1] = codeGen[row][2];
                        codeGen[row][2] = "     ";
                        temporaryValue--;
                    }

                    row++;
                    if (!expressionStack.isEmpty()) {
                        if (expressionStack.peek().getKey().equals("(")) {
                            //expressionStack.push(tempValSymbol);
                            break;
                        }
                        expressionStack.push(tempValSymbol);

                    } else {
                        break;
                    }
                } else {
                    tempValSymbol = new Symbol(codeGen[row][2]);
                    expressionStack.push(tempValSymbol);
                    codeGen[row][3] = "     ";
                    codeGen[row][2] = "";
                    temporaryValue--;
                    break;
                }
            }
        }else{
            expressionStack.push(tempValSymbol);
        }
    }
}
