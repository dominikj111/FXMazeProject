package testmazegenerator;

import EventLibrary.BasicEventOfferer;
import EventLibrary.EventData;
import EventLibrary.EventOffererHandler;
import MazeEntities.Node;
import MazeEntities.NodeCreatedListener;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CellFactory extends BasicEventOfferer implements NodeCreatedListener{
    
    private int countOfNodes;

    CellFactory(Color pathColor, Color wallColor) {
        
        this.countOfNodes = 71;
        
        this.guiCells = null;
        this.rows = -1;
        this.columns = -1;
        this.percentageCycles = -1;
        
        this.pathColor = pathColor;
        this.wallColor = wallColor;
        
        this.widthEnvironmentProperty  = new SimpleDoubleProperty(-1);
        this.heightEnvironmentProperty = new SimpleDoubleProperty(-1);
    }
    
    public Color getPathColor(){
        return this.pathColor;
    }
    
    public Color getWallColor(){
        return this.wallColor;
    }
    
    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public int getPercentegeCycle() {
        return percentageCycles;
    }

    void setRows(int rows) {
        this.rows = rows;
    }

    void setColumns(int columns) {
        this.columns = columns;
    }

    void setPercentageCycle(int percentageCycles) {
        this.percentageCycles = percentageCycles;
    }

    public int getCountOfNodes() {
        return this.countOfNodes;
    }

    public static enum events { 
        colorPathReset, 
        cellCreated,
        cellsGenerationFininshed
    }
    
    private EventOffererHandler primaryCellEvent = (data) -> {
        System.out.println("Primary Cell Handler: " + data.<Node>getData().toString());
        data.<Node>getData().setColor(0);
    };
    
    private EventOffererHandler secondaryCellEvent = (data) -> {
        System.out.println("Secondary Cell Handler: " + data.<Node>getData().toString());
        data.<Node>getData().setColor(1);
    };
    
    private Color pathColor, wallColor;
    private int rows, columns, percentageCycles;
    
    private ColorTranslator colorTranslator = (int value) -> {        
        switch (value){
            case 0: return getPathColor().invert();
            case 1: return getPathColor();
            default: return Color.GRAY; // frontier
        }
    };
      
    private List<Rectangle> guiCells;
    
    private SimpleDoubleProperty widthEnvironmentProperty;
    private SimpleDoubleProperty heightEnvironmentProperty;        

    @Override
    public void nodeCreated(Node node) {
        
        Cell cell = new Cell()                
                .connectTo(node)
                .setRows(this.rows)
                .setColumns(this.columns)
                .setRow(node.getCell().getRow())
                .setColumn(node.getCell().getColumn())
                .setWidthEnvironmentProperty(this.widthEnvironmentProperty)
                .setHeightEnvironmentProperty(this.heightEnvironmentProperty)
                .setCellListener(Cell.events.mousePrimaryClicked, this.primaryCellEvent)
                .setCellListener(Cell.events.mouseSecondaryClicked, this.secondaryCellEvent);
        
        this.guiCells.add(cell);
                
        cell.setFill(this.colorTranslator.translateToColor(node.colorProperty().get()));
        
        node.colorProperty().addListener(
            (Observable observable) -> 
                    Platform.runLater(
                            () -> cell.setFill(this.colorTranslator.translateToColor(node.colorProperty().get()))
                    )
        );
        
        addListener(events.colorPathReset, 
            (EventData data) -> cell.setFill(this.colorTranslator.translateToColor(node.colorProperty().get()))
        );
        
        Platform.runLater(() -> {
            fireEvent(events.cellCreated, new EventData(cell, node));
        });
    }
    
    @Override
    public void nodesGenerationFinished(){
        System.out.println("generation finished");
        fireEvent(events.cellsGenerationFininshed, new EventData(this.guiCells, this));
    }
    
    @Override
    public void setCountOfMazeNodes(int countOfNodes) {
        this.countOfNodes = countOfNodes;
    }

    @Override
    public void setCountOfBridgeConnectors(int countOfBridgeConnectors) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public List<Rectangle> getGuiCells(){
        return this.guiCells;
    }

    public void setWidthEnvironment(double value){
        this.widthEnvironmentProperty.set(value);
    }
    
    public void setHeightEnvironment(double value){
        this.heightEnvironmentProperty.set(value);
    }
    
    public void resetColorPath(Color colorPath){
        this.pathColor = colorPath;
        fireEvent(events.colorPathReset, new EventData(colorPath, this));
    }
    
    public void newMazeRecord(int columns, int rows){
        this.guiCells = new ArrayList<>();
        
        this.columns = columns;
        this.rows = rows;
    }
}
