package com.vahidya;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        List<Task> tasks;
        List<String> words;
        String input;
        File file = new File("tasks.json");
        int lastId=0;
        int id;

        System.out.println("Welcome to task tracker Application!");
        System.out.println("Enter a command (type 'help' for available commands, or 'exit' to quit):");

        while (true) {
            System.out.print("task-cli "); // Command prompt
            input = scanner.nextLine().trim();
            Pattern pattern = Pattern.compile("\"([^\"]*)\"|(\\S+)");
            Matcher matcher = pattern.matcher(input);
            // Find matches
            words = new ArrayList<>();
            while (matcher.find()) {
                if (matcher.group(1) != null) {
                    // Group 1 captures the content inside quotes
                    words.add(matcher.group(1));
                } else if (matcher.group(2) != null) {
                    // Group 2 captures single words
                    words.add(matcher.group(2));
                }
            }
            switch (words.get(0).toLowerCase()) {
                case "help":
                    if (words.size()>1)
                        System.out.println("parameter is not correct!!");
                    else
                        {
                        System.out.println("Available commands:");
                        System.out.println("  add   - add a task to list");
                        System.out.println("  delete - delete a task from list");
                        System.out.println("  mark-in-progress  - mark a task as an in-progress task");
                        System.out.println("  mark-done - mark a task as a done task");
                        System.out.println("  List (done of todo or in-progress) - show list of tasks");
                    }
                    break;

                case "add":
                    if (words.size()>1){
                        try{
                            if (file.exists()&&file.length()>0){
                                tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                lastId=tasks.get(tasks.size()-1).getId();
                                Task task= Task.builder().description(words.get(1))
                                        .id(lastId+1)
                                        .status(TaskStatus.TODO)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                tasks.add(task);
                            }else{
                                tasks = new ArrayList<>();
                                Task task= Task.builder().description(words.get(1))
                                        .id(1)
                                        .status(TaskStatus.TODO)
                                        .createdAt(LocalDateTime.now())
                                        .build();
                                tasks.add(task);

                            }
                            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
                            System.out.println("new task add with id ="+ (lastId+1));
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }else{
                        System.out.println("it has to enter description after add");
                    }
                    break;
                case  "delete":
                    if (words.size()!=2){
                        System.out.println("after delete just enter the id of task ");
                    }else {
                        try {
                            id = Integer.parseInt(words.get(1));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid delete parameter: id is a number");
                            break;
                        }
                        try{
                            if (file.exists()&&file.length()>0){
                                tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                int index=-1;
                                for (Task t:tasks) {
                                    if (t.getId()==id){
                                        index= tasks.indexOf(t);
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    tasks.remove(index);
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
                                    System.out.println("task was deleted");
                                }else{
                                    System.out.println("task was not found");
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case "list":
                    if (words.size()>2){
                        System.out.println("command\'parametrs are not valid ");
                    }else
                        if(words.size()==1){
                            try {
                            tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                for (Task t: tasks) {
                                    System.out.println(t.toString());
                                }
                            }catch (IOException e){
                            e.printStackTrace();
                            }
                    }else {
                        switch (words.get(1).toLowerCase()) {
                            case "todo":
                                try {
                                    tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                    for (Task t: tasks) {
                                        if (t.getStatus().equals(TaskStatus.TODO)){
                                            System.out.println(t.toString());
                                        }
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                break;
                            case "in-progress":
                                try {
                                    tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                    for (Task t: tasks) {
                                        if (t.getStatus().equals(TaskStatus.IN_PROGRESS)){
                                            System.out.println(t.toString());
                                        }
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                break;
                            case "done":
                                try {
                                    tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                    for (Task t: tasks) {
                                        if (t.getStatus().equals(TaskStatus.DONE)){
                                            System.out.println(t.toString());
                                        }
                                    }
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                break;
                            default:
                                System.out.println("command\'parameter is not correct");
                        }
                    }
                    break;
                case "mark-done":
                    if (words.size()!=2){
                        System.out.println("after mark-done just enter the id of task ");
                    }else {
                        try {
                            id = Integer.parseInt(words.get(1));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid mark-done parameter: id is a number");
                            break;
                        }
                        try{
                            if (file.exists()&&file.length()>0){
                                tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                int index=-1;
                                for (Task t:tasks) {
                                    if (t.getId()==id){
                                        index= tasks.indexOf(t);
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    tasks.get(index).setStatus(TaskStatus.DONE);
                                    tasks.get(index).setUpdateAt(LocalDateTime.now());
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
                                    System.out.println("task\'s status was changed");
                                }else{
                                    System.out.println("task was not found");
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case "mark-inprogress" :
                    if (words.size()!=2){
                        System.out.println("after mark-inprogress just enter the id of task ");
                    }else {
                        try {
                            id = Integer.parseInt(words.get(1));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid mark-inprogress parameter: id is a number");
                            break;
                        }
                        try{
                            if (file.exists()&&file.length()>0){
                                tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                int index=-1;
                                for (Task t:tasks) {
                                    if (t.getId()==id){
                                        index= tasks.indexOf(t);
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    tasks.get(index).setStatus(TaskStatus.IN_PROGRESS);
                                    tasks.get(index).setUpdateAt(LocalDateTime.now());
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
                                    System.out.println("task\'s status was changed");
                                }else{
                                    System.out.println("task was not found");
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case "update" :
                    if (words.size()<3){
                        System.out.println("after update enter the id and new description ");
                    }else {
                        try {
                            id = Integer.parseInt(words.get(1));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid update parameter: id is a number");
                            break;
                        }
                        try{
                            if (file.exists()&&file.length()>0){
                                tasks=objectMapper.readValue(file, new TypeReference<List<Task>>() {});
                                int index=-1;
                                for (Task t:tasks) {
                                    if (t.getId()==id){
                                        index= tasks.indexOf(t);
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    tasks.get(index).setDescription(words.get(2));
                                    tasks.get(index).setUpdateAt(LocalDateTime.now());
                                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, tasks);
                                    System.out.println("task was changed");
                                }else{
                                    System.out.println("task was not found");
                                }
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case "exit":
                    System.out.println("Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
            }
        }
    }
}

