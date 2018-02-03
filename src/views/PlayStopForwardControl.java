package views;

import EventLibrary.BasicEventOfferer;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import jiconfont.icons.FontAwesome;
import jiconfont.javafx.IconFontFX;
import jiconfont.javafx.IconNode;
import EventLibrary.EventBundle;
import EventLibrary.EventData;
import EventLibrary.EventOfferer;
import EventLibrary.EventTrigger;

public class PlayStopForwardControl extends HBox{
    
    public enum events { play, stop, forward, stepfordward };
    
    public  EventOfferer eventOffering;
    private EventTrigger eventTrigger;
    
    public PlayStopForwardControl(){
        
        EventBundle eBundle = BasicEventOfferer.createBundle();
        eventOffering = eBundle.offerer;
        eventTrigger  = eBundle.trigger;        
        
        this.alignmentProperty().set(Pos.CENTER);
        this.spacingProperty().set(10);
        
        IconFontFX.register(FontAwesome.getIconFont()); 
        
        IconNode playIcon = new IconNode();
        playIcon.getStyleClass().add("play");        
        playIcon.getStyleClass().add("control");        
        playIcon.onMouseClickedProperty().set((data)->{            
            this.eventTrigger.fireEvent(events.play, new EventData(null, this));
        });

        IconNode pauseIcon = new IconNode();
        pauseIcon.getStyleClass().add("pause");
        pauseIcon.getStyleClass().add("control");
        pauseIcon.onMouseClickedProperty().set((data)->{
            this.eventTrigger.fireEvent(events.stop, new EventData(null, this));
        });
                
        IconNode stepfordwardIcon = new IconNode();
        stepfordwardIcon.getStyleClass().add("stepfordward");
        stepfordwardIcon.getStyleClass().add("control");
        stepfordwardIcon.onMouseClickedProperty().set((data)->{
            this.eventTrigger.fireEvent(events.stepfordward, new EventData(null, this));
        });
                
        IconNode forwardIcon = new IconNode();
        forwardIcon.getStyleClass().add("fordward");
        forwardIcon.getStyleClass().add("control");
        forwardIcon.onMouseClickedProperty().set((data)->{
            this.eventTrigger.fireEvent(events.forward, new EventData(null, this));
        });
        
        this.getChildren().add(playIcon);
        this.getChildren().add(pauseIcon);
        this.getChildren().add(stepfordwardIcon);
        this.getChildren().add(forwardIcon);
    }
}
