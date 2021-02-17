/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

/**
 * QueryException class its throw the run time exception
 *
 * @author chamari
 */
public class QueryException extends Exception {

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }//end QueryException method
}//end QueryException class

