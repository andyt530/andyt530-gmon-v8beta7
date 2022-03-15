package gmon;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LParConnect extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField thost_txt;
    private JTextField tport_txt;
    private Thread kth;
    public Thread mykth;
    public static String thost;
    public static int tport=15050;
    public tryIPConnect nc;
    public tryIPConnect fx;
    public tryIPConnect mync;
    
	
	/**
	 * Launch the application.
	 */
	//public static void main(String[] args) {
		//try {
			//LParConnect dialog = new LParConnect();
			//dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			//dialog.setVisible(true);
		//} catch (Exception e) {
			//e.printStackTrace();
		//}
	//}

	/**
	 * Create the dialog.
	 */
	public LParConnect(Thread tth,int mjj,String lhost,int lport) {
		final int jj=mjj;
                
                //System.out.println("LParConnect started - waiting");
                synchronized(Gmon_Main.lock){
                try {
                    Gmon_Main.lock.wait(2000);          // waits for dialog2 to be set
                } catch (InterruptedException e) {
                }
                }
                //System.out.println("wait complete - dialog2 set"); 
                
                thost=lhost;
                tport=lport;
                
                kth=tth;
            
                
         
                
                if (tport==0) {
                // this is interactive call    

                    setModal(false);
                    setTitle("Connect to LPar / VM");
                    setBounds(100, 100, 311, 138);
                    getContentPane().setLayout(null);
                    contentPanel.setBounds(0, 0, 308, 62);
                    contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    getContentPane().add(contentPanel);
                    contentPanel.setLayout(null);

                    JLabel lblNewLabel = new JLabel("hostname or IP Address");
                    lblNewLabel.setBounds(10, 11, 151, 21);
                    contentPanel.add(lblNewLabel);

                    thost_txt = new JTextField();
                    thost_txt.setText("cts150");
                    thost_txt.setBounds(171, 11, 104, 20);
                    contentPanel.add(thost_txt);
                    thost_txt.setColumns(10);

                    JLabel lblPortdefault = new JLabel("Port (default 15050)");
                    lblPortdefault.setBounds(10, 39, 118, 14);
                    contentPanel.add(lblPortdefault);

                    tport_txt = new JTextField();
                    tport_txt.setText("15050");
                    tport_txt.setBounds(171, 36, 41, 20);
                    contentPanel.add(tport_txt);
                    tport_txt.setColumns(10);


                    JPanel buttonPane = new JPanel();
                    buttonPane.setBounds(10, 64, 273, 33);
                    getContentPane().add(buttonPane);
                    buttonPane.setLayout(null);


                    JButton okButton = new JButton("Connect");
                    okButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {

                                                    if (Gmon_Main.debug > 1) System.out.println("starting Connect thread");
                                                    if (tport==0) {
                                                        tport=Integer.parseInt(tport_txt.getText());
                                                        thost=thost_txt.getText();
                                                        Gmon_Main.debug=Integer.parseInt(Gmon_Main.dbgtxt.getText());
                                                    }

                                                    if (Gmon_Main.debug > 1) System.out.println("starting thread");
                                                    new MyThread(Gmon_Main.activeThreads,2,jj).start();
                                
                                                    
                                                    if (Gmon_Main.debug > 1) System.out.println("trying interrupt");
                                                    
                                                    kth.interrupt();
                                                    setVisible(false);
                                                    dispose();

                                            }
                            }); // end actionlist

                    okButton.setBounds(10, 5, 90, 23);
                    okButton.setHorizontalAlignment(SwingConstants.LEFT);
                    okButton.setActionCommand("OK");
                    buttonPane.add(okButton);
                    getRootPane().setDefaultButton(okButton);


                    JButton cancelButton = new JButton("Cancel");
                    cancelButton.setBounds(198, 5, 75, 23);
                    cancelButton.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent arg0) {
                                            System.out.println("In Cancel");
                                            //activeThreads.remove(this);
                                            kth.interrupt();
                                            setVisible(false);
                                            dispose();
                                            }
                                    }); // end action
                    cancelButton.setActionCommand("Cancel");
                    buttonPane.add(cancelButton);	

            } // end if
        else
            {
            // tport!=0 so non-interactive call
            if (Gmon_Main.debug > 1) System.out.println("Non Interactive thost="+thost+" tport="+tport+" jj="+jj);
            if (Gmon_Main.debug > 1) System.out.println("starting Connect thread");                                        
            Gmon_Main.debug=Integer.parseInt(Gmon_Main.dbgtxt.getText());
                                                    
            if (Gmon_Main.debug > 1) System.out.println("starting thread");
            
            
            new MyThread(Gmon_Main.activeThreads,2,jj).start();

            if (Gmon_Main.debug > 1) System.out.println("trying interrupt");
            kth.interrupt();
            setVisible(false);
            dispose();
                    
            }
		
	} // end lparConnect
} // end class
