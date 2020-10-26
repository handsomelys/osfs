package ui;

import java.util.ArrayList;
import java.util.List;

import filesystem.model.DiskModel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class DiskViewer extends GridPane {
    
    private DiskModel disk;
    private List<StackPane> display;

    public DiskViewer(DiskModel disk) {
        super();
        this.disk = disk;
        this.setStyle("-fx-alignment: TOP-CENTER;-fx-background-color: #ffffff");
        this.setVgap(5.0D);
        this.setHgap(5.0D);
        this.setPadding(new Insets(1.0D, 1.0D, 1.0D, 1.0D));
        display = new ArrayList<StackPane>();

        for(int i = 0; i < 128; ++i) {
            Text numberLabel = new Text(String.valueOf(i));
            final StackPane stackPane = new StackPane();
            stackPane.getChildren().add(numberLabel);
            stackPane.setStyle("-fx-background-color: #c8c8c8");
            stackPane.setOnMouseEntered(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent event) {
                stackPane.setEffect(new DropShadow());
                }
            });
            stackPane.setOnMouseExited(new EventHandler<MouseEvent>()
            {
                public void handle(MouseEvent event) {
                stackPane.setEffect(null);
                }
            });
            display.add(i, stackPane);
            this.add(stackPane, i % 8, i / 8);
        }
        this.update();
    }
    
    public void update() {
        int[] fat = this.disk.getFat().getTable();
        for (int i = 0; i < fat.length; ++i) {
            if (fat[i] == 0) {
                ((StackPane) this.display.get(i)).setStyle("-fx-background-color: #c8c8c8");
            } else {
                ((StackPane) this.display.get(i)).setStyle("-fx-background-color: #66ccff");
            }
        }
    }
}
