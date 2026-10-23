package com.restaurant.util;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResponsiveLayoutHelperTest {

    @BeforeAll
    public static void initJavaFX() {
        // Initialize JavaFX toolkit if needed in test environment
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Toolkit already initialized
        }
    }

    @Test
    @DisplayName("Should identify mobile/portrait vs desktop widescreen aspect ratios")
    public void testIsMobileAspect() {
        // Desktop / Widescreen resolutions
        assertFalse(ResponsiveLayoutHelper.isMobileAspect(1920, 1080), "1920x1080 should be desktop");
        assertFalse(ResponsiveLayoutHelper.isMobileAspect(1366, 768), "1366x768 should be desktop");
        assertFalse(ResponsiveLayoutHelper.isMobileAspect(1280, 800), "1280x800 should be desktop");
        assertFalse(ResponsiveLayoutHelper.isMobileAspect(1024, 768), "1024x768 should be desktop");

        // Mobile / Narrow resolutions
        assertTrue(ResponsiveLayoutHelper.isMobileAspect(400, 800), "400x800 should be mobile");
        assertTrue(ResponsiveLayoutHelper.isMobileAspect(480, 854), "480x854 should be mobile");
        assertTrue(ResponsiveLayoutHelper.isMobileAspect(600, 900), "600x900 should be mobile");
        assertTrue(ResponsiveLayoutHelper.isMobileAspect(768, 1024), "768x1024 portrait tablet should be mobile");
        assertTrue(ResponsiveLayoutHelper.isMobileAspect(850, 800), "850 width should be mobile");
    }

    @Test
    @DisplayName("Should switch SplitPane orientation between HORIZONTAL and VERTICAL")
    public void testApplyResponsiveFormat() {
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(new VBox(), new VBox());
        splitPane.setOrientation(Orientation.HORIZONTAL);

        // Switch to mobile format
        ResponsiveLayoutHelper.applyResponsiveFormat(splitPane, true);
        assertEquals(Orientation.VERTICAL, splitPane.getOrientation(), "Mobile format must set VERTICAL orientation");

        // Switch back to desktop format
        ResponsiveLayoutHelper.applyResponsiveFormat(splitPane, false);
        assertEquals(Orientation.HORIZONTAL, splitPane.getOrientation(), "Desktop format must set HORIZONTAL orientation");
    }
}
