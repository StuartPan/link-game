package com.raindrops.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.raindrops.component.PuzzleComponent;
import com.raindrops.enums.EntityTypeEnum;

import static com.raindrops.constant.CommonConstant.*;

/**
 * PuzzleFactory
 *
 * @author raindrops
 */
public class PuzzleFactory {

    public static Entity createEntity(int iconIndex, int row, int col) {
        Entity entity = FXGL.entityBuilder()
                .at(OFFSET_X + col * ICON_SIZE, OFFSET_Y + row * ICON_SIZE)
                .type(EntityTypeEnum.PUZZLE)
//                .viewWithBBox(FXGL.texture(iconIndex + ".png"))
                .bbox(new HitBox(BoundingShape.box(ICON_SIZE, ICON_SIZE)))
                .with(new PuzzleComponent(iconIndex))
                .build();
        entity.setProperty("type", iconIndex);
        entity.setProperty("col", col);
        entity.setProperty("row", row);
        return entity;
    }
}