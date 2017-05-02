import java.util.ArrayList;

/**
 * Created by Megan on 3/23/2017.
 */
public class Symbol extends DataItem{
    private String name;//name of variable also KEY
    private String kind;//function(func), parameter(par), variable(var)
    private String type;//int, float, void
    private String other;//extern, auto, constant, return
    private boolean array;
    private boolean isReturnValue;
    private HashTable functionChild;
    private int parameterCounter;

    public Symbol(String i){
        super(i);
        isReturnValue = false;
        type = "none";
        kind  = "none";
        other = "none";
        array = false;
        parameterCounter = -1;
    }
    public void setKind(String value){
        if(value.equals("func") || value.equals("par") || value.equals("var")|| value.equals("array")){
            kind = value;
        }else{
            System.out.println("Wrong value for kind");
        }

    }
    public String getKind(){
        return kind;
    }
    public boolean  isArray(){
        return array;
    }
    public void setIsArray(boolean value){
        array = value;
    }
    public void setType(String value){
        type = value;
    }
    public String getType(){
        return type;
    }
    public void setOther(String value){
        other = value;
    }
    public String getOther(){
        return other;
    }
    public boolean isReturnValue(){
        return isReturnValue;

    }
    public void setIsReturnValue(boolean value){
        isReturnValue = value;
    }
    public HashTable getFunctionChild(){
        return functionChild;
    }
    public void setFunctionChild(HashTable value){
        functionChild = value;
    }
    public void setParameterCounter(int value){
        parameterCounter = value;
    }
    public int getParameterCounter(){
        return parameterCounter;
    }

}
