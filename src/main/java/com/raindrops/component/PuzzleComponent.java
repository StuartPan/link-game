package com.raindrops.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * PuzzleComponent
 *
 * @author raindrops
 */
public class PuzzleComponent extends Component {

    private final int iconIndex;

    public PuzzleComponent(int iconIndex) {
        this.iconIndex = iconIndex;
    }

    /**
     * Called after the component is added to entity.
     */
    @Override
    public void onAdded() {
        ImageView backgroundImage = new ImageView(new Image("/assets/textures/pic-bg.png"));
        backgroundImage.setId("bg");
        entity.getViewComponent().addChild(backgroundImage);

        ImageView puzzleImage = new ImageView(new Image("/assets/textures/" + iconIndex + ".png"));
        puzzleImage.setTranslateX(8);
        puzzleImage.setTranslateY(8);
        puzzleImage.setId("puzzle");
        entity.getViewComponent().addChild(puzzleImage);
    }
}