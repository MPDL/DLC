package de.mpg.mpdl.dlc.batchIngest;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.faces.bean.ManagedProperty;

import de.mpg.mpdl.dlc.beans.LoginBean;
import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;

public class SessionExtenderTask {
	

	private UserAccountServiceBean uaService = new UserAccountServiceBean();
	
	//delay in ms 1000 ms*60*15 = 15 mins
    long delay = 1000*60*15; 
    LoopTask task = new LoopTask();
    Timer timer = new Timer("TaskName");
    private String userHandle;
    private String userId;
    
    public SessionExtenderTask(String userHandle, String userId)
    {
    	this.userHandle = userHandle;
    	this.userId = userId;
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
    	 System.err.println("Stop Retrieving user Account every 15 minutes.");
    }

    private class LoopTask extends TimerTask {
	    public void run() {
	    	uaService.retrieveUserHandle(userHandle,userId);
	        System.err.println("Retrieving user Account every 15 minutes.");
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
