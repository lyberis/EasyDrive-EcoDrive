package com.ls.easydrive_ecoddrive;

public class UserParms {

    int level;
    double rpm_limit;
    double thr_limit;
    String calibr_flag;
    int level_rpm;
    int level_thr;
   
    public UserParms(){
         
    }
   
    public UserParms(int level, double rpm_limit, double thr_limit, String calibr_flag, int level_rpm, int level_thr){
        this.level 			= level;
    	this.rpm_limit 		= rpm_limit;
    	this.thr_limit 		= thr_limit;
    	this.calibr_flag 	= calibr_flag;
    	this.level_rpm 		= level_rpm;
    	this.level_thr 		= level_thr;
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public int getLevel(){
        return this.level;
    }
    
    public void setRpm_Limit(double rpm_limit){
        this.rpm_limit = rpm_limit;
    }
    
    public double getRpm_Limit(){
        return this.rpm_limit;
    }
     
    public void setThr_Limit(double thr_limit){
        this.thr_limit = thr_limit;
    }
    
    public double getThr_Limit(){
        return this.thr_limit;
    }
    
    public void setCalibr_Flag(String calibr_flag){
        this.calibr_flag = calibr_flag;
    }
    
    public String getCalibr_Flag(){
        return this.calibr_flag;
    }
    
    public void setLevel_Rpm(int level_rpm){
        this.level_rpm = level_rpm;
    }
    
    public int getLevel_Rpm(){
        return this.level_rpm;
    }
    
    public void setLevel_Thr(int level_thr){
        this.level_thr = level_thr;
    }
    
    public int getLevel_Thr(){
        return this.level_thr;
    }
    
}
