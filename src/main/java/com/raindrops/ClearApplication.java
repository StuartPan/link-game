package com.raindrops;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.collection.PropertyMap;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.ViewComponent;
import com.almasb.fxgl.input.Input;
import com.raindrops.enums.EntityTypeEnum;
import com.raindrops.factory.PuzzleFactory;
import com.raindrops.utils.RandomQueue;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.raindrops.constant.CommonConstant.*;

/**
 * GameApplication
 *
 * @author raindrops
 */
public class ClearApplication extends GameApplication {

    private static final List<Entity> ENTITY_LIST = new ArrayList<>();

    private static final Map<String, int[]> PARENT_MAP = new HashMap<>();

    private static int puzzleNum = 32;

    private static Text titleText;

    private static int LEVEL = 1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("连连看");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setVersion("1.1");
    }

    @Override
    protected void initGame() {
        FXGL.loopBGM("bgm.mp3");
        FXGL.getGameScene().setBackgroundColor(Color.rgb(255, 255, 155));
        titleText = new Text("第1关");
        titleText.setFont(new Font(30));
        titleText.setX((FXGL.getAppWidth() - titleText.getLayoutBounds().getWidth()) / 2);
        titleText.setY((titleText.getLayoutBounds().getWidth() + OFFSET_Y) / 2);
        FXGL.getGameScene().addUINode(titleText);
        this.createGrid();
    }

    @Override
    protected void initInput() {
        Input input = FXGL.getInput();
        input.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            MouseButton button = event.getButton();
            if (button == MouseButton.PRIMARY) {
                Entity clickedEntity = FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(event.getX(), event.getY(), 10, 10)).stream()
                        .findFirst()
                        .orElse(null);
                this.recordEntity(clickedEntity);
                this.checkClear();
            }
        });
    }

    /**
     * 判断是否消除
     *
     * @return 是否消除
     */
    private boolean isDead() {
        ArrayList<Entity> entityList = FXGL.getGameWorld().getEntities();
        Map<Integer, List<Entity>> map = entityList.stream().collect(Collectors.groupingBy(item -> {
            PropertyMap properties = item.getProperties();
            return properties.getInt("type");
        }));
        for (Map.Entry<Integer, List<Entity>> entry : map.entrySet()) {
            List<Entity> list = entry.getValue();
            for (int i = 0; i < list.size() - 1; i++) {
                for (int j = 1; j < list.size(); j++) {
                    boolean canConnect = this.canConnect(list.get(i), list.get(j));
                    if (canConnect) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 记录点击事件
     *
     * @param entity 实体
     */
    private void recordEntity(Entity entity) {
        if (entity == null) {
            return;
        }
        ViewComponent viewComponent = entity.getViewComponent();
        Optional<Node> bgOptional = viewComponent.getChildren().stream().filter(item -> "bg".equals(item.getId())).findFirst();
        bgOptional.ifPresent(item -> {
            ImageView bg = (ImageView) item;
            Image image = new Image("/assets/textures/active.png");
            bg.setImage(image);
            bg.setTranslateX(-18);
            bg.setTranslateY(-18);
        });
        if (ENTITY_LIST.size() < 2) {
            ENTITY_LIST.add(entity);
        }
    }

    /**
     * 判断消除
     */
    private void checkClear() {
        if (ENTITY_LIST.size() != 2) {
            return;
        }
        Entity firstEntity = ENTITY_LIST.get(0);
        Entity secondEntity = ENTITY_LIST.get(1);

        for (Entity entity : ENTITY_LIST) {
            entity.getViewComponent().getChildren().stream().filter(item -> "bg".equals(item.getId())).forEach(item -> {
                ImageView bg = (ImageView) item;
                Image image = new Image("/assets/textures/pic-bg.png");
                bg.setImage(image);
                bg.setTranslateX(0);
                bg.setTranslateY(0);
            });
        }

        boolean result = this.canConnect(firstEntity, secondEntity);
        if (result) {
            this.handleClear(firstEntity, secondEntity);
            if (this.isDead()) {
                FXGL.getDialogService().showMessageBox("无法移动，点击重组...", this::randomAllEntity);
            }
        } else {
            FXGL.play("clear_failed.wav");
        }
        ENTITY_LIST.clear();
        PARENT_MAP.clear();
    }

    /**
     * 处理消除
     *
     * @param firstEntity  第一个实体
     * @param secondEntity 第二个实体
     */
    private void handleClear(Entity firstEntity, Entity secondEntity) {
        PropertyMap firstProperties = firstEntity.getProperties();
        int startRow = firstProperties.getInt("row");
        int startCol = firstProperties.getInt("col");
        PropertyMap secondProperties = secondEntity.getProperties();
        int endRow = secondProperties.getInt("row");
        int endCol = secondProperties.getInt("col");
        List<int[]> path = new ArrayList<>();
        int[] current = {endRow, endCol, 0, 0};
        while (current[0] != startRow || current[1] != startCol) {
            path.add(current);
            current = PARENT_MAP.get(current[0] + "," + current[1]);
        }
        path.add(new int[]{startRow, startCol});
        Collections.reverse(path);
        List<Line> lines = new ArrayList<>();
        AtomicReference<Double> beforeX = new AtomicReference<>(startCol * ICON_SIZE + ICON_SIZE / 2d + OFFSET_X);
        AtomicReference<Double> beforeY = new AtomicReference<>(startRow * ICON_SIZE + ICON_SIZE / 2d + OFFSET_Y);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(30), e -> {
            if (path.size() == 0) {
                timeline.stop();
                lines.forEach(item -> FXGL.getGameScene().removeUINode(item));
                FXGL.play("clear_success.wav");
                FXGL.getGameWorld().removeEntities(firstEntity, secondEntity);
                // 判断游戏是否结束
                int puzzleNum = FXGL.getGameWorld().getEntitiesByType(EntityTypeEnum.PUZZLE).size();
                if (puzzleNum == 0) {
                    FXGL.getDialogService().showConfirmationBox("You Win! Play Again?", yes -> {
                        if (yes) {
                            this.createGrid();
                        } else {
                            FXGL.getGameController().exit();
                        }
                    });
                }
            } else {
                int[] position = path.remove(0);
                Line line = new Line();
                line.setStartX(beforeX.get());
                line.setStartY(beforeY.get());
                line.setEndX(position[1] * ICON_SIZE + ICON_SIZE / 2d + OFFSET_X);
                line.setEndY(position[0] * ICON_SIZE + ICON_SIZE / 2d + OFFSET_Y);
                line.setStroke(Color.BLACK);
                line.setStrokeWidth(5);
                FXGL.getGameScene().addUINode(line);
                lines.add(line);
                beforeX.set(line.getEndX());
                beforeY.set(line.getEndY());
            }
        }));
        timeline.setCycleCount(-1);
        timeline.play();
    }

    /**
     * 判断两个卡片是否能相连
     *
     * @param firstEntity  第一张卡片
     * @param secondEntity 第二张卡片
     * @return 是否能相连
     */
    private boolean canConnect(Entity firstEntity, Entity secondEntity) {
        PARENT_MAP.clear();
        if (firstEntity.equals(secondEntity)) {
            return false;
        }
        PropertyMap firstProperties = firstEntity.getProperties();
        int col1 = firstProperties.getInt("col");
        int row1 = firstProperties.getInt("row");
        int type1 = firstProperties.getInt("type");
        PropertyMap secondProperties = secondEntity.getProperties();
        int col2 = secondProperties.getInt("col");
        int row2 = secondProperties.getInt("row");
        int type2 = secondProperties.getInt("type");
        if (type1 != type2) {
            return false;
        }

        // BFS路径查找算法
        boolean[][] visited = new boolean[GRID_ROWS][GRID_COLS];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{row1, col1, -1, -1});
        visited[row1][col1] = true;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentRow = current[0];
            int currentCol = current[1];
            int direction = current[2];
            int turns = current[3];
            if (currentRow == row2 && currentCol == col2) {
                return true;
            }
            for (int i = 0; i < 4; i++) {
                int nextRow = currentRow + ROW_DIRECTION[i];
                int nextCol = currentCol + COL_DIRECTION[i];
                int newTurns = direction == i ? turns : turns + 1;
                if (newTurns > 2) {
                    continue;
                }
                if (nextRow == row2 && nextCol == col2) {
                    PARENT_MAP.put(nextRow + "," + nextCol, current);
                    return true;
                }
                // 检查是否越界
                if (nextRow < 0 || nextRow >= GRID_ROWS || nextCol < 0 || nextCol >= GRID_COLS) {
                    continue;
                }
                if (visited[nextRow][nextCol] || this.isBlocked(nextRow, nextCol)) {
                    continue;
                }
                visited[nextRow][nextCol] = true;
                PARENT_MAP.put(nextRow + "," + nextCol, current);
                queue.offer(new int[]{nextRow, nextCol, i, newTurns});
            }
        }
        return false;
    }

    /**
     * 判断当前位置是否有卡片
     *
     * @param row 行
     * @param col 列
     * @return 是否有卡片
     */
    private boolean isBlocked(int row, int col) {
        for (Entity entity : FXGL.getGameWorld().getEntities()) {
            PropertyMap properties = entity.getProperties();
            if (properties.getInt("row").equals(row) && properties.getInt("col").equals(col)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 初始化
     */
    private void createGrid() {
        titleText.setText("第" + (LEVEL++) + "关");

        RandomQueue<Integer> queue = new RandomQueue<>();
        for (int i = 0; i < (GRID_COLS - 2) * (GRID_ROWS - 2); i++) {
            queue.offer(i % puzzleNum);
        }

        for (int row = 1; row < GRID_ROWS - 1; row++) {
            for (int col = 1; col < GRID_COLS - 1; col++) {
                int iconIndex = queue.poll();
                Entity entity = PuzzleFactory.createEntity(iconIndex, row, col);
                FXGL.getGameWorld().addEntity(entity);
            }
        }
    }

    /**
     * 重新随机排列
     */
    private void randomAllEntity() {
        ArrayList<Entity> entityList = FXGL.getGameWorld().getEntities();
        RandomQueue<int[]> queue = new RandomQueue<>();
        for (int i = 1; i < GRID_ROWS - 1; i++) {
            for (int j = 1; j < GRID_COLS - 1; j++) {
                int[] position = new int[]{i, j};
                queue.offer(position);
            }
        }
        for (Entity entity : entityList) {
            int[] position = queue.poll();
            entity.setPosition(new Point2D(OFFSET_X + position[1] * ICON_SIZE, OFFSET_Y + position[0] * ICON_SIZE));
            entity.setProperty("col", position[1]);
            entity.setProperty("row", position[0]);
        }
    }
}