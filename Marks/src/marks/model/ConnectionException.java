/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

/**
 * ConnectionException class its throw the run time exception
 *
 * @author chamari
 */
public class ConnectionException extends RuntimeException {

    public ConnectionException(String message, Throwable cause) {
        super(message, cause);
    }// end ConnectionException method

}//end ConnectionException class
