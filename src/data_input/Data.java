/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package data_input;

/**
 *
 * @author Admin
 */
import entity.Student;
import entity.Subject;
import entity.Invigilator;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Data {
    public int numberOfStudents;
    public int numberOfInvigilators;
    public int numberOfRooms;
    public int numberOfSubjects;
    public int numberOfExaminationDays;
    public int maximumNumberStudentsEachRoom;
    public int numberOfSlotsPerDay;
    public int[][] studentTakeSubject;
    public int[] lengthOfSubject;
    public int[][] subjectCanBeSuperviseInvigilator;
    public int[] numberOfSlotsRequiredForInvigilators;
    public int numberOfTotalSlots;
    public int[] numberOfSubjectsOfEachStudent;
    public int[] numberOfStudentsOfEachSubject;
    public int[] numberOfRoomsOfEachSubject;
    public int[][] overlapSubject;
    public ArrayList<Student> listStudents;
    public ArrayList<Subject> listSubjects;
    public ArrayList<Invigilator> listInvigilators;
    
    public Data(){
        // Initialize variables
    }
    
    public void loadData() throws IOException, FileNotFoundException, CsvValidationException {
        this.listStudents = loadStudent();
        this.listInvigilators = loadInvigilator();
        this.listSubjects = loadSubject();
        this.numberOfStudents = this.listStudents.size();
        this.numberOfInvigilators = this.listInvigilators.size();
        this.numberOfSubjects = this.listSubjects.size();
        this.numberOfRooms = 100;
        this.numberOfExaminationDays = 7;
        this.maximumNumberStudentsEachRoom = 22;
        this.numberOfSlotsPerDay = 6;
        
        this.studentTakeSubject = loadStudentSubject(this.numberOfStudents, this.numberOfSubjects);
        this.lengthOfSubject = loadSubjectLength(this.numberOfSubjects, this.listSubjects);
        this.subjectCanBeSuperviseInvigilator = loadInvigilatorSubject(this.numberOfInvigilators, this.numberOfSubjects);
        this.numberOfSlotsRequiredForInvigilators = loadInvigilatorQuota(this.listInvigilators);
        
        this.numberOfTotalSlots = this.numberOfExaminationDays * this.numberOfSlotsPerDay;
        this.numberOfSubjectsOfEachStudent = new int[this.numberOfStudents];
        for (int m =0; m < this.numberOfStudents; m++){
            int countSubject = 0;
            for (int s = 0; s< this.numberOfSubjects; s++){
                countSubject += this.studentTakeSubject[m][s];
            }
            this.numberOfSubjectsOfEachStudent[m] = countSubject;
        }
        this.numberOfStudentsOfEachSubject = new int[this.numberOfSubjects];
        for (int s =0; s < this.numberOfSubjects; s++){
            int countStudent = 0;
            for (int m = 0; m < this.numberOfStudents; m++){
                countStudent += this.studentTakeSubject[m][s];
//                System.out.printf("m = %d, s= %d\n",m,s);
            }
            this.numberOfStudentsOfEachSubject[s] = countStudent;
        }
        this.numberOfRoomsOfEachSubject = new int[this.numberOfSubjects];
        for (int s =0; s<this.numberOfSubjects; s++){
            this.numberOfRoomsOfEachSubject[s] = Math.ceilDiv(this.numberOfStudentsOfEachSubject[s], this.maximumNumberStudentsEachRoom);
        }
        this.overlapSubject = createOverlapSubject(this.numberOfSubjects, this.numberOfStudents, this.studentTakeSubject);
    }
    

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public int getNumberOfInvigilators() {
        return numberOfInvigilators;
    }

    public void setNumberOfInvigilators(int numberOfInvigilators) {
        this.numberOfInvigilators = numberOfInvigilators;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public int getNumberOfSubjects() {
        return numberOfSubjects;
    }

    public void setNumberOfSubjects(int numberOfSubjects) {
        this.numberOfSubjects = numberOfSubjects;
    }

    public int getNumberOfExaminationDays() {
        return numberOfExaminationDays;
    }

    public void setNumberOfExaminationDays(int numberOfExaminationDays) {
        this.numberOfExaminationDays = numberOfExaminationDays;
    }

    public int getMaximumNumberStudentsEachRoom() {
        return maximumNumberStudentsEachRoom;
    }

    public void setMaximumNumberStudentsEachRoom(int maximumNumberStudentsEachRoom) {
        this.maximumNumberStudentsEachRoom = maximumNumberStudentsEachRoom;
    }

    public int getNumberOfSlotsPerDay() {
        return numberOfSlotsPerDay;
    }

    public void setNumberOfSlotsPerDay(int numberOfSlotsPerDay) {
        this.numberOfSlotsPerDay = numberOfSlotsPerDay;
    }

    public int[][] getStudentTakeSubject() {
        return studentTakeSubject;
    }

    public void setStudentTakeSubject(int[][] studentTakeSubject) {
        this.studentTakeSubject = studentTakeSubject;
    }

    public int[] getLengthOfSubject() {
        return lengthOfSubject;
    }

    public void setLengthOfSubject(int[] lengthOfSubject) {
        this.lengthOfSubject = lengthOfSubject;
    }

    public int[][] getSubjectCanBeSuperviseInvigilator() {
        return subjectCanBeSuperviseInvigilator;
    }

    public void setSubjectCanBeSuperviseInvigilator(int[][] subjectCanBeSuperviseInvigilator) {
        this.subjectCanBeSuperviseInvigilator = subjectCanBeSuperviseInvigilator;
    }

    public int[] getNumberOfSlotsRequiredForInvigilators() {
        return numberOfSlotsRequiredForInvigilators;
    }

    public void setNumberOfSlotsRequiredForInvigilators(int[] numberOfSlotsRequiredForInvigilators) {
        this.numberOfSlotsRequiredForInvigilators = numberOfSlotsRequiredForInvigilators;
    }

    public int getNumberOfTotalSlots() {
        return numberOfTotalSlots;
    }

    public void setNumberOfTotalSlots(int numberOfTotalSlots) {
        this.numberOfTotalSlots = numberOfTotalSlots;
    }

    public int[] getNumberOfSubjectsOfEachStudent() {
        return numberOfSubjectsOfEachStudent;
    }

    public void setNumberOfSubjectsOfEachStudent(int[] numberOfSubjectsOfEachStudent) {
        this.numberOfSubjectsOfEachStudent = numberOfSubjectsOfEachStudent;
    }

    public int[] getNumberOfStudentsOfEachSubject() {
        return numberOfStudentsOfEachSubject;
    }

    public void setNumberOfStudentsOfEachSubject(int[] numberOfStudentsOfEachSubject) {
        this.numberOfStudentsOfEachSubject = numberOfStudentsOfEachSubject;
    }

    public int[] getNumberOfRoomsOfEachSubject() {
        return numberOfRoomsOfEachSubject;
    }

    public void setNumberOfRoomsOfEachSubject(int[] numberOfRoomsOfEachSubject) {
        this.numberOfRoomsOfEachSubject = numberOfRoomsOfEachSubject;
    }

    public int[][] getOverlapSubject() {
        return overlapSubject;
    }

    public void setOverlapSubject(int[][] overlapSubject) {
        this.overlapSubject = overlapSubject;
    }

    public ArrayList<Student> getListStudents() {
        return listStudents;
    }

    public void setListStudents(ArrayList<Student> listStudents) {
        this.listStudents = listStudents;
    }

    public ArrayList<Subject> getListSubjects() {
        return listSubjects;
    }

    public void setListSubjects(ArrayList<Subject> listSubjects) {
        this.listSubjects = listSubjects;
    }

    public ArrayList<Invigilator> getListInvigilators() {
        return listInvigilators;
    }

    public void setListInvigilators(ArrayList<Invigilator> listInvigilators) {
        this.listInvigilators = listInvigilators;
    }
    public static CSVReader readCSV(String filePath) throws FileNotFoundException {
        FileReader fileReader = new FileReader(filePath);
        return new CSVReader(fileReader);
    }
    
    private ArrayList<Student> loadStudent() throws FileNotFoundException, IOException, CsvValidationException {
        CSVReader studentReader = readCSV("data/Student.csv");
        studentReader.readNext(); // skip header

        ArrayList<Student> listStudent = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = studentReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String rollNumber = nextLine[1];
            String memberCode = nextLine[2];
            String email = nextLine[3];
            String fullName = nextLine[4];
            listStudent.add(new Student(id, rollNumber, memberCode, email, fullName));
        }
        return listStudent;
    }

    private ArrayList<Invigilator> loadInvigilator() throws FileNotFoundException, CsvValidationException, IOException {
        CSVReader invigilatorReader = readCSV("data/Invigilator.csv");
        invigilatorReader.readNext(); // skip header
        ArrayList<Invigilator> listInvigilator = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = invigilatorReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String code = nextLine[1];
            int numberOfClass = Integer.parseInt(nextLine[2]);
            listInvigilator.add(new Invigilator(id, code, numberOfClass));
        }
        return listInvigilator;
    }

    private ArrayList<Subject> loadSubject() throws FileNotFoundException, IOException, CsvValidationException {
        CSVReader subjectReader = readCSV("data/Subject.csv");
        subjectReader.readNext(); // skip header
        ArrayList<Subject> listSubject = new ArrayList<>();
        String[] nextLine;
        while ((nextLine = subjectReader.readNext()) != null) {
            int id = Integer.parseInt(nextLine[0]);
            String subCode = nextLine[1];
            int duration = Integer.parseInt(nextLine[2]);
            listSubject.add(new Subject(id, subCode, duration));
        }
        return listSubject;
    }

    private int[][] loadStudentSubject(int numberOfStudents, int numberOfSubjects) throws FileNotFoundException, IOException, CsvValidationException {
        int[][] studentTakeSubject = new int[numberOfStudents][numberOfSubjects];
        CSVReader studentSubjectReader = readCSV("data/StudentSubject.csv");
        String[] nextLine;
        int m = 0;
        while ((nextLine = studentSubjectReader.readNext()) != null) {
            int s = 0;
            for (String value : nextLine) {
                studentTakeSubject[m][s] = Integer.parseInt(value);
                s += 1;
            }
            m += 1;
        }
        return studentTakeSubject;
    }
    
    private int[] loadSubjectLength(int numberOfSubjects, ArrayList<Subject> listSubjects) {
        int[] subjectLength = new int[numberOfSubjects];
        int durationPerTimeslot = 1;
        for (int i = 0; i < numberOfSubjects; i++) {
            subjectLength[i] = Math.ceilDiv(listSubjects.get(i).getExamDuration(), durationPerTimeslot);
        }
        return subjectLength;
    }
    private int[][] loadInvigilatorSubject(int numberOfInvigilators, int numberOfSubjects) throws FileNotFoundException, IOException, CsvValidationException {
        int[][] subjectCanBeSuperviseInvigilator= new int[numberOfSubjects][numberOfInvigilators];
        CSVReader subjectInvigilatorReader = readCSV("data/SubjectInvigilator.csv");
        String[] nextLine;
        int s = 0;
        while ((nextLine = subjectInvigilatorReader.readNext()) != null) {
            int i = 0;
            for (String value : nextLine) {
                subjectCanBeSuperviseInvigilator[s][i] = Integer.parseInt(value);
                i += 1;
            }
            s += 1;
        }
        return subjectCanBeSuperviseInvigilator;
    }
    

    private int[] loadInvigilatorQuota(ArrayList<Invigilator> listInvigilators) {
        int[] numberOfSlotsRequiredForInvigilators = new int[listInvigilators.size()];
        for (int i = 0; i < listInvigilators.size(); i++) {
            numberOfSlotsRequiredForInvigilators[i] = (int) Math.ceil(listInvigilators.get(i).getNumberOfClasses()* 1.5);
        }
        return numberOfSlotsRequiredForInvigilators;
    }
    
    private int[][] createOverlapSubject(int numberOfSubjects, int numberOfStudents, int[][] studentTakeSubject) {
        int[][] overlap_subject = new int[numberOfSubjects][numberOfSubjects];
        for (int m = 0; m < numberOfStudents; m++) {
            for (int s1 = 0; s1 < numberOfSubjects; s1++) {
                for (int s2 = 0; s2 < numberOfSubjects; s2++) {
                    if (s1 == s2) {
                        continue;
                    }
                    if (studentTakeSubject[m][s1] == 1 && studentTakeSubject[m][s2] == 1) {
                        overlap_subject[s1][s2] = 1;
                    }
                }
            }
        }
        return overlap_subject;
    } 
}

