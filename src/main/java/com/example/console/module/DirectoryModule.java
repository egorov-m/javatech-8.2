package com.example.console.module;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

@Component
public class DirectoryModule extends Module {
    @Value(value = "#{${directoryModuleTitle}}")
    private String title;

    @Value(value = "#{${directoryModuleExtensions}}")
    private List<String> allowableExtensions;

    @Value(value = "#{${directoryModuleOptions}}")
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

    public void directoryList(String path) {
        File[] files = (new File(path)).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            System.out.println(file.getName());
        }
    }

    private long directorySize(File directory) {
        long length = 0;
        File[] files = directory.listFiles();
        if(files != null){
            for (File file : files) {
                if (file.isFile())
                    length += file.length();
                else
                    length += directorySize(file);
            }
        }

        return length;
    }

    public void directorySize(String path) {
        System.out.printf("Размер всех файлов и папок в директории: %s", directorySize(new File(path)));
    }

    public void directoryLastModified(String path) {
        File file = new File(path);
        String date = (new SimpleDateFormat("MM.dd.yyyy HH:mm:ss")).format(file.lastModified());
        System.out.printf("Дата последней модификации: %s\n", date);
    }
}
