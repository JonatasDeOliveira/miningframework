
package services.outputProcessors.soot

import interfaces.OutputProcessor
import util.*

import static app.MiningFramework.arguments

class RunSootAnalysisOutputProcessor implements OutputProcessor {

    private final String RESULTS_FILE_PATH = "/data/results-with-builds.csv"
    private final String DATAFLOW_MODE = "dataflow"
    private final String REACHABILITY_MODE = "reachability"

    void processOutput () {
        if (arguments.providedAccessKey()) {
            runSootAnalysis()
        }
    }

    private void runSootAnalysis() {
        File sootResultsFile = createOutputFile()
        List<SootScenario> sootScenarios = SootScenario.readScenarios(arguments.getOutputPath() + RESULTS_FILE_PATH);
        
        for (scenario in sootScenarios) {

            String leftRightDataflow, rightLeftDataflow,leftRightReachability, rightLeftReachability = "false"

            println "Running soot scenario ${scenario.getCommitSHA()}"
            String filePath = scenario.getLinesFile(arguments.getOutputPath())
            String filePathReverse = scenario.getLinesFile(arguments.getOutputPath())
            String classPath = scenario.getClassPath(arguments.getOutputPath())

            if (new File(filePath).exists()) {
                println "Running left right dataflow analysis"
                Process analysisLeftRightDataflow = runSootAnalysis(filePath, classPath, DATAFLOW_MODE)
                leftRightDataflow = hasSootFlow(analysisLeftRightDataflow)

                println "Running right left dataflow analysis"
                Process analysisRightLeftDataflow = runSootAnalysis(filePathReverse, classPath, DATAFLOW_MODE)
                rightLeftDataflow = hasSootFlow(analysisRightLeftDataflow)

                println "Running left right reachability analysis"
                Process analysisLeftRightReachability = runSootAnalysis(filePath, classPath, REACHABILITY_MODE)
                leftRightReachability = hasSootFlow(analysisLeftRightReachability)

                println "Running right left reachability analysis"
                Process analysisRightLeftReachability = runSootAnalysis(filePathReverse, classPath, REACHABILITY_MODE)
                rightLeftReachability = hasSootFlow(analysisRightLeftReachability)

                sootResultsFile << "${scenario.toString()};${leftRightDataflow};${rightLeftDataflow};${leftRightReachability};${rightLeftReachability}\n"
            }
        }

    }

    private File createOutputFile() {
        File sootResultsFile = new File(arguments.getOutputPath() + "/data/soot-results.csv")

        if (sootResultsFile.exists()) {
            sootResultsFile.delete()
        }

        sootResultsFile << "project;class;method;merge commit;dataflow left right;dataflow right left;reachability left right;reachability right left\n"
    
        return sootResultsFile
    }

    private String hasSootFlow (Process sootProcess) {
        String result = "error"

        sootProcess.getInputStream().eachLine {
            println it
            if (it.stripIndent().startsWith("Number of conflicts:")) {
                result = "true"
            } else if (it.stripIndent() == "No conflicts detected") {
                result = "false"
            }
        }
        return result
    }

    private Process runSootAnalysis (String filePath, String classPath, String mode) {
        return ProcessRunner
            .runProcess(".", "java", "-jar" ,"dependencies/soot-analysis.jar", "-csv", filePath, "-cp", classPath, "-mode", mode)
    }
}