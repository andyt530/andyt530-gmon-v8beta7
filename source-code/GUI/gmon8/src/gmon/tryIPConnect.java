package gmon;
import java.awt.Color;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ConnectException;
import java.net.SocketException;
import java.text.DecimalFormat;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;


public class tryIPConnect {
	
Socket requestSocket;
public InputStreamReader insr=null;
public BufferedReader in = null;
public PrintWriter out = null;
private String oldsubstr="nop";
private String newsubstr;
private String oldserial="nop";
private Thread kth;
String vers;
String myhead,imessage,omessage;
public DrawLPar mydraw;
public FXRadial myfx;
int s3i;        
double user,sys,wio,idle,ghz,rq,sq;
double totmem,framecpu,rsystem,ruser,rinuse,numperm,rprocess,pmfree,pmtot,pmem;
public double pspin,pspout,scans,cycles,steals,vtot,vactive;
public double pphys,vp,ec,pctec,wt,psptot,pspfree,psprsvd,maxmem,minmem;
public double ame_factor,ame_ci,ame_co,ameonline,ame_type,ame_target,ame_act,ame_deficit,ame_pool_size,ame_pool_free,ame_upool_size;
public double sd0,sd1,sd2,sd3,sd4,sd5,s3pull,s3push,s3grq;
public double mpool_mem,mpoolid,dxfers,dwblks,drblks,rserv,wserv,nibytes,nipacks,nobytes,nopacks,pavp,ppb;
String iscapped,shared,lparname,serial,lparid,smtmode;
public int gotconnect;    
	
	
public tryIPConnect(Thread tth){
if (Gmon_Main.debug > 0) System.out.println("tryConnect init");
int tport;
String thost;
gotconnect=0;
kth=tth;
try{
    //tport=Gmon_Main.dialog2.tport;
    //thost=Gmon_Main.dialog2.thost;
    
    tport=LParConnect.tport;
    thost=LParConnect.thost;
    
    //System.out.println("tryIP thost="+thost);
    
    
    
    //Creating a socket to connect to the server	
    requestSocket = new Socket(thost, tport);
    insr=new InputStreamReader(requestSocket.getInputStream());
    //2. get Input and Output streams
    //in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
    in = new BufferedReader(insr);
    out = new PrintWriter(requestSocket.getOutputStream(), true);
    if (Gmon_Main.debug > 0) System.out.println("Connected to " + thost + " on port " + tport);
    gotconnect=1;
    
    if (Gmon_Main.debug > 0) System.out.println("Exiting tryIPConnect Constructor ");
}
catch(UnknownHostException unknownHost){
	System.err.println("Unknown host!");
         JOptionPane.showMessageDialog(mydraw, "Unknown host!", "Attention!", JOptionPane.WARNING_MESSAGE);
}

catch(ConnectException ConnectionRefused) {
        System.err.println("Connection refused, check agent is running");
         JOptionPane.showMessageDialog(MainLoop.frame, "Connection Refused, check agent is running", "Attention!", JOptionPane.WARNING_MESSAGE);
}

catch(IOException ioException){
	ioException.printStackTrace();
        System.out.println("Above from tryIPConnect catch");
        JOptionPane.showMessageDialog(mydraw, "Exception from tryIPConnect catch", "Exception!", JOptionPane.WARNING_MESSAGE);
}

}
public void hi(){
	int j,tc;
        char tcc;
        int docrdraws=0;
        StringBuffer buffer = new StringBuffer();
	if (Gmon_Main.debug > 0) System.out.println("Starting tryIPConnect hi");
	j=Gmon_Main.thedelay;
        s3i=1;
	omessage="ack";
        // Enter main loop
	do{
            try{
		// Start up processing
                do
                    {
                    imessage=null;
                    imessage = in.readLine();
                    /*                 
                    while (true) {
                        int ch = insr.read();
    
                        if ((ch < 0) || (ch == '\n')) {
                        break;
                        }
                        buffer.append((char) ch);
                    }
                    imessage = buffer.toString();
                    */  
                    
                    imessage=imessage.trim();
                    if (Gmon_Main.debug>8) System.out.println("Raw from Server> " + imessage);
                    
                    if (imessage.length()<3) {
			myhead="nop";
				}
                    else {
			myhead=imessage.substring(0,3);
                     }
                    /*
                    if (imessage.contains("NaN")){
                         System.out.println("server> " + imessage);
                    }
                    */
                    //System.out.println(myhead);
					
                    }   while(!myhead.equals("AIX"));  // end do
		
                // should now have a good valid connection
                // so create draw objects first time around
                
                
                if (docrdraws==0){
                    if (Gmon_Main.debug > 0) System.out.println("in docrdraws in hi");
 
                    if (Gmon_Main.debug > 0) System.out.println("new Draw ");
                     mydraw=new DrawLPar(this);
                    if (Gmon_Main.debug > 0) System.out.println("Have new mydraw "); 	
                    //System.out.println("new FXRadial ");	
                    myfx=new FXRadial(this);
                    //myfx=new FXRadial(mydraw);
                    if (Gmon_Main.debug > 0) System.out.println("FXRadial Done");
    
                    mydraw.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                    
                    mydraw.addWindowListener(new WindowAdapter() {
                            public void windowClosing(WindowEvent we) {
                            //JOptionPane.showMessageDialog(mydraw, "my message!", "Attention!", JOptionPane.WARNING_MESSAGE);
                            closecnx();
                            }
                        });
                    
                    mydraw.setVisible(true);
                    docrdraws=1;
                }
                
                if (Gmon_Main.debug>1) System.out.println("server> " + imessage);
                sendMessage(omessage);
		// PARSE
		parseit(imessage);
		// SET mydraw values ?
		drawit();
		//snooze delay		
		try { // Poll
			j=Gmon_Main.thedelay;
			Thread.sleep(j);
                    }
		catch (InterruptedException e) {
			omessage="exit";
			sendMessage(omessage);
			}// end catch
		}//end try 
	// main catch	
        catch(SocketException ioSock){
           JOptionPane.showMessageDialog(mydraw, "Socket Exception", "Attention!", JOptionPane.WARNING_MESSAGE);
           omessage="exit";
           closecnx();
        }
            
            
        catch(IOException ioException){
		ioException.printStackTrace();
	} // end catch
	
	} while(!omessage.equals("exit"));  // end main loop
if (Gmon_Main.debug > 0) System.out.println("Got an EXIT");
} // end hi

void sendMessage(String msg)
{
		out.print(msg);
		out.flush();
		//System.out.println("client>" + msg);
}


public void closecnx(){
    int fi;
    String ts;
	//4: Closing connection
	try{
		if (Gmon_Main.debug > 0) System.out.println("Closing socket");
		in.close();
		out.close();
		requestSocket.close();
		if (Gmon_Main.debug > 0) System.out.println("Destoying Widgets");
                if (Gmon_Main.debug > 0) System.out.println("removelpar"+serial+" "+lparname);
                fi=MainLoop.map.get(serial);                                                                  
                ts=serial+"++"+lparname;
                if (lparname !=null) MainLoop.removelpar.put(ts, 1);
                
                myfx=null;
		mydraw.dispose();
                
                //mydraw=null;
                lparname=null;
                kth.interrupt();
                
	}
	catch(IOException ioException){
                System.out.println("From closecnx");
		ioException.printStackTrace();
	}
} //end try


private void parseit(String tim){
String im;
im=tim;
int count=0;
char separator=',';
//Dertermine number of substrings
int index=0;
do
{
	++count;
	++index;
	index=im.indexOf(separator,index);
} while (index != -1);

String[] substr=new String[count];
index=0;
int endIndex=0;
for (int i=0;i<count;i++){
	endIndex=im.indexOf(separator,index);
	if (endIndex==-1)
		substr[i]=im.substring(index);
	else
		substr[i]=im.substring(index,endIndex);
	
	index=endIndex+1;
}// end for
/*
//Display substrings
for (int i=0;i<substr.length;i++){
System.out.println(i+" = "+substr[i]);
}*/

//pctfr=(nc.pphys/nc.framecpu)*100.0;

vers=substr[0];
shared=substr[1];
user=Double.parseDouble(substr[2]);
sys=Double.parseDouble(substr[3]);
wio=Double.parseDouble(substr[4]);
idle=Double.parseDouble(substr[5]);
pphys=Double.parseDouble(substr[6]);
//

pctec=Double.parseDouble(substr[7]);
ppb=Double.parseDouble(substr[8]);
pavp=Double.parseDouble(substr[9]);
pavp=pavp/2.0;
framecpu=Double.parseDouble(substr[10]);
//
//System.out.println("sub6="+substr[6]+" sub10="+substr[10]);

iscapped=substr[11];
wt=Double.parseDouble(substr[12]);
vp=Double.parseDouble(substr[13]);
ec=Double.parseDouble(substr[14]);
smtmode=substr[15];
ec=ec/100.0;
lparname=substr[16];
if (lparname.length()>16) lparname=lparname.substring(0,16);

lparid=substr[17];
totmem=Double.parseDouble(substr[18]);
pmtot=totmem;
pmfree=Double.parseDouble(substr[19]);
pspin=Double.parseDouble(substr[20]);
pspout=Double.parseDouble(substr[21]);
scans=Double.parseDouble(substr[22]);
cycles=Double.parseDouble(substr[23]);
steals=Double.parseDouble(substr[24]);
serial=substr[25];
//26 reconfig
//27 drtype        '
// 28 frame_mem (not)
rinuse=Double.parseDouble(substr[29]);
rsystem=Double.parseDouble(substr[30]);
ruser=Double.parseDouble(substr[31]);
rprocess=Double.parseDouble(substr[32]);
numperm=Double.parseDouble(substr[33]);
psptot=Double.parseDouble(substr[34]);
pspfree=Double.parseDouble(substr[35]);
psprsvd=Double.parseDouble(substr[36]);
vtot=Double.parseDouble(substr[37]);
vactive=Double.parseDouble(substr[38]);
maxmem=Double.parseDouble(substr[39]);
minmem=Double.parseDouble(substr[40]);
dxfers=Double.parseDouble(substr[41])/(Gmon_Main.thedelay/1000);
dwblks=Double.parseDouble(substr[42])/((Gmon_Main.thedelay/1000)*2);
drblks=Double.parseDouble(substr[43])/((Gmon_Main.thedelay/1000)*2);
rserv=Double.parseDouble(substr[44]);
nibytes=Double.parseDouble(substr[45])/((Gmon_Main.thedelay/1000)*1024*1024);
wserv=Double.parseDouble(substr[46]);
nobytes=Double.parseDouble(substr[47])/((Gmon_Main.thedelay/1000)*1024*1024);
rq=Double.parseDouble(substr[48]);
sq=Double.parseDouble(substr[49]);  // sq is actually hpi
ghz=Double.parseDouble(substr[50]);
ghz=ghz/1000000000.0;
pmem=Double.parseDouble(substr[51]);
mpoolid=Double.parseDouble(substr[52]);
mpool_mem=Double.parseDouble(substr[53]);
ameonline=Double.parseDouble(substr[54]);
ame_factor=Double.parseDouble(substr[55]);
ame_type=Double.parseDouble(substr[56]);
ame_target=Double.parseDouble(substr[57]);
ame_act=Double.parseDouble(substr[58]);
ame_deficit=Double.parseDouble(substr[59]);
ame_pool_size=Double.parseDouble(substr[60]);
ame_pool_free=Double.parseDouble(substr[61]);
ame_upool_size=Double.parseDouble(substr[62]);
ame_ci=Double.parseDouble(substr[63]);
ame_co=Double.parseDouble(substr[64]);

if (!vers.matches("AIX613")) {
sd0=Double.parseDouble(substr[65]);
sd1=Double.parseDouble(substr[66]);
sd2=Double.parseDouble(substr[67]);
sd3=Double.parseDouble(substr[68]);
sd4=Double.parseDouble(substr[69]);
sd5=Double.parseDouble(substr[70]);
s3pull=Double.parseDouble(substr[71]);
s3push=Double.parseDouble(substr[72]);
s3grq=Double.parseDouble(substr[73]);
}
else
{
sd0=0;
sd1=0;
sd2=0;
sd3=0;
sd4=0;
sd5=0;
s3pull=0;
s3push=0;
s3grq=0;
//System.out.println("vers: "+vers);
}


//System.out.println("rserv: "+rserv);
//System.out.println("wserv: "+wserv);
//        iscapped +   wt +       vp +       ec +   smtmode +   lparid +    serial +    gz +      pmem
newsubstr=substr[11]+substr[12]+substr[13]+substr[14]+substr[15]+substr[17]+substr[25]+substr[50]+substr[51];

}//end parseit


private void drawit(){
//mydraw
String ts;	
	
	if (!(newsubstr.matches(oldsubstr))){
		

            //System.out.println("Calling draw "+newsubstr+oldsubstr);
            if (!(oldserial.matches("nop"))){
                if (!(oldserial.matches(serial))){
                    ts=oldserial+"++"+lparname;
                    //System.out.println("Calling removelpar from tryIPConnect ts="+ts);
                    if (lparname !=null) MainLoop.removelpar.put(ts, 1);      
                }
            }
            set_max();
	}
	set_pphs(pphys);
        
	mydraw.drawuswi();
        //mydraw.drawpsp();
	mydraw.drawmm();
        mydraw.drawaff();
        oldserial=serial;
	oldsubstr=newsubstr;

}


public void set_pphs(double pphys){	
	//radial1.setValue(pphys
	float myred;
	double pctfr,um,memused;
        
        DecimalFormat myn = new DecimalFormat("#0.0");
        DecimalFormat myd = new DecimalFormat("#0");
        try{
	
	//pctec=kth.pctec;
	//rq=kth.rq;
        pctfr=(pphys/framecpu)*100.0;
	/*
	//mydraw.pctlpar.setValueAnimated(pctec);
	mydraw.ds3.setLcdValue(pctec);
        //mydraw.ds2.setLcdValue(pavp);
        //mydraw.lblapp.setText(""+pavp);
	
	if (pctec <= 85)
		mydraw.ds3.setLcdColor(LcdColor.WHITE_LCD);
	if (pctec > 85)
		mydraw.ds3.setLcdColor(LcdColor.YELLOW_LCD);
	if (pctec >100)
		mydraw.ds3.setLcdColor(LcdColor.ORANGE_LCD);
	if (pctec >200)
		mydraw.ds3.setLcdColor(LcdColor.RED_LCD);
        */
	
        //mydraw.ds6.setLcdValue(rq);
	//mydraw.pctframe.setValueAnimated(pctfr);
	
      
      
        mydraw.lblpspin.setText("pi="+myd.format(pspin));
        mydraw.lblpspout.setText("po="+myd.format(pspout));
        
        mydraw.lblpspout.setBackground(Color.green);
        if (pspout>10) {
           mydraw.lblpspout.setBackground(Color.yellow);
        }
        if (pspout>40) {
           mydraw.lblpspout.setBackground(Color.orange);
        }
        if (pspout>80) {
           mydraw.lblpspout.setBackground(Color.red);
        }
        
        mydraw.lblpspin.setBackground(Color.green);
        if (pspin>10) {
           mydraw.lblpspin.setBackground(Color.yellow);
        }
        if (pspin>40) {
           mydraw.lblpspin.setBackground(Color.orange);
        }
        if (pspin>80) {
           mydraw.lblpspin.setBackground(Color.red);
        }
        
        
        mydraw.lblpsptot.setText("Psz="+myd.format(psptot));
        mydraw.lblpspfree.setText("Pfr="+myd.format(pspfree));
        mydraw.lblpspfree.setBackground(Color.green);
        
        
        if ((pspfree/psptot) < 0.5) {
            mydraw.lblpspfree.setBackground(Color.yellow);
        }
        if ((pspfree/psptot) < 0.33) {
            mydraw.lblpspfree.setBackground(Color.orange);
        }
        if ((pspfree/psptot) < 0.2) {
            mydraw.lblpspfree.setBackground(Color.red);
        }
        
        
        
        mydraw.lblvtot.setText("Vtot="+myd.format(vtot));
        mydraw.lblvact.setText("Vact="+myd.format(vactive));
    
        mydraw.lblvact.setBackground(Color.green);
        
        if (vactive/pmtot > 0.6) {
            mydraw.lblvact.setBackground(Color.yellow);
        }
        if (vactive/pmtot > 0.75) {
            mydraw.lblvact.setBackground(Color.orange);
        }
        
        if (vactive/pmtot > 0.9) {
            mydraw.lblvact.setBackground(Color.red);
        }
        
            
        
        
        mydraw.lblamet.setText("T="+ame_target);
        mydraw.lblamea.setText("A="+ame_act);
        mydraw.lblamea.setBackground(Color.green);
        if (ame_act != ame_target) {
            mydraw.lblamea.setBackground(Color.red);
        }
        
        mydraw.lblamed.setText("Df="+myd.format(ame_deficit)+" MB");
        mydraw.lblamed.setBackground(Color.green);
        if (ame_deficit>0){
            mydraw.lblamed.setBackground(Color.red);
        }
        
        mydraw.lblameci.setText("cio="+myd.format((ame_ci+ame_co)));
        //mydraw.lblameco.setText("co: "+ame_co);
        mydraw.lblameci.setBackground(Color.green);
        if (ame_ci+ame_co>10){
            mydraw.lblameci.setBackground(Color.yellow);
        }
        if (ame_ci+ame_co>100){
            mydraw.lblameci.setBackground(Color.orange);
        }
        if (ame_ci+ame_co>250){
            mydraw.lblameci.setBackground(Color.red);
        }
        
        
        mydraw.lbldr.setText("Dr="+myd.format(drblks/1024));
        mydraw.lbldw.setText("Dw="+myd.format(dwblks/1024));
        mydraw.lbldxfers.setText("Dxfs="+myd.format(dxfers));
        
        if (drblks/1024<=100){
            mydraw.lbldr.setBackground(Color.green);
        }
        if  (drblks/1024 > 100){
            mydraw.lbldr.setBackground(Color.yellow);
        }
        if (drblks/1024 > 400) {
            mydraw.lbldr.setBackground(Color.orange);
        }    
        if (drblks/1024 > 800) {
            mydraw.lbldr.setBackground(Color.red);
        }    
        
        if (dwblks/1024<=100){
            mydraw.lbldw.setBackground(Color.green);
        }
        if  (dwblks/1024 > 100){
            mydraw.lbldw.setBackground(Color.yellow);
        }
        if (dwblks/1024 > 400) {
            mydraw.lbldw.setBackground(Color.orange);
        }    
        if (dwblks/1024 > 800) {
            mydraw.lbldw.setBackground(Color.red);
        }    
        
        if (dxfers<=1000){
            mydraw.lbldxfers.setBackground(Color.green);
        }
        if (dxfers>1000) {
            mydraw.lbldxfers.setBackground(Color.yellow);
        }
        if (dxfers>2000) {
            mydraw.lbldxfers.setBackground(Color.orange);
        }    
        if (dxfers>4000) {
            mydraw.lbldxfers.setBackground(Color.red);
        }    
        
        
        mydraw.lblnr.setText("Nr="+myd.format(nibytes));
        mydraw.lblnr.setBackground(Color.green);
        if (nibytes>32) {
            mydraw.lblnr.setBackground(Color.yellow);
        }
        if (nibytes>100) {
            mydraw.lblnr.setBackground(Color.orange);
        }
        if (nibytes>700) {
            mydraw.lblnr.setBackground(Color.red);
        }
        
                
        
        mydraw.lblnw.setText("Nw="+myd.format(nobytes));
        mydraw.lblnw.setBackground(Color.green); 
        if (nobytes>32) {
            mydraw.lblnw.setBackground(Color.yellow);
        }
        if (nobytes>100) {
            mydraw.lblnw.setBackground(Color.orange);
        }
        if (nobytes>700) {
            mydraw.lblnw.setBackground(Color.red);
        }
        
        mydraw.lblsq.setBackground(Color.green);
        if (mpool_mem>0){
            mydraw.lblsq.setText("AMS:"+myn.format(mpool_mem/1024)+"GB");
        
            if ((pmem/mpool_mem) > 0.2) mydraw.lblsq.setBackground(Color.yellow);    
        
            if ((pmem/mpool_mem) > 0.4) mydraw.lblsq.setBackground(Color.orange);    
        
            if ((pmem/mpool_mem) > 0.6) mydraw.lblsq.setBackground(Color.red);    
            mydraw.lblrserv.setText("hpi="+myd.format(sq));  // sq is actuall hpi
            mydraw.lblrserv.setBackground(Color.green);
            if (sq>2) mydraw.lblrserv.setBackground(Color.yellow);
            if (sq>32) mydraw.lblrserv.setBackground(Color.orange);
            if (sq>100) mydraw.lblrserv.setBackground(Color.red);
            
            
        }
        else
        {
            mydraw.lblsq.setText("AMS Off");  
            mydraw.lblrserv.setText("np="+myd.format(rserv+wserv));  // sq is actuall hpi
            mydraw.lblrserv.setBackground(Color.green);
            if (rserv+wserv>10000) mydraw.lblrserv.setBackground(Color.yellow);
            if (rserv+wserv>40000) mydraw.lblrserv.setBackground(Color.orange);
            if (rserv+wserv>70000) mydraw.lblrserv.setBackground(Color.red);      
        }
        
        
        
        mydraw.lblwserv.setText("Bck="+myd.format(pmem));
        mydraw.lblsrfr.setBackground(Color.green);
        if (steals>0) {
        mydraw.lblsrfr.setText("sr/fr="+ myd.format(scans/steals));
            
        if ((scans/steals) > 3) {
            mydraw.lblsrfr.setBackground(Color.yellow);
        }
        
        if ((scans/steals) > 6) {
            mydraw.lblsrfr.setBackground(Color.orange);
        }       
        if ((scans/steals) > 9) {
            mydraw.lblsrfr.setBackground(Color.red);
        }
        
        } 
        else
        {
          mydraw.lblsrfr.setText("sr/fr=0");  
          
        }
        
        mydraw.lblcycles.setText("cyc="+myd.format(cycles));
        mydraw.lblcycles.setBackground(Color.green);
        if (cycles>0) {
            mydraw.lblcycles.setBackground(Color.yellow);
        }
        if (cycles>2) {
            mydraw.lblcycles.setBackground(Color.orange);
        }
        
        if (cycles>4) {
            mydraw.lblcycles.setBackground(Color.red);
        }
        
        
        
        
        
        mydraw.s3.setBackground(Color.green);
        if (s3i==1) mydraw.s3.setText("push="+myd.format(s3push));
        if (s3push>2) mydraw.s3.setBackground(Color.yellow);
        if (s3i==2) mydraw.s3.setText("pull="+myd.format(s3pull));
        if (s3pull>2) mydraw.s3.setBackground(Color.yellow);
        if (s3i==3) mydraw.s3.setText("grq="+myd.format(s3grq));
        if (s3grq>2) mydraw.s3.setBackground(Color.yellow);
        s3i++;
        if (s3i>3) s3i=1; 
	//myred=(float)(kth.pctec/1000.0);
	//if (myred<0.8f)
	//	myred=0.8f;
	//System.out.println(myred);
	//radial1.setGlowColor(new java.awt.Color(myred, 0.0f, 0.0f, 0.0f));	
        }
	catch(Exception ioException){
		ioException.printStackTrace(); 
  		}

}



public void set_max(){	
   
  try {
	mydraw.setTitle(serial +" "+ lparname+ " (" + lparid + ")");
        
	//mydraw.ds2.setLcdValue(pavp);
	//mydraw.ds4.setLcdValue(wt);
	//mydraw.ds5.setLcdValue(ghz);
	mydraw.lblSmt.setText(smtmode);
        if (ame_target == 0) {
            mydraw.lblame.setText("AME Off");
            mydraw.lblame.setBackground(Color.green);
        }
        else {
            mydraw.lblame.setText("AME On");
        }
        
        if (mpool_mem==0) mydraw.lblsq.setText("AMS Off");
        
        //mydraw.lblapp.setText(""+pavp);
/*
	if (vp < 2*ec)
		mydraw.ds1.setLcdColor(LcdColor.WHITE_LCD);
	if (vp > 2*ec)
		mydraw.ds1.setLcdColor(LcdColor.YELLOW_LCD);
	if (vp >4*ec)
		mydraw.ds1.setLcdColor(LcdColor.ORANGE_LCD);
	if (vp> 5*ec)
		mydraw.ds1.setLcdColor(LcdColor.RED_LCD);
	
  */
   //mydraw.ds1.setLcdValue(vp);
        //mydraw.setResizable(false);
        }
        catch(Exception ioException){
		ioException.printStackTrace(); 
  		}
}

}// end class

