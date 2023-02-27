package com.example.console;

import ch.qos.logback.classic.net.SyslogAppender;
import com.example.console.module.Module;
import com.example.console.module.TextModule;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;

@SpringBootApplication
public class ConsoleApplication implements ApplicationRunner {
    private static ClassPathScanningCandidateComponentProvider scanner;

    private static ApplicationContext applicationContext;
    private static Map<String, Module> applicationModules;
    private static Set<String> applicationOptions;
    private static List<String> files;
    private static boolean isApplicationHelp;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ConsoleApplication.class, args);
        applicationModules = applicationContext.getBeansOfType(Module.class);

        if (files != null && files.size() > 0) {
            if (isApplicationHelp) {
                printHelpModulesForFiles(files, applicationModules);
            } else {
                if (applicationModules.size() > 0 && applicationOptions.size() > 0) {
                    runModules(files, applicationModules, applicationOptions);
                } else {
                    printHelp();
                }
            }
        } else {
            printHelp();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        files = args.getNonOptionArgs();
        applicationOptions = args.getOptionNames();
        isApplicationHelp = args.containsOption("help");
    }

    public static void runModules(List<String> files, Map<String, Module> applicationModules,
            Set<String> applicationOptions) {
        boolean isError = true;
        for (String f : files) {
            Optional<String> extOpt = getExtensionByStringHandling(f);
            if (extOpt.isPresent()) {
                isError = false;
                String ext = extOpt.get();
                for (Map.Entry<String, Module> moduleEntry : applicationModules.entrySet()) {
                    Module module = moduleEntry.getValue();
                    if (module.isSuitableExtension(ext)) {
                        module.run(f, applicationOptions);
                    }
                }
            }
        }

        if (isError)
            printHelp();
    }

    public static void printHelp() {
        System.out.println("Введите имя_файла --help для получения списка операций модуля.");
        printListAvailableModules(applicationModules);
    }

    public static void printListAvailableModules(Map<String, Module> applicationModules) {
        System.out.println("Список доступных модулей:\n--------------------------------");
        for (Map.Entry<String, Module> moduleEntry : applicationModules.entrySet()) {
            Module module = moduleEntry.getValue();
            System.out.printf("%s: %s\n", module.getTitle(), moduleEntry.getValue());
        }
    }

    public static void printHelpModulesForFiles(List<String> files, Map<String, Module> applicationModules) {
        for (String f : files) {
            Optional<String> extOpt = getExtensionByStringHandling(f);
            if (extOpt.isPresent()) {
                String ext = extOpt.get();
                for (Map.Entry<String, Module> moduleEntry : applicationModules.entrySet()) {
                    Module module = moduleEntry.getValue();
                    if (module.isSuitableExtension(ext)) {
                        module.printHelp();
                    }
                }
            }
        }
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

}
