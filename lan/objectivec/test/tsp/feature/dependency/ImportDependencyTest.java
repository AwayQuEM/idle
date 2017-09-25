/**
 * Created by away on 13/08/2017.
 */
package tsp.feature.dependency;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import tsp.feature.dependency.fd.ImportDependency;
import tsp.feature.dependency.fd.visitor.DirectiveImportDependencyVisitor;
import tsp.feature.dependency.fd.visitor.PureCodeImportDependencyVisitor;
import tsp.g4.ObjcG4Util;
import tsp.gen.ObjectiveCPreprocessorParser;
import tsp.visitor.ObjectiveCPreprocessor;

import java.util.ArrayList;

import static tsp.constants.TestConstants.ACTIVITY_PROJECT_PATH;

public class ImportDependencyTest {

    @Test
    public void testDirectiveImportDependency() {
        String emptyClassString = "#import \"TestObject.h\"\n" +
                "#import \"FooObject.h\"\n" +
                "\n" +
                "@implementation TestObject {\n" +
                "\n" +
                "}\n" +
                "@end";

        ANTLRInputStream preInputStream = new ANTLRInputStream(emptyClassString);
        ParseTree preParseTree = ObjcG4Util.getPreParseTree(preInputStream);


        DirectiveImportDependencyVisitor visitor = new DirectiveImportDependencyVisitor();

        visitor.visit(preParseTree);

    }


    @Test
    public void testPureImportDependency() {
        String emptyClassString = "#import \"TestObject.h\"\n" +
                "#import \"FooObject.h\"\n" +
                "@import UIKit;\n" +
                "\n" +
                "@implementation TestObject {\n" +
                "\n" +
                "}\n" +
                "@end";

        ANTLRInputStream preInputStream = new ANTLRInputStream(emptyClassString);

        ObjcG4Util.PreprocessorParser preprocessorParser = new ObjcG4Util.PreprocessorParser(preInputStream).invoke();
        ObjectiveCPreprocessor preprocessor = preprocessorParser.getPreprocessor();
        ObjectiveCPreprocessorParser.ObjectiveCDocumentContext preParseTree = preprocessorParser.getPreParseTree();


        DirectiveImportDependencyVisitor visitor = new DirectiveImportDependencyVisitor();
        ImportDependency.FileNode node0 = visitor.visit(preParseTree);

        String removedSourceString = preprocessor.visit(preParseTree);


        ParseTree parseTree = ObjcG4Util.getPureParseTree(removedSourceString);

        PureCodeImportDependencyVisitor visitor1 = new PureCodeImportDependencyVisitor();

        ImportDependency.FileNode node = visitor1.visit(parseTree);
    }

    @Test
    public void testParseProject() {
        String projectPath = ACTIVITY_PROJECT_PATH;
        ImportDependency id = new ImportDependency(projectPath);
        ArrayList<ImportDependency.FileNode> fileNodes = id.parseProject();
        System.out.println("============================");
        System.out.println(fileNodes.toString());
        System.out.println("============================");
    }

}
