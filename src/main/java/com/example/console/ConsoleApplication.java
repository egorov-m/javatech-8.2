package com.example.console;

import com.example.console.module.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.*;

@SpringBootApplication
public class ConsoleApplication implements ApplicationRunner {
    private final Collection<Module> applicationModules;
    private Set<String> applicationOptions;
    private List<String> files;
    private boolean isApplicationHelp;

    @Autowired
    public ConsoleApplication(Collection<Module> applicationModules) {
        this.applicationModules = applicationModules;
    }

    public static void main(String[] args) {
        SpringApplication.run(ConsoleApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        files = args.getNonOptionArgs();
        applicationOptions = args.getOptionNames();
        isApplicationHelp = args.containsOption("help");

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

    public void runModules(List<String> files, Collection<Module> applicationModules,
            Set<String> applicationOptions) {
        boolean isError = true;
        for (String f : files) {
            Optional<String> extOpt = getExtensionByStringHandling(f);
            if (extOpt.isPresent()) {
                isError = false;
                String ext = extOpt.get();
                for (Module module : applicationModules) {
                    if (module.isSuitableExtension(ext)) {
                        module.run(f, applicationOptions);
                    }
                }
            }
        }

        if (isError)
            printHelp();
    }

    public void printHelp() {
        System.out.println("Введите имя_файла --help для получения списка операций модуля.");
        printListAvailableModules(applicationModules);
    }

    public static void printListAvailableModules(Collection<Module> applicationModules) {
        System.out.println("Список доступных модулей:\n--------------------------------");
        for (Module module : applicationModules) {
            System.out.printf("%s: %s\n", module.getTitle(), module);
        }
    }

    public static void printHelpModulesForFiles(List<String> files, Collection<Module> applicationModules) {
        for (String f : files) {
            Optional<String> extOpt = getExtensionByStringHandling(f);
            if (extOpt.isPresent()) {
                String ext = extOpt.get();
                for (Module module : applicationModules) {
                    if (module.isSuitableExtension(ext)) {
                        module.printHelp();
                    }
                }
            }
        }
    }

    public static Optional<String> getExtensionByStringHandling(String filename) {
        File file = new File(filename);
        if (file.isFile()) {
            return Optional.ofNullable(filename)
                    .filter(f -> f.contains("."))
                    .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        } else {
            return Optional.of("DIRECTORY");
        }
    }
}
