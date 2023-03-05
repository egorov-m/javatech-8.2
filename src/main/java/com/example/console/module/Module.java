package com.example.console.module;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

public abstract class Module {
    public abstract String getTitle();
    public abstract boolean isSuitableExtension(String extension);
    public abstract List<String> getAllowableExtensions();
    public abstract List<String> getOptions();

    public void run(String path, Collection<String> options) {
        for (String o : options) {
            try {
                this.getClass().getMethod(o, String.class).invoke(this, path);
            } catch (NoSuchMethodException e) {
                System.out.printf("Опция %s не существует для модуля %s.", o, this.getClass().getName());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void printHelp() {
        System.out.printf("%s:\n------------------%n", getTitle());
        System.out.print("Поддерживаемы форматы: ");
        for (String ext : getAllowableExtensions()) System.out.printf("%s ", ext);
        System.out.print("\nОперации: ");
        for (String ext : getOptions()) System.out.printf("%s ", ext);
        System.out.println();
    }
}
