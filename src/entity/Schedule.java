package entity;

import com.opencsv.exceptions.CsvValidationException;
import data_input.Data;
import data_input.Data;
import data_input.Data;
import data_input.Data;
import static java.awt.PageAttributes.MediaType.D;
import java.util.ArrayList;
import java.util.Arrays;
import utils.DistributedRandom;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class Schedule {

    public Data data;

    public Schedule(Data data) {
        this.data = data;
    }

    //Constraint 1: No student should be required to sit two examinations simultaneously
    public boolean checkNoSimultaneousExams(int[][][] chromosome) {
        int[][] subjectHeldAtSlot = createSubjectHeldAtSlot(chromosome);
        for (int m = 0; m < data.getNumberOfStudents(); m++) {
            for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                int subjectCounts = 0;
                for (int s = 0; s < data.getNumberOfSubjects(); s++) {
                    if (subjectHeldAtSlot[s][t] == 1
                            && data.getStudentTakeSubject()[m][s] == 1) {
                        subjectCounts += 1;
                    }
                }
                if (subjectCounts > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    //Constraint 2: In each slot, the number of invigilator must be equal to number of room required for each subject
    public boolean checkInvigilatorRoomMatch(int[][][] chromosome) {
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                int invigilatorCounts = 0;
                for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                    invigilatorCounts += chromosome[s][t][i];
                }
                if (invigilatorCounts != 0 && invigilatorCounts != data.getNumberOfRoomsOfEachSubject()[s]) {
//                    System.out.println(invigilatorCounts);
//                    System.out.println(data.getNumberOfRoomsOfEachSubject()[s]);
                    return false;
                }
            }
        }
        return true;
    }

    //Constraint 3: No invigilator should be required to sit two examinations simultaneously
    public boolean checkNoInvigilatorClashes(int[][][] chromosome) {
        for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
            for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                int subjectCounts = 0;
                for (int s = 0; s < data.getNumberOfSubjects(); s++) {
                    subjectCounts += chromosome[s][t][i];
                }
                if (subjectCounts > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    //Constraint 4: Invigilator is scheduled to subject that belong to their capacity
    public boolean checkInvigilatorCapacity(int[][][] chromosome) {
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                    if (chromosome[s][t][i] == 1 && data.getSubjectCanBeSuperviseInvigilator()[s][i] == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //Constraint 5: Number of rooms used in one slot is not larger than university's capacity
    public boolean checkRoomCapacity(int[][][] chromosome) {
        for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
            int roomCounts = 0;
            for (int s = 0; s < data.getNumberOfSubjects(); s++) {
                for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                    roomCounts += chromosome[s][t][i];
                }
            }
            if (roomCounts > data.getNumberOfRooms()) {
                return false;
            }
        }

        return true;
    }

    //Constraint 6: One subject only take place at one time
    public boolean checkSingleSubjectAtATime(int[][][] chromosome) {
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            ArrayList<Integer> cnt = new ArrayList<>();
            for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                int numberOfInvigilators = 0;
                for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                    numberOfInvigilators += chromosome[s][t][i];
                }
                if (numberOfInvigilators > 0) {
                    cnt.add(t);
                }
            }
            if (cnt.size() != data.getLengthOfSubject()[s]) {
                return false;
            }
        }
        return true;
    }

    //Constraint 7: All subject must happen only one part of day (in the morning or in the afternoon)
    public boolean checkSubjectPartOfDay(int[][][] chromosome) {
        int[][] subjectHeldAtSlot = createSubjectHeldAtSlot(chromosome);
        int[][] slotStartOfSubject = createSlotStartOfSubject(subjectHeldAtSlot);
        int[][] slotEndOfSubject = createSlotEndOfSubject(slotStartOfSubject);

        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            int[] startOfSubject = new int[slotStartOfSubject[s].length];
            int[] endOfSubject = new int[slotEndOfSubject[s].length];

            for (int i = 0; i < slotStartOfSubject[s].length; i++) {
                startOfSubject[i] = (int) (2.0 * slotStartOfSubject[s][i] / data.getNumberOfSlotsPerDay());
                endOfSubject[i] = (int) (2.0 * slotEndOfSubject[s][i] / data.getNumberOfSlotsPerDay());
            }

            for (int i = 0; i < startOfSubject.length; i++) {
                if (Math.floor(startOfSubject[i]) != Math.floor(endOfSubject[i])) {
                    return false;
                }
            }
        }
        return true;
    }

    //Constraint 8: With each subject, Invigilator need to supervise all consecutive slot of this subject happen.
    public boolean checkInvigilatorConsecutiveSlots(int[][][] chromosome) {
        int[][] subjectHeldAtSlot = createSubjectHeldAtSlot(chromosome);
        int[][] slotStartOfSubject = createSlotStartOfSubject(subjectHeldAtSlot);
        int[][] slotEndOfSubject = createSlotEndOfSubject(slotStartOfSubject);

        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                int consecutiveCount = 0;
                int startSlot = slotStartOfSubject[s][0];
                int endSlot = slotEndOfSubject[s][0];
                for (int t = startSlot; t <= endSlot; t++) {
                    consecutiveCount += chromosome[s][t][i];
                }
                if (consecutiveCount != 0 && consecutiveCount != data.getLengthOfSubject()[s]) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] createSubjectHeldAtSlot(int[][][] chromosome) {
        int[][] subjectHeldAtSlot = new int[data.getNumberOfSubjects()][data.getNumberOfTotalSlots()];
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            for (int t = 0; t < data.getNumberOfTotalSlots(); t++) {
                int numberOfInvigilators = 0;
                for (int i = 0; i < data.getNumberOfInvigilators(); i++) {
                    numberOfInvigilators += chromosome[s][t][i];
                }
                if (numberOfInvigilators > 0) {
                    subjectHeldAtSlot[s][t] = 1;
                }
            }
        }
        return subjectHeldAtSlot;
    }

    public int[][] createSlotStartOfSubject(int[][] subjectHeldAtSlot) {
        int[][] slotStartOfSubject = new int[data.getNumberOfSubjects()][1];
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            for (int t = 1; t < data.getNumberOfTotalSlots(); t++) {
                if (subjectHeldAtSlot[s][t - 1] == 0 && subjectHeldAtSlot[s][t] == 1) {
                    slotStartOfSubject[s][0] = t;
                    break; // Assuming only one start per subject
                }
            }
        }
        return slotStartOfSubject;
    }

    public int[][] createSlotEndOfSubject(int[][] slotStartOfSubject) {
        int[][] slotEndOfSubject = new int[data.getNumberOfSubjects()][1];
        for (int s = 0; s < data.getNumberOfSubjects(); s++) {
            slotEndOfSubject[s][0] = slotStartOfSubject[s][0] + data.getLengthOfSubject()[s] - 1;
        }
        return slotEndOfSubject;
    }

    public boolean passAllConstraints(int[][][] chromosome) {
        boolean constraint1 = checkNoSimultaneousExams(chromosome);
        boolean constraint2 = checkInvigilatorRoomMatch(chromosome);
        boolean constraint3 = checkNoInvigilatorClashes(chromosome);
        boolean constraint4 = checkInvigilatorCapacity(chromosome);
        boolean constraint5 = checkRoomCapacity(chromosome);
        boolean constraint6 = checkSingleSubjectAtATime(chromosome);
        boolean constraint7 = checkSubjectPartOfDay(chromosome);
        boolean constraint8 = checkInvigilatorConsecutiveSlots(chromosome);

//        System.out.println(constraint1 + " " + constraint2 + " " + constraint3 + " " + constraint4 + " " + constraint5 + " " + constraint6 + " " + constraint7 + " " + constraint8);

        return constraint1 && constraint2 && constraint3 && constraint4 && constraint5 && constraint6 && constraint7 && constraint8;
    }

    public int[][][] createChromosome() {
        while (true) {
            int[][][] chromosome = new int[data.numberOfSubjects][data.numberOfTotalSlots][data.numberOfInvigilators];
            int[] numberOfSlotScheduleInvigilator = new int[data.numberOfInvigilators];
            int[][] invigilatorTakeSlot = new int[data.numberOfInvigilators][data.numberOfTotalSlots];
            int[][] subjectHeldAtSlot = new int[data.numberOfSubjects][data.numberOfTotalSlots];
//            DistributedRandom randomSlot = DistributedRandom.newDistributedRandomSlot(data.numberOfTotalSlots, data.numberOfRooms);
            Random rand = new Random();

            for (int i = 0; i < data.numberOfInvigilators; i++) {
                numberOfSlotScheduleInvigilator[i] = data.numberOfSlotsRequiredForInvigilators[i];
            }

            for (int s = 0; s < data.numberOfSubjects; s++) {
//                System.out.println("s: "+ s);
                int[] haveChoosen = new int[data.numberOfTotalSlots];
                int sLength = data.lengthOfSubject[s];
//                int t = randomSlot.getRandom();
                int t = rand.nextInt(data.numberOfTotalSlots - sLength);
                boolean flag = false;
                while (true) {
                    int sum = 0;
                    haveChoosen[t] = 1;
                    for (int index = 0; index < haveChoosen.length; index++) {
                        sum += haveChoosen[index];
                    }
                    if (sum == data.numberOfTotalSlots - sLength) {
                        flag = true;
                        break;
                    }

                    if (checkSubjectFitSlot(sLength, t, data.numberOfSlotsPerDay)
                            && checkSubjectOverlapSlot(s, t, sLength, data.numberOfSubjects, subjectHeldAtSlot, data.overlapSubject)
                            && checkRoomCapacityAtOneTime(chromosome, t, s, sLength)) {
                        break;
                    }
                    t = rand.nextInt(data.numberOfTotalSlots - sLength);
                }
                if (flag == true) {
//                    System.out.println("Can't choose t");
                    break;
                }

                DistributedRandom randomInvigilator = new DistributedRandom();
                int countInvigilator = 0;
                int invigilatorNeed = data.numberOfRoomsOfEachSubject[s];

                for (int i = 0; i < data.numberOfInvigilators; i++) {
                    boolean canAdd = true;
                    for (int eachSlot = t; eachSlot < t + sLength; eachSlot++) {
                        if (data.subjectCanBeSuperviseInvigilator[s][i] == 1 && invigilatorTakeSlot[i][eachSlot] == 0 && numberOfSlotScheduleInvigilator[i] > 0) {
                            canAdd = true;
                        } else {
                            canAdd = false;
                            break;
                        }
                    }

                    if (canAdd) {
                        randomInvigilator.add(i, numberOfSlotScheduleInvigilator[i]);
                        countInvigilator++;
                    }
                }

                if (countInvigilator < invigilatorNeed) {
                    System.out.println("Not enough invigilators: " + s);
                }

                for (int i = 0; i < invigilatorNeed; i++) {
                    int currentInvigilator = randomInvigilator.getRandom();

                    if (data.subjectCanBeSuperviseInvigilator[s][currentInvigilator] != 1) {
                        System.out.println("Invalid invigilator assigned for subject");
                    }

                    for (int eachSlot = t; eachSlot < t + sLength; eachSlot++) {
                        if (invigilatorTakeSlot[currentInvigilator][eachSlot] != 0) {
                            System.out.println("Invalid slot assignment for invigilator: " + currentInvigilator);
                        }

                        chromosome[s][eachSlot][currentInvigilator] = 1;
                        invigilatorTakeSlot[currentInvigilator][eachSlot] = 1;
                        subjectHeldAtSlot[s][eachSlot] = 1;
//                        randomSlot.add(eachSlot, -1);
                    }

                    randomInvigilator.delete(currentInvigilator);
                    numberOfSlotScheduleInvigilator[currentInvigilator]--;
                }
            }
            if (passAllConstraints(chromosome)) {
                return chromosome;
            }
        }

    }

    private boolean checkRoomCapacityAtOneTime(int[][][] chromosome, int t, int s, int sLength) {
        boolean flag = true;
        if (t + sLength > data.numberOfTotalSlots - 1) {
            return false;
        }
        for (int eachSlot = t; eachSlot < t + sLength; eachSlot++) {
            int roomCounts = 0;
            for (int k = 0; k < s; k++) {
                for (int i = 0; i < data.numberOfInvigilators; i++) {
                    roomCounts += chromosome[k][eachSlot][i];
                }
            }
            if (data.numberOfRooms - roomCounts - data.numberOfRoomsOfEachSubject[s] < 0) {
                flag = false;
            }
        }

        return flag;
    }

    private boolean checkSubjectFitSlot(int subLength, int slot, int slotsPerDay) {
        double half = slotsPerDay / 2;
        return (int) (slot / half) == (int) ((slot + subLength - 1) / half);
    }

    private boolean checkSubjectOverlapSlot(int currentSubject, int currentTime, int subjectLength, int numberOfSubjects, int[][] subjectTakeSlot, int[][] overlapSubject) {
        for (int slot = currentTime; slot < currentTime + subjectLength; slot++) {
            for (int preSubject = 0; preSubject < numberOfSubjects; preSubject++) {
                if (preSubject == currentSubject || overlapSubject[currentSubject][preSubject] == 0) {
                    continue;
                }
                if (subjectTakeSlot[preSubject][slot] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public double calPayoffStudent(int[][][] chromosome) {
        int[][] subjectHeldAtSlot = createSubjectHeldAtSlot(chromosome);
        int[][] slotStartOfSubject = createSlotStartOfSubject(subjectHeldAtSlot);
        int[][] slotStartOfStudent = new int[data.numberOfStudents][data.numberOfSubjects];

        for (int m = 0; m < data.numberOfStudents; m++) {
            for (int s = 0; s < data.numberOfSubjects; s++) {
                slotStartOfStudent[m][s] = data.studentTakeSubject[m][s] * slotStartOfSubject[s][0];
            }

        }
        int[][] sortedSlotDesc = new int[data.numberOfStudents][data.numberOfSubjects];
        for (int m = 0; m < data.numberOfStudents; m++) {
            sortedSlotDesc[m] = Arrays.copyOf(slotStartOfStudent[m], slotStartOfStudent[m].length);
            Arrays.sort(sortedSlotDesc[m]);
            reverseArray(sortedSlotDesc[m]);
        }
        double payoffValueStudent = 0;
        for (int m = 0; m < data.numberOfStudents; m++) {
            double payoffOneStudent = 0;
            for (int i = 0; i < data.numberOfSubjectsOfEachStudent[m] - 1; i++) {
                payoffOneStudent += Math.abs(sortedSlotDesc[m][i] - sortedSlotDesc[m][i + 1]
                        - ((float) data.numberOfTotalSlots / data.numberOfSubjectsOfEachStudent[m]));
            }
            payoffValueStudent += payoffOneStudent;
        }
        return payoffValueStudent;
    }

    private void reverseArray(int[] arr) {
        int start = 0;
        int end = arr.length - 1;
        while (start < end) {
            int temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }

    public double calPayoffInvigilator(int[][][] chromosome) {
        int[][] numberSlotScheduleInvigilator = new int[data.numberOfInvigilators][data.numberOfExaminationDays];
        for (int i = 0; i < data.numberOfInvigilators; i++) {
            for (int d = 0; d < data.numberOfExaminationDays; d++) {
                int count = 0;
                for (int t = data.numberOfSlotsPerDay * d; t < data.numberOfSlotsPerDay * (d + 1); t++) {
                    for (int s = 0; s < data.numberOfSubjects; s++) {
                        count += chromosome[s][t][i];
                    }
                }
                if (count > 0) {
                    numberSlotScheduleInvigilator[i][d] = 1;
                }
            }
        }

        double payoffValueInvigilator = 0;
        double payoff1 = 0;
        double payoff2 = 0;
        double w1 = 1 / 4.0;
        double w2 = 3 / 4.0;

        for (int i = 0; i < data.numberOfInvigilators; i++) {
            for (int d = 0; d < data.numberOfExaminationDays; d++) {
                payoff1 += numberSlotScheduleInvigilator[i][d];
            }
        }

        for (int i = 0; i < data.numberOfInvigilators; i++) {
            int totalSlotOfInvigilator = 0;
            for (int s = 0; s < data.numberOfSubjects; s++) {
                for (int t = 0; t < data.numberOfTotalSlots; t++) {
                    totalSlotOfInvigilator += chromosome[s][t][i];
                }
            }
            payoff2 += Math.abs(totalSlotOfInvigilator - data.numberOfSlotsRequiredForInvigilators[i]);
        }

        payoffValueInvigilator = w1 * payoff1 + w2 * payoff2;
//        System.out.println("payoff1 (workday): " + payoff1);
//        System.out.println("payoff2 (workslot): " + payoff2);
        return payoffValueInvigilator;
    }

    public double calPayoffP0(int[][][] chromosome) {
        double meanRoomEachSlot = 0;
        int totalRooms = 0;
        for (int t = 0; t < data.numberOfTotalSlots; t++) {
            for (int s = 0; s < data.numberOfSubjects; s++) {
                for (int i = 0; i < data.numberOfInvigilators; i++) {
                    totalRooms += chromosome[s][t][i];
                }
            }
        }
        meanRoomEachSlot = (double) totalRooms / data.numberOfTotalSlots;
        double payOffP0 = 0;
        for (int t = 0; t < data.numberOfTotalSlots; t++) {
            int totalRoomsEachSlot = 0;
            for (int s = 0; s < data.numberOfSubjects; s++) {
                for (int i = 0; i < data.numberOfInvigilators; i++) {
                    totalRoomsEachSlot += chromosome[s][t][i];
                }
            }
            payOffP0 += Math.pow(totalRoomsEachSlot - meanRoomEachSlot, 2);
        }
        payOffP0 = Math.sqrt(payOffP0 / (data.numberOfTotalSlots - 1));
        return payOffP0;
    }

    public double getFitness(int[][][] chromosome) {
        double w3 = 1 / 3.0;
        double w4 = 1 / 3.0;
        double w5 = 1 / 3.0;
        double fitnessValue;
        double payoffStudent = calPayoffStudent(chromosome);
        double payoffInvigilator = calPayoffInvigilator(chromosome);
        double payoffP0 = calPayoffP0(chromosome);
//        System.out.println("payoff student: " + payoffStudent);
//        System.out.println("pay off invigiglator: " + payoffInvigilator);
//        System.out.println("pay off pdt: " + payoffP0);
        fitnessValue = w3 * payoffStudent + w4 * 250 * payoffInvigilator + w5 * 5000 * payoffP0;
        return fitnessValue;
    }

    public int[][][] readChromosomeFromFile(String filePath) {
        int[][][] chromosome = new int[data.numberOfSubjects][data.numberOfTotalSlots][data.numberOfInvigilators];
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line = reader.readLine();
            while (line != null) {
                // Extract subject, slot, invigilator, and value from each line
                String[] parts = line.split(":")[0].trim().split(",");
                int subject = Integer.parseInt(parts[0].split(" ")[1]);
                int slot = Integer.parseInt(parts[1].split(" ")[2]);
                int invigilator = Integer.parseInt(parts[2].split(" ")[2]);
                int value = Integer.parseInt(line.split(":")[1].trim());
                chromosome[subject][slot][invigilator] = value;
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chromosome;
//        try {
//            File file = new File("data/Solution.csv");
//            Scanner myReader = new Scanner(file);
//            while (myReader.hasNextLine()) {
//                String line = myReader.nextLine();
//                String[] words = line.split("\\D+");
//                chromosome[Integer.parseInt(words[1])][Integer.parseInt(words[2])][Integer.parseInt(words[3])] = Integer.parseInt(words[4]);
//            }
//            myReader.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//        return chromosome;
    }

    public void testChromosomeFromFile(String filePath) throws IOException {
        int[][][] chromosome = readChromosomeFromFile(filePath);

        if (passAllConstraints(chromosome)) {
            System.out.println("Chromosome passes all constraints.");
        } else {
            System.out.println("Chromosome does not pass all constraints.");
        }
    }

    public void writeChromosomeToFile(String filePath, int[][][] chromosome) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (int s = 0; s < data.numberOfSubjects; s++) {
            for (int t = 0; t < data.numberOfTotalSlots; t++) {
                for (int i = 0; i < data.numberOfInvigilators; i++) {
                    String line = String.format("Subject %d, Slot %d, Invigilator %d: %d\n", s, t, i, chromosome[s][t][i]);
                    writer.write(line);
                }
            }
        }
        writer.close();
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, CsvValidationException {
        Data data = new Data();
        data.loadData();
        Schedule s = new Schedule(data);
        String filePath = "data/chromosome_output1.txt";
//        int[][][] chromosome = s.readChromosomeFromFile(filePath);
        int[][][] chromosome = s.createChromosome();
//        System.out.println(chromosome[1][30][259]);
        System.out.println(s.getFitness(chromosome));
        // System.out.println(data.invigilatorCanSuperviseSubject[274][4]);
        
    }

}
