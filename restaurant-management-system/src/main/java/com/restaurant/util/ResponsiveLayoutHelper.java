package com.restaurant.util;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

/**
 * Utility helper to handle responsive format switching between Desktop (Full Screen)
 * and Mobile (Portrait / Narrow Screen) across all views in the Restaurant Management System.
 */
public class ResponsiveLayoutHelper {

    /**
     * Determines whether the current window dimensions represent a mobile or portrait aspect ratio.
     *
     * @param width  Current window width
     * @param height Current window height
     * @return true if mobile aspect ratio or narrow screen, false if full desktop
     */
    public static boolean isMobileAspect(double width, double height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        double ratio = width / height;
        // Mobile criteria:
        // 1. Width under 880px (e.g. phones, small tablets, half-screen snap)
        // 2. Aspect ratio < 1.15 (portrait tablets, phone screens)
        return width < 880 || ratio < 1.15;
    }

    /**
     * Traverses the node hierarchy and updates any SplitPane to match the target format:
     * - Mobile format: Orientation.VERTICAL (table on top, form/action panel on bottom)
     * - Desktop format: Orientation.HORIZONTAL (table on left, form/action panel on right)
     *
     * @param root     The root container or loaded view node
     * @param isMobile true for mobile format, false for desktop full-screen format
     */
    public static void applyResponsiveFormat(Node root, boolean isMobile) {
        if (root == null) {
            return;
        }

        if (root instanceof SplitPane) {
            applyToSplitPane((SplitPane) root, isMobile);
        }

        if (root instanceof Parent) {
            List<SplitPane> splitPanes = findSplitPanes((Parent) root);
            for (SplitPane sp : splitPanes) {
                applyToSplitPane(sp, isMobile);
            }
        }
    }

    private static void applyToSplitPane(SplitPane sp, boolean isMobile) {
        Orientation targetOrientation = isMobile ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        if (sp.getOrientation() != targetOrientation) {
            sp.setOrientation(targetOrientation);
            if (isMobile) {
                sp.setDividerPositions(0.48);
            } else {
                sp.setDividerPositions(0.65);
            }
        }
    }

    /**
     * Recursively locates all SplitPanes within a parent container.
     */
    public static List<SplitPane> findSplitPanes(Parent parent) {
        List<SplitPane> list = new ArrayList<>();
        if (parent instanceof SplitPane) {
            SplitPane sp = (SplitPane) parent;
            for (Node item : sp.getItems()) {
                if (item instanceof SplitPane) {
                    list.add((SplitPane) item);
                }
                if (item instanceof Parent) {
                    list.addAll(findSplitPanes((Parent) item));
                }
            }
        } else {
            for (Node child : parent.getChildrenUnmodifiable()) {
                if (child instanceof SplitPane) {
                    list.add((SplitPane) child);
                }
                if (child instanceof Parent) {
                    list.addAll(findSplitPanes((Parent) child));
                }
            }
        }
        return list;
    }
}
