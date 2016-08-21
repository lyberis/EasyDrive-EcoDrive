package com.ls.easydrive_ecoddrive;

public class TripHeader {
    
    int trip_aa;
    String start_date;
    String start_time;
    String end_date;
    String end_time;
    double distance;
    double avg_spd;
    double avg_rpm;
    double avg_thr;
    String calibr_flag;
    int level;
   
    public TripHeader(){
         
    }
   
    public TripHeader(int trip_aa, String start_date, String start_time){
        this.trip_aa = trip_aa;
    	this.start_date = start_date;
        this.start_time = start_time;
    }
    
    public void setTrip_AA(int trip_aa){
        this.trip_aa = trip_aa;
    }
    
    public int getTrip_AA(){
        return this.trip_aa;
    }
     
    public void setStart_Date(String start_date){
        this.start_date = start_date;
    }
    
    public String getStart_Date(){
        return this.start_date;
    }
    
    public void setStart_Time(String start_time){
        this.start_time = start_time;
    }
    
    public String getStart_Time(){
        return this.start_time;
    }
    
    public void setEnd_Date(String end_date){
        this.end_date = end_date;
    }
    
    public String getEnd_Date(){
        return this.end_date;
    }
    
    public void setEnd_Time(String end_time){
        this.end_time = end_time;
    }
    
    public String getEnd_Time(){
        return this.end_time;
    }
    
    public void setDistance(double distance){
        this.distance = distance;
    }
    
    public double getDistance(){
        return this.distance;
    }
    
    public void setAvg_Spd(double avg_spd){
        this.avg_spd = avg_spd;
    }
    
    public double getAvg_Spd(){
        return this.avg_spd;
    }
    
    public void setAvg_Rpm(double avg_rpm){
        this.avg_rpm = avg_rpm;
    }
    
    public double getAvg_Rpm(){
        return this.avg_rpm;
    }
    
    public void setAvg_Thr(double avg_thr){
        this.avg_thr = avg_thr;
    }
    
    public double getAvg_Thr(){
        return this.avg_thr;
    }
    
    public void setCalibr_Flag(String calibr_flag){
        this.calibr_flag = calibr_flag;
    }
    
    public String getCalibr_Flag(){
        return this.calibr_flag;
    }
    
    public void setLevel(int level){
        this.level = level;
    }
    
    public int getLevel(){
        return this.level;
    }
    
}
