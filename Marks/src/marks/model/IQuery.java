/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.model;

import java.util.List;

/**
 * @param <Q> enum specifying supported queries
 * @param <T> the type of list objects returned by selection methods and the of
 * the argument for command methods
 *
 * @author chamari
 */
public interface IQuery<Q, T> {

    /**
     * Performs a selection query on the underlying data source. Note that
     * different queries will have different numbers of parameters and types for
     * those parameters.
     *
     * @param q The enum value for the selection
     * @param o The parameters for the selection specified as a vararg list of
     * type Object. No type conversion is required because of boxing.
     * @return a list of data transfer objects of type T
     * @throws QueryException
     */
    public List<T> select(Q q, Object... o) throws QueryException;

}//end interface
