/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package marks;

import marks.model.MarksModel;
import marks.model.ConnectionException;

import marks.presenter.StudentPresenter;

import marks.view.StudentView;


/**
 * This is Marks class. Its contain the main method. Its create and initialize
 * the connection for marks model database
 *
 * @author chamari
 */
public class Marks {

    public static void main(String args[]) {
        // Create the marks model. Exit the application if connection be made to the marks 
        MarksModel sqm = new MarksModel();

        try {
            sqm.connect();
            sqm.initialise();
        }// end try
        catch (ConnectionException e) {//handle error
            System.err.println(e.getMessage());
            e.getCause().printStackTrace();
            System.exit(1);
        }//end catch

        // Create the presenter and view
        // an explicit binding method (bind()) is required.
        StudentPresenter sp = new StudentPresenter(sqm, sqm);
        StudentView sv = new StudentView(sp);
        sp.bind(sv);
        // Start the application
        sv.setVisible(true);
    }//end main method

}// end marks class 
