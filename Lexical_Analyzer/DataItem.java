   class DataItem{
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
      
      public DataItem(String i){
      
      key = i;   
      counter = 0;
      nested = 0;
      
               
      }
      public String getKey(){
    
         return key;
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
      public int getProbelength(){
         return probelength;
      }
      public void setProbelengthFind(int probe){
         probelengthfind = probe;
      }
      public int getProbelengthFind(){
         return probelengthfind;
      }
      public void setProbelengthDelete(int probe){
         probelengthdelete = probe;
      }
      public int getProbelengthDelete(){
         return probelengthdelete;
      }
      public void setArrayPlace(int place){
         arrayPlace = place;
      }
      public int getArrayPlace(){
         return arrayPlace;
      }
      public void setCollisionValue(boolean value){
         collision = value;
      }
      public boolean getCollisionValue(){
         return collision;
      }
      public void setCounter(int value){
         counter = value;
      }
      public int getCounter(){
         return counter;
      }
}