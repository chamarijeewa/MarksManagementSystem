/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.presenter;

import static java.lang.Integer.parseInt;

import marks.model.IQuery;
import marks.model.QueryException;
import marks.model.IConnect;
import marks.model.Student;

import marks.view.IView;

import java.util.List;

import javax.swing.JTextArea;
import marks.model.ConnectionException;

// The queries that are available for the marks
import static marks.model.MarksModel.Query.*;

/**
 * StudentPresenter provides a presenter implementation of the MVP pattern for
 * the marks management application. As such, the class provides methods that
 * manage the connection to the address book model (via the IConnect interface),
 * query the model (via the IQuery interface) and update the view (via the IView
 * interface). In this implementation, the presenter only interacts with the
 * view for display updating, and not for retrieval of user input. Furthermore,
 * user input is validated in the presenter and not in the view.
 *
 * @author chamari
 */
public class StudentPresenter {


    /*
    * ViewModel functionality was originally part of the presenter class
     */
    private static class ViewModel {

        List<Student> model;
        Student current;
        int index;
        int n;

        ViewModel() {
        }

        void set(List<Student> m) {
            model = m;
            index = 0;
            n = model.size();
            current = model.get(index);
        }

        IndexedStudent previous() {
            if (--index < 0) {
                index = n - 1;
            }
            return new IndexedStudent(model.get(index), index + 1, n);
        }

        IndexedStudent next() {
            if (++index > n - 1) {
                index = 0;
            }
            return new IndexedStudent(model.get(index), index + 1, n);
        }

        IndexedStudent current() {
            return new IndexedStudent(model.get(index), index + 1, n);
        }
    }//end viewModel class

    // The context for model and view interaction
    IView view;
    IQuery queries;
    IConnect connector;
    ViewModel viewModel;

    /**
     * Create a presenter instance. As there is a circular dependency between
     * the view and the presenter, only the presenters model dependencies are
     * injected via the constructor - the view dependency is explictly injected
     * via the bind() method.
     *
     * @param iq
     * @param ic
     */
    public StudentPresenter(IQuery iq, IConnect ic) {
        // intialise model access
        queries = iq;
        connector = ic;
        // initialise the browsing context
        viewModel = new ViewModel();
    }//end StudentPresenter method

    /**
     * Set the view dependency for the presenter
     *
     * @param iv the view
     */
    public void bind(IView iv) {
        view = iv;
    }//end bind

    /**
     * For the records being browsed, make the previous record the current
     * record and display it, together with its position in the browsing
     * context. If the current record is the first record, the last record will
     * become the current record.
     */
    public void showPrevious() {
        view.displayRecord(viewModel.previous());
    }//end showPrevious method

    /**
     * For the records being browsed, make the next record the current record
     * and display it, together with its position in the browsing context. If
     * the current record is the last record, the first record will become the
     * current record.
     */
    public void showNext() {
        view.displayRecord(viewModel.next());
    }//end showNext method

    private void displayCurrentRecord(List results) {
        if (results.isEmpty()) {
            view.displayMessage("No records found");
            view.setBrowsing(false);
            return;
        }//end if
        viewModel.set(results);
        view.displayRecord(viewModel.current());
        view.displayRecordTxt(viewModel.current());
        view.setBrowsing(true);
    }//end displayCurrentRecord

    /**
     * Set the browsing context to all records in the marks and display the
     * current record.
     */
    public void selectAll() {
        try {
            List results = queries.select(ALL);
            displayCurrentRecord(results);

        } //end try
        catch (QueryException e) {//handle error
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch
    }//end selectAll method

    /**
     * Set the browsing context to contain all records in the marks with a
     * specified student ID. Display he first record or an error message if no
     * records are found.
     *
     * @param studentIdCalculateGrade the id being searched for.
     * @throws IllegalArgumentException if studentIdCalculateGrade is an empty
     * string.
     */
    public void selectByStudentIDCalculateGrade(String studentIdCalculateGrade) throws IllegalArgumentException {

                if (studentIdCalculateGrade.equals("")) {
            throw new IllegalArgumentException("Argument must not be an empty string");
        }//end if
        try {

            List results = queries.select(UPDATE_RESULTS, studentIdCalculateGrade);
            //if (!results.isEmpty()) {
              //  List res = queries.select(STUDENT_ID, studentIDSearchStudent);
                displayCurrentRecord(results);
           // }//end if
        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch
    }//end method

    /**
     * Set the browsing context to all records in the marks, calculate and
     * display the current records.
     */
    public void calculateGradeAllStudent() {
        try {
            List results = queries.select(CALCULATE_ALL_GRADE);

            //if (!results.isEmpty()) {
               // List res = queries.select(CALCULATE_ALL_GRADE);
                displayCurrentRecord(results);
           

        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch
    }//end calculateGradeAllStudent method

    /**
     * @param toleranceValue the tolerance being searched for.
     * @throws IllegalArgumentException if toleranceValue is an empty string.
     */
    public void selectByTolerance(String toleranceValue) throws IllegalArgumentException {
        if (toleranceValue.equals("")) {
            throw new IllegalArgumentException("Arguments must not contain an empty string");
        }//end if
        try {
            List results = queries.select(FIND_BORDERLINE, parseInt(toleranceValue));
            displayCurrentRecord(results);

        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch

    }//end selectByTolerance

    /**
     * @param minValue minimum value
     * @param maxValue max value
     * @throws IllegalArgumentException if any of the parameters are empty
     * strings
     */
    public void selectByFindRange(String minValue, String maxValue) throws IllegalArgumentException {
        if (minValue.equals("") || minValue.equals("")) {
            throw new IllegalArgumentException("Arguments must not contain an empty string");
        }//end if
        try {
            List results = queries.select(FIND_RANGE, parseInt(minValue), parseInt(maxValue));
            
                    displayCurrentRecord(results);

        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch
    }

    /**
     * Set the browsing context to contain all records in the marks with a
     * specified student ID. Display he first record or an error message if no
     * records are found.
     *
     * @param studentIDSearchStudent the name being searched for.
     * @throws IllegalArgumentException if studentIDSearchStudent is an empty
     * string.
     */
    public void selectByStudentIDSearchStudent(String studentIDSearchStudent) throws IllegalArgumentException {

        if (studentIDSearchStudent.equals("")) {
            throw new IllegalArgumentException("Argument must not be an empty string");
        }//end if
        try {

            List results = queries.select(STUDENT_ID, studentIDSearchStudent);
            //if (!results.isEmpty()) {
              //  List res = queries.select(STUDENT_ID, studentIDSearchStudent);
                displayCurrentRecord(results);
           // }//end if
        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch

    }//end selectByStudentIDSearchStudent

    /**
     * Update entry into the marks.
     *
     * @param studentId student ID
     * @param assignment1 assignment1 marks
     * @param assignment2 assignment2 marks
     * @param exam exam marks
     * @param total total marks
     * @param grade grades
     * @throws IllegalArgumentException if any of the parameters are empty
     * strings
     */
    public void updateStudent(String studentId, String assignment1, String assignment2, String exam, String total, String grade) throws IllegalArgumentException {

        if (studentId.equals("") || assignment1.equals("") || assignment2.equals("") || exam.equals("") || total.equals("") || grade.equals("")) {
            throw new IllegalArgumentException("Arguments must not contain an empty string");
        }//end if
        try {

            // The id field  will be created by the model
            Student student = new Student(studentId, parseInt(assignment1), parseInt(assignment2), parseInt(exam), parseInt(total), grade);
            List results = queries.select(UPDATE, student);

            //if (!results.isEmpty()) {
               // List results = queries.select(STUDENT_ID, studentId);
                displayCurrentRecord(results);
           // }//end if

        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch

    }//end update student

    /**
     * searchStudentByGrade the marks
     *
     * @param grade search grade
     */
    public void searchStudentByGrade(String grade) {
        if (grade.equals("")) {
            throw new IllegalArgumentException("Arguments must not contain an empty string");
        }//end if
        try {
            List results = queries.select(SEARCH_STUDENT, grade);
            displayCurrentRecord(results);

        }//end try
        catch (QueryException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }//end catch 
    }//end searchStudentByGrade

    /**
     * Close the marks
     */
    public void close() {
        try {
            connector.disconnect();
        } catch (ConnectionException e) {
            view.displayError(e.getMessage());
            System.exit(1);
        }
    }

}
