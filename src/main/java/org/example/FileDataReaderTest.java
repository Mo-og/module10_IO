package org.example;

public class FileDataReaderTest {

    private static final String TASK_DIVIDER = "#".repeat(25);

    public static void main(String[] args) {
        printHeader("Task 1");
        FileDataReader.printValidNumbersFromFile("src/main/resources/phones.txt");

        printHeader("Task 2");
        FileDataReader.printUsersDataAsJson("src/main/resources/users.txt", "src/main/resources/users.json");

        printHeader("Task 2 alt");
        FileDataReader.printFileDataAsJson("src/main/resources/users.txt", "src/main/resources/users1.json");
        printHeader("Task 2 alt other_file");
        FileDataReader.printFileDataAsJson("src/main/resources/users_extra.txt", "src/main/resources/users_extra.json");

        printHeader("Task 3");
        FileDataReader.printWordsFrequencies("src/main/resources/words.txt");
        printHeader("Task 3 larger text");
        FileDataReader.printWordsFrequencies("src/main/resources/text.txt", 25);
    }

    private static void printHeader(String header) {
        System.out.println();
        System.out.println(TASK_DIVIDER);
        System.out.printf("# %-21s #%n", header);
        System.out.println(TASK_DIVIDER);
    }
}
