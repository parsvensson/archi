/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.factory.IGraphicalObjectUIProvider;
import com.archimatetool.editor.ui.factory.IObjectUIProvider;
import com.archimatetool.editor.ui.factory.ObjectUIFactory;
import com.archimatetool.editor.ui.services.ViewManager;
import com.archimatetool.editor.views.tree.ITreeModelView;
import com.archimatetool.editor.views.tree.TreeModelView;
import com.archimatetool.model.IArchimatePackage;


/**
 * Factory for creating themed icons that use the default fill colors from preferences.
 * Icons are created by taking the original PNG icon and replacing the inbuilt default
 * fill color with the current theme color.
 *
 * The ImageDescriptors returned are "live" - they always use the current theme color
 * when getImageData() is called, so they automatically reflect color changes.
 *
 * @author Phillip Beauvoir
 */
public class ThemedIconFactory {

    // Cache for rendered Images keyed by EClass name + color string
    // This cache is cleared when colors change
    private static Map<String, Image> imageCache = new ConcurrentHashMap<>();

    /**
     * Get a themed Image for an EClass using the default fill color from preferences.
     * Falls back to static icon if theming is not possible.
     *
     * @param eClass The EClass for the ArchiMate element
     * @return The themed Image, or null if not available
     */
    public static Image getThemedImage(EClass eClass) {
        if(eClass == null || !isThemedElement(eClass)) {
            return null;
        }

        IObjectUIProvider provider = ObjectUIFactory.INSTANCE.getProviderForClass(eClass);
        if(!(provider instanceof IGraphicalObjectUIProvider graphicalProvider)) {
            return null;
        }

        // Get the inbuilt default color (baked into PNG) and theme color
        Color inbuiltColor = graphicalProvider.getDefaultColor();
        Color themeColor = ColorFactory.getDefaultFillColor(eClass);

        // If colors are the same, no need for themed icon
        if(inbuiltColor == null || themeColor == null || colorsEqual(inbuiltColor, themeColor)) {
            return null;
        }

        // Cache key includes current color so we get a new image when color changes
        String key = eClass.getName() + ColorFactory.convertColorToString(themeColor);

        return imageCache.computeIfAbsent(key, k -> {
            ImageDescriptor descriptor = getThemedImageDescriptor(eClass);
            return descriptor != null ? descriptor.createImage() : null;
        });
    }

    /**
     * Get a themed ImageDescriptor for an EClass.
     * Returns a NEW ImageDescriptor each time so that GEF will create fresh images
     * when palette entries are recreated after color changes.
     *
     * @param eClass The EClass for the ArchiMate element
     * @return The themed ImageDescriptor, or null if not available
     */
    public static ImageDescriptor getThemedImageDescriptor(EClass eClass) {
        if(eClass == null || !isThemedElement(eClass)) {
            return null;
        }

        IObjectUIProvider provider = ObjectUIFactory.INSTANCE.getProviderForClass(eClass);
        if(!(provider instanceof IGraphicalObjectUIProvider graphicalProvider)) {
            return null;
        }

        // Get the inbuilt default color - if none, can't theme
        Color inbuiltColor = graphicalProvider.getDefaultColor();
        if(inbuiltColor == null) {
            return null;
        }

        // Get current theme color
        Color themeColor = ColorFactory.getDefaultFillColor(eClass);

        // If theme color matches inbuilt color, no theming needed
        if(themeColor == null || colorsEqual(inbuiltColor, themeColor)) {
            return null;
        }

        // Return a NEW descriptor each time (don't cache) so GEF creates fresh images
        return createColorReplacedImageDescriptor(provider, inbuiltColor.getRGB(), themeColor.getRGB());
    }

    /**
     * Create an ImageDescriptor that replaces the source color with the target color.
     */
    private static ImageDescriptor createColorReplacedImageDescriptor(IObjectUIProvider provider, RGB sourceRGB, RGB targetRGB) {
        ImageDescriptor originalDescriptor = provider.getImageDescriptor();
        if(originalDescriptor == null) {
            return null;
        }

        return new ImageDescriptor() {
            @Override
            public ImageData getImageData(int zoom) {
                ImageData imageData = originalDescriptor.getImageData(zoom);
                if(imageData == null) {
                    return null;
                }

                replaceColor(imageData, sourceRGB, targetRGB);
                return imageData;
            }
        };
    }

    /**
     * Replace pixels matching the source color with the target color.
     * Uses a tolerance to handle anti-aliased edges by blending colors.
     */
    private static void replaceColor(ImageData imageData, RGB sourceRGB, RGB targetRGB) {
        PaletteData palette = imageData.palette;

        // For indexed palette images
        if(!palette.isDirect) {
            // Replace in the palette itself
            for(int i = 0; i < palette.colors.length; i++) {
                RGB rgb = palette.colors[i];
                if(isRGBEqual(rgb, sourceRGB)) {
                    palette.colors[i] = targetRGB;
                }
                else if(isColorClose(rgb, sourceRGB, 60)) {
                    // Blend for anti-aliased pixels
                    palette.colors[i] = blendColors(rgb, sourceRGB, targetRGB);
                }
            }
            return;
        }

        // For direct palette (true color) images
        int sourcePixel = palette.getPixel(sourceRGB);
        int targetPixel = palette.getPixel(targetRGB);

        for(int y = 0; y < imageData.height; y++) {
            for(int x = 0; x < imageData.width; x++) {
                int pixel = imageData.getPixel(x, y);
                RGB pixelRGB = palette.getRGB(pixel);

                // Exact match - replace directly
                if(pixel == sourcePixel || isRGBEqual(pixelRGB, sourceRGB)) {
                    imageData.setPixel(x, y, targetPixel);
                }
                // Close match - this handles anti-aliased edges
                else if(isColorClose(pixelRGB, sourceRGB, 60)) {
                    RGB blended = blendColors(pixelRGB, sourceRGB, targetRGB);
                    imageData.setPixel(x, y, palette.getPixel(blended));
                }
            }
        }
    }

    /**
     * Check if two RGB values are equal.
     */
    private static boolean isRGBEqual(RGB a, RGB b) {
        return a.red == b.red && a.green == b.green && a.blue == b.blue;
    }

    /**
     * Check if a color is close to another (for detecting anti-aliased blend pixels).
     */
    private static boolean isColorClose(RGB pixel, RGB source, int threshold) {
        int dr = pixel.red - source.red;
        int dg = pixel.green - source.green;
        int db = pixel.blue - source.blue;
        double distance = Math.sqrt(dr * dr + dg * dg + db * db);

        if(distance > threshold) {
            return false;
        }

        // Also check it's not too dark (outline color) - must have some brightness
        int brightness = (pixel.red + pixel.green + pixel.blue) / 3;
        return brightness > 80;
    }

    /**
     * Blend a pixel color from source to target, maintaining the relationship.
     */
    private static RGB blendColors(RGB pixel, RGB source, RGB target) {
        float rRatio = source.red > 0 ? (float) pixel.red / source.red : 1;
        float gRatio = source.green > 0 ? (float) pixel.green / source.green : 1;
        float bRatio = source.blue > 0 ? (float) pixel.blue / source.blue : 1;

        int newR = Math.min(255, Math.max(0, Math.round(target.red * rRatio)));
        int newG = Math.min(255, Math.max(0, Math.round(target.green * gRatio)));
        int newB = Math.min(255, Math.max(0, Math.round(target.blue * bRatio)));

        return new RGB(newR, newG, newB);
    }

    /**
     * Check if two SWT Colors are equal.
     */
    private static boolean colorsEqual(Color a, Color b) {
        return a.getRed() == b.getRed() &&
               a.getGreen() == b.getGreen() &&
               a.getBlue() == b.getBlue();
    }

    /**
     * Check if an EClass should use themed icons.
     * Only ArchiMate elements (not relationships or junctions) use themed icons.
     */
    private static boolean isThemedElement(EClass eClass) {
        return IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(eClass);
    }

    /**
     * Clear cached images and refresh UI components.
     * This should be called when default colors are changed in preferences.
     */
    public static void refreshThemedImages() {
        // Dispose and clear image cache
        for(Image image : imageCache.values()) {
            if(image != null && !image.isDisposed()) {
                image.dispose();
            }
        }
        imageCache.clear();

        // Refresh UI components to pick up new colors
        refreshUI();
    }

    /**
     * Refresh UI components that display themed icons.
     * Note: Diagram editor palettes refresh themselves via preference change listeners.
     */
    private static void refreshUI() {
        if(!PlatformUI.isWorkbenchRunning()) {
            return;
        }

        // Refresh the Model Tree
        TreeModelView treeView = (TreeModelView)ViewManager.findViewPart(ITreeModelView.ID);
        if(treeView != null && treeView.getViewer() != null) {
            treeView.getViewer().refresh();
        }
    }
}
