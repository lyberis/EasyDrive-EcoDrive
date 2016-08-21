package com.ls.easydrive_ecoddrive;

public class GeneralParms {
    
    double rpm_limit_y;
    double thr_limit_y;
    double level_prc_rpm;
    double level_prc_thr;
    int calibr_dist;
    int calibr_num;
    int valid_dist;
   
    public GeneralParms(){
         
    }
   
    public GeneralParms(double rpm_limit_y, double thr_limit_y, double level_prc_rpm, double level_prc_thr, int calibr_dist,
    					int calibr_num, int valid_dist){
        this.rpm_limit_y 		= rpm_limit_y;
    	this.thr_limit_y 		= thr_limit_y;
    	this.level_prc_rpm 		= level_prc_rpm;
    	this.level_prc_thr      = level_prc_thr;
    	this.calibr_dist 		= calibr_dist;
    	this.calibr_num 		= calibr_num;
    	this.valid_dist		 	= valid_dist;

    }
    
    public void setRpm_Limit_Y(double rpm_limit_y){
        this.rpm_limit_y = rpm_limit_y;
    }
    
    public double getRpm_Limit_Y(){
        return this.rpm_limit_y;
    }
    
    public void setThr_Limit_Y(double thr_limit_y){
        this.thr_limit_y = thr_limit_y;
    }
    
    public double getThr_Limit_Y(){
        return this.thr_limit_y;
    }
    
    public void setLevel_Prc_RPM(double level_prc_rpm){
        this.level_prc_rpm = level_prc_rpm;
    }
    
    public double getLevel_Prc_RPM(){
        return this.level_prc_rpm;
    }
    
    public void setLevel_Prc_THR(double level_prc_thr){
        this.level_prc_thr = level_prc_thr;
    }
    
    public double getLevel_Prc_THR(){
        return this.level_prc_thr;
    }
    
    public void setCalibr_Dist(int calibr_dist){
        this.calibr_dist = calibr_dist;
    }
    
    public int getCalibr_Dist(){
        return this.calibr_dist;
    }
    
    public void setCalibr_Num(int calibr_num){
        this.calibr_num = calibr_num;
    }
    
    public int getCalibr_Num(){
        return this.calibr_num;
    }
    
    public void setValid_Distance(int valid_dist){
        this.valid_dist = valid_dist;
    }
    
    public int getValid_Distance(){
        return this.valid_dist;
    }

}
