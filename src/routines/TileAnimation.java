package routines;

import DeferedLibrary.SimpleDefered;
import FxUtils.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.util.Duration;

public class TileAnimation implements Animation{

    public final Node tile;
    public final SimpleDoubleProperty durationTime;
    
    private SimpleDoubleProperty endBlurValue, targetBlurValue;
    private GaussianBlur blur;
    private Timeline timeline;
    
    public TileAnimation(Node tile, SimpleDoubleProperty durationTime){
        this.tile = tile;
        this.durationTime = durationTime;
        
        this.endBlurValue = new SimpleDoubleProperty(0);
        this.targetBlurValue = new SimpleDoubleProperty(63);
        this.blur = new GaussianBlur(targetBlurValue.get());
        
        this.targetBlurValue.addListener((observable, oldV, newV) -> blur.setRadius(newV.doubleValue()));
        this.tile.setVisible(false);
        this.tile.setEffect(blur);
    }
    
    public SimpleDefered run(){
        
        final SimpleDefered defered = SimpleDefered.defer();

        defered.preserve(this.tile);
        
        Platform.runLater(() -> {
            this.timeline = new Timeline();
            this.timeline
                    .getKeyFrames()
                        .add(new KeyFrame(
                                Duration.millis(this.durationTime.getValue()), 
                                new KeyValue(targetBlurValue, endBlurValue.get())
                        ));
            timeline.setOnFinished((ActionEvent event) -> defered.resolveIt());
            this.tile.setVisible(true);
            timeline.play();
        });

        return defered;
    }
}
