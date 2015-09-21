/*******************************************************************************
 * CDDL HEADER START
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0 only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or http://www.escidoc.de/license. 
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * CDDL HEADER END
 * 
 * Copyright 2011-2015 Fachinformationszentrum Karlsruhe Gesellschaft für wissenschaftlich-technische Information mbH and Max-Planck-Gesellschaft zur Förderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 *
 * Initial Developer (as defined by CDDL 1.0) of the DLC-Software are five
 * institutes and facilities of the Max Planck Society: Bibliotheca Hertziana 
 * (Rome), Kunsthistorisches Institut in Florenz (Florence), Max Planck Institute
 * for European Legal History (Frankfurt a.M.), Max Planck Institute for Human
 * Development (Berlin), and the Max Planck Digital Library (Munich). Users of the
 * DLC-Software are requested to include a "powered by DLC" on their webpage,
 * linking to the DLC documentation (http://dlcproject.wordpress.com/).
 ******************************************************************************/
package de.mpg.mpdl.dlc.util;

import java.util.Date;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import de.mpg.mpdl.dlc.beans.UserAccountServiceBean;


public class SessionExtenderTask {
	private Logger logger = Logger.getLogger(SessionExtenderTask.class);

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
    	try {
			timer.cancel();
			timer.purge();
			logger.info("Stop Retrieving user Account every 20 minutes.");
		} catch (Exception e) {
			logger.error("Error while stopping timer task for refreshing user handle",e);
		}
    }

    private class LoopTask extends TimerTask {
	   
    	public void run() {
	    	try {
				uaService.refreshUserHandle(userHandle);
				logger.info("Retrieving user Account every 20 minutes.");
			} catch (Exception e) {
				logger.error("Error while trying to refresh user handle", e);
				stop();
			}
	//    	uaService.retrieveUserHandle(userHandle,userId);
	     
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
