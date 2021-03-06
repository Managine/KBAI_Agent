package ravensproject;

import java.util.ArrayList;
import java.util.List;

// Uncomment these lines to access image processing.
//import java.awt.Image;
//import java.io.File;
//import javax.imageio.ImageIO;

/**
 * Your Agent for solving Raven's Progressive Matrices. You MUST modify this
 * file.
 * 
 * You may also create and submit new files in addition to modifying this file.
 * 
 * Make sure your file retains methods with the signatures:
 * public Agent()
 * public char Solve(RavensProblem problem)
 * 
 * These methods will be necessary for the project's main method to run.
 * 
 */
public class Agent {
    /**
     * The default constructor for your Agent. Make sure to execute any
     * processing necessary before your Agent starts solving problems here.
     * 
     * Do not add any variables to this signature; they will not be used by
     * main().
     * 
     */
    public Agent() {
        
    }
    /**
     * The primary method for solving incoming Raven's Progressive Matrices.
     * For each problem, your Agent's Solve() method will be called. At the
     * conclusion of Solve(), your Agent should return an int representing its
     * answer to the question: 1, 2, 3, 4, 5, or 6. Strings of these ints 
     * are also the Names of the individual RavensFigures, obtained through
     * RavensFigure.getName(). Return a negative number to skip a problem.
     * 
     * Make sure to return your answer *as an integer* at the end of Solve().
     * Returning your answer as a string may cause your program to crash.
     * @param problem the RavensProblem your agent should solve
     * @return your Agent's answer to this problem
     */
    public int Solve(RavensProblem problem) {
        if (problem.getProblemType().equals("2x2")) {
        	RavensFigure a=problem.getFigures().get("A");
        	RavensFigure b=problem.getFigures().get("B");
        	RavensFigure c=problem.getFigures().get("C");
        	List<RavensFigure> answerList=new ArrayList<RavensFigure>();
        	for (String key:problem.getFigures().keySet()) {
        		if (!key.equals("A") && !key.equals("B") && !key.equals("C"))
        			answerList.add(problem.getFigures().get(key));
        	}
        	
        	SemanticNetwork abNet=new SemanticNetwork("2x2",a,b);
        	int dif=a.getObjects().size()-b.getObjects().size();
        	ChangeVector minDifPoint=new ChangeVector(Integer.MAX_VALUE);
        	RavensFigure ans=null;
        	for (RavensFigure answer : answerList) {
        		if (c.getObjects().size()-answer.getObjects().size()!=dif)
        			continue;
        		SemanticNetwork answerNet=new SemanticNetwork("2x2",c,answer);
        		ChangeVector newDif=abNet.calculateSimilarity(answerNet);
        		if (!newDif.usable)
        			continue;
        		if (newDif.compareTo(new ChangeVector())==0)
        			return Integer.parseInt(answer.getName());
        		if (newDif.compareTo(minDifPoint)==-1) {
        			ans=answer;
        			minDifPoint.setToVector(newDif);
        		}
        	}
        	if (ans!=null)
        		return Integer.parseInt(ans.getName());
        	else {
	        	SemanticNetwork acNet=new SemanticNetwork("2x2",a,c);
	        	dif=a.getObjects().size()-c.getObjects().size();
	        	minDifPoint=new ChangeVector(Integer.MAX_VALUE);
	        	ans=null;
	        	for (RavensFigure answer : answerList) {
	        		if (b.getObjects().size()-answer.getObjects().size()!=dif)
	        			continue;
	        		SemanticNetwork answerNet=new SemanticNetwork("2x2",b,answer);
	        		ChangeVector newDif=acNet.calculateSimilarity(answerNet);
	        		if (!newDif.usable)
	        			continue;
	        		if (newDif.compareTo(new ChangeVector())==0)
	        			return Integer.parseInt(answer.getName());
	        		if (newDif.compareTo(minDifPoint)==-1) {
	        			ans=answer;
	        			minDifPoint.setToVector(newDif);
	        		}
	        	}
	        	if (ans!=null)
	        		return Integer.parseInt(ans.getName());
	        	return -1;
        	}
        }
        else
        	return -1;
    }
}
