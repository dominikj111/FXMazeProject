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

public class SwapArrowControl extends HBox {
    
    public enum events { swaped };
    
    public  EventOfferer eventOffering;
    private EventTrigger eventTrigger;
    
    public SwapArrowControl(){
        
        EventBundle eBundle = BasicEventOfferer.createBundle();
        eventOffering = eBundle.offerer;
        eventTrigger  = eBundle.trigger;        
        
        this.alignmentProperty().set(Pos.CENTER);
        
        IconFontFX.register(FontAwesome.getIconFont()); 
        
        IconNode arrowLeft = new IconNode();
        arrowLeft.getStyleClass().add("doubleArrow");
        arrowLeft.getStyleClass().add("control");
        arrowLeft.onMouseClickedProperty().set((data)->{            
            this.eventTrigger.fireEvent(events.swaped, new EventData(null, this));
        });
        
        this.getChildren().add(arrowLeft);
    }
}
