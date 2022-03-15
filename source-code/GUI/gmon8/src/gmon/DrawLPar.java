package gmon;



import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;

import java.awt.Canvas;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;


//import eu.hansolo.steelseries.tools.*;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;





public class DrawLPar extends JFrame {

	private JPanel contentPane;
	private JSeparator separator;
	public JMenuBar menuBar;
	public JLabel lblapp,lblSmt,lbluswi,lblmm,lblaff,lblpsp,lblpspin,lblpspout;
        public JLabel lbllpar,lblframe,lblpspp,lblmem,lblmem2;
        public JLabel lblpsptot,lblpspfree,lblvtot,lblvact;
        public JLabel lblame,lblamet,lblamea,lblamed,lblameci,lblameco;
        public JLabel lblio,lblnr,lblnw,lbldxfers,lbldr,lbldw;
        public JLabel lblio2,lblsq,lblrserv,lblwserv,lblsrfr,lblcycles;
        public JLabel s3;
        private tryIPConnect kth;
	
	public FXRadial myfx;
	
	public int doframe=0;
	private int i,fxresize,fxwidth,fxheight;
	private double t;
	private JLabel lblWidth;
	private BufferStrategy mybuffer;
	private Graphics g;
	public JPanel uswi,mm,aff,psp;
	private Canvas uswi2;
	Graphics2D g2=null,g4=null,g6=null,g8=null;
	Graphics g1,g3,g5,g7;
	//private DesignSet night,day;
	
	/**
	 * Create the frame.
	 */
	public DrawLPar(final tryIPConnect tth) {
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				double rw,rh;
				int rx,ry;
				rw=getWidth();
				rh=getHeight();
                                rx=getX();
                                ry=getY();
		/*        
				if (rw<400)
				{
					//setBounds(rx,ry,450,500);
                                        setBounds(rx,ry,400,450);
					
				}
				if (rh<500)
				{
					setBounds(rx,ry,400,450);
					
				}
				/*if (rh>700)
				{
					setBounds(0,0,700,900);
					
				}
				if (rh>1000)
				{
					setBounds(0,0,700,900);
					
				}*/
				rw=getWidth();
				rh=getHeight();
				/*
				if (rh>(1.3*rw)){
					setBounds(rx,ry,(int)rw,(int)(rw*1.3));
				}
				
				if (rw>(1.3*rh)){
					setBounds(rx,ry,(int)(rh*1.3),(int)(rh));
				}
				
				//myfx.doresize=1;
				
				//no sure this can be called from here !
				*/
				
				setmybounds();
				
			}
		});
	
		
		
		try{
			
		//System.out.println("In DrawLPar init 1");
		kth=tth;
		//setUndecorated(true);  removes window manager !
		
		setTitle("<Serial> <LPar name> (<id>)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
              
                //setResizable(false);
                //mydraw.setBounds default
                
		setBounds(100, 100, 350, 420);
                contentPane = new JPanel();
                
        
                                
		contentPane.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		separator = new JSeparator();
		separator.setBounds(0, (getHeight()/2)+20, getWidth(), 2);
		contentPane.add(separator);
		
                
		menuBar = new JMenuBar();
		contentPane.add(menuBar,1,1);		
		
		JMenu mnMenu = new JMenu("Menu");
		menuBar.add(mnMenu);
		
		JMenuItem mntmExit = new JMenuItem("Exit     ");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("in exit of draw");
				setVisible(false);
                                kth.omessage="exit";
                                kth.closecnx();
                                	
                                
				// above outs tryIPConnect while loop
				/*try { // Poll
					int j=Gmon_Main.thedelay;
					Thread.sleep(j);
				}
				catch (InterruptedException e) {}*/
				dispose();
			}
		});

		JMenu mnBounds = new JMenu("Bounds");
		mnMenu.add(mnBounds);
		
		JMenuItem mntmLpar = new JMenuItem("LPar");
		mntmLpar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doframe=0;
				tth.set_max();
			}
		});
		mnBounds.add(mntmLpar);
		
		JMenuItem mntmFrame = new JMenuItem("Frame");
		mntmFrame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doframe=1;
				tth.set_max();
			}
		});
		mnBounds.add(mntmFrame);
		mnMenu.add(mntmExit);
		Border loweredetched = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
                Border loweredbevel = BorderFactory.createLoweredBevelBorder();
                
                /*
                
                lblmem = new JLabel(" Mem: xxxx MB Used, yyyy MB Free, zzzz MB LPar");
		lblmem.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 12));
                lblmem.setOpaque(true);
                lblmem.setBorder(loweredbevel);
                
                lblmem2 = new JLabel(" Mem: xxxx MB Used, yyyy MB Free, zzzz MB LPar");
		lblmem2.setFont(new Font("Tahoma", Font.TRUETYPE_FONT, 12));
                lblmem2.setOpaque(true);
                lblmem2.setBorder(loweredbevel);
                
                
		contentPane.add(lblmem);
                if (Gmon_Main.animadv==1){
                    lblmem.setVisible(false);
                    lblmem2.setVisible(false);
                }
                
                */
		
                lblSmt = new JLabel("SMT=4");
		lblSmt.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblSmt);
                lblapp = new JLabel("Avail CPU");
		lblapp.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblapp);
                lbllpar = new JLabel("% LPar");
		lbllpar.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lbllpar);
                lblframe = new JLabel("% Frame");
		lblframe.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblframe);
                lblpspp = new JLabel("PSP %Used");
		lblpspp.setFont(new Font("Tahoma", Font.BOLD, 12));
		contentPane.add(lblpspp);
                
               
                
                
		lbluswi = new JLabel("U:S:W:I");
		lbluswi.setBorder(loweredbevel);
		lbluswi.setHorizontalAlignment(SwingConstants.CENTER);
		lbluswi.setFont(new Font("Tahoma", Font.PLAIN, 10));
		contentPane.add(lbluswi);
		
		lblmm = new JLabel("MM");
		lblmm.setBorder(loweredbevel);
		lblmm.setHorizontalAlignment(SwingConstants.CENTER);
		lblmm.setFont(new Font("Tahoma", Font.PLAIN, 10));
		contentPane.add(lblmm);
	
                lblaff = new JLabel("AFF");
		lblaff.setBorder(loweredbevel);
		lblaff.setHorizontalAlignment(SwingConstants.CENTER);
		lblaff.setFont(new Font("Tahoma", Font.PLAIN, 10));
		contentPane.add(lblaff);
                             
                lblpsp = new JLabel("Pg Sp");
		lblpsp.setBorder(loweredbevel);
		lblpsp.setHorizontalAlignment(SwingConstants.CENTER);
		lblpsp.setFont(new Font("Tahoma", Font.PLAIN, 10));
		contentPane.add(lblpsp);
                        
                // inject labels
                
                lblpspin = new JLabel("Pi: 0");
                lblpspin.setOpaque(true);
                lblpspin.setBorder(loweredbevel);
		lblpspin.setHorizontalAlignment(SwingConstants.CENTER);
		lblpspin.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblpspin);
                
                
                lblpspout = new JLabel("Po: 0");
		lblpspout.setOpaque(true);
                lblpspout.setBorder(loweredbevel);
		lblpspout.setHorizontalAlignment(SwingConstants.CENTER);
		lblpspout.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblpspout);
                
                lblpsptot = new JLabel("Psz: 0");
                lblpsptot.setBorder(loweredbevel);
		lblpsptot.setHorizontalAlignment(SwingConstants.CENTER);
		lblpsptot.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblpsptot);
                
                lblpspfree = new JLabel("Pfr: 0");
                lblpspfree.setOpaque(true);
		lblpspfree.setBorder(loweredbevel);
		lblpspfree.setHorizontalAlignment(SwingConstants.CENTER);
		lblpspfree.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblpspfree);
                
                lblvtot = new JLabel("Vtot: 0");
		lblvtot.setBorder(loweredbevel);
		lblvtot.setHorizontalAlignment(SwingConstants.CENTER);
		lblvtot.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblvtot);
                
                lblvact = new JLabel("Vact: 0");
                lblvact.setOpaque(true);
		lblvact.setBorder(loweredbevel);
		lblvact.setHorizontalAlignment(SwingConstants.CENTER);
		lblvact.setFont(new Font("Tahoma", Font.PLAIN, 12));
                lblvact.setForeground(Color.black);
                lblvact.setBackground(Color.LIGHT_GRAY);
                contentPane.add(lblvact);
                
                
                lblame=new JLabel("AME Off");
                lblame.setOpaque(true);
                lblame.setBorder(loweredbevel);
		lblame.setHorizontalAlignment(SwingConstants.CENTER);
		lblame.setFont(new Font("Tahoma", Font.PLAIN, 10));
                contentPane.add(lblame);
                
                lblamet=new JLabel("tgt: 0");
                lblamet.setBorder(loweredbevel);
		lblamet.setHorizontalAlignment(SwingConstants.CENTER);
		lblamet.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblamet);
                
                lblamea=new JLabel("act: 0");
                lblamea.setOpaque(true);
                lblamea.setBorder(loweredbevel);
		lblamea.setHorizontalAlignment(SwingConstants.CENTER);
		lblamea.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblamea);
                
                lblamed=new JLabel("def: 0");
                lblamed.setOpaque(true);
                lblamed.setBorder(loweredbevel);
		lblamed.setHorizontalAlignment(SwingConstants.CENTER);
		lblamed.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblamed);
                
                lblameci=new JLabel("ci+co: 0");
                lblameci.setOpaque(true);
                lblameci.setBorder(loweredbevel);
		lblameci.setHorizontalAlignment(SwingConstants.CENTER);
		lblameci.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblameci);
                
                /*
                lblameco=new JLabel("co: 0");
                lblameco.setOpaque(true);
                lblameco.setBorder(loweredbevel);
		lblameco.setHorizontalAlignment(SwingConstants.CENTER);
		lblameco.setFont(new Font("Tahoma", Font.PLAIN, 12));
                //contentPane.add(lblameco);
                */

                lblio=new JLabel("IO");
                lblio.setOpaque(true);
                lblio.setBorder(loweredbevel);
		lblio.setHorizontalAlignment(SwingConstants.CENTER);
		lblio.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblio);
                
                lblnr=new JLabel("Nr");
                lblnr.setOpaque(true);
                lblnr.setBorder(loweredbevel);
		lblnr.setHorizontalAlignment(SwingConstants.CENTER);
		lblnr.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblnr);
                
                lblnw=new JLabel("Nw");
                lblnw.setOpaque(true);
                lblnw.setBorder(loweredbevel);
		lblnw.setHorizontalAlignment(SwingConstants.CENTER);
		lblnw.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblnw);
                
                lbldxfers=new JLabel("dxfrs");
                lbldxfers.setOpaque(true);
                lbldxfers.setBorder(loweredbevel);
		lbldxfers.setHorizontalAlignment(SwingConstants.CENTER);
		lbldxfers.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lbldxfers);
                
                lbldr=new JLabel("dr");
                lbldr.setOpaque(true);
                lbldr.setBorder(loweredbevel);
		lbldr.setHorizontalAlignment(SwingConstants.CENTER);
		lbldr.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lbldr);
                
                lbldw=new JLabel("dw");
                lbldw.setOpaque(true);
                lbldw.setBorder(loweredbevel);
		lbldw.setHorizontalAlignment(SwingConstants.CENTER);
		lbldw.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lbldw);
                
                
                lblio2=new JLabel("IO 2");
                lblio2.setOpaque(true);
                lblio2.setBorder(loweredbevel);
		lblio2.setHorizontalAlignment(SwingConstants.CENTER);
		lblio2.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblio2);
                
                lblsq=new JLabel("sq");
                lblsq.setOpaque(true);
                lblsq.setBorder(loweredbevel);
		lblsq.setHorizontalAlignment(SwingConstants.CENTER);
		lblsq.setFont(new Font("Tahoma", Font.PLAIN, 11));
                contentPane.add(lblsq);
                
                
                lblrserv=new JLabel("rsv");
                lblrserv.setOpaque(true);
                lblrserv.setBorder(loweredbevel);
		lblrserv.setHorizontalAlignment(SwingConstants.CENTER);
		lblrserv.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblrserv);
                
                lblwserv=new JLabel("wsv");
                lblwserv.setOpaque(true);
                lblwserv.setBorder(loweredbevel);
		lblwserv.setHorizontalAlignment(SwingConstants.CENTER);
		lblwserv.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblwserv);
                
                lblsrfr=new JLabel("sr/fr");
                lblsrfr.setOpaque(true);
                lblsrfr.setBorder(loweredbevel);
		lblsrfr.setHorizontalAlignment(SwingConstants.CENTER);
		lblsrfr.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblsrfr);
                
                lblcycles=new JLabel("cycles");
                lblcycles.setOpaque(true);
                lblcycles.setBorder(loweredbevel);
		lblcycles.setHorizontalAlignment(SwingConstants.CENTER);
		lblcycles.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(lblcycles);
                
	
		lblWidth = new JLabel("Width");
		//contentPane.add(lblWidth);
		
		uswi = new JPanel();
		
                uswi.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		uswi.setBorder(loweredbevel);
                uswi.setBackground(Color.WHITE);
		uswi.setBounds(217, 359, 183, 40);
		//System.out.println("DrawLPar init 2 - Setting uswi Double Buff strategy");
		contentPane.add(uswi);
		uswi.setLayout(null);
		uswi.setDoubleBuffered(true);
		uswi.setVisible(true);
                
		mm = new JPanel();
		mm.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		mm.setBackground(Color.WHITE);
		//mm.setBounds(217, 359, 183, 40);
		//System.out.println("DrawLPar init 2.5 - Setting mm Double Buff strategy");
		//uswi2.createBufferStrategy(2);
		contentPane.add(mm);
		mm.setLayout(null);
		mm.setDoubleBuffered(true);	
		
		
                aff = new JPanel();
		aff.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		aff.setBackground(Color.WHITE);
		//aff.setBounds(217, 359, 183, 40);
		//System.out.println("DrawLPar init 2.6 - Setting aff Double Buff strategy");
		//uswi2.createBufferStrategy(2);
		contentPane.add(aff);
		aff.setLayout(null);
		aff.setDoubleBuffered(true);
                
                
                s3=new JLabel("Push=");
                s3.setOpaque(true);
                s3.setBorder(loweredbevel);
		s3.setHorizontalAlignment(SwingConstants.CENTER);
		s3.setFont(new Font("Tahoma", Font.PLAIN, 12));
                contentPane.add(s3);
                
                
                
            
                lbluswi.setVisible(true);   
    	
                contentPane.setVisible(true);
                Toolkit.getDefaultToolkit().sync();
                
                
                // should this be here ??
               // g1 = uswi.getGraphics();
                g3 = mm.getGraphics();
                //g5 = psp.getGraphics();
                g7 = aff.getGraphics(); 
                
        
                
            
		//System.out.println("DrawLPar init 4 - Entering set bounds");
		setmybounds();
		//setResizable(false);
                //System.out.println("Exiting DrawLPar init ....n");
		}
		catch(Exception myException){
                    System.out.println("Exception from drawLPar");
                    myException.printStackTrace();
		}
		
		
	}// end DrawLPar

private void setmybounds()
{
	int xi,i,ti,wi,dsw,dsw2,dsh,hdsh,winh,winw,ti1,ti2;
	double t,th,wh,td1,td2;
	
	if (!(menuBar==null)){
		winw=getWidth()-15;
		winh=getHeight();
		t=winw/5.0;
		dsw=(int)t;
		t=winh/12.5;
		dsh=(int)t+1;
		hdsh=(int)(t/2)+1;
		dsw2=dsw;
		th=winh*0.5;
		ti=(int)th;
		wh=winh*0.45;
		wi=(int)wh;
	
	        fxresize=1;
	        fxwidth=winw/2;
	        fxheight=(winh/2)-20;
	
		menuBar.setBounds(0, 0, winw, 20);
                
                lbllpar.setBounds((int)(fxwidth*1.15),(fxwidth/2)+1,70,30);
                lblframe.setBounds((int)(fxwidth*1.60),(fxwidth/2)+1,70,30);
                
		lblapp.setBounds((int)(fxwidth*1.10),fxwidth+6,70,30);
                lblpspp.setBounds((int)(fxwidth*1.55),fxwidth+6,75,30);
               
               
                ti=21+35+((winw+15)/2);
		separator.setBounds(0, ti, winw, 2);	
	
		ti1=(int)(winw/5.0)+2;
		ti2=(int)(winw*(4.0/5.0));
		/*
                lblmem.setBounds(2,ti-35,winw-5,hdsh);
                lblmem2.setBounds(2,ti-17,winw-5,hdsh);
                lblmem2.setVisible(true);
                */
                lblmm.setBounds(2,ti, dsw, hdsh);
		mm.setBounds(ti1, ti, ti2-6, hdsh);                
                
	
		ti=ti+hdsh+1;
                dsw=winw/4;
                
                /*
                
		ds1.setBounds(2,ti,dsw,dsh);
		//ds2.setBounds(2+(dsw2*1),ti,dsw,dsh);
		//ds3.setBounds(2+(dsw2*1),ti,dsw,dsh);
		ds4.setBounds(dsw,ti,dsw,dsh);
		ds5.setBounds(dsw*2,ti,dsw,dsh);
		ds6.setBounds(dsw*3,ti,dsw,dsh);
	        */
                
		//ti=ti+dsh+1;
                //lblmm.setBounds(2,ti, dsw, hdsh);
		//mm.setBounds(ti1, ti, ti2-4, hdsh);
		uswi.setBounds(ti1, ti, ti2-6, hdsh);
                uswi.setVisible(true);
		lbluswi.setBounds(2,ti, dsw, hdsh);
                
                
                
                
		ti=ti+hdsh+1;
		
                dsw=(winw/4)-1;
                lblio.setBounds(2,ti, dsw, hdsh);
                lbldxfers.setBounds(dsw,ti, dsw, hdsh);
                lbldr.setBounds(dsw*2,ti, dsw, hdsh);
                lbldw.setBounds(dsw*3,ti, dsw, hdsh);
                
                ti=ti+hdsh+1;
	
                dsw=(winw/4)-1;
                //lblio2.setBounds(2,ti, dsw, hdsh);    
                //lblwserv.setBounds(dsw*2,ti, dsw, hdsh);
              
                lblvtot.setBounds(2,ti,dsw,hdsh);
                lblvact.setBounds(dsw,ti,dsw,hdsh);
                lblsrfr.setBounds(dsw*2,ti, dsw, hdsh);
                lblcycles.setBounds(dsw*3,ti, dsw, hdsh);
                ti=ti+hdsh+1;
                //System.out.println("v ti="+i);
                dsw=(winw/4)-2;
                lblnr.setBounds(2,ti, dsw, hdsh);
                lblnw.setBounds((int)(winw*0.25),ti, dsw, hdsh);
                lblrserv.setBounds((int)(winw*0.5),ti, dsw, hdsh); // this is hpi or nw pkts/sec
                lblsq.setBounds((int)(winw*0.75),ti, dsw, hdsh);  // this is AMS
                ti=ti+hdsh+1;
	
                
                //psp.setBounds(2+dsw,ti,winw-dsw-3,hdsh);
                //psp.setBounds(2+dsw,ti,ti2-4,hdsh);
                
                
                i=(int)winh-(dsh*2)-20;
                dsw=(winw/5)-1;
                lblpsp.setBounds(2,ti,dsw-20,hdsh);
                lblpspin.setBounds(dsw-15,ti,dsw,hdsh);
                lblpspout.setBounds((dsw*2)-10,ti,dsw,hdsh);               
                lblpspfree.setBounds((dsw*3)-2,ti,dsw,hdsh);
                lblpsptot.setBounds((dsw*4)-5,ti,dsw,hdsh);
                //lblvtot.setBounds(dsw*2,i,dsw,hdsh);
                //lblvact.setBounds(dsw*3,i,dsw,hdsh);
               
                i=i+hdsh;
                ti=ti+hdsh+1;
                xi=2;
                lblame.setBounds(xi,ti,dsw-20,hdsh);
                lblamet.setBounds(dsw-15,ti,dsw-20,hdsh);
                lblamea.setBounds((dsw*2)-35,ti,dsw-20,hdsh);
                lblamed.setBounds((dsw*3)-50,ti,dsw+20,hdsh);
                lblameci.setBounds((dsw*4)-22,ti,dsw+20,hdsh);
                //lblameco.setBounds(dsw*5,i,dsw,hdsh);
         
                ti=ti+hdsh+1;
                
                //System.out.println("winh="+winh+" ti="+ti+" hdsh="+hdsh);
                aff.setBounds(2+dsw-20,ti+1,winw-(2*dsw)-4,hdsh+1);
                lblaff.setBounds(2,ti,dsw-20,hdsh+3);
		//lblWidth.setBounds(2,350,100,30);
		//lblWidth.setText("W="+winw+" H="+winh);
                s3.setBounds((4*dsw)-15,ti,dsw+14,hdsh+3);
                
                Toolkit.getDefaultToolkit().sync();
                
                g4 = (Graphics2D) g3;
                //g6 = (Graphics2D) g5;
                g8 = (Graphics2D) g7;
    

}
}
       

public void drawuswi(){
double u,s,w,i,tt,tu,ts,tw,ti,ddw;
//BufferStrategy bf = uswi.getBufferStrategy();

int dw,dh,iu,is,iw,ii,startx,starty,th,lbloff;
startx=0;
starty=0;
th=13;
lbloff=5;
dw=uswi.getWidth();
dh=uswi.getHeight();
dw=dw-(startx)-0;
ddw=(double)dw;
dh=dh-(starty*2);
//System.out.println("draw Width="+dw);

u=kth.user;
s=kth.sys;
w=kth.wio;
i=kth.idle;
//System.out.println("u s w i="+" "+u+" "+s+" "+w+" "+i);
tu=(u/100.0)*dw;
iu=(int)tu;

ts=(s/100.0)*dw;
is=(int)ts;

tw=(w/100.0)*dw;
iw=(int)tw;

ti=(i/100.0)*dw;
ii=(int)ti;

//System.out.println("iu is iw ii="+" "+iu+" "+is+" "+iw+" "+ii);
if (g2==null){
    g1=uswi.getGraphics();
    g2=(Graphics2D)g1;
}
try {
	//
        if (g2 != null) {
	g2.clearRect(startx, 0, dw, dh);
	//Toolkit.getDefaultToolkit().sync();
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	// It is assumed that mySprite is created somewhere else.
	// This is just an example for passing off the Graphics object.
	//mySprite.draw(g);
	g2.setColor(Color.blue);
	g2.fill(new Rectangle2D.Double(startx, starty, iu, dw));
	g2.setColor(Color.black);
	g2.drawRect(startx, starty, iu, dw);
	g2.setPaint(Color.lightGray);
	g2.drawString("User "+u+"%", startx+lbloff,th );
	
	g2.setColor(Color.red);
	g2.fill(new Rectangle2D.Double(startx+iu, starty, is, dw));
	g2.setColor(Color.black);
	g2.drawRect(startx+iu, starty, is, dw);
	g2.setPaint(Color.white);
	g2.drawString("Sys "+s+"%", startx+iu+lbloff,th);
	
	g2.setColor(Color.yellow);
	g2.fill(new Rectangle2D.Double(startx+iu+is, starty, iw, dw));
	g2.setColor(Color.black);
	g2.drawRect(startx+iu+is, starty, iw, dw);
	g2.setPaint(Color.darkGray);
	g2.drawString("WIO "+w+"%", startx+iu+is+lbloff,th );
	
	g2.setColor(Color.green);
	g2.fill(new Rectangle2D.Double(startx+iu+is+iw, starty, ii, dw));
	g2.setColor(Color.black);
	g2.drawRect(startx+iu+is+iw, starty, ii, dw);
	g2.setPaint(Color.darkGray);
	g2.drawString("Idle "+i+"%", startx+iu+is+iw+lbloff,th );
	
        }

} finally {
	// It is best to dispose() a Graphics object when done with it.
	//g2.dispose();
}

// Shows the contents of the backbuffer on the screen.
//g1.show();

    //Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
    //Drawing is done which looks very jerky
    Toolkit.getDefaultToolkit().sync();
	
	
}



/*
public void drawpsp(){
double u,s,w,i,tt,tu,ts,ddw,pu,ps;
//BufferStrategy bf = uswi.getBufferStrategy();

int dw,dh,iu,is,startx,starty,th;

String spu,sps;

DecimalFormat Currency = new DecimalFormat("#0.0%");

startx=0;
starty=0;
th=13;

dw=psp.getWidth();
dh=psp.getHeight();
dw=dw-(startx)-0;
ddw=(double)dw;
dh=dh-(starty*2);
//System.out.println("draw Width="+dw);
tt=kth.psptot;
s=kth.pspfree;
u=tt-s;
//System.out.println("u s="+" "+u+" "+s);
tu=(u/tt)*dw;
iu=(int)tu;

ts=(s/tt)*dw;
is=(int)ts;

pu=(u/tt);
ps=(s/tt);
spu= Currency.format(pu);
sps= Currency.format(ps);

//System.out.println("iu is ="+" "+iu+" "+is);
try {
	//
	g6.clearRect(startx, 0, dw, dh);
	//Toolkit.getDefaultToolkit().sync();
	g6.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	// It is assumed that mySprite is created somewhere else.
	// This is just an example for passing off the Graphics object.
	//mySprite.draw(g);
	g6.setColor(Color.red);
	g6.fill(new Rectangle2D.Double(startx, starty, iu, dw));
	g6.setColor(Color.black);
	g6.drawRect(startx, starty, iu, dw);
	g6.setPaint(Color.lightGray);
	g6.drawString("Used "+spu, startx+10,th );
	
	g6.setColor(Color.green);
	g6.fill(new Rectangle2D.Double(startx+iu, starty, is, dw));
	g6.setColor(Color.black);
	g6.drawRect(startx+iu, starty, is, dw);
	g6.setPaint(Color.black);
	g6.drawString("Free "+sps, startx+iu+10,th);
	

} finally {
	// It is best to dispose() a Graphics object when done with it.
	//g1.dispose();
}

// Shows the contents of the backbuffer on the screen.
//g1.show();

    //Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
    //Drawing is done which looks very jerky
    Toolkit.getDefaultToolkit().sync();
	
	
}
*/




public void drawmm(){
double u,s,w,i;
double tu,ts,tw,ti;
double ddw;
double tt;
double mtot,pu,ps,pw,pi;
//BufferStrategy bf = uswi.getBufferStrategy();
//DecimalFormat Currency = new DecimalFormat("#0.0%");
DecimalFormat Currency = new DecimalFormat("#0%");

int dw,dh,iu,is,iw,ii,startx,starty,th,lbloff;
startx=0;
starty=0;
th=13;
lbloff=5;

dw=mm.getWidth();
dh=mm.getHeight();
dw=dw-(startx)-0;
ddw=(double)dw;
dh=dh-(starty*2);
//System.out.println("draw Width="+dw);

u=kth.rsystem;
s=kth.rprocess;
w=kth.numperm;
i=kth.pmfree;
mtot=kth.pmtot;

//System.out.println("sys proc nump free mtot="+" "+u+" "+s+" "+w+" "+i+" "+mtot);
pu=(u/mtot);
ps=(s/mtot);
pw=(w/mtot);
pi=(i/mtot);

String spu= Currency.format(pu);
String sps= Currency.format(ps);
String spw= Currency.format(pw);
String spi= Currency.format(pi);

tu=(u/mtot)*dw;
iu=(int)tu;

ts=(s/mtot)*dw;
is=(int)ts;

tw=(w/mtot)*dw;
iw=(int)tw;

ti=(i/mtot)*dw;
ii=(int)ti;

//System.out.println("iu is iw ii="+" "+iu+" "+is+" "+iw+" "+ii);

if (g4==null){
    g3=mm.getGraphics();
    g4=(Graphics2D)g3;
}


try {
	//
        if (g4 != null) {
	g4.clearRect(startx, 0, dw, dh);
	g4.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	
	g4.setColor(Color.red);
	g4.fill(new Rectangle2D.Double(startx, starty, iu, dw));
	g4.setColor(Color.black);
	g4.drawRect(startx, starty, iu, dw);
	g4.setPaint(Color.lightGray);
	g4.drawString("Syst "+spu, startx+lbloff,th );
	
	g4.setColor(Color.CYAN);
	g4.fill(new Rectangle2D.Double(startx+iu, starty, is, dw));
	g4.setColor(Color.black);
	g4.drawRect(startx+iu, starty, is, dw);
	g4.setPaint(Color.black);
	g4.drawString("Proc "+sps, startx+iu+lbloff,th);
	
	g4.setColor(Color.yellow);
	g4.fill(new Rectangle2D.Double(startx+iu+is, starty, iw, dw));
	g4.setColor(Color.black);
	g4.drawRect(startx+iu+is, starty, iw, dw);
	g4.setPaint(Color.darkGray);
	g4.drawString("NumP "+spw, startx+iu+is+lbloff,th );
	
	g4.setColor(Color.green);
	g4.fill(new Rectangle2D.Double(startx+iu+is+iw, starty, ii, dw));
	g4.setColor(Color.black);
	g4.drawRect(startx+iu+is+iw, starty, ii, dw);
	g4.setPaint(Color.darkGray);
	g4.drawString("Free "+spi, startx+iu+is+iw+lbloff,th );
        }

} finally {
	// It is best to dispose() a Graphics object when done with it.
	//g1.dispose();
}

Toolkit.getDefaultToolkit().sync();
	
}



public void drawaff(){
double sd0,sd1,sd2,sd3,sd4,sd5,sdtot,p0,p1,p2,p3,p4,p5,t0,t1,t2,t3,t4,t5,ddw;
String p0s,p1s,p2s,p3s,p4s,p5s;

DecimalFormat Currency = new DecimalFormat("#0");

int dw,dh,i0,i1,i2,i3,i4,i5,startx,starty,th;
startx=0;
starty=0;
th=13;

dw=aff.getWidth();
dh=aff.getHeight();
dw=dw-(startx)-0;
ddw=(double)dw;
dh=dh-(starty*2);
//System.out.println("draw Width="+dw);

sd0=kth.sd0;
sd1=kth.sd1;
sd2=kth.sd2;
sd3=kth.sd3;
sd4=kth.sd4;
sd5=kth.sd5;

sdtot=sd0+sd1+sd2+sd3+sd4+sd5;

//System.out.println("sys proc nump free mtot="+" "+u+" "+s+" "+w+" "+i+" "+mtot);
p0=(sd0/sdtot);
p1=(sd1/sdtot);
p2=(sd2/sdtot);
p3=(sd3/sdtot);
p4=(sd4/sdtot);
p5=(sd5/sdtot);

t0=(p0)*dw;
i0=(int)t0;
t1=(p1)*dw;
i1=(int)t1;
t2=(p2)*dw;
i2=(int)t2;
t3=(p3)*dw;
i3=(int)t3;
t4=(p4)*dw;
i4=(int)t4;
t5=(p5)*dw;
i5=(int)t5;

//System.out.println("iu is iw ii="+" "+iu+" "+is+" "+iw+" "+ii);
//System.out.println("i0 i1 i2 i3="+" "+i0+" "+i1+" "+i2+" "+i3);
if (g8==null){
    g7=aff.getGraphics();
    g8=(Graphics2D)g7;
}


try {
	//
        if (g8 != null) {
	g8.clearRect(startx, 0, dw, dh);
	g8.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	        RenderingHints.VALUE_ANTIALIAS_ON);
	
	g8.setColor(Color.green);
	g8.fill(new Rectangle2D.Double(startx, starty, i0, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx, starty, i0, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+(int)sd0, startx+10,th );
	
	g8.setColor(Color.yellow);
	g8.fill(new Rectangle2D.Double(startx+i0, starty, i1, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx+i0, starty, i1, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+sd1, startx+i0+10,th);
	
	g8.setColor(Color.orange);
	g8.fill(new Rectangle2D.Double(startx+i0+i1, starty, i2, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx+i0+i1, starty, i2, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+sd2, startx+i0+i1+10,th );
	
	g8.setColor(Color.pink);
	g8.fill(new Rectangle2D.Double(startx+i0+i1+i2, starty, i3, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx+i0+i1+i2, starty, i3, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+sd3, startx+i0+i1+i2+10,th );
	
        g8.setColor(Color.red);
	g8.fill(new Rectangle2D.Double(startx+i0+i1+i2+i3, starty, i4, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx+i0+i1+i2+i3, starty, i4, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+sd4, startx+i0+i1+i2+i3+10,th );
        
        
        g8.setColor(Color.green);
	g8.fill(new Rectangle2D.Double(startx+i0+i1+i2+i3+i4, starty, i5, dw));
	g8.setColor(Color.black);
	g8.drawRect(startx+i0+i1+i2+i3+i4, starty, i5, dw);
	g8.setPaint(Color.darkGray);
	g8.drawString(" "+sd5, startx+i0+i1+i2+i3+i4+10,th );
        }
        
} finally {
	// It is best to dispose() a Graphics object when done with it.
	//g1.dispose();
}

Toolkit.getDefaultToolkit().sync();
	
} // end drawaff


public int getFxresize()
{
	return fxresize;
}

public void setFxresize(int i)
{
	fxresize=i;
}

public int getFxw()
{
	return getWidth()/2;
}

public int getFxh()
{
	return (getHeight()/2)-21;
}



}//end class
