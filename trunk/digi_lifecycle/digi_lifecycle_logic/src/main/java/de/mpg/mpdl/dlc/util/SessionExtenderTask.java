package de.mpg.mpdl.dlc.util;

import java.util.Date;

import java.util.Timer;
import java.util.TimerTask;

import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;


public class SessionExtenderTask {
	

	private UserAccountServiceBean uaService = new UserAccountServiceBean();
	
	//delay in ms 1000 ms*60*15 = 20 mins
    long delay = 1000*60*20; 
    LoopTask task = new LoopTask();
    Timer timer = new Timer("TaskName");
    private String userHandle;
    
    public SessionExtenderTask(String userHandle)
    {
    	this.userHandle = userHandle;
    }

    public void start() 
    {
	    timer.cancel();
	    timer = new Timer("TaskName");
	    Date executionDate = new Date(); // no params = now
	    timer.scheduleAtFixedRate(task, executionDate, delay);
    }
    
    public void stop()
    {
    	timer.cancel();
    	timer.purge();
    	 System.err.println("Stop Retrieving user Account every 20 minutes.");
    }

    private class LoopTask extends TimerTask {
	    public void run() {
	    	uaService.retrieveCurrentUser(userHandle);
	//    	uaService.retrieveUserHandle(userHandle,userId);
	        System.err.println("Retrieving user Account every 20 minutes.");
	    }
    }


    
    public static void main(String[] args) {
//    SessionExtenderTask task = new SessionExtenderTask();
//    task.start();
//    try {
//		Thread.sleep(22000);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//    task.stop();

    }


}
