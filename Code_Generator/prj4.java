/** Name: Megan Molumby
 **  N#:N00942101
 **  Class:COP 4620 Construction of Language Translators
 **  Project 3
 ** Due Date: 3/29/2017
 ** PROBLEM: construction of semantic analyzer for compiler.
 ** Last Update: 3 April 2017
 *
 **/

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;


public class prj4 {

    public static void main(String[] args)throws IOException {
        LexicalAnalyzer la = new LexicalAnalyzer();
        Parser pa = new Parser();
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
        HashTable globalSymbolTable = new HashTable(parseString.size());
        pa.program(parseString, globalSymbolTable);



    }




}