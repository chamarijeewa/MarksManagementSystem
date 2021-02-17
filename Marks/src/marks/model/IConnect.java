/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

/**
 * IConnect interface
 *
 * @author chamari
 */
public interface IConnect {

    /**
     * Establish a connection to the data source being managed
     *
     * @throws ConnectionException
     */
    public void connect() throws ConnectionException;

    /**
     * Initialize the connection being managed. If the data source is a
     * database, queries are constructed at this point
     *
     * @throws ConnectionException
     */
    public void initialise() throws ConnectionException;

    /**
     * Disestablish the connection being managed
     *
     * @throws ConnectionException
     */
    public void disconnect() throws ConnectionException;
}//end interface
