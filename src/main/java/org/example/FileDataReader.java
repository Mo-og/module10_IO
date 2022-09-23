package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

public class FileDataReader {
    private static final String TASK_DIVIDER = "#".repeat(25);
    static final Pattern PHONE_PATTERN = Pattern.compile("((\\(\\d{3}\\) )|(\\d{3}-))(\\d{3}-\\d{4})"); //'(xxx) xxx-xxxx' or 'xxx-xxx-xxxx'

    public static void main(String[] args) {
        printHeader("Task 1");
        printValidNumbersFromFile("src/main/resources/phones.txt");

        printHeader("Task 2");
        printUsersDataAsJson("src/main/resources/users.txt", "src/main/resources/users.json");

        printHeader("Task 2 alt");
        printFileDataAsJson("src/main/resources/users.txt", "src/main/resources/users1.json");
        printHeader("Task 2 alt other_file");
        printFileDataAsJson("src/main/resources/users_extra.txt", "src/main/resources/users_extra.json");

        printHeader("Task 3");
        printWordsFrequencies("src/main/resources/words.txt");
        printHeader("Task 3");
        printWordsFrequencies("src/main/resources/text.txt", 25);
    }

    private static void printHeader(String header) {
        System.out.println();
        System.out.println(TASK_DIVIDER);
        System.out.printf("# %-21s #%n", header);
        System.out.println(TASK_DIVIDER);
    }

    /////////////////////////
    //  Task 1
    /////////////////////////
    public static void printValidNumbersFromFile(String filePath) {
        Path path = Path.of(filePath);
        String s;
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                s = scanner.nextLine();
                if (PHONE_PATTERN.matcher(s).matches()) System.out.println(s);
            }
        } catch (IOException e) {
            printIoException(e);
        }
    }

    private static void printIoException(IOException e) {
        System.out.println("Unable to process file: " + e.getMessage() + " -> " + e.getClass().getSimpleName());
    }

    /////////////////////////
    //  Task 2
    /////////////////////////

    private static void writeStringToFile(String data, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            System.out.println("Unable to write data to file: " + e.getMessage());
        }
    }

    //#1 option (as was literally asked in task)
    static class User {
        private final String name;
        private final int age;

        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String asJson() {
            return "\n{\n" + "\t\"name\": \"" + name + "\",\n" + "\t\"age\": \"" + age + "\"\n" + '}';
        }

        public String asIndentedJson(int indentionLevel) {
            String indent = "\t".repeat(indentionLevel);
            return MessageFormat.format("""
                                        
                    {2}'{'
                    {2}\t"name": "{0}",
                    {2}\t"age": "{1}"
                    {2}'}'""", name, age, indent);
        }
    }

    public static void printUsersDataAsJson(String filePathToRead, String filePathToSave) {
        Path path = Path.of(filePathToRead);
        if (isFileInvalidForUsers(filePathToRead, path)) return;

        List<User> users = new ArrayList<>();
        try (Scanner scanner = new Scanner(path)) {
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                users.add(new User(scanner.next(), scanner.nextInt()));
            }
            String result = getUsersListJsonString(users);
            writeStringToFile(result, filePathToSave);

            //Also prints
            System.out.println(result);
        } catch (IOException e) {
            printIoException(e);
        }
    }

    private static boolean isFileInvalidForUsers(String filePath, Path path) {
        try (Scanner temp = new Scanner(path)) {
            if (!temp.hasNext() || !temp.nextLine().equalsIgnoreCase("name age")) {
                System.out.printf("No data or file with filepath %s corrupted or does not exist%n", filePath);
                return true;
            }
        } catch (IOException e) {
            printIoException(e);
            return true;
        }
        return false;
    }

    private static String getUsersListJsonString(List<User> users) {
        if (users.isEmpty()) {
            System.out.println("No users data");
            return "";
        }
        if (users.size() == 1) {
            return users.get(0).asJson();
        }
        StringJoiner sj = new StringJoiner(",", "[", "\n]");
        for (User user : users) {
            //could instead write directly to file, made to display to console as well
            sj.add(user.asIndentedJson(1));
        }
        return sj.toString();
    }

    /////////////////////////
    //  Task 2 alt
    /////////////////////////
    //#2 option (more general, but no type validation)

    static class ObjectJsonPrinter {
        private final String[] headers;
        private final String DIVIDER = " ";

        public ObjectJsonPrinter(String headersLine) {
            if (headersLine == null || headersLine.isBlank())
                throw new IllegalArgumentException("There is no heading line");
            headers = headersLine.split(" ");
        }

        public String getLineAsJson(String line, int indentSize) {
            String[] attributes = line.split(DIVIDER);
            if (attributes.length != headers.length) throw new IllegalArgumentException();

            String indent = "\t".repeat(indentSize);
            StringBuilder sb = new StringBuilder();
            sb.append("\n").append(indent).append("{\n");

            for (int i = 0; i < attributes.length; i++) {
                sb.append(indent).append("\t\"").append(headers[i]).append("\": \"").append(attributes[i]).append("\"");
                if (i < attributes.length - 1) sb.append(",\n");
            }
            sb.append("\n").append(indent).append("}");
            return sb.toString();
        }
    }

    public static void printFileDataAsJson(String filePathToRead, String filePathToSave) {
        Path path = Path.of(filePathToRead);
        try (Scanner scanner = new Scanner(path)) {
            ObjectJsonPrinter jsonPrinter = new ObjectJsonPrinter(scanner.nextLine());

            StringJoiner sj = new StringJoiner(",", "[", "\n]");
            int counter = 1;
            while (scanner.hasNextLine()) {
                counter++;
                try {
                    //could instead write directly to file
                    sj.add(jsonPrinter.getLineAsJson(scanner.nextLine(), 1));
                } catch (IllegalArgumentException e) {
                    System.out.println("Illegally formed data on line " + counter);
                }
            }
            writeStringToFile(sj.toString(), filePathToSave);

            //Also prints to console
            System.out.println(sj);

        } catch (IOException e) {
            printIoException(e);
        }

    }

    /////////////////////////
    //  Task 3
    /////////////////////////
    public static void printWordsFrequencies(String filePath) {
        printWordsFrequencies(filePath, 0);
    }

    public static void printWordsFrequencies(String filePath, int limit) {
        Path path = Path.of(filePath);
        Map<String, Integer> map = new HashMap<>();
        try (Scanner scanner = new Scanner(path)) {
            while (scanner.hasNextLine()) {
                map.compute(scanner.next(), (k, v) -> (v == null) ? 1 : v + 1);
            }
        } catch (IOException e) {
            printIoException(e);
        }

        var stream = map.entrySet().stream().sorted((o1, o2) -> {
            if (o2.getValue() - o1.getValue() == 0) return o1.getKey().compareTo(o2.getKey());
            else return o2.getValue() - o1.getValue();
        });
        if (limit > 0) stream = stream.limit(limit);
        // can be printed or returned by stream.toList()
        stream.map(entry -> (entry.getKey() + " " + entry.getValue())).forEach(System.out::println);
    }

}