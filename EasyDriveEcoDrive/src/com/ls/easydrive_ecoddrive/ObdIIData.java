package com.ls.easydrive_ecoddrive;

public class ObdIIData {

	int engineRpm;
	int vehicleSpeed;
	int throttlePosition;    
	
	public ObdIIData(){
        
    }
   
    public ObdIIData(int Rpm, int Speed, int Throttle){
        this.engineRpm = Rpm;
    	this.vehicleSpeed = Speed;
    	this.throttlePosition = Throttle;
    }
    
    public void setRPM(int rpm){
        this.engineRpm = rpm;
    }
    
    public int getRPM(){
        return this.engineRpm;
    }
     
    public void setSpeed(int speed){
        this.vehicleSpeed = speed;
    }
    
    public int getSpeed(){
        return this.vehicleSpeed;
    }
    
    public void setThrottle(int throttle){
        this.throttlePosition = throttle;
    }
    
    public int getThrottle(){
        return this.throttlePosition;
    }
}
