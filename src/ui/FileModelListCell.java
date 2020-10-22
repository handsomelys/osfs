package ui;

import filesystem.model.FileModel;
import javafx.scene.control.ListCell;

public class FileModelListCell extends ListCell<FileModel> {
    public static final String COLUMN_1_MAP_KEY = "name";
    public static final String COLUMN_2_MAP_KEY = "type";
    public static final String COLUMN_3_MAP_KEY = "size";
    public static final String COLUMN_4_MAP_KEY = "readonly";
    public static final String COLUMN_5_MAP_KEY = "startindex";
}
