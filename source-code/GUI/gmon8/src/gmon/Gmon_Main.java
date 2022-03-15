package gmon;


import java.awt.EventQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;
import javax.swing.JRadioButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ButtonGroup;
import javax.swing.border.BevelBorder;
import javax.swing.JToggleButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JDesktopPane;


import java.awt.Panel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.SwingConstants;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;


public class Gmon_Main extends JFrame {
    
    private final ButtonGroup buttonGroup = new ButtonGroup();
    private final ButtonGroup buttonGroup2 = new ButtonGroup();
    static public  JTextField dbgtxt;
       
    static public int maxth=32;
    public static int animadv;
    static public long mytracker=0;
    public int myth=0;
    private int tpi=0;

    public Thread kt;
    
    public static final Object lock = new Object();
    public static LParConnect dialog2;
    public LParConnect[] myd;
    static public JSlider slider;
    static public JLabel lblRefreshplaybackRate,thdlabel;
    static public int thedelay;
    static public int debug;
    static public ConcurrentLinkedQueue<MyThread> activeThreads = new ConcurrentLinkedQueue<MyThread>();
    
 
    
	/**
	 * Launch the application.
	 */
	/*public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gmon_Main frame = new Gmon_Main();
					frame.setVisible(true);
					
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}*/

	/**
	 * Create the frame.
	 */
	public Gmon_Main() {
            int jj=0;
           
            debug=0;
            animadv=0;
            //lock = new Object();
            myd=new LParConnect[32]; 
            for (jj=0;jj<32;jj++)
                myd[jj]=null;
            if (Gmon_Main.debug > 0) System.out.println("out of myd init");
            
            setTitle("gmon v8.0 beta 0.7");
		setBounds(100, 100, 340, 220);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		JRadioButton rdbtn1 = new JRadioButton("Interactive Monitoring Mode");
		rdbtn1.setSelected(true);
		buttonGroup.add(rdbtn1);
		rdbtn1.setBounds(10, 17, 194, 23);
		getContentPane().add(rdbtn1);
		
		JRadioButton rdbtn2 = new JRadioButton("nmon file(s) playback mode");
		rdbtn2.setEnabled(false);
		buttonGroup.add(rdbtn2);
		rdbtn2.setBounds(10, 34, 194, 23);
		getContentPane().add(rdbtn2);
		
                JRadioButton animbtn2 = new JRadioButton("Animation Off");
		animbtn2.setSelected(true);
                buttonGroup2.add(animbtn2);
		animbtn2.setBounds(10, 140, 194, 23);
		getContentPane().add(animbtn2);
        
                
                JRadioButton animbtn1 = new JRadioButton("Animation On");
		animbtn1.setSelected(false);
		buttonGroup2.add(animbtn1);
		animbtn1.setBounds(10, 160, 194, 23);
		getContentPane().add(animbtn1);
                
                
                dbgtxt = new JTextField();
		dbgtxt.setText("0");
		dbgtxt.setBounds(302, 160, 13, 20);
                getContentPane().add(dbgtxt);
		dbgtxt.setColumns(1);
                
                
		
		
                animbtn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				animadv=1;
				//System.out.println("animadv="+animadv);
			} //end action performed
		});// end action listener
                
                animbtn2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				animadv=0;
				//System.out.println("animadv="+animadv);
			} //end action performed
		});// end action listener
                        
               
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				while(activeThreads.size()>0){
					kt=activeThreads.poll();
					kt.interrupt();
			}// end while
				System.exit(0);
				//this.exit();
			} //end action performed
		});// end action listener
		btnQuit.setBounds(225, 17, 89, 23);
		getContentPane().add(btnQuit);
		
		lblRefreshplaybackRate = new JLabel("Refresh/Playback Rate = 1s");
		lblRefreshplaybackRate.setBounds(10, 98, 163, 14);
		getContentPane().add(lblRefreshplaybackRate);
		
		slider = new JSlider();
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int j =slider.getValue();
				lblRefreshplaybackRate.setText("Refresh/Playback rate = "+j+"s");
				thedelay=j*1000;
			}
		});
		//slider.addChangeListener(new MysliderChangeListener());
		
		
		slider.setMajorTickSpacing(1);
		slider.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		slider.setMinimum(1);
		slider.setMaximum(5);
		slider.setValue(2);
		slider.setBounds(10, 114, 304, 23);
		getContentPane().add(slider);
		
		
		
		JButton btnAddLparTo = new JButton("Add LPar to Monitor");

                btnAddLparTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		                myth=myth+1;
				myd[myth] = new LParConnect(Thread.currentThread(),myth,null,0);
	    		        dialog2=myd[myth];
                                
                                 
                                //Have dialog2 so lparconnect can continue
                                synchronized(Gmon_Main.lock){
                                    Gmon_Main.lock.notifyAll();
                                }
                                
                                
                                //LParConnect dialog = new LParConnect(this);
				myd[myth].setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				myd[myth].setVisible(true);
                                myd[myth].setResizable(false);
				//System.out.println("OUT OF DIALOG");
				
				dialog2=myd[myth];
				
				/*if(activeThreads.size() < maxth) {
					System.out.println("starting thread");
		            new MyThread(activeThreads,1).start();
				}
				System.out.println("running threads= " + activeThreads.size());*/
				
			} // end action performed
		}); //end action listener
		btnAddLparTo.setBounds(10, 64, 150, 23);
		getContentPane().add(btnAddLparTo);
		
		JButton btnImportLpars = new JButton("Import LPars");
		btnImportLpars.setEnabled(true);
		btnImportLpars.setBounds(200, 64, 115, 23);
		getContentPane().add(btnImportLpars);

                btnImportLpars.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
		                
			JFileChooser fc = new JFileChooser();
                            fc.setCurrentDirectory(new File("."));
                            fc.setFileFilter(new FileFilter(){
                               public boolean accept(File f) {
                                return f.getName().toLowerCase().endsWith(".lp") || f.isDirectory();
                                }
                                
                                public String getDescription() {
                                    return ".lp Files";
                                }
                            
                            });
                        
                        
                        int returnVal = fc.showOpenDialog(getContentPane());  	
	
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                             
                            //This is where a real application would open the file.
                                //System.out.println("Opening: " + file.getPath() );
                                try{
                                    // Open the file that is the first 
                                    // command line parameter
                                    FileInputStream fstream = new FileInputStream(file.getPath());
                                    // Get the object of DataInputStream
                                    DataInputStream in = new DataInputStream(fstream);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                    String strLine;
                                    //Read File Line By Line
                                    while ((strLine = br.readLine()) != null)   {
                                        String chr1=strLine.substring(0,1);
                                        if (!(chr1.matches("#"))) {
                                            //System.out.println (strLine);
                                            int index=strLine.indexOf(",");
                                            if (index==-1){
                                                JOptionPane.showMessageDialog(getContentPane(), "Invalid File Contents?\n"+strLine+ "\nhost,port needed", "Attention!", JOptionPane.WARNING_MESSAGE);       
                                            }
                                        else {
                              
                                            String th=strLine.substring(0,index);
                                            th=th.trim();
                                        //System.out.println("th="+th);
                                            String tp=strLine.substring(index+1);
                                        tp=tp.trim();
                                        tpi=Integer.parseInt(tp);
                                        th=th.trim();
                                        //System.out.println("th="+th+" tp="+tp);
                                        // fire off 
                                        myth=myth+1;
                                        myd[myth] = new LParConnect(Thread.currentThread(),myth,th,tpi);
	    		                dialog2=myd[myth];
                                        //dialog2.thost=th;
                                        //dialog2.tport=tpi;
                                       
                                        synchronized(Gmon_Main.lock){
                                            Gmon_Main.lock.notifyAll();
                                        }
                                        
                                        
                                        //System.out.println("Sleeping non-interactive");
                                        
                                        synchronized(Gmon_Main.lock){
                                            try {
                                                Gmon_Main.lock.wait(2000);          // waits for dialog2 to be set
                                            } catch (InterruptedException e) {
                                            }
                                            }
                                        //System.out.println("wait complete - simulated user"); 
                                        
                                        
                                       
                                        
                                        
                                        //LParConnect dialog = new LParConnect(this);
                                        myd[myth].setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                                        myd[myth].setVisible(false);
                                        //myd[myth].dispose();
                                        //System.out.println("OUT OF DIALOG");
				
                                        //dialog2=myd[myth];  
                                       
                                        /*
                                        try{
                                        Thread.sleep(500);
                                        }
                                        catch (InterruptedException e) {
                                        e.printStackTrace();
                                        }// end catch
                                        */
                                     
                                        
                                        }
                                        } // end if #
                                    }// end while
                                    //Close the input stream
                                    in.close();
                                }
                                catch (Exception e){//Catch exception if any
                                            System.err.println("Error: " + e.getMessage());
                                            }
                                
                        } // end if
                                
                            
                             else {
                                System.out.println("Open command cancelled by user.");
                            } // end else
                     
			} // end action performed
		}); //end action listener
                
          
                
		JLabel lblMode = new JLabel("Mode");
		lblMode.setBounds(10, 5, 46, 14);
		getContentPane().add(lblMode);
		
		
		thdlabel = new JLabel("Threads=0");
		thdlabel.setHorizontalAlignment(SwingConstants.RIGHT);
		thdlabel.setBounds(233, 140, 81, 19);
		getContentPane().add(thdlabel);
		
		/*
		JToggleButton tglbtnDetailedDials = new JToggleButton("Detailed Dials");
		tglbtnDetailedDials.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Gmon_Main.debug > 0) System.out.println("list running threads= " + activeThreads.size());
			}
		});
                
		tglbtnDetailedDials.setSelected(true);
		tglbtnDetailedDials.setBounds(10, 148, 121, 23);
		getContentPane().add(tglbtnDetailedDials);
		*/

	}

	
}
