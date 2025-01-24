package com.raindrops.component;

import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.raindrops.enums.EntityTypeEnum;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.List;

import static com.raindrops.constant.CommonConstant.*;

/**
 * PuzzleComponent
 *
 * @author raindrops
 */
public class PuzzleComponent extends Component {

    private final int iconIndex;

    private final int moveDirection;

    public PuzzleComponent(int iconIndex, int moveDirection) {
        this.iconIndex = iconIndex;
        this.moveDirection = moveDirection;
    }

    /**
     * Called after the component is added to entity.
     */
    @Override
    public void onAdded() {
        ImageView backgroundImage = new ImageView(new Image("/assets/textures/ui/pic-bg.png"));
        backgroundImage.setId("bg");
        backgroundImage.setOnMouseEntered(e -> entity.setScaleUniform(1.1));
        backgroundImage.setOnMouseExited(e -> entity.setScaleUniform(1));
        entity.getViewComponent().addChild(backgroundImage);

        ImageView puzzleImage = new ImageView(new Image("/assets/textures/puzzle/" + iconIndex + ".png"));
        puzzleImage.setTranslateX(8);
        puzzleImage.setTranslateY(8);
        puzzleImage.setId("puzzle");
        entity.getViewComponent().addChild(puzzleImage);
    }

    @Override
    public void onRemoved() {
        if (moveDirection == 0) {
            return;
        }
        PropertyMap properties = entity.getProperties();
        int row = properties.getInt("row");
        int col = properties.getInt("col");
        Timeline timeline = new Timeline();
        switch (moveDirection) {
            // 向左移动
            case 1 -> {
                List<Entity> sameRowList = FXGL.getGameWorld().getEntitiesByType(EntityTypeEnum.PUZZLE).stream().filter(item -> {
                    PropertyMap itemProperties = item.getProperties();
                    return itemProperties.getInt("row") == row && itemProperties.getInt("col") > col;
                }).toList();
                for (Entity sameRowEntity : sameRowList) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> {
                        PropertyMap sameRowProperties = sameRowEntity.getProperties();
                        int newCol = sameRowProperties.getInt("col") - 1;
                        sameRowProperties.setValue("col", newCol);
                        sameRowEntity.setX(newCol * ICON_SIZE + OFFSET_X);
                    }));
                }
            }
            // 向右移动
            case 2 -> {
                List<Entity> sameRowList = FXGL.getGameWorld().getEntitiesByType(EntityTypeEnum.PUZZLE).stream().filter(item -> {
                    PropertyMap itemProperties = item.getProperties();
                    return itemProperties.getInt("row") == row && itemProperties.getInt("col") < col;
                }).toList();
                for (Entity sameRowEntity : sameRowList) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> {
                        PropertyMap sameRowProperties = sameRowEntity.getProperties();
                        int newCol = sameRowProperties.getInt("col") + 1;
                        sameRowProperties.setValue("col", newCol);
                        sameRowEntity.setX(newCol * ICON_SIZE + OFFSET_X);
                    }));
                }
            }
            // 向上移动
            case 3 -> {
                List<Entity> sameColList = FXGL.getGameWorld().getEntitiesByType(EntityTypeEnum.PUZZLE).stream().filter(item -> {
                    PropertyMap itemProperties = item.getProperties();
                    return itemProperties.getInt("col") == col && itemProperties.getInt("row") > row;
                }).toList();
                for (Entity sameColEntity : sameColList) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> {
                        PropertyMap sameColProperties = sameColEntity.getProperties();
                        int newRow = sameColProperties.getInt("row") - 1;
                        sameColProperties.setValue("row", newRow);
                        sameColEntity.setY(newRow * ICON_SIZE + OFFSET_Y);
                    }));
                }
            }
            // 向下移动
            case 4 -> {
                List<Entity> sameColList = FXGL.getGameWorld().getEntitiesByType(EntityTypeEnum.PUZZLE).stream().filter(item -> {
                    PropertyMap itemProperties = item.getProperties();
                    return itemProperties.getInt("col") == col && itemProperties.getInt("row") < row;
                }).toList();
                for (Entity sameColEntity : sameColList) {
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(200), e -> {
                        PropertyMap sameColProperties = sameColEntity.getProperties();
                        int newRow = sameColProperties.getInt("row") + 1;
                        sameColProperties.setValue("row", newRow);
                        sameColEntity.setY(newRow * ICON_SIZE + OFFSET_Y);
                    }));
                }
            }
        }
        timeline.setCycleCount(1);
        timeline.play();
    }
}