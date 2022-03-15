package gmon;

import java.awt.Toolkit;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

import jfxtras.labs.scene.control.gauge.ColorDef;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Linear;
import jfxtras.labs.scene.control.gauge.LinearScale;
import jfxtras.labs.scene.control.gauge.Gauge.Trend;
import jfxtras.labs.scene.control.gauge.GaugeBuilder;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.LedColor;
import jfxtras.labs.scene.control.gauge.Marker;
import jfxtras.labs.scene.control.gauge.Radial;
import jfxtras.labs.scene.control.gauge.RadialHalfN;
import jfxtras.labs.scene.control.gauge.RadialQuarterN;
import jfxtras.labs.scene.control.gauge.Section;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.FontWeight;
import javax.swing.BorderFactory;
import jfxtras.labs.scene.control.gauge.DotMatrixSegment;
import jfxtras.labs.scene.control.gauge.SimpleGauge;
import jfxtras.labs.scene.control.gauge.SimpleGaugeBuilder;



public class FXRadial {
	public Radial fxradial1;
        public Linear pctframe,pctlpar;
        public Label lparlabel,caplab,eclab,vplab,ghzlab,wtlab,rqlab,smtlab;
        public Text captxt,vptxt;
        public SimpleGauge lpargauge,framegauge,appgauge,pspgauge;
	public JFrame frame;
	public JFXPanel myfxp,mydotm,mylcd1;
	int j;
	public tryIPConnect fxnc;
	public double vp;
	public double fxw,fxh,dsh;
	public int ifxw,ifxh,idsh,doresize=0;
	Marker[] markers1;
	Section[] sections1;
	Section[] areas1;
	String newstring;
	String oldstring="nop";
        String word1,word2,word3,word4,wordall;
        int fxtr;
        
        private List<DotMatrixSegment> segments1    = new ArrayList<DotMatrixSegment>(50);
        private List<DotMatrixSegment> segments2    = new ArrayList<DotMatrixSegment>(50);
        private List<DotMatrixSegment> segments3    = new ArrayList<DotMatrixSegment>(50);
        private List<DotMatrixSegment> segments4    = new ArrayList<DotMatrixSegment>(50);
        
	public FXRadial(final tryIPConnect nc) {
	//public FXRadial(final JFrame frame) {
		vp=nc.vp;
		markers1 = new Marker[2];
		sections1 = new Section[2];
		areas1 = new Section[2];
		fxtr=1;
                SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {				
                            fxtr=2;
                            initAndShowGUI(nc);
                            fxtr=99;
                            //System.out.println("Finished with invoke later");
                        }// end run	
		}); // end runnable
		
	}// end initiator
	
	private void initAndShowGUI(final tryIPConnect nc) {
	
		try{
		//nc.mydraw.lblSmt.setText("Yo");
		// This method is invoked on the EDT thread
		fxtr=3;
                if (Gmon_Main.debug > 0) System.out.println("Getting new fxPanel - initAndShow");
                final JFXPanel fxPanel = new JFXPanel();
                fxtr=4;
                final JFXPanel fxDotmem = new JFXPanel();
		if (Gmon_Main.debug > 0) System.out.println("Got new fxPanel - initAndShow");
                myfxp=fxPanel;
                mydotm=fxDotmem;
                fxtr=5;
		//nc.mydraw - 
                if (Gmon_Main.debug > 0) System.out.println("fxpanel .add to mydraw");
		nc.mydraw.add(fxPanel);	
                nc.mydraw.add(fxDotmem);
                if (Gmon_Main.debug > 0) System.out.println("fxpanel added to mydraw");
		fxtr=6;
                fxw=nc.mydraw.getWidth()/2.0;
		fxh=nc.mydraw.getHeight()/2.0;
                idsh=34;
		ifxw=(int)fxw*2;
		ifxh=(int)fxw+2;
		
                fxPanel.setBackground(java.awt.Color.red);
                fxDotmem.setBackground(java.awt.Color.white);
                
		fxPanel.setBounds(0, 21, ifxw,ifxh );
		fxDotmem.setBounds(0, ifxh, ifxw*2,idsh);
                fxtr=7;
                Thread.sleep(2000);
	    //frame.add(fxPanel);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {	
			        
                                fxtr=8;
                                initFX(fxPanel);
                                fxtr=50;
                                
                                initFXdotmem(fxDotmem);
                                fxtr=80;
                        }
		});
		
		
		//put this in loop - gui never updates
		
		// separate non-FX thread
                fxtr=81;
            Thread.sleep(ifxw);
            new Thread() {
            // runnable for that thread
            public void run() {
		do{
            	try {
                    // imitating work
            		j=Gmon_Main.thedelay;
            		j=j-300;
                        if (Gmon_Main.debug>1) System.out.println("in sleep FX Radial "+nc.lparname+" - "+nc.pphys);
			Thread.sleep(j);
                        } 
                        catch (InterruptedException ex) {
                            System.out.println("Exception from FXRadial sleep loop fxtr="+fxtr);
                            ex.printStackTrace();
                        }         	            	
		
                    Platform.runLater(new Runnable() {
				@Override
				public void run() {	
                                        if (Gmon_Main.debug>1) System.out.println("In FX loop....");
                                        fxtr=90;
                                       set_max(nc);
                                       fxtr=95;
                                       set_pphs(nc);						
                                       //if (doresize==1)
                                       //dorz(nc.mydraw);
                                    } // end run
			    }); // Runable
		     }while(nc.omessage.matches("ack"));
                if (Gmon_Main.debug > 0) System.out.println("out of FXRadial while loop");
                
                //this.interrupt();
               
            } // end run()
        }.start(); // end new thread()
			
	
        }
	catch(Exception myException){
	    System.out.println("Exception from FXRadial sleep loop fxtr="+fxtr);
            myException.printStackTrace();
            //activeThreads.remove(this);
            
	}//end try
        if (Gmon_Main.debug > 1) System.out.println("FX - end initAndShow");        
}// end initandshow
		

  
       
	
private void initFX(JFXPanel fxPanel) {
// This method is invoked on the JavaFX thread
	//fxPanel.setBounds(2, 20, 200, 200);
	Scene scene = createScene();
	fxPanel.setScene(scene);
	fxPanel.setVisible(true);
        fxPanel.setOpaque(true);
        //fxPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.red));
        
	//fxradial1.setPrefSize(fxPanel.getWidth(), fxPanel.getHeight());
}

private void initFXdotmem(JFXPanel fxPanel) {
// This method is invoked on the JavaFX thread
	//fxPanel.setBounds(2, 20, 200, 200);
	Scene scene = createScenedotmem();
	fxPanel.setScene(scene);
	fxPanel.setVisible(true);
        fxPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.blue));
	//fxradial1.setPrefSize(fxPanel.getWidth(), fxPanel.getHeight());
}


private Scene createScene() {
    
	Group root = new Group();
	Scene scene = new Scene(root, Color.TRANSPARENT);
	if (Gmon_Main.debug > 0) System.out.println("In FX CreateScene");
	try {
	StyleModel STYLE_MODEL_1 = StyleModelBuilder.create()
			.backgroundDesign(Gauge.BackgroundDesign.WHITE)
			.frameDesign(Gauge.FrameDesign.SHINY_METAL)
			.tickLabelOrientation(Gauge.TicklabelOrientation.HORIZONTAL)
			.pointerType(Gauge.PointerType.TYPE14)
			.thresholdVisible(true)
			.titleVisible(false)
			.unitVisible(false)
			.markersVisible(false)
			.areasVisible(false)
                        .sectionsVisible(false)
			.trendVisible(false)
			.trendUpColor(Color.RED)
			.lcdDesign(LcdDesign.STANDARD_GREEN)
			.build();
        
        StyleModel STYLE_MODEL_2 = StyleModelBuilder.create()
			.backgroundDesign(Gauge.BackgroundDesign.WHITE)
			.frameDesign(Gauge.FrameDesign.CHROME)
                        .frameVisible(false)
			.tickLabelOrientation(Gauge.TicklabelOrientation.HORIZONTAL)
			.pointerType(Gauge.PointerType.TYPE14)
			.thresholdVisible(true)
			.titleVisible(false)
			.unitVisible(false)
                        .sectionsVisible(true)
			.markersVisible(false)
			.areasVisible(false)
			.sectionsVisible(false)
			.trendVisible(false)
			.trendUpColor(Color.RED)
			.lcdDesign(LcdDesign.STANDARD_GREEN)
			.build();
        
        lpargauge = SimpleGaugeBuilder.create()
                .minValue(0)
                .maxValue(100)
                .valueLabelFontSize(14)
                .type(SimpleGaugeBuilder.GaugeType.SIMPLE_RADIAL_GAUGE)
                .unitFontSize(8)
                .maxLabelVisible(false)
                .minLabelVisible(false)
                .noOfDecimals(0)
                .barFrameColor(Color.DARKSLATEBLUE)
                .minMaxLabelFontSize(8)
                .roundedBar(true)
                .unitLabelVisible(false)
                .barColor(Color.GREEN)
                .barBackgroundColor(Color.WHITE)
                .barWidth(10)
                    .build();
        
        framegauge = SimpleGaugeBuilder.create()
                .minValue(0)
                .maxValue(100)
                .type(SimpleGaugeBuilder.GaugeType.SIMPLE_RADIAL_GAUGE)
                .valueLabelFontSize(14)
                .unitFontSize(10)
                .maxLabelVisible(false)
                .minLabelVisible(false)
                .barFrameColor(Color.DARKSLATEBLUE)
                .noOfDecimals(0)
                .minMaxLabelFontSize(8)
                .roundedBar(true)
                .unit("%Mach")
                .unitLabelVisible(false)
                .barColor(Color.GREEN)
                .barBackgroundColor(Color.WHITE)
                .barWidth(10)
                    .build();
        
        appgauge = SimpleGaugeBuilder.create()
                .minValue(0)
                .maxValue(64)
                .type(SimpleGaugeBuilder.GaugeType.SIMPLE_RADIAL_GAUGE)
                .valueLabelFontSize(14)
                .unitFontSize(10)
                .maxLabelVisible(true)
                .minLabelVisible(true)
                .barFrameColor(Color.DARKSLATEBLUE)
                .noOfDecimals(2)
                .minMaxLabelFontSize(10)
                .roundedBar(true)
                .unit("Avail")
                .unitLabelVisible(false)
                .barColor(Color.GREEN)
                .barBackgroundColor(Color.WHITE)
                .barWidth(10)
                    .build();
        
        pspgauge = SimpleGaugeBuilder.create()
                .minValue(0)
                .maxValue(100)
                .type(SimpleGaugeBuilder.GaugeType.SIMPLE_RADIAL_GAUGE)
                .valueLabelFontSize(14)
                .unitFontSize(10)
                .maxLabelVisible(false)
                .minLabelVisible(false)
                .barFrameColor(Color.DARKSLATEBLUE)
                .noOfDecimals(1)
                .minMaxLabelFontSize(10)
                .roundedBar(true)
                .unit("%Psp")
                .unitLabelVisible(false)
                .barColor(Color.GREEN)
                .barBackgroundColor(Color.WHITE)
                .barWidth(10)
                    .build();
        
        
        
                //mygauge2=new SimpleGauge();
                                
                lpargauge.setLedVisible(false);
                lpargauge.setUserLedVisible(false);

                final Section[] SECTIONlpar = {        
			    new Section(0, 30, Color.GREEN),
			    new Section(30, 50, Color.YELLOW),
			    new Section(50, 85, Color.ORANGE),
                            new Section(85, 100, Color.RED),
			};
                
                
                
                Section criticalRange = new Section(95, 1000, Color.RED);
                Section warnRange = new Section(65, 95, Color.ORANGE);
                lpargauge.setSections(warnRange);
                lpargauge.setSections(criticalRange);
                lpargauge.setSectionsVisible(true);
                
                Section criticalRange2 = new Section(75, 100, Color.RED);
                Section warnRange2 = new Section(50, 75, Color.ORANGE);
                framegauge.setSections(warnRange2);
                framegauge.setSections(criticalRange2);
                framegauge.setSectionsVisible(true);

               
                Section warnRange3 = new Section(1.0, 2.0, Color.ORANGE);
                Section criticalRange3 = new Section(0.01, 0.99, Color.RED);
                appgauge.setSections(criticalRange3);
                appgauge.setSections(warnRange3);    
                appgauge.setSectionsVisible(true);
                
                Section criticalRange4 = new Section(85, 100, Color.RED);
                Section warnRange4 = new Section(65, 85, Color.ORANGE);
                Section alertRange4 = new Section(50, 65, Color.YELLOW);
                
                pspgauge.setSections(criticalRange4);
                pspgauge.setSections(warnRange4);
                pspgauge.setSections(alertRange4);
                pspgauge.setSectionsVisible(true);
                
                /*
                pctlpar=new Linear(STYLE_MODEL_2);
                //pctlpar=new jfxtras.labs.scene.control.gauge.SimpleGauge(STYLE_MODEL_3);
                
                pctframe=new Linear(STYLE_MODEL_2);
		*/
                
                fxradial1 = new Radial(STYLE_MODEL_1);
                //fxlinear1.setFrameVisible(false);
		//fxradial1.setLayoutX(0);
		fxradial1.setLcdDecimals(2);
		fxradial1.setMaxValue(2);
		//fxradial1.setVisible(true);
                //fxradial1.setTitle("Uncapped");
		fxradial1.autosize();
                fxradial1.autosize();
		fxradial1.setFrameVisible(false);

                if (Gmon_Main.animadv==1) {
                    fxradial1.setValueAnimationEnabled(true);
                    appgauge.setValueAnimationEnabled(true);
                    pspgauge.setValueAnimationEnabled(true);
                    framegauge.setValueAnimationEnabled(true);
                    lpargauge.setValueAnimationEnabled(true);
                    //appgauge.setAnimationDuration(0.2);
                    appgauge.setTimeToValueInMs(600);
                    pspgauge.setTimeToValueInMs(600);
                    framegauge.setTimeToValueInMs(600);
                    lpargauge.setTimeToValueInMs(600);
                    fxradial1.setAnimationDuration(600);
           
                    
                }
                if (Gmon_Main.animadv==0) {
                    fxradial1.setValueAnimationEnabled(false);
                    appgauge.setValueAnimationEnabled(false);
                    pspgauge.setValueAnimationEnabled(false);
                    framegauge.setValueAnimationEnabled(false);
                    lpargauge.setValueAnimationEnabled(false);
                }
                
                fxradial1.setGlowColor(Color.RED);
                fxradial1.setGlowVisible(true);
                //fxradial1.setUserLedVisible(true);
                //fxradial1.setUserLedColor(LedColor.YELLOW);
                //fxradial1.setLedColor(LedColor.YELLOW);
                //fxradial1.setLedVisible(true);
               
                /*
		pctlpar.addAllSections(SECTIONlpar);
                pctframe.addAllSections(SECTIONlpar);
  
                pctframe.setFrameVisible(false);
		pctlpar.setFrameVisible(false);
		pctlpar.setLcdUnit("%");
                pctlpar.setLcdUnitVisible(true);
                pctlpar.setUserLedVisible(true);
		pctlpar.setUserLedOn(true);
                pctlpar.setMaxValue(100);
                
		//pctlpar.setTitleAndUnitFont(new Font("Verdana", Font.PLAIN, 12));
		pctlpar.setTitle("% of LPar's EC");
		//pctlpar.setTitleAndUnitFontEnabled(true);
		
		pctframe.setLcdUnit("%");
                pctframe.setLcdUnitVisible(true);
                pctframe.setMajorTickSpacing(100);
                pctframe.setMinorTickSpacing(10);
                pctlpar.setMajorTickSpacing(100);
                //pctlpar.setMinorTickSpacing(100);
                pctlpar.setMinorTicksVisible(false);
                pctlpar.setMajorTicksVisible(false);

		pctframe.setTitle("% of Frame");
                */

                lparlabel=new Label("%% LPar");
                captxt=new Text("Uncapped");
                captxt.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
                captxt.setFill(Color.RED);
                vptxt=new Text("VP=1");
                caplab=new Label("Uncapped");
                eclab=new Label("EC=");
                rqlab=new Label("rq=1");
                ghzlab=new Label("GHz=3.0");
                wtlab=new Label("Wt=128");
                smtlab=new Label("SMT=4");
                
             
                
                root.getChildren().add(fxradial1);
                root.getChildren().add(lpargauge);
                root.getChildren().add(lparlabel);
                root.getChildren().add(captxt);
                root.getChildren().add(vptxt);
                root.getChildren().add(eclab);
                root.getChildren().add(rqlab);
                root.getChildren().add(ghzlab);
                root.getChildren().add(wtlab);
                root.getChildren().add(smtlab);
                root.getChildren().add(framegauge);
                root.getChildren().add(appgauge);
                root.getChildren().add(pspgauge);
              
 
		return (scene);	
        }
            catch(Exception ioException){
		ioException.printStackTrace(); 
            }
                
            return (scene);	    
  
}//end createScene

private Scene createScenedotmem() {
    
        char myc;
        
	Group root = new Group();
	Scene scene = new Scene(root, Color.TRANSPARENT);
        //Scene scene = new Scene(root, Color.BLUE);
	if (Gmon_Main.debug > 0) System.out.println("In FX CreateScenedotmem");
        
        final GridPane pane = new GridPane();
//        pane.setPadding(new Insets(3));
        pane.setPadding(new Insets(2));
        pane.setHgap(0);
        pane.setVgap(1);
        pane.setAlignment(Pos.TOP_LEFT);
          
	for (int i = 0 ; i <= 34  ; i++) {
            DotMatrixSegment segment = new DotMatrixSegment();
            segment.setPrefSize(8, 12);
            segment.setColor(Color.BLACK);
            segment.setCharacter(" ");
            segments1.add(segment);
            pane.add(segment, i, 1);
        }
       for (int i = 0 ; i <= 34  ; i++) {
            DotMatrixSegment segment = new DotMatrixSegment();
            segment.setPrefSize(8, 12);
            segment.setColor(Color.BLACK);
            segment.setCharacter(" ");
            segments2.add(segment);
            pane.add(segment, i, 2);
        }
/*        
       for (int i = 0 ; i <= 42  ; i++) {
            DotMatrixSegment segment = new DotMatrixSegment();
            segment.setPrefSize(7, 11);
            segment.setColor(Color.BLACK);
            segment.setCharacter(" ");
            segments3.add(segment);
            pane.add(segment, i, 3);
        }
  */     
       
       
        root.getChildren().add(pane);
	
		return (scene);	
		
}//end createScene




public void fxset(FXRadial myrad){

/*radial1.setSectionsVisible(true);

Section[] sections1 ={
new Section(0, 1, Color.GREEN)};


Section[] sections ={
        new Section(0, ec, Color.GREEN),
        new Section(ec, vp, Color.YELLOW),
        new Section(vp, framecpu, Color.ORANGE)};
    
    radial1.setSections(sections1);
    radial1.setSectionsVisible(true);*/


/* should work - but stack dumping */
   Section[] areas1 ={
            new Section(0, 0.2, Color.GREEN)};
  
   fxradial1.addAllAreas(areas1);
    //myfx.fxradial1.setTransparentAreasEnabled(true);
    fxradial1.setAreasVisible(true); 
     

}


public void set_max(tryIPConnect nc){	
	int w1,w2,w3,w4;
        DecimalFormat myn = new DecimalFormat("#0");
        DecimalFormat myf = new DecimalFormat("#0.0");
//  iscapped +   wt +       vp +       ec +   smtmode +   lparid +    serial +    gz +      pmem + doframe
	
	  try {
		//System.out.println("In FX setmax");
		//System.out.println("In FX setmax" + nc.mydraw.doframe);
		  if (!(nc.iscapped==null)) {
			  newstring=nc.iscapped+nc.wt+nc.vp+nc.ec+nc.smtmode+nc.lparid+nc.serial+nc.ghz+nc.pmem+nc.mydraw.doframe;
			  if (!(newstring.matches(oldstring))){
				  oldstring=newstring;
			  
				  if (Gmon_Main.debug > 1) System.out.println("In FX setmax inside null & newstring");
                                  
                                  vptxt.setText("VP="+myn.format(nc.vp));
                                  wtlab.setText("Wt="+myn.format(nc.wt));
                                  ghzlab.setText("GHz="+myf.format(nc.ghz));
                                  smtlab.setText(""+nc.smtmode);
                                  
				  if (nc.mydraw.doframe==0) 
					fxradial1.setMaxValue(nc.vp);// lpar bounds
                                        
		 
				  if (nc.mydraw.doframe==1)
					fxradial1.setMaxValue(nc.framecpu); // frame bounds
				
				  if (nc.iscapped.matches("Capped")) {		
				    fxradial1.setThreshold(nc.ec);
                                    captxt.setFill(Color.GREEN);
                                    //caplab.
                                  }
				
				  if (nc.iscapped.matches("Uncapped"))
					fxradial1.setThreshold(nc.vp);
                                        captxt.setFill(Color.RED);
			
                                  captxt.setText(nc.iscapped);
                                  eclab.setText("EC="+nc.ec);
				  //fxradial1.setTitle(nc.iscapped);  
				  //fxradial1.setUnit("EC= "+nc.ec);
	
                                 
				  if (fxradial1.isMarkersVisible()){
					  fxradial1.removeMarker(markers1[0]);
					  fxradial1.removeMarker(markers1[1]);
				  }
				
				  if (fxradial1.isAreasVisible())	
					  fxradial1.removeArea(areas1[0]);
		
				  if (fxradial1.isSectionsVisible())	
					  fxradial1.removeArea(sections1[0]);
				
				  markers1[0] = new Marker(nc.ec, Color.GREEN);
				  markers1[1] = new Marker(nc.vp, Color.RED);
		
				  areas1[0] =new Section(0, nc.ec, Color.GREEN);
		
				  sections1[0] =new Section(nc.ec,nc.vp, Color.YELLOW);	
		
				  fxradial1.addAllAreas(areas1[0]);
				  fxradial1.addAllSections(sections1[0]);
				  fxradial1.addMarker(markers1[0]);
				  fxradial1.addMarker(markers1[1]);
				  fxradial1.setMarkersVisible(true);
				  fxradial1.setAreasVisible(true);
				  fxradial1.setSectionsVisible(true);
                                  
                                  //System.out.println((nc.vp/nc.ec)*100.0);
                                  //pctlpar.setMaxValue((nc.vp/nc.ec)*100.0);
                                  //pctlpar.setMinValue(0);
                                  //pctlpar.setMaxValue(200);
                        
			  } // end if newstring
		  } // end if null
	  }
	  catch(Exception ioException){
			ioException.printStackTrace(); 
	  		}
	  //System.out.println("Exitting FX setmax");
}



public void set_pphs(tryIPConnect nc){	
	//radial1.setValue(pphys
	float myred;
	double pctfr,sx,s3,s,u,tt,tu,pu,ps;
	int fxw,fxh;
        int w1,w2,w3,w4;
        DecimalFormat myn = new DecimalFormat("#0");
        
        
        String spu,spctfr;
try{
	
        tt=nc.psptot;
        s=nc.pspfree;
        u=tt-s;
//System.out.println("u s="+" "+u+" "+s);


        pu=(u/tt)*100.0;
        ps=(s/tt);
        
	//pctec=kth.pctec;
	//rq=kth.rq;
        pctfr=(nc.pphys/nc.framecpu)*100.0;

        rqlab.setText("rq="+myn.format(nc.rq));
	fxradial1.setValue(nc.pphys);
	fxradial1.setLcdValue(nc.pphys);
        //pctlpar.setValue(nc.pctec);
        //pctframe.setValue(pctfr);
        lpargauge.setValue(nc.pctec);
        framegauge.setValue(pctfr);
        appgauge.setValue(nc.pavp);        
        
        spu=Double.toString(pu);
        spctfr=Double.toString(pctfr);
        //System.out.println("PSP="+spu);
        //System.out.println("Fr="+spctfr);
      
        if (!spu.equals("NaN")) {
            pspgauge.setValue(pu);    
            
        }
        
        if (pctfr>=0 && pctfr <= 100) framegauge.setValue(pctfr);
        
        
        
        //fxradial1.setUnit("App="+nc.pavp);
	/*myred=(float)(kth.pctec/1000.0);
	if (myred<0.8f)
		myred=0.8f;
	System.out.println(myred);*/
	//fxradial1.setGlowColor(Color.RED);
	
	if (nc.pphys > nc.ec) {
		
            fxradial1.setGlowOn(true);	
            //fxradial1.set
	}
	else
	{
		fxradial1.setGlowOn(false);
	}
	 
	if (nc.pctec > 200.0){
                //System.out.println("led on");
		fxradial1.setUserLedOn(true);
	}
	else
	{
		fxradial1.setUserLedOn(false);
	}
        
        
        // do dot matrix 
                
                
                word1="Mem Used:" + myn.format(nc.pmtot-nc.pmfree) +"MB";
                word2="  Free:" + myn.format(nc.pmfree)+"MB";
                word3="OS Total:"+myn.format(nc.pmtot)+"MB";
                //word4=" Phys:"+myn.format(nc.pmem)+"MB AME="+nc.ame_act;
                word4="  Back:"+myn.format(nc.pmem)+"MB";
                /*
                word1="Mem Used=" + myn.format(nc.pmtot-nc.pmfree);
                word2=" of "+myn.format(nc.pmtot);
                word3="MB: Free=" + myn.format(nc.pmfree);
                //word4=" Phys:"+myn.format(nc.pmem)+"MB AME="+nc.ame_act;
                word4=" Back="+myn.format(nc.pmem);
                */
                            
                w1=word1.length();
                w2=word2.length();
                w3=word3.length();
                w4=word4.length();
                                  
                wordall=word1+word2;
                if (Gmon_Main.debug > 0) System.out.println(wordall);
                                                          
                int counter = 0;
                                    for (DotMatrixSegment segment : segments1) {
                                
                                        if (counter < wordall.length()) {
                                            segment.setCharacter(wordall.charAt(counter));
                                            } else {
                                                segment.setCharacter(" ");
                                            }
                                        counter++;                          
                                    }  // end for
                                    wordall=word3+word4;
                                   counter = 0;
                                    for (DotMatrixSegment segment : segments2) {
                                
                                        if (counter < wordall.length()) {
                                            segment.setCharacter(wordall.charAt(counter));
                                            } else {
                                                segment.setCharacter(" ");
                                            }
                                        counter++;                          
                                    }  // end for
                
        // end dot matrix      
        
        
        
        
        
	if (nc.mydraw.getFxresize()==1) {
		fxw=nc.mydraw.getFxw();
		//fxh=nc.mydraw.getFxh();
                sx=fxw*0.45;
                s3=fxw*0.32;
                fxh=fxw;
		myfxp.setBounds(1, 21, fxw*2, fxw);
                mydotm.setBounds(0, fxh+21, fxw*2, idsh);
                //fxDotmem.setBounds(0, 21+ifxh, ifxw*2,idsh);
		fxradial1.setPrefSize(fxw, fxw);
                //pctlpar.setPrefSize((fxw*0.45),fxh);
                //pctframe.setPrefSize((fxw*0.45),fxh);
                
                lpargauge.setPrefSize(sx,sx);
                lpargauge.setLayoutX(fxw);
                lpargauge.setMaxValue((nc.vp/nc.ec)*100);
          
                framegauge.setPrefSize(sx,sx);
                framegauge.setLayoutX(fxw+sx);
                
                appgauge.setPrefSize(sx,sx);
                appgauge.setLayoutX(fxw);
                appgauge.setLayoutY(fxh/2);
                appgauge.setMaxValue(nc.framecpu);
                
                pspgauge.setPrefSize(sx,sx);
                pspgauge.setLayoutX(fxw+sx);
                pspgauge.setLayoutY(fxh/2);
                //pctlpar.setLayoutX(fxw);
                //pctframe.setLayoutX(fxw+(int)(fxw/2.0));
		nc.mydraw.setFxresize(0);
                lpargauge.setVisible(true);
                framegauge.setVisible(true);
                appgauge.setVisible(true);
                pspgauge.setVisible(true);
                
                lparlabel.setLayoutX(fxw+10);
                lparlabel.setLayoutY(fxw+10);
                lparlabel.setVisible(true);
                
              
                eclab.setLayoutX(fxw*.38);                 
                eclab.setLayoutY(fxw*.22);
                captxt.setLayoutX(fxw*.30);
                captxt.setLayoutY(fxw*.36);
                rqlab.setLayoutX(fxw*.39);                 
                rqlab.setLayoutY((fxw*.36));
                
                vptxt.setLayoutX(fxw*.78);
                vptxt.setLayoutY(fxw*.98);
                wtlab.setLayoutX(1);
                wtlab.setLayoutY(-3);
                smtlab.setLayoutX(fxw*.80);
                smtlab.setLayoutY(-3);
                
                ghzlab.setLayoutX(1);
                ghzlab.setLayoutY(fxw*.92);
                       
                
	}
	
	
}// end try
	catch(Exception ioException){
		ioException.printStackTrace(); 
  		}

}

public void setOldstring(String s)
{	
	oldstring=s;	
}






} // end class


/*
	
try{
	JFXPanel fxPanel = new JFXPanel();
	myframe.add(fxPanel);
	fxPanel.setBounds(200, 250, 200, 200);

	StyleModel STYLE_MODEL_1 = StyleModelBuilder.create()
							.frameDesign(Gauge.FrameDesign.STEEL)
							.tickLabelOrientation(Gauge.TicklabelOrientation.HORIZONTAL)
							.pointerType(Gauge.PointerType.TYPE14)
							.thresholdVisible(true)
							.lcdDesign(LcdDesign.STANDARD_GREEN)
							.build();

	radial1 = new Radial(STYLE_MODEL_1);
	radial1.setThreshold(30);
	radial1.setPrefSize(200, 200);


//Scene scene = createScene();
	Group root = new Group();
	Scene scene = new Scene(root, Color.ALICEBLUE);
	fxPanel.setScene(scene);
	root.getChildren().add(radial1);
	fxPanel.setVisible(true);
}
catch(Exception myException){
	myException.printStackTrace();
}


}//end initiator
}// end class

*/
