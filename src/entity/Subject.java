/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package entity;

/**
 *
 * @author Admin
 */
public class Subject {
    public int id;
    public String subjectCode;
    public int examDuration; // Converted to represent exam duration in terms of 90-minute blocks

    public Subject(int subjectId, String subjectCode, int examDuration) {
        this.id = subjectId;
        this.subjectCode = subjectCode;
        this.examDuration = (int) (examDuration / 90.0); // Convert exam duration to 90-minute blocks
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public int getExamDuration() {
        return examDuration;
    }

    public void setExamDuration(int examDuration) {
        this.examDuration =  examDuration;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", subjectCode='" + subjectCode + '\'' +
                ", examDuration=" + examDuration +
                '}';
    }

}

