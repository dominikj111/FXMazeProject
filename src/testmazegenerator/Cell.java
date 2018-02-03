package testmazegenerator;

import EventLibrary.BasicEventOfferer;
import EventLibrary.EventBundle;
import EventLibrary.EventData;
import EventLibrary.EventOfferer;
import EventLibrary.EventOffererHandler;
import EventLibrary.EventTrigger;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class Cell<ConnectedTo> extends Rectangle {
    
    public static enum events { mousePrimaryClicked, mouseSecondaryClicked }
        
    private EventTrigger eventTrigger;
    public EventOfferer eventOffering;
       
    private int rows, columns, row, column;
    private SimpleDoubleProperty enviWidth, enviHeight;
    private ConnectedTo connectedObject;
    
    public Cell(){ 
        EventBundle eBundle = BasicEventOfferer.createBundle();
        this.eventOffering = eBundle.offerer;
        this.eventTrigger  = eBundle.trigger;
        
        addEventHandler(
                MouseEvent.MOUSE_CLICKED, 
                (mouseEvent) -> this.eventTrigger.fireEvent(
                                    (mouseEvent.getButton() == MouseButton.PRIMARY) ? events.mousePrimaryClicked : events.mouseSecondaryClicked , 
                                    new EventData(connectedObject, this)
                                )
        );
    }
    
    /* *********************/
    /* INITIALIZE ROUTINES */
    /* *********************/    

    public Cell connectTo(ConnectedTo connectedObject) {
        this.connectedObject = connectedObject;
        return this;
    }   

    public Cell setCellListener(events eventType, EventOffererHandler cellEvent) {
        this.eventOffering.addListener(eventType, cellEvent);
        return this;
    }

    public Cell setRows(int rows) {
        this.rows = rows;
        return this;
    }

    public Cell setColumns(int columns) {
        this.columns = columns;
        return this;
    }
 
    public Cell setRow(int row) {
        this.row = row;
        return this;
    }

    public Cell setColumn(int column) {
        this.column = column;
        return this;
    }
    
    public Cell setWidthEnvironmentProperty(SimpleDoubleProperty enviWidth) {
        this.enviWidth = enviWidth;
        
        InvalidationListener setWidthInvalidate = (Observable observable) -> {
            setWidth(enviWidth.get() / columns);
            setX(column * getWidth());
        };
        
        this.enviWidth.addListener(setWidthInvalidate);
        setWidthInvalidate.invalidated(null);
        
        return this;
    }

    public Cell setHeightEnvironmentProperty(SimpleDoubleProperty enviHeight) {
        this.enviHeight = enviHeight;
        
        InvalidationListener setHeightInvalidate = (Observable observable) -> {
            setHeight(enviHeight.get() / rows);
            setY(row * getHeight());
        };
        
        this.enviHeight.addListener(setHeightInvalidate);
        setHeightInvalidate.invalidated(null);
        
        return this;
    }
    
    /* ********************/
    /* Functional API     */
    /* ********************/
    
    public void onRefill() {
        setFill(getFill());        
    }
}
