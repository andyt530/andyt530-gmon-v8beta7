package gmon;

import java.awt.EventQueue;

import javax.swing.Timer;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import javafx.application.Platform;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MainLoop {
public static Gmon_Main frame;
//public static JFrame myframe;
public static tryIPConnect mync;
public static JFrame[] myframe;
public static drawFrame[] newf;

static int flip,jj,haveframe,fi;
static int framec,lparc,fc;
static String myserial,mylparname,tss3,rs;
static int ti,tj,frx;
public static Map<String,Integer> map = new HashMap<String,Integer>();
public static Map<String,Integer> lparmap = new HashMap<String,Integer>();
public static Map<String,Integer> removelpar = new HashMap<String,Integer>();
public static Map<String,Integer> addlpar = new HashMap<String,Integer>();
public static Map<String,Double>[] lparv;

//public static ArrayList <ArrayList <String>> alpar = new ArrayList  <ArrayList <String>>();
//public static ArrayList <ArrayList <Double>> apphys = new ArrayList  <ArrayList <Double>>();
public static String alpar[][];
public static Double apphys[][];
public static Double aindex[];



	public static void main(String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
                                myframe=new JFrame[32];
                                newf=new drawFrame[32];
                                lparv= new HashMap[32];
                                aindex=new Double[32];
                                alpar=new String[32][32];
                                apphys=new Double[32][32];
                                frx=0;
                                Platform.setImplicitExit(false);				
				try {
					//Gmon_Main frame = new Gmon_Main();
                                        frame = new Gmon_Main();
					frame.setVisible(true);	
				
				} catch (Exception e) {
					e.printStackTrace();
                                }
                                      
                                //myframe=new JFrame();
                                //myframe.setBounds(0, 0, 150, 900);
                               // myframe.setTitle("Frame");
                                //myframe.setVisible(true);
                                //drawFrame newf= new drawFrame(myframe);
                                //Gmon_Main.thdlabel.setText("  Threads="+Thread.activeCount());
				int delay = 4000; //milliseconds
                                framec=0;
                                lparc=0;
				  ActionListener taskPerformer = new ActionListener() {
				      public void actionPerformed(ActionEvent evt) {
				          //...Perform a task...
                                          for (jj=0;jj<32;jj++){
                                              if (frame.myd[jj] != null){
                                                  
                                                  if (frame.myd[jj].mync != null){
                                                        myserial=frame.myd[jj].mync.serial;
                                                        
                                                        if (Gmon_Main.debug > 1) System.out.println("Serial = "+myserial);
                                                        //populate multi-d array                                
                                                        if (myserial!=null){
                                                                mylparname=frame.myd[jj].mync.lparname;
                                                                if (map.get(myserial)==null){
                                                                    
                                                                    if (Gmon_Main.debug > 1) System.out.println("Setting map to "+framec+" for "+myserial);
                                                                    myframe[framec]=new JFrame();
                                                                    
                                                                    //myframe[framec].setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                                                                    myframe[framec].setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                                                    myframe[framec].setName(myserial);
                                                           
                                                                    
                                                                    myframe[framec].setBounds(frx,frx/2, 150, 900);
                                                                    frx=frx+20;
                                                                    if (frx>600) frx=0;
                                                                    myframe[framec].setTitle(myserial);
                                                                    myframe[framec].setVisible(true);
                                                                    
                                                                    myframe[framec].addWindowListener(new WindowAdapter() {
                                                                        public void windowClosing(WindowEvent we) {
                                                                            rs=we.getWindow().getName();
                                                                            //JOptionPane.showMessageDialog(myframe[framec], rs, "Attention!", JOptionPane.WARNING_MESSAGE);
                                                                            
                                                                            //closecnx();
                                                                            fc=map.get(we.getWindow().getName());
                                                                            System.out.println("disposing "+rs+" "+fc);
                                                                            myframe[fc].dispose();
                                                                           
                                                                            map.remove(rs);
                                                                        }
                                                                    });
                                                                    
                                                                    
                                                                    if (Gmon_Main.debug > 1) System.out.println("CALLING NEW FRAME FRAMEC="+framec);
                                                                    newf[framec]= new drawFrame(myframe[framec],myserial);
                                                                    aindex[framec]=frame.myd[jj].mync.framecpu;
                                                                    map.put(myserial, Integer.valueOf(framec));
                                                                    framec=framec+1;
                                                                }  // end if map
                                                          
                                                                if (mylparname !=null){
                                                                    tss3=myserial+"++"+mylparname;
                                                                    if (lparmap.get(tss3)==null){
                                                                    fi=map.get(myserial);
                                                                        if (Gmon_Main.debug > 1) System.out.println("calling add series with "+myserial+" "+mylparname+" "+fi);
                                                                 
                                                                        MainLoop.addlpar.put(tss3, 1);                                                                        
                                                                        if (Gmon_Main.debug > 1 ) System.out.println("Setting lparmap to "+lparc+" for "+mylparname);
                                                                        
                                                                        lparmap.put(tss3, Integer.valueOf(lparc));
                                                                        lparc=lparc+1;
                                                                    
                                                                
                                                                    }  // end if map
                                                                }
                                                                
                                                            //System.out.println("PUTTING ! "+framec+frame.myd[jj].mync.lparname+frame.myd[jj].mync.pphys);
                                                  
                                                            //lparv[framec].put(frame.myd[jj].mync.lparname,frame.myd[jj].mync.pphys);
                                                        
                                                        //map.remove(myserial);
                                                        //System.out.println("In NC jj="+jj);
                                                        //System.out.println(frame.myd[jj].mync.lparname);
                                                        ti = map.get(myserial);
                                                        if (mylparname!=null){
                                                            tss3=myserial+"++"+mylparname;
                                                            tj=lparmap.get(tss3);
                                                        //alpar.put(ti).put(tj)=frame.myd[jj].mync.lparname;
                                                            alpar[ti][tj]=frame.myd[jj].mync.lparname;
                                                            apphys[ti][tj]=frame.myd[jj].mync.pphys;
                                                            }
                                                       
                                                        if (Gmon_Main.debug>1) System.out.println("array "+ti+" "+tj+ " contents= "+alpar[ti][tj]+" "+apphys[ti][tj]);
                                                        
                                                        
                                                        } // end if serial
                                                  } // end if frame.myd    
                                              }// end for
                                              
                                          }
                                              
     
                                          if (flip==0){ 
				    		  Gmon_Main.thdlabel.setText("  Threads="+Thread.activeCount());
				    		  flip=1;
				    	  }
				    	  else {
				    		  flip=0;
				    		  Gmon_Main.thdlabel.setText("_ Threads="+Thread.activeCount());
				    	  }
				      }
				  };
                                
				  new Timer(delay, taskPerformer).start();
				
				
			}//end run
		}); //end main
	}// end runnable

	
	
}// end class
