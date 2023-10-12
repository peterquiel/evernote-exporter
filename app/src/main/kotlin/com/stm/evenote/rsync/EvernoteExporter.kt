package com.stm.evenote.rsync

import com.evernote.auth.EvernoteAuth
import com.evernote.auth.EvernoteService
import com.evernote.clients.ClientFactory
import com.evernote.clients.NoteStoreClient
import org.slf4j.LoggerFactory
import picocli.CommandLine
import java.nio.file.Paths
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@CommandLine.Command(
    name = "Evernote Exporter",
    mixinStandardHelpOptions = true,
    showAtFileInUsageHelp = true,
    version = ["Evernote Exporter Version: 0.9"]
)
class EvernoteExporter : Callable<Int> {
    @CommandLine.Option(
        names = ["-d", "--directory"],
        description = ["Local directory to write ENEX files to. Default is current working directory"]
    )
    var localDirectory = Paths.get(".").toAbsolutePath().normalize().toString()

    @CommandLine.Option(
        names = ["-s", "--stacks"],
        description = ["A list of Evernote stack names that should be exported. If empty stack will be exported."])
    var stacks: List<String?> = emptyList<String>()

    @CommandLine.Option(
        names = ["-n", "--notebooks"],
        description = ["A list of notebook names that should be exported. If empty every notebook will be exported."])
    var notebooks: List<String?> = emptyList<String>()

    @CommandLine.Option(
        names = ["-t", "--token"],
        description = ["Evernote access token; read readme in order to know how to extract the token from web client login" +
                " process"],
        interactive = true,
        required = true
    )
    var token: String? = null

    @CommandLine.Option(
        names = ["-es", "--evernote-service"],
        description = ["Evernote service to use. Default is PRODUCTION; possible values: \${COMPLETION-CANDIDATES}"]
    )
    var evernoteService = EvernoteService.PRODUCTION

    @Suppress("unused")
    @CommandLine.Option(
        names = ["-v", "--version"],
        versionHelp = true,
        description = ["print version information and exit"]
    )
    var versionRequested = false

    @Suppress("unused")
    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["display this help message"])
    var usageHelpRequested = false

    override fun call(): Int {
        println("Connecting to ${evernoteService.host} using given token")

        val clientFactory = ClientFactory(EvernoteAuth(evernoteService, token))
        val noteClient = createNoteStoreClient(clientFactory)

        val evernoteClient = EvernoteClient(noteClient!!)
            .loadAllNotebooks()
            .filterForNotebooks(this.notebooks)
            .filterForStacknames(this.stacks)

        evernoteClient.export(this.localDirectory)
        return 0
    }

    private fun createNoteStoreClient(clientFactory: ClientFactory): NoteStoreClient? {
        return try {
            clientFactory.createNoteStoreClient()
        } catch (e: Exception) {
            println("Could not connect to evernote with probably invalid token: '${token}'.")
            e.printStackTrace()
            exitProcess(1)
            null
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val exitCode = CommandLine(EvernoteExporter()).execute(*args)
            exitProcess(exitCode)
        }
    }
}