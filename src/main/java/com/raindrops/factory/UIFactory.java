package com.raindrops.factory;

import com.raindrops.enums.EntityTypeEnum;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import static com.raindrops.constant.CommonConstant.*;

/**
 * UIFactory
 *
 * @author raindrops
 */
public class UIFactory {

    public static Rectangle createButton(EntityTypeEnum type) {
        Rectangle refresh = new Rectangle(50, 50);
        if (type == EntityTypeEnum.REFRESH) {
            refresh.setFill(new ImagePattern(new Image("/assets/textures/ui/refresh.png")));
            refresh.setX(REFRESH_OFFSET_X);
            refresh.setY(REFRESH_OFFSET_Y);
        } else if (type == EntityTypeEnum.TIPS) {
            refresh.setFill(new ImagePattern(new Image("/assets/textures/ui/tips.png")));
            refresh.setX(TIPS_OFFSET_X);
            refresh.setY(TIPS_OFFSET_Y);
        }
        refresh.setOnMouseEntered(e -> {
            refresh.setScaleX(1.1);
            refresh.setScaleY(1.1);
        });
        refresh.setOnMouseExited(e -> {
            refresh.setScaleX(1);
            refresh.setScaleY(1);
        });
        return refresh;
    }
}