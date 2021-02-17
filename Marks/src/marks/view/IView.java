/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks.view;

/**
 * IView provides a generic interface for the display of browsing records
 *
 * @author chamari
 * @param <T> the type for the record that is to be displayed
 */
public interface IView<T> {

    void displayRecord(T r);

    void displayMessage(String m);

    void displayRecordTxt(T r);

    void setBrowsing(boolean flag);

    void displayError(String e);
}//end IView interface
