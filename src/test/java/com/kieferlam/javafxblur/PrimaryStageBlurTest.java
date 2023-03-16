package com.kieferlam.javafxblur;

import java.util.concurrent.atomic.AtomicReference;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PrimaryStageBlurTest extends Application {

	public static final Color TITLE_BAR_COLOR = new Color(Color.SLATEGREY.getRed(), Color.SLATEGREY.getGreen(),
			Color.SLATEGREY.getBlue(), 0.5);

	public static final Background TITLE_BAR_BACKGROUND = new Background(
			new BackgroundFill(TITLE_BAR_COLOR, CornerRadii.EMPTY, Insets.EMPTY));

	public static final Color HIGHLIGHT_COLOR = new Color(Color.SLATEGREY.getRed(), Color.SLATEGREY.getGreen(),
			Color.SLATEGREY.getBlue(), 0.9);

	public static final Background HIGHLIGHT_BACKGROUND = new Background(
			new BackgroundFill(HIGHLIGHT_COLOR, CornerRadii.EMPTY, Insets.EMPTY));

	public static final Color TEXT_COLOR = Color.GHOSTWHITE;

	public static void main(String[] args) {
		Blur.loadBlurLibrary();
		launch(args);
	}

	public static Pane createTitleBar(Stage primaryStage, StringProperty titleProperty) {
		var titlebar = new AnchorPane();

		titlebar.setBackground(TITLE_BAR_BACKGROUND);

		var titleText = new Text(0, 0, titleProperty.getValue());
		titleText.textProperty().bind(titleProperty);
		titleText.setFill(TEXT_COLOR);
		titleText.setFont(new Font(14.0));
		var titleTextContainer = new HBox();
		titleTextContainer.getChildren().add(titleText);

		var titlebarControls = new HBox();
		var minBtn = new Button("Min");
		minBtn.setBackground(TITLE_BAR_BACKGROUND);
		var maxBtn = new Button("Max");
		maxBtn.setBackground(TITLE_BAR_BACKGROUND);
		var closeBtn = new Button("Close");
		closeBtn.setBackground(TITLE_BAR_BACKGROUND);

		closeBtn.setOnAction(event -> primaryStage.close());

		titlebarControls.getChildren().addAll(minBtn, maxBtn, closeBtn);
		titlebarControls.getChildren().forEach(node -> {
			((Button) node).setTextFill(TEXT_COLOR);
			node.addEventHandler(MouseEvent.MOUSE_ENTERED,
					mouseEvent -> ((Button) node).setBackground(HIGHLIGHT_BACKGROUND));
			node.addEventHandler(MouseEvent.MOUSE_EXITED,
					mouseEvent -> ((Button) node).setBackground(TITLE_BAR_BACKGROUND));
		});

		titlebar.getChildren().add(titleTextContainer);
		titlebar.getChildren().add(titlebarControls);

		var stageDragOffset = new AtomicReference<>(new Point2D(0.0, 0.0));
		titlebar.addEventHandler(MouseEvent.MOUSE_PRESSED,
				mouseEvent -> stageDragOffset.set(new Point2D(mouseEvent.getSceneX(), mouseEvent.getSceneY())));
		titlebar.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
			primaryStage.setX(mouseEvent.getScreenX() - stageDragOffset.get().getX());
			primaryStage.setY(mouseEvent.getScreenY() - stageDragOffset.get().getY());
		});

		AnchorPane.setLeftAnchor(titleTextContainer, Double.valueOf(5.0));
		AnchorPane.setRightAnchor(titlebarControls, Double.valueOf(0.0));

		return titlebar;
	}

	public static Pane createContentPane(Stage primaryStage) {
		var pane = new BorderPane();

		pane.setBackground(new Background(new BackgroundFill(Color.gray(0.2, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));

		var content = new Text("Content");
		content.setFont(new Font(72.0));
		content.setFill(TEXT_COLOR);

		pane.setCenter(content);

		pane.prefWidthProperty().bind(primaryStage.widthProperty());
		pane.prefHeightProperty().bind(primaryStage.heightProperty());

		return pane;
	}

	@Override
	public void start(Stage primaryStage) {
		var titlebar = createTitleBar(primaryStage, new SimpleStringProperty("JavaFX Blur Test"));
		var contentPane = createContentPane(primaryStage);

		var root = new AnchorPane();
		root.getChildren().addAll(titlebar, contentPane);
		AnchorPane.setTopAnchor(titlebar, Double.valueOf(0.0));
		AnchorPane.setTopAnchor(contentPane, Double.valueOf(24.0));

		root.setBackground(Background.EMPTY);
		var scene = new Scene(root, 640.0, 480.0);
		scene.setFill(Color.TRANSPARENT);

		titlebar.prefWidthProperty().bind(scene.widthProperty());
		titlebar.prefHeight(24.0);

		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setTitle("JavaFX");

		primaryStage.setScene(scene);
		primaryStage.show();

		Blur.applyBlur(primaryStage, Blur.ACRYLIC);
	}

}
