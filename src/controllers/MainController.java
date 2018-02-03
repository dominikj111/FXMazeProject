package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import EventLibrary.EventData;
import FxUtils.SequentialAnimator;
import FxUtils.UniversalController;
import MathUtils.CalcRoutines;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import routines.MazeSize;
import routines.TileAnimation;
import testmazegenerator.Cell;
import testmazegenerator.CellFactory;
import views.CameraControl;
import views.PlayStopForwardControl;
import views.SwapArrowControl;

public class MainController extends UniversalController {

    public void newMaze() {
        this.canvasField.getChildren().clear();
        this.canvasField.getChildren().add(this.disabledFilter);
        this.tileAnimator.stop();
        this.tileAnimator = new SequentialAnimator();
    }

    public void initSlider(int sliderMinValue, int sliderValue, int sliderMaxValue) {
        this.speedSlider.minProperty().set(sliderMinValue);
        this.speedSlider.maxProperty().set(sliderMaxValue);
        this.speedSlider.valueProperty().set(sliderValue);
        
        this.tileAnimDuration = new SimpleDoubleProperty(this.speedSlider.getValue());
        this.tileAnimDuration.bind(this.speedSlider.valueProperty());
    }
    
    public void initMazeGenetareProgessbar(){
        this.progressBar.setProgress(0);
        this.progressBar.setStyle("-fx-accent: #e62e00");
        this.statusText.setText("Graph generating");
    }
    
    public void initMazeRenderProgessbar(){
        this.progressBar.setProgress(0);
        this.progressBar.setStyle("-fx-accent: #adff2f");
        this.statusText.setText("Graph rendering");
    }

    public static enum events { startMazeGeneration,                                 
                                buttonTakePicture, 
                                colorPathPickerChanged, 
                                colorWallPickerChanged,
                              }
    
    private Rectangle disabledFilter;
    private int rows, columns, percentegeCycle;
    private SequentialAnimator<TileAnimation> tileAnimator;
    private SimpleDoubleProperty tileAnimDuration, countTiles;
    
    @FXML
    private AnchorPane canvasField;
    
    @FXML
    private StackPane buttonsControlPane;
    
    @FXML
    private StackPane colorSwapArrowPane;
   
    @FXML
    private Label statusText;
   
    @FXML
    private ProgressBar progressBar;
    
    @FXML
    private Region mainPane;
    
    @FXML
    private TextField rowsInput;
    
    @FXML
    private TextField columnsInput;
    
    @FXML
    private HBox generalControls;
    
    @FXML
    private Button takePictureButton;
    
    @FXML 
    private ColorPicker colorPickerPath, colorPickerWall;
    
    @FXML
    private Slider speedSlider;
    
    public MainController(){   
        System.out.println("<> MainController");
        this.tileAnimator = new SequentialAnimator();
        this.countTiles = new SimpleDoubleProperty(0);
    }

    @Override
    protected Node getMainPane() {
        return this.mainPane;
    }
    
    public void addCellFactory(CellFactory cfactory){
        
        this.countTiles.set(cfactory.getCountOfNodes());
        
        this.rowsInput.setText(String.valueOf(cfactory.getRows()));
        this.columnsInput.setText(String.valueOf(cfactory.getColumns()));
        this.percentegeCycle = cfactory.getPercentegeCycle();
        
        this.colorPickerPath.valueProperty().set(cfactory.getPathColor());
        this.colorPickerWall.valueProperty().set(cfactory.getWallColor());
        
        this.canvasField.backgroundProperty().set(new Background(new BackgroundFill(cfactory.getWallColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        
        addListener(events.colorPathPickerChanged, 
                (EventData data) -> cfactory.resetColorPath(data.<Color>getData())
        );
        addListener(events.colorWallPickerChanged, 
                (EventData data) -> 
                        canvasField.setBackground(new Background(new BackgroundFill(data.<Color>getData(), CornerRadii.EMPTY, Insets.EMPTY)))
        );
        
        System.out.println("Canvas width: "  + this.canvasField.getWidth());
        System.out.println("Canvas height: " + this.canvasField.getHeight());
                        
        cfactory.setWidthEnvironment(this.canvasField.getWidth());
        cfactory.setHeightEnvironment(this.canvasField.getHeight());
        
        this.canvasField.widthProperty().addListener(
                (listener) -> cfactory.setWidthEnvironment(this.canvasField.getWidth())
        );
        this.canvasField.heightProperty().addListener(
                (listener) -> cfactory.setHeightEnvironment(this.canvasField.getHeight())
        );
                
        cfactory.addListener(CellFactory.events.cellCreated, (EventData edata) -> {
            
            this.canvasField.getChildren().add(
                    this.tileAnimator.addAnimation(new TileAnimation(edata.<Rectangle>getData(), this.tileAnimDuration)).tile
            );
            
            this.progressBar.setProgress(this.canvasField.getChildren().size() / this.countTiles.get());

            if(this.canvasField.getChildren().size() == this.countTiles.get()){
                this.initMazeRenderProgessbar();
                this.tileAnimator.addListener(SequentialAnimator.Events.NodeRendered, (observable) -> {
                    
                    this.progressBar.setProgress(this.tileAnimator.getRenderedTiles() / this.countTiles.get());
                });
            }
        });
    }
    
    @Override
    public String toString() {
        return getClass().getName();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("location: " + location);
        System.out.println("resources: " + resources);
        
        this.disabledFilter = initDisabledFilter(
                this.canvasField.widthProperty(), 
                this.canvasField.heightProperty(),
                this.mainPane.disableProperty());
        
        this.canvasField.getChildren().add(this.disabledFilter);
        
        this.rowsInput.textProperty().addListener( createInputValidationListenerForBackcolor(this.rowsInput) );
        this.rowsInput.textProperty().addListener( (Observable observable) -> { 
            if(validateUserInput(this.rowsInput.textProperty())){
                setRows(Integer.parseInt(this.rowsInput.textProperty().get()));
            }
        });
        this.columnsInput.textProperty().addListener( createInputValidationListenerForBackcolor(this.columnsInput));
        this.columnsInput.textProperty().addListener( (Observable observable) -> {
            if(validateUserInput(this.columnsInput.textProperty())){
                setColumns(Integer.parseInt(this.columnsInput.textProperty().get()));
            }
        });
        
        
        PlayStopForwardControl playStopForwardControl = new PlayStopForwardControl();
        playStopForwardControl.eventOffering.addListener(
                PlayStopForwardControl.events.play, 
                (eventData) -> {
                    if(acceptableMazeSizeValue(getRows()) && acceptableMazeSizeValue(getColumns())){
                        fireEvent(events.startMazeGeneration, new EventData(new MazeSize(getRows(), getColumns()), this));
                    }
                }
        );
        playStopForwardControl.eventOffering.addListener(PlayStopForwardControl.events.stop, (data) -> this.tileAnimator.stop());
        playStopForwardControl.eventOffering.addListener(PlayStopForwardControl.events.play, (data) -> this.tileAnimator.go());
        playStopForwardControl.eventOffering.addListener(PlayStopForwardControl.events.stepfordward, (data) -> this.tileAnimator.oneStep());
        playStopForwardControl.eventOffering.addListener(PlayStopForwardControl.events.forward, (data) -> this.tileAnimator.go());
        this.buttonsControlPane.getChildren().add(playStopForwardControl);        
        
        SwapArrowControl arrow = new SwapArrowControl();
        arrow.eventOffering.addListener(SwapArrowControl.events.swaped, (EventData data) -> {
            Color savedColor = this.colorPickerPath.valueProperty().get();
            this.colorPickerPath.valueProperty().set(this.colorPickerWall.valueProperty().get());
            this.colorPickerWall.valueProperty().set(savedColor);
        });
        this.colorSwapArrowPane.getChildren().add(arrow);
        
        CameraControl cameraControl = new CameraControl();
        this.takePictureButton.setGraphic(cameraControl);
        this.takePictureButton.setOnMouseClicked((mouseEvent) -> fireEvent(events.buttonTakePicture, new EventData(this.canvasField.snapshot(new SnapshotParameters(), null), this)));
        
        this.colorPickerPath.valueProperty().addListener((Observable observable) -> {
            fireEvent(events.colorPathPickerChanged, new EventData(((ObjectProperty)observable).getValue(), this));
        });
        
        this.colorPickerWall.valueProperty().addListener((Observable observable) -> {
            fireEvent(events.colorWallPickerChanged, new EventData(((ObjectProperty)observable).getValue(), this));
        });
        
        this.initMazeGenetareProgessbar();
    }            
    
    /* ++++++++ */
    /* ROUTINES */
    /* ++++++++ */    
    
    private InvalidationListener createInputValidationListenerForBackcolor(final TextField textField){
        
        InvalidationListener returnListener = (Observable listener) -> {
            
            Integer rowValue = 0;
            Color backColor = new Color(1, 0.5, 0.5, 1);
            
            try{ rowValue = Integer.parseInt(textField.textProperty().get()); } catch (Exception e){ }
            
            if(acceptableMazeSizeValue(rowValue)){
                backColor = new Color(0.5, 1, 0.5, 1);
            }
                        
            textField.backgroundProperty().set(new Background(new BackgroundFill(backColor, CornerRadii.EMPTY, Insets.EMPTY)));
        };
        
        return returnListener;
    }
    
    private boolean validateUserInput(StringProperty input){
        
        try{
            return acceptableMazeSizeValue(Integer.parseInt(input.get()));
        } catch (NumberFormatException numberExc){
            return false;
        }
    }
    
    private boolean acceptableMazeSizeValue(int dimension){
        return dimension > 0 && CalcRoutines.isOdd(dimension);
    } 
    
    private Rectangle initDisabledFilter(ReadOnlyDoubleProperty widthProp, ReadOnlyDoubleProperty heightProp, ReadOnlyBooleanProperty disability){
        
        Rectangle filter = new Rectangle();
        filter.fillProperty().set(new Color(0.8, 0.8, 0.8, 0.5));        
        
        filter.widthProperty().set(widthProp.get());
        filter.heightProperty().set(heightProp.get());
        
        filter.xProperty().set(0);
        filter.yProperty().set(0);
        
        filter.setVisible(disability.get());        
        
        widthProp.addListener(observable -> filter.widthProperty().set(widthProp.get()));
        heightProp.addListener(observable -> filter.heightProperty().set(heightProp.get()));
        disability.addListener(observable -> filter.visibleProperty().set(disability.get()));
        
        return filter;
    }

    @Override
    public void hideAnimation(Runnable callbackAfter) {
        setToBack();
        callbackAfter.run();
    }

    @Override
    public void showAnimation(Runnable callbackAfter) {
        setToFront();
        callbackAfter.run();
    }        
    
    public void setRows(int rows) {         
        this.rows = rows;
    }

    public void setColumns(int columns) {        
        this.columns = columns;
    }

    public int getColumns(){
        return this.columns;
    }
    
    public int getRows(){
        return this.rows;
    }    
    
    public int getPercentageCycles(){
        return this.percentegeCycle;
    }
}
