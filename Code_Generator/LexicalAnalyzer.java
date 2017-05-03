/**
 * Created by Megan on 3/23/2017.
 */
import java.util.ArrayList;

public class LexicalAnalyzer {

    public static boolean charCategorization(DataItem input, HashTable list, ArrayList<DataItem> parseString) {
        String creator = "";
        boolean didNotFinishComment = false;
        String temp = "";

        while (input.getCounter() < input.getKey().length()) {
            DataItem aDataTemp = new DataItem("");
            //starts with a letter, builds a word ID or keyword
            if ((input.getKey().charAt(input.getCounter()) >= 'A' && input.getKey().charAt(input.getCounter()) <= 'Z') || (input.getKey().charAt(input.getCounter()) >= 'a' &&
                    input.getKey().charAt(input.getCounter()) <= 'z')) {

                temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                input.setCounter(input.getCounter() + 1);
                //if not at end of line
                if (input.getCounter() < input.getKey().length()) {//if last value on line, break out of while and print available

                    while ((input.getKey().charAt(input.getCounter()) >= 'A' && input.getKey().charAt(input.getCounter()) <= 'Z') || (input.getKey().charAt(input.getCounter()) >= 'a' &&
                            input.getKey().charAt(input.getCounter()) <= 'z')) {

                        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                        input.setCounter(input.getCounter() + 1);
                        if (input.getCounter() >= input.getKey().length()) {//if last value on line, break out of while and print available
                            break;
                        }
                    }
                  
                    if (input.getCounter() < input.getKey().length()) {//checks for end of line
                        if (isAnumber(input, 0)) {//if next is a number, this is an error!
                            //add number to error string.
                            errorHandler(input, temp);

                            break;
                        }//isaNumber()
                    }

                }
                //at end of line
                printOutput(temp, list, input, parseString);
                temp = "";

            }
            if (input.getCounter() >= input.getKey().length()) {//if last value on line, break out of while and print available
                break;
            }

            //if encounter a number, begin number sequence.
            if (isAnumber(input, 0)) {
                numBuilder(input, temp, parseString);
                temp = "";
                if (input.getCounter() >= input.getKey().length()) {
                    break;
                }
                // break;
            }
            char symbol = input.getKey().charAt(input.getCounter());
            switch (symbol) {

                case '(':

                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case ')':

                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case '+':
                    if (endOfLine(input)) {

                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);

                    } else {

                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                    }
                    break;
                case '-':
                    if (endOfLine(input)) {

                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);

                    } else {

                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                    }
                    break;
                case '*':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case '/':
                    if (endOfLine(input)) {

                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        temp = "";
                        break;
                    }
                    if (input.getKey().charAt(input.getCounter() + 1) == '*') {

                     /*increments when entering a possible nest, decrements on closing
                        when the variable gets to zero, that means it is breaking on outer nest*/
                        input.setCounter(input.getCounter() + 1);
                        if (input.getCounter() + 1 < input.getKey().length()) {
                            //select case where starts with a /*lllll*/no nesting
                            if (input.getKey().charAt(input.getCounter() + 1) == '*') {
                                input.setCounter(input.getCounter() + 1);
                                if (input.getKey().charAt(input.getCounter() + 1) == '/') {
                                    input.setCounter(input.getCounter() + 1);
                                    break;
                                } else {
                                    input.setNested(input.getNested() + 1);
                                    in_comment(input);
                                    break;
                                }
                            } else {
                                input.setCounter(input.getCounter() + 1);
                                input.setNested(input.getNested() + 1);
                                in_comment(input);
                                break;

                            }
                        } else {
                            input.setCounter(input.getCounter() + 1);
                            input.setNested(input.getNested() + 1);
                            in_comment(input);
                            break;
                        }
                    }//if(input.getKey().charAt(input.getCounter()+1) == '*')
                    if (input.getKey().charAt(input.getCounter() + 1) == '/') {
                        while (input.getCounter() < input.getKey().length()) {
                            input.setCounter(input.getCounter() + 1);
                        }
                        break;
                    } else {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                case '<':
                    if (endOfLine(input)) {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                    if (input.getKey().charAt(input.getCounter() + 1) == '=') {
                        creator = String.valueOf(symbol);
                        input.setCounter(input.getCounter() + 1);
                        creator = creator.concat(String.valueOf(input.getKey().charAt(input.getCounter())));
                        aDataTemp.setKey(creator);
                        parseString.add(aDataTemp);
                        break;
                    } else {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                case '=':
                    if (endOfLine(input)) {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                    if (input.getKey().charAt(input.getCounter() + 1) == '=') {
                        creator = String.valueOf(symbol);
                        input.setCounter(input.getCounter() + 1);
                        creator = creator.concat(String.valueOf(input.getKey().charAt(input.getCounter())));
                        aDataTemp.setKey(creator);
                        parseString.add(aDataTemp);

                        break;
                    } else {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                case ';':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case ',':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case '[':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case ']':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    break;
                case '{':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    temp = "";
                    input.setDepth(input.getDepth() + 1);
                    break;
                case '}':
                    aDataTemp.setKey(String.valueOf(symbol));
                    parseString.add(aDataTemp);
                    input.setDepth(input.getDepth() - 1);
                    temp = "";
                    break;
                case '>':
                    if (endOfLine(input)) {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                    if (input.getKey().charAt(input.getCounter() + 1) == '=') {
                        creator = String.valueOf(symbol);
                        input.setCounter(input.getCounter() + 1);
                        creator = creator.concat(String.valueOf(input.getKey().charAt(input.getCounter())));
                        aDataTemp.setKey(creator);
                        parseString.add(aDataTemp);
                        break;
                    } else {
                        aDataTemp.setKey(String.valueOf(symbol));
                        parseString.add(aDataTemp);
                        break;
                    }
                case '!':
                    if (endOfLine(input)) {
                        System.out.println("Error: " + symbol);
                        break;
                    }
                    if (input.getKey().charAt(input.getCounter() + 1) == '=') {
                        creator = String.valueOf(symbol);
                        input.setCounter(input.getCounter() + 1);
                        creator = creator.concat(String.valueOf(input.getKey().charAt(input.getCounter())));
                        aDataTemp.setKey(creator);
                        parseString.add(aDataTemp);
                        break;
                    } else {
                        System.out.println("Error: " + symbol);
                        break;
                    }

                case ' ':
                    break;
                default:
                    errorHandler(input, temp);

                    break;

            }

            input.setCounter(input.getCounter() + 1);


        }
        if (input.getEnteredComment()) {
            if (!input.getFinishComment()) {
                didNotFinishComment = true;
            }
        }

        return didNotFinishComment;

    }

    //OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
//------------------------------------------------------
    public static boolean isAnumber(DataItem input, int value) {
        boolean isAnumber = false;
        if ((input.getKey().charAt(input.getCounter() + value)) >= '0' && (input.getKey().charAt(input.getCounter() + value)) <= '9') {
            isAnumber = true;
        }

        return isAnumber;
    }

    //------------------------------------------------------
    public static void numBuilder(DataItem input, String temp, ArrayList parseString) {
        DataItem aDataTemp = new DataItem("");
        //integer string set up
        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
        input.setCounter(input.getCounter() + 1);
        if (input.getCounter() < input.getKey().length()) {
            while (isAnumber(input, 0)) {
                temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                input.setCounter(input.getCounter() + 1);
                if (input.getCounter() >= input.getKey().length()) {
                    break;
                }
            }
        }
        //CHECKs FOR END OF LINE
        if (input.getCounter() >= input.getKey().length()) {
            input.setCounter(input.getCounter() + 1);

            aDataTemp.setKey(String.valueOf(temp));
            aDataTemp.setType("int");
            parseString.add(aDataTemp);

        }
        //ENCOUNTERS A PERIOD, which moves to next float state
        else if (input.getKey().charAt(input.getCounter()) == '.') {
            //checks if end of line
            if (input.getCounter() + 1 < input.getKey().length()) {
                //if no number follows the . then not a decimal, ERROR!
                if (!isAnumber(input, 1)) {
                    //ERROR HANDLER
                    errorHandler(input, temp);

                } else {

                    temp = enterDecimalFloat(input, temp);

                    if (temp != "") {//if decimal was not an error~

                        aDataTemp.setKey(String.valueOf(temp));
                        aDataTemp.setType("float");
                        parseString.add(aDataTemp);
                    }


                }
            } else {
                //ERROR HANDLER
                errorHandler(input, temp);
            }
        }
        //ENCOUNTERS an E signaling scientific float state
        else if (input.getKey().charAt(input.getCounter()) == 'E') {
            if (!temp.endsWith("E")) {
                temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                input.setCounter(input.getCounter() + 1);
            }
            //if there is no +, - or number that follows the E then it is not a decimal number.
            if (input.getCounter() + 1 <= input.getKey().length()) {//if not at end
                if (input.getCounter() + 1 == '+' || input.getCounter() + 1 == '-' || isAnumber(input, 1)) {
                    temp = enterScientificNotation(input, temp);

                    aDataTemp.setKey(temp);
                    aDataTemp.setType("float");
                    parseString.add(aDataTemp);
                } else {
                    errorHandler(input, temp);
                }
            } else {
                errorHandler(input, temp);
            }
        } else {

            aDataTemp.setKey(String.valueOf(temp));
            aDataTemp.setType("int");
            parseString.add(aDataTemp);


        }


    }

    //------------------------------------------------------------
    public static void errorHandler(DataItem input, String temp) {
        //ERROR HANDLER
        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
        input.setCounter(input.getCounter() + 1);
        for (int j = input.getCounter(); j < input.getKey().length(); j++) {//check if at end or not
            char errorTemp = input.getKey().charAt(input.getCounter());
            boolean endOfErrorToken = false;
            switch (errorTemp) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '<':
                case '>':
                case '=':
                case '!':
                case ';':
                case ',':
                case '(':
                case ')':
                case '[':
                case ']':
                case '}':
                case '{':
                case ' ':
                    endOfErrorToken = true;


            }
            if (endOfErrorToken) {

                input.setCounter(input.getCounter() - 1);
                temp = "";
                break;
            } else {
                temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                input.setCounter(input.getCounter() + 1);
            }

        }
        if (input.getCounter() >= input.getKey().length()) {

            temp = "";


        }
    }

    //--------------------------------------------------------------------------
    public static String enterScientificNotation(DataItem input, String temp) {

        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
        input.setCounter(input.getCounter() + 1);

        if (input.getCounter() < input.getKey().length()) {
            if (input.getKey().charAt(input.getCounter()) == ' ') {
                errorHandler(input, temp);
                temp = "";
            } else {
                //If it encounters a plus or minus
                if (input.getKey().charAt(input.getCounter()) == '+' || input.getKey().charAt(input.getCounter()) == '-' /*|| isAnumber(input, 0)*/) {
                    temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                    input.setCounter(input.getCounter() + 1);
                }
                //encounters a number
                if (input.getCounter() < input.getKey().length()) {
                    while (isAnumber(input, 0)) {
                        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
                        input.setCounter(input.getCounter() + 1);
                        if (input.getCounter() >= input.getKey().length()) {
                            break;
                        }
                    }
                }
            }
        }

        return temp;
    }

    //---------------------------------------------------------------------------
    public static String enterDecimalFloat(DataItem input, String temp) {
        ///adds period to string.
        temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
        input.setCounter(input.getCounter() + 1);
        //while the current char is a number

        while (isAnumber(input, 0)) {
            temp = temp + Character.toString(input.getKey().charAt(input.getCounter()));
            input.setCounter(input.getCounter() + 1);
            if (input.getCounter() >= input.getKey().length()) {
                break;
            }
        }
        //ENCOUNTERS an E
        if (input.getCounter() < input.getKey().length()) {
            if (input.getKey().charAt(input.getCounter()) == 'E') {
                temp = enterScientificNotation(input, temp);
            } else if ((input.getKey().charAt(input.getCounter()) >= 'A' && input.getKey().charAt(input.getCounter()) <= 'Z') || (input.getKey().charAt(input.getCounter()) >= 'a' &&
                    input.getKey().charAt(input.getCounter()) <= 'z')) {
                errorHandler(input, temp);
                temp = "";
            }
        }

        return temp;
    }

    //--------------------------------------------------------
    //RETURNS TRUE if next to last token
    public static boolean endOfLine(DataItem input) {
        boolean endOfLine = false;
        if (input.getCounter() + 1 > input.getKey().length()) {
            endOfLine = true;

        }
        if (input.getCounter() + 1 == input.getKey().length()) {
            endOfLine = true;

        }

        return endOfLine;

    }

    //-----------------------------------------------------------
    public static void in_comment(DataItem input) {
        int start = input.getCounter();//start point of comment
        input.setEnteredComment(true);//has entered a comment
        String temp;

        //while > end of line look for /* bump up nest value
        while (input.getCounter() <= input.getKey().length()) {
            temp = input.getKey().substring(input.getCounter());
            if (temp.contains("/*")) {
                //setting counter to new indexof most recent looked place.
                int var = input.getKey().indexOf("/*", input.getCounter());
                input.setCounter(input.getKey().indexOf("/*", input.getCounter()) + 1);
                input.setNested(input.getNested() + 1);
            } else {
                break;
            }
        }
        input.setCounter(start);
        //reset start while > end of line look for */ decrease nest value
        while (input.getCounter() <= input.getKey().length()) {
            if (input.getNested() > 0) {
                temp = input.getKey().substring(input.getCounter());
                if (temp.contains("*/")) {
                    input.setCounter(input.getKey().indexOf("*/", input.getCounter()) + 1);
                    input.setNested(input.getNested() - 1);
                } else {
                    break;
                }
            } else {
                break;
            }

        }
        //if nest value >0
        if (input.getNested() > 0) {
            input.setCounter(input.getKey().length());
            return;
            //return to main function and look at next line.
        } else { //else call finish function
            finish(input);
        }


    }

    //-------------------------------------------------------------
    public static void finish(DataItem input) {
//STATE 5

        input.setFinishComment(true);
        return;

    }

    //---------------------------------------
    public static void printOutput(String temp, HashTable list, DataItem input, ArrayList parseString) {
        DataItem aDataTemp = new DataItem("");
        if (keywordCheck(temp, list)) {


            aDataTemp.setKey(String.valueOf(temp));
            aDataTemp.setType("keyword");
            parseString.add(aDataTemp);
        } else if (temp.equals("")) {
            return;
        } else {
            aDataTemp.setKey(temp);
            aDataTemp.setType("id");
            parseString.add(aDataTemp);
        }

    }

    //---------------------------------------------------
    public static boolean keywordCheck(String input, HashTable list) {
        //Checks if word is a keyword
        boolean isAkeyword = false;
        input.trim();
        DataItem test = new DataItem(input);

        if (list.quadFind(test) != null) {
            isAkeyword = true;
        }
        return isAkeyword;
    }

    //---------------------------------------------------
    public static int getPrime(int min) {

        for (int j = min * 2; true; j++) {
            if (isPrime(j)) {
                return j;
            }
        }
    }

    //------------------------------------------------------------
    private static boolean isPrime(int n) {
        for (int j = 2; (j * j <= n); j++) {
            if (n % j == 0) {
                return false;
            }
        }
        return true;

    }

//-----------------------------------------------------------------
}
