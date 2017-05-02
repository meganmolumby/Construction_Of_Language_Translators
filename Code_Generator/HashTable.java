/**
 * Created by Megan on 3/23/2017.
 */

import java.util.ArrayList;


public class HashTable {
    private DataItem[] hashArray;
    private int arraySize;
    private DataItem nonItem;
    private float averageprobelength;
    private HashTable parent;
    private String returnType;
    private ArrayList<Symbol> parameterTuple = new ArrayList<>();
    private int numberOfParameterItems;
    private boolean calledAReturnValue;
    private boolean inConditional;
    private Symbol mostRecentDeclaration;



    public HashTable(int size){
        arraySize = size;
        hashArray = new DataItem[arraySize];
    }
    public HashTable getParent(){
        return parent;
    }
    public void setParent(HashTable temp){
        parent = temp;
    }

    public int getArraySize(){
        return arraySize;
    }


    //------------------------------------------------------------------
    public int hashFunc(String key){
        int hashVal = 0;
        for(int j =0; j<key.length(); j++){

            int letter = key.charAt(j);
            hashVal = (hashVal*26 + letter) % arraySize;
        }
        return hashVal; // hash function
    }

//--------------------------------------------------------------

    public void insertQuad(DataItem item){

        String key = item.getKey(); // extract key

        int hashVal = hashFunc(key); // hash the key
        int quadjump = 1;
        int hashtemp = hashVal;
        int probelength = 1;
        // until empty cell or -1,

        while(hashArray[hashVal] != null){
            item.setCollisionValue(true);
            hashtemp = (hashtemp + quadjump * quadjump++); // go to next cell
            hashtemp %= arraySize; // wraparound if necessary
            hashVal= hashtemp;
            probelength++;
        }

        hashArray[hashVal] = item; // insert item
        hashArray[hashVal].setProbelength(probelength);
    }
///---------------------------------------------------------------------------------

    public DataItem quadFind(DataItem value){
        int probelength = 1;
        int quadjump =1;

        int hashVal = hashFunc(value.getKey()); // hash the key
        int hashtemp = hashVal;
        while(hashArray[hashVal] != null){ // found the key?

            if(hashArray[hashVal].getKey().equals(value.getKey())){
                value.setProbelengthFind(probelength);
                value.setArrayPlace(hashVal);
                return hashArray[hashVal];
            } // yes, return item
            hashtemp =(hashtemp + quadjump * quadjump++);
            hashtemp %= arraySize;
            hashVal = hashtemp;
            probelength++; // wraparound if necessary
        }
        value.setProbelengthFind(probelength);
        value.setArrayPlace(hashVal);
        return null; // can't find item
    }
//---------------------------------------------------------------------
    public void setReturnType(String value){
        returnType = value;

    }
    public String getReturnType(){
        return returnType;
    }
    public ArrayList<Symbol> getParameterList(){
        return parameterTuple;
    }
    public  int getNumberOfParameterItems(){
        return numberOfParameterItems;
    }
    public void setNumberOfParameterItems(int value){
        numberOfParameterItems = value;
    }
    public void setCalledAReturnValue(boolean value){
        calledAReturnValue = value;
    }
    public boolean getCalledAReturnValue(){
        return calledAReturnValue;
    }
    public void setInConditional(boolean  value){
        inConditional = value;
    }
    public boolean getInConditional(){
        return inConditional;
    }
    public void setMostRecentDeclaration(Symbol temp){
        mostRecentDeclaration = temp;
    }
    public Symbol getMostRecentDeclaration(){
        return mostRecentDeclaration;
    }
}

