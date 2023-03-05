package com.example.console.module;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ImageModule extends Module {
    @Value(value = "#{${imageModuleTitle}}")
    private String title;

    @Value(value = "#{${imageModuleExtensions}}")
    private List<String> allowableExtensions;

    @Value(value = "#{${imageModuleOptions}}")
    private List<String> options;
    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean isSuitableExtension(String extension) {
        return allowableExtensions.contains(extension);
    }

    @Override
    public List<String> getAllowableExtensions() {
        return allowableExtensions;
    }

    @Override
    public List<String> getOptions() {
        return options;
    }

    public void imageSize(String path) {
        try (InputStream stream = new FileInputStream(path)) {
            BufferedImage bimg  = ImageIO.read(stream);
            int width           = bimg.getWidth();
            int height          = bimg.getHeight();
            System.out.printf("Размер изображения: %dpx / %dpx", width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void imageExif(String path) {
        try (InputStream stream = new FileInputStream(path)) {
            Metadata metadata = ImageMetadataReader.readMetadata(stream);

            System.out.println("EXIF: ");
            for (Directory directory : metadata.getDirectories()) {
                for (Tag tag : directory.getTags()) {
                    System.out.println(tag);
                }
            }
        } catch (IOException | ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void imagePixelColor(String path) {
        try (InputStream stream = new FileInputStream(path)) {
            BufferedImage bimg  = ImageIO.read(stream);
            int width           = bimg.getWidth();
            int height          = bimg.getHeight();
            int count           = 0;

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    Color color = new Color(bimg.getRGB(j, i));
                    System.out.printf("№%d: Red: %d Green: %d Blue: %d\n", count++, color.getRed(), color.getGreen(), color.getBlue());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
