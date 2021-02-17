/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

/**
 * A data container for address book records. No setters are provided, as
 * attributes are read-only. Getters for all attributes are provided.
 * 
 * @author chamari
 */
public class Student {
    private final String id;// student ID
    private final int assignment1;// student assignment1 marks
    private final int assignment2;// student assignment2 marks
    private final int examMarks;// student exam marks
    private final int totalMarks;// student total marks
    private final String grade;// student grade marks

    /**
     * Create a data transfer object for an marks record. 
     *
     * @param id record 
     * @param assignment1 assignment1 marks
     * @param assignment2 assignment1 marks
     * @param examMarks exam marks
     * @param totalMarks total marks
     * @param grade
     */
    
    public Student(String id, int assignment1, int assignment2, int examMarks, int totalMarks, String grade) {
        this.id = id;
        this.assignment1 = assignment1;
        this.assignment2 = assignment2;
        this.examMarks = examMarks;
        this.totalMarks = totalMarks;
        this.grade = grade;
    }

    /**
     * @return record identifier
     */
    public String getId() {
        return id;
    }
    /**
     * @return student Assignment 1
     */
    public int getAssignment1() {
        return assignment1;
    }

    /**
     * @return student Assignment 2
     */
    public int getAssignment2() {
        return assignment2;
    }

    /**
     * @return student Exam marks
     */
    public int getExamMarks() {
        return examMarks;
    }

    /**
     * @return student total marks
     */
    public int getTotalMarks() {
        return totalMarks;
    }

    /**
     * @return student grade
     */
    public String getGrade() {
        return grade;
    }

    
   
}//end student class
