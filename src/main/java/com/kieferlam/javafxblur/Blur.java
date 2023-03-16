package com.kieferlam.javafxblur;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.stage.Stage;

/**
 * Singleton handler enum class Blur. This class provides global methods to load
 * and apply blur effects to a JavaFX stage.
 */
public enum Blur {

	NONE(0), BLUR_BEHIND(3), ACRYLIC(4);

	private static final Logger LOGGER = LoggerFactory.getLogger(Blur.class);

	private static final String BLUR_TARGET_PREFIX = "_JFX";

	private static final NativeBlur NATIVE_BLUR = new NativeBlur();

	private final int accentState;

	Blur(int accentState) {
		this.accentState = accentState;
	}

	private static void extApplyBlur(String target, int accentState) {
		NATIVE_BLUR._extApplyBlur(target, accentState);
	}

	/**
	 * Loads the required blur library. This should be called at the very start of
	 * your main function. The "javafxblur" library file should be added to your
	 * library path.
	 */
	public static void loadBlurLibrary() {
		if (isJar()) {
			// In a jar file; extract .dll with NativeUtils (which also calls System.load).
			try {
				NativeUtils.loadLibraryFromJar("/javafxblur.dll");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// We are not in a jar file, which means we can assume the .dll file can be
			// accessed directly (development environment).
			// Load the javafxblur.dll library directory through System.load with the
			// directory pointing to our resources folder.
			try {
				var url = Blur.class.getResource("/javafxblur.dll");
				var dll = new File(url.toString().replace("file:/", "").replace("%20", " "));
				System.load(dll.getAbsolutePath());
			} catch (Exception e) {
				LOGGER.error("Unable to load javafxblur.dll", e);
			}
		}
	}

	/**
	 * Returns whether this application is running from a jar file.
	 *
	 * <p>
	 * Used for determining whether to extract .dll files before loading them.
	 *
	 * @return whether this application is running from a jar file
	 */
	public static boolean isJar() {
		return Blur.class.getResource("Blur.class").toString().startsWith("jar:");
	}

	/**
	 * Calls the external (native) function to apply the blur effect to a JavaFX
	 * stage. The JavaFX stage must be visible before this function is called. If
	 * the stage is ever hidden (destroyed, not minimised), this function must be
	 * called again once visible.
	 *
	 * @param stage
	 */
	public static void applyBlur(Stage stage, Blur blur) {
		if (!stage.isShowing()) {
			LOGGER.warn("Blur effect was called on a hidden stage");
		}
		var stageTitle = stage.getTitle();
		var targetTitle = BLUR_TARGET_PREFIX + (System.currentTimeMillis() % 1000);
		stage.setTitle(targetTitle);
		try {
			extApplyBlur(targetTitle, blur.accentState);
		} catch (UnsatisfiedLinkError e) {
			LOGGER.error("Unable to call native method", e);
		}
		stage.setTitle(stageTitle);
	}

}
