package memory;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import process.PCB;

public class MemoryRectangle
        extends Pane
{
    private static Pane memorypane=MemoryDispatcher.memoryPane;
    private static Pane ospane;
    private Color[] colors = new Color[] { Color.AQUA, Color.BURLYWOOD, Color.CORNFLOWERBLUE, Color.RED, Color.YELLOW, Color.YELLOWGREEN, Color.GREEN };
    private Rectangle rectangle;
    private Pane pane;
    private ProgressIndicator pi;
    private Label pidLabel;

    public MemoryRectangle( double x ,double width, double height, final PCB pcb) {
        final double true_x=x/512D;
        final double true_width=width/512D;
        this.maxHeightProperty().set(USE_COMPUTED_SIZE);
        this.maxWidthProperty().set(USE_COMPUTED_SIZE);
        this.layoutXProperty().bind(memorypane.widthProperty().multiply(true_x));
        this.prefHeightProperty().bind(memorypane.heightProperty());
        this.prefWidthProperty().bind(memorypane.widthProperty().multiply(true_width));
        this.setBackground(new Background(new BackgroundFill(getRandomColor(),null,null)));
        this.pi = new ProgressIndicator();
        this.pi.setLayoutY(10.0D);
        this.pi.prefWidthProperty().bind(this.widthProperty());
        this.pi.prefHeightProperty().bind(this.widthProperty());
        // System.out.println(memorypane.widthProperty().multiply(true_x));
        // System.out.println(this.prefWidthProperty());
        this.pi.setProgress(0.0D);
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                while (pcb.getResttime() != 0.0D) {
                    Platform.runLater(new Runnable()
                    {
                        public void run() {
                            MemoryRectangle.this.pi.setProgress(pcb.getProgress());
                        }
                    });
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                MemoryRectangle.this.pi.setProgress(pcb.getProgress());
            }
        });
        thread.setDaemon(true);
        thread.setName("memoryractangle-run");
        thread.start();

        getChildren().add(this.pi);
        this.pidLabel = new Label(pcb.getName());
        this.pidLabel.setLayoutY(100.0D);
        this.pidLabel.setAlignment(Pos.CENTER);
        this.pidLabel.layoutXProperty().bind(this.prefWidthProperty().multiply(0.5).subtract(10));
        getChildren().add(this.pidLabel);

    }

    private Color getRandomColor() {
        return this.colors[(int)(Math.random() * this.colors.length)];
    }
}