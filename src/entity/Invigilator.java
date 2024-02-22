/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class Invigilator {
    public int id;
    public String code;
    public int numberOfClasses;

    public Invigilator(int invigilatorId, String code, int numberOfClasses) {
        this.id = invigilatorId;
        this.code = code;
        this.numberOfClasses = numberOfClasses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(int numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }

    // toString method
    @Override
    public String toString() {
        return "Invigilator{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", numberOfClasses=" + numberOfClasses +
                '}';
    }
}

