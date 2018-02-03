package controllers;

import EventLibrary.EventData;
import EventLibrary.SingleInvokeEvent;
import FxUtils.UniversalController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.TranslateTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class ImgSaveController extends UniversalController {

    private static int durationSlideAnimation = 300;
    
    public static enum events { cancelButtonClicked, saveButtonClicked }
    
    private SimpleBooleanProperty mainPaneOnFrontProperty;
    
    @FXML
    private ImageView imagePort;
    
    @FXML
    private VBox mainNode;

    @Override
    protected Node getMainPane() {
        return this.mainNode;
    }
    
    @FXML
    private void eventCancelClicked(){
        System.out.println("event cancel");
        fireEvent(events.cancelButtonClicked, new EventData(null, this));
    }
    
    @FXML
    private void eventSaveToClicked(){
        System.out.println("event save");
        fireEvent(events.saveButtonClicked, new EventData(null, this));
    }

    public void setImage(Image img){
        
        
//        System.out.println("width: " + ((Region)this.imagePort.getParent()).widthProperty().get());
//        System.out.println("height: " + ((Region)this.imagePort.getParent()).heightProperty().get());
//        
        this.imagePort.fitWidthProperty().set(((Region)this.imagePort.getParent()).widthProperty().get());
        this.imagePort.fitHeightProperty().set(((Region)this.imagePort.getParent()).heightProperty().get());
        
        this.imagePort.setPreserveRatio(false);
        this.imagePort.setImage(img);
//        this.imagePort.fitWidthProperty().bind(((Region)this.imagePort.getParent()).widthProperty());
//        this.imagePort.fitHeightProperty().bind(((Region)this.imagePort.getParent()).heightProperty());
//        
//        this.imagePort.setPreserveRatio(false);
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        this.mainPaneOnFrontProperty  = new SimpleBooleanProperty();
        
        ((SingleInvokeEvent)getSingleEvents()).removeListenersAfter();
        
        Animation animation = getSlideAnimation(this.mainPaneOnFrontProperty, this.mainNode, durationSlideAnimation);        
        animation.statusProperty().addListener(observable -> {
            if(animation.getStatus().equals(Status.STOPPED)){
                getSingleEvents().fireShowHideAnimationFinished();
                ((SingleInvokeEvent)getSingleEvents()).alowAnotherFire();
            }
        });
        
        //animation.setOnFinished((ActionEvent event) -> getSingleEvents().fireShowHideAnimationFinished());
    }
    
    @Override
    public void hideAnimation(Runnable callbackAfter) {
        
        getSingleEvents().registerShowHideAnimationFinished((EventData data) -> {
            setToBack();
            callbackAfter.run();
        });
        
        this.mainPaneOnFrontProperty.set(false); // switch property on the first
    }   

    @Override
    public void showAnimation(Runnable callbackAfter) {
        
        getSingleEvents().registerShowHideAnimationFinished((EventData data) -> callbackAfter.run());
        setToFront();
        
        this.mainPaneOnFrontProperty.set(true); // switch property on the last
    }        
    
    private TranslateTransition getSlideAnimation(SimpleBooleanProperty controller, Region node, double duration) {
               
        TranslateTransition animation = new TranslateTransition(new Duration(duration), node);                
        
        controller.addListener( (Observable observable) -> {
            
            double yPath = (node.getHeight() + node.parentProperty().getValue().getBoundsInLocal().getMaxY()) / 2.0;
            
            if(controller.get()){ // show animation    
                animation.setFromY(-yPath);
                animation.setToY(0);
            } else { // hide animation
                animation.setFromY(0); 
                animation.setToY(-yPath);
            }
            animation.play();
        });
        
        return animation;
    }
}
