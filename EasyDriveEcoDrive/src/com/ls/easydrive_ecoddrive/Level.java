package com.ls.easydrive_ecoddrive;

public class Level {

    String code;
    int level;
    double limit_val;
   
    public Level(){
         
    }
   
    public Level(String code, int level, double limit_val){
        this.code 	= code;
    	this.level 	= level;
    	this.limit_val 	= limit_val;
    }
    
    public void setCode(String code){
        this.code = code;
    }
   
    public String getCode(){
        return this.code;
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public int getLevel(){
        return this.level;
    }
    
    public void setLimit(double limit_val){
        this.limit_val = limit_val;
    }
    
    public double getLimit(){
        return this.limit_val;
    }

}
