package org.openscience.jchempaint;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(value=Suite.class)
@SuiteClasses( value = { 
        JCPEditorAppletMenuTest.class,
        JCPEditorAppletDrawingTest.class,
        JCPEditorAppletBugsTest.class,
        JCPEditorAppletUndoRedoTest.class,
        BugSF65Test.class,
        BugSF75Test.class,
        Issue4Test.class,
        Issue8Test.class,
        Issue10Test.class,
        Issue11Test.class,
        Issue19Test.class,
        Issue32Test.class,
        Issue40Test.class,
        Issue58Test.class,
        Issue71Test.class,
        Issue73Test.class,
	Issue76Test.class,
        Issue81Test.class,
        Issue82Test.class,
        MenuCutTest.class,
        MenuIsotopeTest.class,
        SetSmilesTest.class
        } )
public class AllJCPTests {

}
