package test;

import com.opencsv.exceptions.CsvValidationException;
import data_input.Data;
import entity.Schedule;
import genetic_algorithm.GeneticAlgorithm;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class Main {

    public static void main(String[] args) throws IOException, FileNotFoundException, CsvValidationException, InterruptedException, ExecutionException {
        // Load data
        Data data = new Data();
        data.loadData();

        // Define parameters
        int populationSize = 100;
        int numGenerations = 300;
        double crossoverRate = 0.6;
        double mutationRate = 0.8;
        double eliteRate = 0.2;

        // Initialize Genetic Algorithm
        GeneticAlgorithm ga = new GeneticAlgorithm(
                new Schedule(data),
                populationSize,
                mutationRate,
                crossoverRate,
                eliteRate
        );
//        ga.initializePopulation();
        // Run Genetic Algorithm
        int[][][] bestChromosome = ga.run(numGenerations);

//         Print the best fitness value and the corresponding schedule
        double bestFitness = ga.schedule.getFitness(bestChromosome);
        System.out.println("Best fitness: " + bestFitness);
        System.out.println("Best schedule:");
        String filePath = "data/chromosome_output.txt";
        ga.schedule.writeChromosomeToFile(filePath, bestChromosome);
    }

    private static void printChromosome(int[][][] chromosome) {
        for (int s = 0; s < chromosome.length; s++) {
            for (int t = 0; t < chromosome[0].length; t++) {
                for (int i = 0; i < chromosome[0][0].length; i++) {
                    System.out.println("Subject " + s + ", Slot " + t + ", Invigilator " + i + ": " + chromosome[s][t][i]);
                }
            }
        }
    }
}
