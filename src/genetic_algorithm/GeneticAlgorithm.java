package genetic_algorithm;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Admin
 */
import entity.Schedule;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class GeneticAlgorithm {

    public Schedule schedule;
    public int populationSize;
    public double mutationRate;
    public double crossoverRate;
    public double eliteRate;

    public GeneticAlgorithm(Schedule schedule, int populationSize, double mutationRate, double crossoverRate, double eliteRate) {
        this.schedule = schedule;
        this.populationSize = populationSize;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.eliteRate = eliteRate;
    }

    public List<int[][][]> initializePopulation() {
        List<int[][][]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            System.out.println("i: " + i);
            int[][][] chromosome = schedule.createChromosome();
            population.add(chromosome);
        }
        return population;
    }

//    public int[][][] selection(List<int[][][]> population) {
//        int phi = 5;
//        Random rand = new Random();
//        List<int[][][]> selectedParents = new ArrayList<>();
//        for (int i = 0; i < phi; i++) {
//            int index = rand.nextInt(population.size());
//            selectedParents.add(population.get(index));
//        }
//        int[][][] bestParent = getBestParent(selectedParents);
//        return bestParent;
//    }

//    private int[][][] getBestParent(List<int[][][]> parents) {
//        int[][][] bestParent = parents.get(0);
//        double bestFitness = schedule.getFitness(bestParent);
//        for (int i = 1; i < parents.size(); i++) {
//            int[][][] currentParent = parents.get(i);
//            double currentFitness = schedule.getFitness(currentParent);
//            if (currentFitness < bestFitness) {
//                bestFitness = currentFitness;
//                bestParent = currentParent;
//            }
//        }
//        return bestParent;
//    }

    private int[][][] mutation(int[][][] chromosome) {
        Random rand = new Random();
        int[][][] mutatedChromosome = new int[chromosome.length][chromosome[0].length][];
        for (int s = 0; s < chromosome.length; s++) {
            for (int t = 0; t < chromosome[0].length; t++) {
                mutatedChromosome[s][t] = Arrays.copyOf(chromosome[s][t], chromosome[s][t].length);
            }
        }
        int subjectIndex1 = rand.nextInt(chromosome.length);
        int subjectIndex2 = rand.nextInt(chromosome.length);
        while (subjectIndex1 == subjectIndex2) {
            subjectIndex2 = rand.nextInt(chromosome.length);
        }
        mutatedChromosome = swapTimeslotOfTwoSubject(mutatedChromosome, subjectIndex1, subjectIndex2);
        if (!schedule.passAllConstraints(mutatedChromosome)) {
            return chromosome;
        }
        return mutatedChromosome;
    }

    private int[][][] swapTimeslotOfTwoSubject(int[][][] mutatedChromosome, int subjectIndex1, int subjectIndex2) {
        int[][] subjectHeldAtSlot = schedule.createSubjectHeldAtSlot(mutatedChromosome);
        int[][] slotStartOfSubject = schedule.createSlotStartOfSubject(subjectHeldAtSlot);
        int[] slotStart1 = slotStartOfSubject[subjectIndex1];
        int[] slotStart2 = slotStartOfSubject[subjectIndex2];
        int lengthS1 = schedule.data.lengthOfSubject[subjectIndex1];
        int lengthS2 = schedule.data.lengthOfSubject[subjectIndex2];
        if (slotStart1 == slotStart2) {
            return mutatedChromosome;
        }
        if (slotStart1[0] + lengthS2 >= mutatedChromosome[0].length || slotStart2[0] + lengthS1 >= mutatedChromosome[0].length) {
            return mutatedChromosome;
        }
        List<Integer> inviSubject1 = new ArrayList<>();
        List<Integer> inviSubject2 = new ArrayList<>();
        for (int i = 0; i < mutatedChromosome[0][0].length; i++) {
            for (int t = slotStart1[0]; t < slotStart1[0] + lengthS1; t++) {
                if (mutatedChromosome[subjectIndex1][t][i] == 1) {
                    inviSubject1.add(i);
                    mutatedChromosome[subjectIndex1][t][i] = 0;
                }
            }
            for (int t = slotStart2[0]; t < slotStart2[0] + lengthS2; t++) {
                if (mutatedChromosome[subjectIndex2][t][i] == 1) {
                    inviSubject2.add(i);
                    mutatedChromosome[subjectIndex2][t][i] = 0;
                }
            }
        }
        for (int i = 0; i < mutatedChromosome[0][0].length; i++) {
            for (int t = slotStart1[0]; t < slotStart1[0] + lengthS2; t++) {
                if (inviSubject2.contains(i)) {
                    mutatedChromosome[subjectIndex2][t][i] = 1;
                }
            }
            for (int t = slotStart2[0]; t < slotStart2[0] + lengthS1; t++) {
                if (inviSubject1.contains(i)) {
                    mutatedChromosome[subjectIndex1][t][i] = 1;
                }
            }
        }
        return mutatedChromosome;
    }

    public List<int[][][]> evolvePopulation(List<int[][][]> population, List<int[][][]> elitIndividual) {
        List<int[][][]> nextGeneration = new ArrayList<>();
        nextGeneration.addAll(elitIndividual);

        while (nextGeneration.size() < populationSize) {
            int[][][] offspring1;
            int[][][] offspring2;
            List<int[][][]> offspring = crossover(population);
            offspring1 = offspring.get(0);
            offspring2 = offspring.get(1);
            nextGeneration.add(offspring1);
            nextGeneration.add(offspring2);
        }
        return nextGeneration;
    }

    public List<int[][][]> getEliteIndividuals(List<int[][][]> population) {
        List<int[][][]> eliteIndividuals = new ArrayList<>();
        population.sort((a, b) -> Double.compare(schedule.getFitness(a), schedule.getFitness(b)));
        for (int i = 0; i < (int) (eliteRate * populationSize); i++) {
            eliteIndividuals.add(population.get(i));
        }
        return eliteIndividuals;
    }

    public List<int[][][]> crossover(List<int[][][]> population) {
        List<int[][][]> listoutput = new ArrayList<>();
        List<int[][][]> populationSorted = new ArrayList<>(population);
        populationSorted.sort((a, b) -> Double.compare(schedule.getFitness(a), schedule.getFitness(b)));

        List<int[][][]> topPhiItems = populationSorted.subList(0, (int) (populationSize * eliteRate));
        int[][][] parent1 = topPhiItems.get(0);
        int[][][] parent2 = topPhiItems.get(1);
        int i = 2;
        while (parent1 == parent2) {
            parent2 = topPhiItems.get(i++);
            if (i > topPhiItems.size()) {
                break;
            }
        }

        Random rand = new Random();
        double crossoverProb = rand.nextDouble();
        double w = crossoverRate;
        double omega = mutationRate;

        int[][][] child1;
        int[][][] child2;

        if (crossoverProb <= w) {
            child1 = parent1.clone();
            child2 = parent2.clone();
            for (int s = 0; s < child1.length; s++) {
                if (rand.nextDouble() < w) {
                    int[][][] child1Copy = child1;
                    int[][][] child2Copy = child2;
                    List<int[][][]> output = swapTwoGen(child1, child2, s);
                    child1 = output.get(0);
                    child2 = output.get(1);

                    if (schedule.passAllConstraints(child1) && schedule.passAllConstraints(child2)) {
                        continue;
                    } else {
                        child1 = child1Copy;
                        child2 = child2Copy;
                    }
                }
            }
        } else if (w < crossoverProb && crossoverProb <= omega) {
            if (rand.nextDouble() < w) {
                child1 = mutation(parent1);
                child2 = parent2.clone();
            } else {
                child1 = parent1.clone();
                child2 = mutation(parent2);
            }
        } else {
            child1 = mutation(parent1);
            child2 = mutation(parent2);
        }
        listoutput.add(child1);
        listoutput.add(child2);
        return listoutput;
    }

    public int[][][] run(int numGenerations) {
        int[][][] bestSolution = null;
        List<int[][][]> population = initializePopulation();
        List<int[][][]> elitIndividual = getEliteIndividuals(population);
        bestSolution = elitIndividual.get(0);
        for (int generation = 0; generation < numGenerations; generation++) {
            System.out.println("Generation " + (generation + 1));
            population = evolvePopulation(population, elitIndividual);
            elitIndividual = getEliteIndividuals(population);
            int[][][] s = elitIndividual.get(0);
            double bestFitness = schedule.getFitness(s);
            if (bestFitness < schedule.getFitness(bestSolution)) {
                bestSolution = s;
            }
            System.out.println("Generation " + (generation + 1) + ", Best Fitness: " + bestFitness);
        }
        return bestSolution;
    }

    private List<int[][][]> swapTwoGen(int[][][] child1, int[][][] child2, int s) {
        List<int[][][]> listoutput = new ArrayList<>();
        int[][] temp = child1[s];
        child1[s] = child2[s];
        child2[s] = temp;
        listoutput.add(child1);
        listoutput.add(child2);
        return listoutput;
    }

}
