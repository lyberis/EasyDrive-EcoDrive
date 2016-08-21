package com.ls.easydrive_ecoddrive;

public class TripDetail {

	int trip_aa;
    int gauge_aa;
    String timestamp;
    String gauge_cod;
    double gauge_value;
   
    public TripDetail(){
         
    }
   
    public TripDetail(int trip_aa, int gauge_aa, String timestamp, String gauge_cod, double gauge_value){
        this.trip_aa 		= trip_aa;
    	this.gauge_aa 		= gauge_aa;
    	this.timestamp 		= timestamp;
    	this.gauge_cod 		= gauge_cod;
    	this.gauge_value 	= gauge_value;
    }
    
    public void setTrip_AA(int trip_aa){
        this.trip_aa = trip_aa;
    }
    
    public int getTrip_AA(){
        return this.trip_aa;
    }
    
    public void setGauge_AA(int gauge_aa){
        this.gauge_aa = gauge_aa;
    }
    
    public int getGauge_AA(){
        return this.gauge_aa;
    }
     
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
    
    public String getTimestamp(){
        return this.timestamp;
    }
    
    public void setGauge_Cod(String gauge_cod){
        this.gauge_cod = gauge_cod;
    }
    
    public String getGauge_Cod(){
        return this.gauge_cod;
    }
    
    public void setGauge_Value(double gauge_value){
        this.gauge_value = gauge_value;
    }
    
    public double getGauge_Value(){
        return this.gauge_value;
    }
    
}
