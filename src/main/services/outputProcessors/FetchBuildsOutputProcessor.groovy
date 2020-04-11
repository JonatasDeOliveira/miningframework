package services.outputProcessors

import exception.ExternalScriptException
import interfaces.OutputProcessor
import util.ProcessRunner

import static app.MiningFramework.arguments

/**
 * @requires: python3 installed on the machine, that the access key was provided in the arguments,
 * that the dataCollectors ModifiedLinesCollector and BuildRequester were run on the data collecting step
 * @provides: downloads the builds generated by the BuildRequester collector and add then do the folders
 * [outputPath]/files/[projectName]/[commitSha]/build/ also creates a [outputPath]/data/results-with-builds.csv file
 * that has only the scenarios that the builds were generated
 */
class FetchBuildsOutputProcessor implements  OutputProcessor {
    private final String SCRIPT_RUNNER = "python3"
    private final String FETCH_JARS_PATH = "./scripts/fetch_jars.py"

    @Override
    void processOutput() {
        if (arguments.providedAccessKey()) {
            fetchBuildsScript(arguments.getInputPath(),
                    arguments.getOutputPath(), arguments.getAccessKey())

        }
    }
    private void fetchBuildsScript (String inputPath, String outputPath, String token) {
        println "Running fetch_jars script"
        ProcessBuilder builder = ProcessRunner.buildProcess(".", SCRIPT_RUNNER, FETCH_JARS_PATH, inputPath, outputPath, token)
        builder.redirectOutput(ProcessBuilder.Redirect.INHERIT)

        Process process = ProcessRunner.startProcess(builder)
        int exitStatus = process.waitFor()

        if (exitStatus != 0) {
            throw new ExternalScriptException(FETCH_JARS_PATH, exitStatus);
        }
    }
}
