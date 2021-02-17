/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
import java.util.ArrayList;
import java.util.EnumMap;

/**
 * The MarksModel class is responsible for the management of a single marks.
 * Connection functionality is accessed via the IConnect interface; query
 * functionality via the IQuery interface.
 *
 * @author chamari
 */
public class MarksModel implements IConnect, IQuery<MarksModel.Query, Student> {

    /**
     * The Query enum specifies the queries that are supported by this manager
     */
    public static enum Query {

        ALL, STUDENT_ID, UPDATE, UPDATE_RESULTS, CALCULATE_ALL_GRADE, FIND_RANGE, FIND_BORDERLINE,SEARCH_STUDENT

    };

    // Database details for the marks management being managed
    private static final String URL = "jdbc:derby://localhost:1527/marks";
    private static final String USERNAME = "marks";
    private static final String PASSWORD = "marks";

    /* 
     * enummaps to map queries (enum values) to SQL commands and prepared 
     * statements in a typesafe manner.
     */
    private EnumMap<Query, String> sqlCommands
            = new EnumMap<>(MarksModel.Query.class);
    private EnumMap<Query, PreparedStatement> statements
            = new EnumMap<>(MarksModel.Query.class);

    // The connection to the marks management
    private Connection connection = null;

    /**
     * Create an instance of the marks manager.
     */
    public MarksModel() {
        // Specify the queries that are supported
        sqlCommands.put(Query.ALL,
                "SELECT * FROM MARKS");
        sqlCommands.put(Query.STUDENT_ID,
                "SELECT * FROM MARKS WHERE STUDENTID = ?");
        sqlCommands.put(Query.UPDATE,
                "UPDATE MARKS SET  ASSIGNMENT1=?, ASSIGNMENT2=?, EXAM=?, TOTAL=?, GRADE=? WHERE STUDENTID = ?");
        sqlCommands.put(Query.UPDATE_RESULTS,
                "UPDATE MARKS SET   TOTAL=?, GRADE=? WHERE STUDENTID = ?");
        sqlCommands.put(Query.CALCULATE_ALL_GRADE,
                "UPDATE MARKS SET   TOTAL=?, GRADE=? WHERE STUDENTID = ?");
        sqlCommands.put(Query.FIND_RANGE,
                "SELECT * FROM MARKS WHERE TOTAL BETWEEN ? AND ?");
        sqlCommands.put(Query.FIND_BORDERLINE,
                "SELECT * FROM MARKS WHERE TOTAL BETWEEN ? AND ?");
         sqlCommands.put(Query.SEARCH_STUDENT,
                "SELECT * FROM MARKS WHERE GRADE = ? ORDER BY TOTAL ASC");
    }//end MarksModel method

    // IConnect implementation
    /**
     * Connect to the marks
     *
     * @throws ConnectionException
     */
    @Override
    public void connect() throws ConnectionException {
        // Connect to the marks database
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } //end try
        catch (SQLException e) {//handle error
            throw new ConnectionException("Unable to open data source", e);
        }//end catch
    }//end connect method

    /**
     * Perform any initialization that is needed before queries can be
     * performed.
     *
     * @throws ConnectionException
     */
    @Override
    public void initialise() throws ConnectionException {
        // Create prepared statements for each query
        try {
            for (Query q : Query.values()) {
                statements.put(q, connection.prepareStatement(sqlCommands.get(q)));
            }
        } //end try
        catch (SQLException e) {//handle error
            throw new ConnectionException("Unable to initialise data source", e);
        }//end catch
    }//end initialise method

    /**
     * Disconnect from the marks
     *
     * @throws ConnectionException
     */
    @Override
    public void disconnect() throws ConnectionException {
        // Close the connection 
        try (Connection c = connection) {
            // connection is closed automatically with try with resources
            // close prepared statements first
            for (Query q : Query.values()) {
                statements.get(q).close();
            }//end for loop
        }//end try
        catch (SQLException e) {//handle error
            throw new ConnectionException("Unable to close data source", e);
        }//end catch
    }//end disconnet method

    // IQuery implementation
    /**
     * Perform a selection on the marks.
     *
     * @param q the selection as specified in the Query enum
     * @param p parameters for the query specified as a varags of type Object
     * @return a List of Student objects that match query specification
     * @throws QueryException
     */
    @Override
    public List<Student> select(Query q, Object... p) throws QueryException {
        switch (q) {
            case ALL:
                return getAllStudent();
            case STUDENT_ID:
                return getStudentByStudentID((String) p[0]);
            case UPDATE:
                return updateStudent((Student) p[0]);
            case UPDATE_RESULTS:
                return CalculateResultsByStudent((String) p[0]);
            case CALCULATE_ALL_GRADE:
                return calculateAllResults();
            case FIND_RANGE:
                return getAllStudentInRange((int) p[0], (int) p[1]);
            case FIND_BORDERLINE:
                return getAllStudentInBorderline((int) p[0]);
            case SEARCH_STUDENT:
                return getStudentByGrade((String) p[0]);

        }// end switch
        return null;
    }//end select method

    /*
     * Create student from student class
     */
    private Student createStudent(ResultSet rs) throws QueryException {
        Student s = null;
        try {
            //create new student object
            s = new Student(
                    rs.getString("STUDENTID"),
                    rs.getInt("ASSIGNMENT1"),
                    rs.getInt("ASSIGNMENT2"),
                    rs.getInt("EXAM"),
                    rs.getInt("TOTAL"),
                    rs.getString("GRADE")
            );
        }//end try 
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to process the result of selection query", e));
        }//end catch
        return s;
    }//end createStudent

    /*
    *Calulate grades marks for a student
     */
    private String calculateGrade(String id) throws QueryException {
        //get student records into a list by student ID
        List<Student> results = getStudentByStudentID(id);
        Student student = results.get(0);//get student 
        int assignment1 = student.getAssignment1();//get student assignment1 marks
        int assignment2 = student.getAssignment2();//get student assignment2 marks
        int exam = student.getExamMarks();//get student exam marks
        int total = calculateTotal(id);//get total marks 
        String grade = student.getGrade();

        //grade calculation
        if ((total >= 85) && (total <= 100)) {
            grade = "HD";
        } else if ((total >= 75) && (total <= 84)) {
            grade = "D";
        } else if ((total >=65) && (total <= 74)) {
            grade = "C";
        } else if ((total >= 50) && (total <= 64)) {
            grade = "P";
        } else if ((total >= 45) && (total <= 50)) {
            if ((assignment1 >= 0) && (assignment1 <= 10) || (assignment2 >= 0) && (assignment2 <= 15)) {
                grade = "SA";
            } else if ((exam >= 0) && (exam <= 25)) {
                grade = "SE";
            } else if ((assignment1 == 0) && (assignment2 == 0)) {
                grade = "AF";
            }
        } else {
            grade = "F";
        }//end if

        return grade;
    }//end calculateGrade method

    /*
    *Calulate total marks for a student
     */
    public int calculateTotal(String id) throws QueryException {
        //get student records into a list by student ID
        List<Student> results = getStudentByStudentID(id);
        Student student = results.get(0);//get student 
        String studentID = student.getId();//get student ID
        int assignment1 = student.getAssignment1();//get student assignment1 marks
        int assignment2 = student.getAssignment2();//get student assignment2 marks
        int exam = student.getExamMarks();//get student exam marks
        int total = assignment1 + assignment2 + exam;//calculate total marks

        return total;
    }//end calculatetotal method

    // Helper methods

    /*
     * Select all of the entries in the marks
     */
    private List< Student> getAllStudent() throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.ALL);

        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = ps.executeQuery()) {
            List<Student> results = new ArrayList<>();

            //loop through the result set
            while (resultSet.next()) {
                results.add(createStudent(resultSet));
            }//end while

            return results;
        } //end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch
    }//end getAllStudent method

    /*
     * Select student by studentId
     */
    private List< Student> getStudentByStudentID(String studentID) throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.STUDENT_ID);

        try {
            // Insert student id into prepared statement
            ps.setString(1, studentID);
        }//end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }
        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = ps.executeQuery()) {
            List<Student> results = new ArrayList<>();

            //loop through the result set
            while (resultSet.next()) {
                results.add(createStudent(resultSet));
            }//end while

            return results;
        }//end try 
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch
    }// end getStudentByStudentID method

    /*
     * Calculate total and grade by studentId
     */
    private List< Student> CalculateResultsByStudent(String studentID) throws QueryException {
        // Look up prepared statement
        PreparedStatement ps = statements.get(Query.UPDATE_RESULTS);

        // insert student attributes into prepared statement
        try {
            String grade = calculateGrade(studentID);//get calculate grade
            int total = calculateTotal(studentID);// get calculate total marks
            ps.setInt(1, total);
            ps.setString(2, grade);
            ps.setString(3, studentID);

        } //end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }//end catch

        // executeUpdate returns getStudentByStudentID containing matching entries
        try {
            ps.executeUpdate();

            return getStudentByStudentID(studentID);
        }//end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to perform update command", e));
        }//end catch
    }//end CalculateResultsByStudent

    /*
     * Calculate all student's total and grade 
     */
    private List< Student> calculateAllResults() throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.ALL);

        // executeQuery returns ResultSet containing matching entries 
        try (ResultSet resultSet = ps.executeQuery()) {

            //loop through the result set
            while (resultSet.next()) {
                CalculateResultsByStudent(resultSet.getString("STUDENTID"));
            }//end while

            return getAllStudent();
        } //end try
        catch (SQLException e) {
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch
    }//end calculateAllGrades

    /*
     * Update a record to the marks. Record fields are extracted from the method
     * parameter, which is a Student object.    
     */
    private List< Student> updateStudent(Student student) throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.UPDATE);

        // instert student attributes into prepared statement
        try {
            String studentID = student.getId();// get studentID
            String grade = calculateGrade(studentID);// get calculate grade
            int total = calculateTotal(studentID);//get calculate total marks 
            ps.setInt(1, student.getAssignment1());
            ps.setInt(2, student.getAssignment2());
            ps.setInt(3, student.getExamMarks());
            ps.setInt(4, total);
            ps.setString(5, grade);
            ps.setString(6, student.getId());
        }//end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }//end catch

        // executeUpdate returns getStudentByStudentID containing matching entries
        try {
            ps.executeUpdate();

            return getStudentByStudentID(student.getId());
        }//end try 
        catch (SQLException e) {// handle error
            throw (new QueryException("Unable to perform update command", e));
        }//end catch 
    }//end updateStudent method

    /*
     * find all students in borderline
     */
    private List<Student> getAllStudentInBorderline(int toleranceValue) throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.FIND_BORDERLINE);

        List<Student> results = new ArrayList<>();

        // insert grade range attributes into prepared statement
        try {
            final int GRADE_HD_LOWEST_VALUE = 85;//lowest marks of the HD grade range
            int gradeHDLowestRangeValueByTolerance = GRADE_HD_LOWEST_VALUE - toleranceValue ;//lowest marks of the HD grade range acording to tolerance
            ps.setInt(1, gradeHDLowestRangeValueByTolerance);
            ps.setInt(2, GRADE_HD_LOWEST_VALUE - 1);

            // executeQuery returns ResultSet containing matching entries
            try (ResultSet resultSet = ps.executeQuery()) {
                //loop through the result set
                while (resultSet.next()) {
                    results.add(createStudent(resultSet));
                }//end while
            } //end try
            catch (SQLException e) {//handle error
                throw (new QueryException("Unable to execute selection query", e));
            }//end catch

            final int GRADE_D_LOWEST_VALUE = 75;//lowest marks of the D grade range
            int gradeDLowestRangeValueByTolerance = GRADE_D_LOWEST_VALUE - toleranceValue;//lowest marks of the HD grade range acording to tolerance
            ps.setInt(1, gradeDLowestRangeValueByTolerance);
            ps.setInt(2, GRADE_D_LOWEST_VALUE - 1);

            // executeQuery returns ResultSet containing matching entries
            try (ResultSet resultSet = ps.executeQuery()) {
                //loop through the result set
                while (resultSet.next()) {
                    results.add(createStudent(resultSet));
                }//end while
            } //end try
            catch (SQLException e) {//handle error
                throw (new QueryException("Unable to execute selection query", e));
            }//end catch

            final int GRADE_C_LOWEST_VALUE = 65;//lowest marks of the C grade range
            int gradeCLowestRangeValueByTolerance = GRADE_C_LOWEST_VALUE -toleranceValue;//lowest marks of the C grade range acording to tolerance
            ps.setInt(1, gradeCLowestRangeValueByTolerance);
            ps.setInt(2, GRADE_C_LOWEST_VALUE - 1);

            // executeQuery returns ResultSet containing matching entries
            try (ResultSet resultSet = ps.executeQuery()) {
                //loop through the result set
                while (resultSet.next()) {
                    results.add(createStudent(resultSet));
                }//end while
            } //end try
            catch (SQLException e) {//handle error
                throw (new QueryException("Unable to execute selection query", e));
            }//end catch

            final int GRADE_P_LOWEST_VALUE = 50;//lowest marks of the P grade range
            int gradePLowestRangeValueByTolerance = GRADE_P_LOWEST_VALUE - toleranceValue;//lowest marks of the P grade range acording to tolerance
            ps.setInt(1, gradePLowestRangeValueByTolerance);
            ps.setInt(2, GRADE_P_LOWEST_VALUE - 1);

        } //end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }//end catch

        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = ps.executeQuery()) {
            //loop through the result set
            while (resultSet.next()) {
                results.add(createStudent(resultSet));
            }//end while

            return results;
        } //end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch

    }//end getAllStudentInBorderline method

    /*
     * find all students in range
     */
    private List<Student> getAllStudentInRange(int minValue, int maxValue) throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.FIND_RANGE);

        // insert range attributes into prepared statement
        try {

            ps.setInt(1, minValue);
            ps.setInt(2, maxValue);
        } //end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }//end catch
        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = ps.executeQuery()) {
            List<Student> results = new ArrayList<>();

            //loop through the result set
            while (resultSet.next()) {
                results.add(createStudent(resultSet));
            }//end while
            return results;
        }//end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch

    }//end getAllStudentInRange

        /*
     * Select student by studentId
     */
    private List< Student> getStudentByGrade(String grade) throws QueryException {
        // get prepared statement
        PreparedStatement ps = statements.get(Query.SEARCH_STUDENT);

        try {
            // Insert student grade into prepared statement
            ps.setString(1, grade);
        }//end try
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to paramaterise selection query", e));
        }
        // executeQuery returns ResultSet containing matching entries
        try (ResultSet resultSet = ps.executeQuery()) {
            List<Student> results = new ArrayList<>();

            //loop through the result set
            while (resultSet.next()) {
                results.add(createStudent(resultSet));
            }//end while

            return results;
        }//end try 
        catch (SQLException e) {//handle error
            throw (new QueryException("Unable to execute selection query", e));
        }//end catch
    }// end getStudentByStudentID method
}//end MarksModel class
