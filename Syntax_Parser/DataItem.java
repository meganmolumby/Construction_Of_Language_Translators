/**
 * Created by Megan on 2/22/2017.
 */
public class DataItem {
    private String key;
    private String comment;
    private int probelength;
    private int probelengthfind;
    private int probelengthdelete;
    private int arrayPlace;
    private boolean collision = false;
    private int counter;
    private boolean finishComment= false;
    private boolean enteredComment = false;
    private int nested;
    private int depth;
    private String type;
    public DataItem(String i){

        key = i;
        counter = 0;
        nested = 0;
        type = "none";


    }
    public void setType(String value){
        type = value;
    }
    public String getType(){

        return type;
    }
    public String getKey(){

        return key;
    }
    public void setKey(String i){
        key = i;
    }
    public void setDepth(int value){
        depth = value;
    }
    public int getDepth(){
        return depth;
    }
    public void setNested(int value){
        nested = value;
    }
    public int getNested(){
        return nested;
    }
    public void setEnteredComment(boolean value){
        enteredComment = value;
    }
    public boolean getEnteredComment(){
        return enteredComment;
    }
    public void setFinishComment(boolean value){
        finishComment = value;
    }
    public boolean getFinishComment(){
        return finishComment;
    }
    public void setProbelength(int probe){
        probelength = probe;
    }
    public void setProbelengthFind(int probe){
        probelengthfind = probe;
    }
    public void setArrayPlace(int place){
        arrayPlace = place;
    }
    public void setCollisionValue(boolean value){
        collision = value;
    }
    public void setCounter(int value){
        counter = value;
    }
    public int getCounter(){
        return counter;
    }

}
