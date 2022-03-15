/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gmon;

import java.util.Arrays;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;



import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javax.swing.BorderFactory;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import jfxtras.labs.scene.control.gauge.DotMatrixSegment;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.LcdDesign;
import jfxtras.labs.scene.control.gauge.Radial;
import jfxtras.labs.scene.control.gauge.Section;
import jfxtras.labs.scene.control.gauge.SimpleGaugeBuilder;
import jfxtras.labs.scene.control.gauge.StyleModel;
import jfxtras.labs.scene.control.gauge.StyleModelBuilder;

public class drawFrame {
     
    double j;
    static String usa = "";
    String ts;
    int killme=0;
    //StackedBarChart<String,Number> sbc;  
    JFrame myf;
    
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
    final StackedBarChart<String,Number> sbc =
            new StackedBarChart<String,Number>(xAxis, yAxis);
    
    
    XYChart.Series<String,Number>[] series1;
   
    /*
    final XYChart.Series<String,Number> series1 =
            new XYChart.Series<String,Number>();
    
    
    final XYChart.Series<String,Number> series2 =
            new XYChart.Series<String,Number>();
    final XYChart.Series<String,Number> series3 =
            new XYChart.Series<String,Number>();
    final XYChart.Series<String,Number> series4 =
            new XYChart.Series<String,Number>();
    */
    //final int myindex;
    
    
    
    
         
public drawFrame(JFrame myframe,String myserial){   
 
myf=myframe; 
ts=myserial;


 SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {				
                            initAndShowGUI();
                            if (Gmon_Main.debug > 1) System.out.println("Finished with invoke later");
                            }// end run	
		}); 
 } // end drawFrame

//------------------
          
public void initAndShowGUI() {

    final JFXPanel fxPanel = new JFXPanel();
    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();
  
    myf.add(fxPanel);
    
    Platform.runLater(new Runnable() {
			@Override
			public void run() {
                            initFX(fxPanel);        
                            }
                        });
    
    
    
    new Thread() {
            // runnable for that thread
            public void run() {
		do{
            	try {
                    // imitating work
            		j=Gmon_Main.thedelay;
            		j=j-300;
                        if (Gmon_Main.debug>2) System.out.println("in FRAME sleep sleeping for "+j);
			
                        Thread.sleep((int)j);
                        } catch (InterruptedException ex) {
                            System.out.println("Exception from Frame loop");
                            ex.printStackTrace();
                        }         	            	
                
                        Platform.runLater(new Runnable() {
				@Override
				public void run() {	                                                  
                                       set_pphs();						
                                       //if (doresize==1)
                                       //dorz(nc.mydraw);
                                       
                                } // end run
                                
                                
			    }); // Runable
		     }while(killme==0);
            
                System.out.println("out of drawFrame while loop");
                
                //this.interrupt();
               
            } // end run()
        }.start(); // end new thread()
			
	if (Gmon_Main.debug > 2) System.out.println("Frame FX - After new runnable");
	
	
}//end initandShow
        
              

    


private void initFX(JFXPanel fxPanel) {
    int ai;
// This method is invoked on the JavaFX thread
	//fxPanel.setBounds(2, 20, 200, 200);
        series1=new XYChart.Series[32];
        Scene scene = createScene();
	fxPanel.setScene(scene);
	fxPanel.setVisible(true);
        fxPanel.setOpaque(true);
        fxPanel.setBackground(java.awt.Color.red);
        //fxPanel.setBounds(0, 0, 200, 900);
        
        fxPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.red));
        //fxPanel.setBounds(0, 0, myf.getWidth(), myf.getHeight());
        //fxPanel.setBorder(BorderFactory.createLineBorder(java.awt.Color.red));
        
	//fxradial1.setPrefSize(fxPanel.getWidth(), fxPanel.getHeight());
}
    
    
private Scene createScene() {
    
	//Group root = new Group();
	//Scene scene = new Scene(root, Color.TRANSPARENT);
        
	if (Gmon_Main.debug > 0) System.out.println("In FX CreateScene");
	
       
    
            sbc.setTitle("Frame "+ts);
            xAxis.setLabel("LPar's");
            xAxis.setCategories(FXCollections.<String>observableArrayList(
                Arrays.asList(ts)));
            yAxis.setLabel("CPU's");
            yAxis.setAutoRanging(false);
            
            
            
            int i=MainLoop.map.get(ts);
            if (i>16) {
                yAxis.setTickUnit(2);
            }
            else
            {
                yAxis.setTickUnit(1);
            }
            
            yAxis.setUpperBound(MainLoop.aindex[i]);
            
            
            
            sbc.setPrefSize(30, 800);
            if (Gmon_Main.animadv==1){
                sbc.setAnimated(true);
            }
            if (Gmon_Main.animadv==0)  sbc.setAnimated(false);
            
            Scene scene = new Scene(sbc,150, 800);
            return (scene);
            
}//end createScene





public void set_pphs() {
    
int tp,i,j,il;
String tlpar,tserial,tss2;
Double tpphys;


tp=MainLoop.map.get(ts);
//System.out.println("In drawFrame ts="+ts+" "+tp);

for (j=0;j<31;j++){
  tlpar=  MainLoop.alpar[tp][j];
  tpphys=  MainLoop.apphys[tp][j];
  
  if (tlpar != null){
    //System.out.println("In drawFrame ts=" +tlpar+" "+tpphys);  
    //series1.setName(tlpar);
    il=0;
        for (XYChart.Series<String,Number> series : sbc.getData()) {
                il=il+1;     
                for (XYChart.Data<String,Number> data : series.getData()) {
                           
                        if (Gmon_Main.debug > 1) System.out.println(series.getName()+" tlpar="+tlpar+" "+il);
                        
                        //series.setName(series.getName()+ (int)(Math.random() *10));
                        if (tlpar.equals(series.getName())){
                            if (Gmon_Main.debug > 0) System.out.println("Setting for "+series.getName());
                            data.setYValue(tpphys);
                            } // end if
                } // end for
              } // end for
    
    } // end if
    tss2=ts+"++"+tlpar;
    if (MainLoop.removelpar.get(tss2)!=null){
        removeSeries(ts,tlpar);
        MainLoop.removelpar.remove(tss2);
    }
     if (MainLoop.addlpar.get(tss2)!=null){
        addSeries(ts,tlpar);
        MainLoop.addlpar.remove(tss2);
    }   
  
  
  
  }//end for
}
    
public void addSeries(String serialname,String lparname){
String tlps,theserial,tss;
int ai;
if (Gmon_Main.debug > 1) System.out.println("In add series"+lparname);
tlps=lparname;
theserial=serialname;

tss=theserial+"++"+tlps;
ai=MainLoop.lparmap.get(tss);
if (Gmon_Main.debug > 1) System.out.println("In add series tss="+tss+" ai= "+ai);
series1[ai]=new XYChart.Series<String,Number>();
series1[ai].setName(tlps);
series1[ai].getData().add(new XYChart.Data<String,Number>(theserial,0.0));    
    
sbc.getData().addAll(series1[ai]);    
    
}
 
public void removeSeries(String serialname,String lparname){    
String tlps,theserial,tss;
int di,dj,ai;
if (Gmon_Main.debug > 0) System.out.println("In remove series"+lparname);
if (lparname!=null)
    {
    tlps=lparname;
    theserial=serialname;
    tss=theserial+"++"+tlps;
    
    // net line to stop us being called multiple times
    MainLoop.removelpar.remove(tss);
    ai=MainLoop.lparmap.get(tss);
    //System.out.println("In remove series tss="+tss+" ai= "+ai);
    //System.out.println("In remove series tlps="+tlps);
    //series1[ai].setName(tlps);
    //series1[ai].getData().add(new XYChart.Data<String,Number>(theserial,0.0));    
      
    sbc.getData().removeAll(series1[ai]);
    tss=theserial+"++"+tlps;
       
    di = MainLoop.map.get(theserial);    
    dj=MainLoop.lparmap.get(tss);
                                                        //alpar.put(ti).put(tj)=frame.myd[jj].mync.lparname;
    MainLoop.alpar[di][dj]=null;
    MainLoop.apphys[di][dj]=null;

    MainLoop.lparmap.remove(tss);
   
    
    }
}

public void killme(){
    killme=1;
}


}  // end class


