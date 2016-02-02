package  wekaCore;
import java.io.*;
import java.util.*;

public class C45ToArff //extends DataFileConversion
{
  public static void main(String [] args)
  {
    String inputFile1Name = "/DataGE/BreastCancer/breastCancer.names"; // input .names file name
    String inputFile2Name = null; // input .data file name

    BufferedReader inputFile1 = null; // buffered reader for input .names file
    BufferedReader inputFile2 = null; // buffered reader for input .data file

     try {
      FileInputStream inFile1 = new FileInputStream(new File(inputFile1Name));
      inputFile1 = new BufferedReader(new InputStreamReader(inFile1));
    }
    catch(Exception e) { // if cannot found the .names file user just indicated
      System.out.println("Names file \"" + inputFile1Name + "\" not found");
      System.exit(0);
    }

    String fileNameStem = null;
    String temp = null, temp2 = null;
    String classLabels = null;

    fileNameStem = inputFile1Name.substring(0,inputFile1Name.indexOf('.'));

    try {
      inputFile2Name = fileNameStem + ".data";

      FileInputStream inFile2 = new FileInputStream(new File(inputFile2Name));
      inputFile2 = new BufferedReader(new InputStreamReader(inFile2));
    }
    catch(Exception e) { // if cannot found the .data file stated in the command line
      System.out.println("Data File \"" + inputFile2Name + "\" not found");
      System.exit(0);
    }

    String outputFileName = fileNameStem + ".arff"; // for output file

    System.out.print("\nThe output files is: ");
    System.out.println(outputFileName);

    try {
      // output from .names file
      File outFile = new File(outputFileName);
      FileWriter outputFile = new FileWriter(outFile);

      outputFile.write("@relation " + fileNameStem + "\n\n"); // to print out the relation name

      while((temp = inputFile1.readLine()) != null) {
        if(temp.startsWith("|")) {
          outputFile.write("%");
          outputFile.write(temp.substring(1) + "\n");
        }

        else if(temp.indexOf(':') == -1) { // for the class labels
          if(temp.trim().length() > 0)
            classLabels = temp.substring(0,temp.length()); // store the class labels temporarily
        }

        else {
          outputFile.write("@attribute " + temp.substring(0,temp.indexOf(':')).trim() + " ");
          temp = temp.substring(temp.indexOf(':'));

          if(temp.endsWith("continuous") || temp.endsWith("continuous.")) // if it is a continuous attribute
            outputFile.write("numeric\n");


          else { // if it is a nominal attributes
            outputFile.write("{ ");
            temp = temp.substring(2);
            StringTokenizer token = new StringTokenizer(temp, ",");

            String temp1;
            temp1 = token.nextToken();
            while(token.hasMoreElements()) {
              outputFile.write(temp1.trim() + ", ");
              temp1 = token.nextToken();
            }

            outputFile.write(temp1.trim() + " }\n");
          }
        }
      }

      // writing the class labels
      if(classLabels.trim().equals("continuous.") || classLabels.trim().equals("continuous") )
        outputFile.write("@attribute class numeric\n\n");

      else {
        if(classLabels.indexOf('.') >= 0)
          outputFile.write("@attribute class " + "{ " + classLabels.substring(0, classLabels.indexOf('.')).trim() + " }\n\n");
        else
          outputFile.write("@attribute class " + "{ " + classLabels.trim() + " }\n\n");
      }
      inputFile1.close(); // close the .names file

      // output from .data files
      outputFile.write("@data\n");
      while((temp = inputFile2.readLine()) != null) {
        outputFile.write(temp + "\n");
      }

      inputFile2.close(); // close .data file

      outputFile.close(); // close .arff file
    }
    catch (Exception ex) {
      System.out.println("File creation error");
    }
  }
}