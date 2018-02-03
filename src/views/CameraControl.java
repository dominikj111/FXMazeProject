package views;

import EventLibrary.BasicEventOfferer;
import EventLibrary.EventBundle;
import EventLibrary.EventData;
import EventLibrary.EventOfferer;
import EventLibrary.EventTrigger;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import jiconfont.icons.FontAwesome;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;

public class CameraControl extends HBox {
    
    public enum events { shot };
    
    public  EventOfferer eventOffering;
    private EventTrigger eventTrigger;
    
    public CameraControl(){
        
        EventBundle eBundle = BasicEventOfferer.createBundle();
        eventOffering = eBundle.offerer;
        eventTrigger  = eBundle.trigger;        
        
        this.alignmentProperty().set(Pos.CENTER);
        
        IconFontFX.register(FontAwesome.getIconFont()); 
        
        IconNode cameraNode = new IconNode();
        cameraNode.getStyleClass().add("camera");
//        cameraNode.getStyleClass().add("control");
        cameraNode.onMouseClickedProperty().set((data)->{            
            this.eventTrigger.fireEvent(events.shot, new EventData(null, this));
        });
        
        this.getChildren().add(cameraNode);
    }
}