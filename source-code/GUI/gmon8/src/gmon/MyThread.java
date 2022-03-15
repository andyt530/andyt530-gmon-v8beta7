package gmon;

import java.util.concurrent.ConcurrentLinkedQueue;
 
class MyThread extends Thread {
   private ConcurrentLinkedQueue<MyThread> activeThreads;
   private int myaction;
   public FXRadial myfx;
   public tryIPConnect nc;
   private final Object lock = new Object();
    
   private static int threadCount = 0;
   private final int threadId;

 
   public MyThread(ConcurrentLinkedQueue<MyThread> activeThreads,int myaction,int jj) {
      this.activeThreads = activeThreads;
      this.myaction=myaction; 
      
      synchronized(lock) { //make sure to protect shared data
      threadId = threadCount++;
      setDaemon(true); // if commented the threads run on for 10 seconds
      }
   }
 
   public void start() {
      if (Gmon_Main.debug > 0) System.out.println("["+threadId + "] Adding Thread to Active List");
      activeThreads.add(this);
      super.start();
   }
 
   public void run() {  // stuff to do in thread
      LParConnect dialog2;
	   try {
    	    /*if (myaction==1){
    	    	try {
    	    		System.out.println("new FXRadial ");	
    	    		myfx=new FXRadial(nc);
    	    		System.out.println("FXRadial Done");
    	    	} 
    	    	catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    }*/
    	    
    	    if (myaction==2){  	
    	        nc=new tryIPConnect(this); 
                Gmon_Main.dialog2.mync=nc;
                if (nc.gotconnect==1) {
                    
                    
                    
                    nc.hi();
                }
                if (Gmon_Main.debug > 0) System.out.println("Out of hi in MyThread");
                //nc.closecnx();
                
                
                nc=null;
                Gmon_Main.dialog2.mync=nc;
                if (Gmon_Main.debug > 0) System.out.println("Calling this.interrupt in hi");
	        this.interrupt();
    	    }
    
    	 //while (true){
         //sleep(2000);  //sleep for a random amount
    	 //Gmon_Main.thdlabel.setText("Threads="+ Thread.activeCount());
    	 //}// end while
         //connect did not work
         //dialog2.setVisible(false);
         //dialog2.dispose();        
			// if model - get here via cancel  
	    	//System.out.println("["+threadId + "] Thread loop out.");
    	 //activeThreads.remove(this);
    	 
         
      }//end try
      // if interrupted do this:
      catch(Exception e) {
    	  if (Gmon_Main.debug > 0) System.out.println("["+threadId + "] Interrupted MyThread.run.");
    	  e.printStackTrace(); 
    	  if (Gmon_Main.debug > 0) System.out.println("["+threadId + "] Due to exception Removing Thread from Active List");
    	  activeThreads.remove(this);
    	  }
      
	  //got here - so normal tread execution done.
      if (Gmon_Main.debug > 0) System.out.println("["+threadId + "] MyThread Removing Thread from Active List");
      activeThreads.remove(this);
   }//end run
   
public tryIPConnect getNc()
{
	return nc;
}   
   
}//end MyThread
 
