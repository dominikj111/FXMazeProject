package testmazegenerator;

import EventLibrary.EventData;
import EventLibrary.EventOffererHandler;
import FxUtils.Window;
import controllers.MainController;
import Generator.MazeBuilder;
import MazeDefinitions.MazeDataWorkThread;
import controllers.ImgSaveController;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class TestMazeGenerator extends Application {
    
    private Window<VBox, MainController> mainView;
    private Window<VBox, ImgSaveController> picModal;
    
    private Stage primaryStage;
    
    private CellFactory cfactory;
    
    private static int columns  = 11;
    private static int rows = 11;
    private static int percentageCycles = 0;
    
    private static int sliderValue = 150;
    private static int sliderMaxValue = 1000;
    private static int sliderMinValue = 1;
    
    private static Color pathColor = Color.GREENYELLOW;
    private static Color wallColor = Color.GREEN;
    
    @Override
    public void start(Stage primaryStage) throws IOException {        
        
        this.primaryStage = primaryStage;
        
        mainView = Window.loadWindow("/views/MainView.fxml", true, true);
        picModal = Window.loadWindow("/views/ImgSave.fxml");
        
        /*=======================*/
        /* DEFINE WINDOW TO SHOW */
        /*=======================*/
        
        Scene scene = new Scene(Window.getMainPane());
        scene.getStylesheets().add(getClass().getResource("controlIcons.css").toExternalForm());        
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fx - Maze demo tester");
        primaryStage.show();
        
        primaryStage.minWidthProperty().set(
                primaryStage.widthProperty().get() - primaryStage.getScene().widthProperty().get() + Window.getMainPane().minWidthProperty().get()
        );
        
        primaryStage.minHeightProperty().set(
                primaryStage.heightProperty().get() - primaryStage.getScene().heightProperty().get() + Window.getMainPane().minHeightProperty().get()
        );
        
        /*=============================*/
        /* GENERATE MAZE AND UTILITIES */
        /*=============================*/                       
        
        cfactory = new CellFactory(pathColor, wallColor);
        
        mainView.getController().initSlider(sliderMinValue, sliderValue, sliderMaxValue);
        
        cfactory.setRows(rows);
        cfactory.setColumns(columns);
        cfactory.setPercentageCycle(percentageCycles);
        
        mainView.getController().addCellFactory(cfactory);
        
        mainView.getController().addListener(MainController.events.startMazeGeneration,  startMazeGenerationHandler);
        mainView.getController().addListener(MainController.events.buttonTakePicture,   takePictureHandler);
        
        picModal.getController().addListener(ImgSaveController.events.cancelButtonClicked, (data) -> mainView.toFront() );
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
    
    /*===================*/
    /* HANDLER FUNSTIONS */
    /*===================*/
    
    private EventOffererHandler startMazeGenerationHandler = (data) -> {                
        
        MainController con = (MainController)data.getSource();
        cfactory.newMazeRecord(con.getColumns(), con.getRows());
        con.newMaze();
//        MazeData md = MazeBuilder.generateMaze(con.getColumns(), con.getRows(), con.getPercentageCycles(), cfactory);
        MazeDataWorkThread md = MazeBuilder.generateMazeWork(con.getColumns(), con.getRows(), con.getPercentageCycles(), cfactory);
        try {
            md.doMazeGenerating();
        } catch (Exception ex){
            System.out.println("Start of doing maze exception: " + ex.getMessage());
        }
    };        
    
    private EventOffererHandler takePictureHandler = (data) -> {
        picModal.toFront();
                    
        Image img = data.getData();
        picModal.getController().setImage(img); 
                    
        picModal.getController().addListener(ImgSaveController.events.saveButtonClicked, eventData -> {
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Image");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            fileChooser.getExtensionFilters().add(new ExtensionFilter("PNG file (*.png)", "*.png"));
            fileChooser.setInitialFileName("maze.png");            
            
            File file = fileChooser.showSaveDialog(this.primaryStage);
            if(file == null) { return; }
            
            // multi save dialog
            // check maths equations and do it output here
            
            try {
                
                file.deleteOnExit();
                
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", file);
            } catch (IOException e) {
                System.out.println("Can't save image to " + file);
            }
        });
//                    nodeValuesSolver.colorState = !nodeValuesSolver.colorState;
                     
//                    fieldPresenter.reFillRectangles();
                    
                    // Node:ImageView
                    // StackPane <- ImageView, mainPane
                    //http://code.makery.ch/blog/javafx-2-snapshot-as-png-image/
                    //https://docs.oracle.com/javase/8/javafx/api/javafx/scene/image/ImageView.html
    };
}
