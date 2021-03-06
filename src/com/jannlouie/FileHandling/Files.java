package com.jannlouie.FileHandling;

import com.jannlouie.Apps.ClassRoom;
import com.jannlouie.Config.Exam;
import com.jannlouie.Config.MainDatabase;
import com.jannlouie.Config.Student;
import java.io.*;
import java.util.Objects;
import java.util.Vector;

public class Files {
    /*
    This class is used for handling all file input and output
    as well as for data storage.
    */
    private static Vector<String> students = new Vector<>();
    private static Vector<String> studentCredentials = new Vector<>();

    public static void saveData() throws IOException {
        Student student;
        FileWriter fileWriter;

        if (MainDatabase.isListNull()) {
            return;
        }

        // Get the ID to be used by new users by retrieving the last Student ID in the list.
        int currentStudentNumber = MainDatabase.getStudent(MainDatabase.getListSize() - 1).getId();

        // Saves each Student instance to a file.
        for (int i = 0; i < MainDatabase.getListSize(); i++) {
            student = MainDatabase.getStudent(i);
            fileWriter = new FileWriter("Database\\Students\\" + student.getName() + ".txt");

            fileWriter.write(student.getName());
            fileWriter.append("\n");
            fileWriter.append(student.getEmail()).append("\n");
            fileWriter.append(String.valueOf(student.getId())).append("\n");
            fileWriter.append(String.valueOf(student.getAge())).append("\n");
            fileWriter.append(String.valueOf(student.getGrade())).append("\n");
            fileWriter.close();
        }

         /*
         Creates a file to store all the names of the students to be used for searching files
         when the program loads again.
        */
        fileWriter = new FileWriter("Database\\student names.txt");

        for (int i = 0; i < MainDatabase.getListSize(); i++) {
            fileWriter.append(MainDatabase.getStudent(i).getName()).append("\n");
        }
        fileWriter.close();

        // Creates a file for remembering the latest student ID
        fileWriter = new FileWriter("Database\\current student number.txt");
        fileWriter.write(String.valueOf(currentStudentNumber));
        fileWriter.close();
    }

    // This method is invoked after opening the program
    public static void load() throws IOException {
        File file;
        Student student;
        FileReader fileReader;
        BufferedReader bufferedReader;

        String name;
        String email;
        int count = 0;
        int id;
        int age;
        float grade;

        // Searches the files created in saveData() to load the information to the program.
        file = new File("Database\\verification.txt");

        if (!file.isFile()) {
            // When the verification file is not found, the system creates files used for storage.
            createFiles();
        } else {
            // When the verification file is found, the system loads all information to the program.
            String line;

            file = new File("Database\\password.txt");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            fileReader.close();
            bufferedReader.close();

            Login.setPassword(line);

            file = new File("Database\\student names.txt");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            while ((line = bufferedReader.readLine()) != null) {
                students.add(line);
            }

            fileReader.close();
            bufferedReader.close();

            file = new File("Database\\current student number.txt");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            fileReader.close();
            bufferedReader.close();

            if (Objects.equals(line, null)) {
                return;
            }
            MainDatabase.setCurrentStudentNumber(Integer.parseInt(line));

            file = new File("Database\\Logs\\Current Exam Number.txt");
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            fileReader.close();
            bufferedReader.close();

            ClassRoom.setCurrentExamNumber(Integer.parseInt(line));

            for (String s : students) {
                file = new File("Database\\Students\\" + s + ".txt");
                fileReader = new FileReader(file);
                bufferedReader = new BufferedReader(fileReader);

                while ((line = bufferedReader.readLine()) != null) {
                    studentCredentials.add(line);
                    count++;
                }
                fileReader.close();
                bufferedReader.close();
            }

            for (int i = 0; i < count; i += 5) {
                name = studentCredentials.get(i);
                email = studentCredentials.get(i + 1);
                id = Integer.parseInt(studentCredentials.get(i + 2));
                age = Integer.parseInt(studentCredentials.get(i + 3));
                grade = Float.parseFloat(studentCredentials.get(i + 4));

                student = new Student(name, email, id, age);
                student.setGrade(grade);

                MainDatabase.addStudentToRoot(student);
            }

            for (int i = 0; i < ClassRoom.getCurrentExamNumber(); i++) {
                file = new File("Database\\Logs\\Exam #" + (i+1) + ".txt");

                if (file.isFile()) {
                    fileReader = new FileReader(file);
                    bufferedReader = new BufferedReader(fileReader);
                    Vector<Float> studentScores = new Vector<>();
                    Vector<String> studentNames = new Vector<>();

                    int ID = Integer.parseInt(bufferedReader.readLine());
                    float maxScore = Float.parseFloat(bufferedReader.readLine());
                    int counter = 0;

                    while ((line = bufferedReader.readLine()) != null) {
                        if (counter % 2 == 0) {
                            studentNames.add(line);
                        }
                        else {
                            studentScores.add(Float.parseFloat(line));
                        }
                        counter++;
                    }

                    fileReader.close();
                    bufferedReader.close();

                    Exam exam = new Exam(ID, maxScore);
                    exam.setStudentNamesVector(studentNames);
                    exam.setScoresVector(studentScores);
                    ClassRoom.addExam(exam);
                }
            }
        }
    }

    private static void createFiles() {

        try {
            File file;
            file = new File("Database\\verification.txt");

            if (!file.isFile()) {
                FileWriter fileWriter = new FileWriter("Database\\verification.txt");
                fileWriter.append("Verified");
                fileWriter.close();

                fileWriter = new FileWriter("Database\\password.txt");
                fileWriter.append("default");
                fileWriter.close();

                fileWriter = new FileWriter("Database\\current student number.txt");
                fileWriter.close();

                fileWriter = new FileWriter("Database\\student names.txt");
                fileWriter.close();

                fileWriter = new FileWriter("Database\\Logs\\Exam Logs.txt");
                fileWriter.close();

                fileWriter = new FileWriter("Database\\Logs\\Current Exam Number.txt");
                fileWriter.write("0");
                fileWriter.close();

                Login.setPassword("default");
                System.out.println("\n[INFO] Password is \"default\"");
                System.out.println("This message only shows up after first initialization of the app.");
                System.out.println("You can change the password later.\n");
            } else {
                file = new File("Database\\password.txt");
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                Login.setPassword(bufferedReader.readLine());
                bufferedReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearAllStudentRecords() throws Exception {
        File file;
        FileWriter fileWriter;
        Student student;

        for (int i = 0; i < MainDatabase.getListSize(); i++) {
            student = MainDatabase.getStudent(i);
            file = new File("Database\\Students\\" + student.getName() + ".txt");

            if (file.isFile()) {
                file.delete();
            }
        }

        fileWriter = new FileWriter("Database\\student names.txt");
        fileWriter.close();

        MainDatabase.deleteRecords();
    }

    public static void clearExamRecords() throws Exception{
        File file;
        FileWriter fileWriter;

        for (int i = 0; i < ClassRoom.getCurrentExamNumber(); i++) {
            file = new File("Database\\Logs\\Exam #" + (i+1) + ".txt");

            if (file.isFile()) {
                file.delete();
            }
        }

        fileWriter = new FileWriter("Database\\Logs\\Exam Logs.txt");
        fileWriter.close();

        fileWriter = new FileWriter("Database\\Logs\\Current Exam Number.txt");
        fileWriter.close();

        ClassRoom.clearRecords();
    }

    public static void addExamToLogs() throws Exception {
        FileWriter fileWriter;
        File file;

        fileWriter = new FileWriter("Database\\Logs\\Current Exam Number.txt");
        fileWriter.write(String.valueOf(ClassRoom.getCurrentExamNumber()));
        fileWriter.close();

        for (int i = 0; i < ClassRoom.getRecordSize(); i++) {
            Exam exam = ClassRoom.getExam(i);
            Vector<Float> studentScores = exam.getStudentScoresRecord();
            Vector<String> studentNames = exam.getStudentNamesRecord();
            file = new File("Database\\Logs\\" + exam + ".txt");

            if (!file.isFile()) {
                fileWriter = new FileWriter(file);

                fileWriter.append(String.valueOf(exam.getID())).append("\n");
                fileWriter.append(String.valueOf(exam.getMaxScore())).append("\n");

                for (int j = 0; j < exam.getRecordSize(); j++) {
                    fileWriter.append(studentNames.get(j)).append("\n");
                    fileWriter.append(String.valueOf(studentScores.get(j))).append("\n");
                }
                fileWriter.close();

                fileWriter = new FileWriter("Database\\Logs\\Exam Logs.txt");
                fileWriter.append(exam.toString());
                fileWriter.close();
            }
        }
    }

    public static void loadExamInfo(Exam exam) {
        Vector<String> studentNames = exam.getStudentNamesRecord();
        Vector<Float> scores = exam.getStudentScoresRecord();

        System.out.println("[DISPLAYING EXAM LOG #" + exam.getID() + "]");
        System.out.println("Max Score Possible: " + exam.getMaxScore());
        System.out.println("\n[Score]\t[Name]");

        for (int i = 0; i < studentNames.size(); i++) {
            System.out.println(scores.get(i) + "->\t" + studentNames.get(i));
        }
        System.out.println("[END OF RECORD]");
    }

    public static boolean deleteAllFiles() {
        File file;
        boolean isProgramReset = false;

        try {
            clearAllStudentRecords();
            clearExamRecords();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        file = new File("Database\\verification.txt");

        if (file.isFile()) {
            file.delete();

            file = new File("Database\\password.txt");
            file.delete();

            file = new File("Database\\Logs\\Current Exam Number.txt");
            file.delete();

            file = new File("Database\\Logs\\Exam Logs.txt");
            file.delete();

            file = new File("Database\\student names.txt");
            file.delete();

            file = new File("Database\\current student number.txt");
            file.delete();

            System.out.println("\n[INFO] Program was reset");
            isProgramReset = true;
        } else {
            System.out.println("\n[INFO] Internal Error");
        }
        return isProgramReset;
    }
}
