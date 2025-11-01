package poliplanner.excel.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public class ParserTest {
    private final File CLASSPATH_TEST_EXCEL;

    public ParserTest() throws IOException {
        this.CLASSPATH_TEST_EXCEL = new ClassPathResource("testExcel.xlsx").getFile();
    }

    @Test
    void testE2EParsingWithProfiling() throws Exception {
        Path jfrFile = Path.of("profiling_excel.jfr");

        try (Recording recording = new Recording()) {
            recording.setName("ExcelParserProfile");
            recording.setToDisk(true);

            // Hot methods / stacks
            recording.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(10));

            // CPU usage
            recording.enable("jdk.CPULoad");
            recording.enable("jdk.JavaThreadStatistics");

            // GC & memory
            recording.enable("jdk.GarbageCollection");
            recording.enable("jdk.GCHeapSummary");
            recording.enable("jdk.MetaspaceSummary");
            recording.enable("jdk.ObjectAllocationInNewTLAB");
            recording.enable("jdk.ObjectAllocationOutsideTLAB");

            // Threads & locks
            recording.enable("jdk.ThreadSleep");
            recording.enable("jdk.ThreadPark");
            recording.enable("jdk.JavaMonitorEnter");

            // I/O
            recording.enable("jdk.FileRead");
            recording.enable("jdk.FileWrite");
            recording.enable("jdk.SocketRead");
            recording.enable("jdk.SocketWrite");

            // Exceptions
            recording.enable("jdk.ExceptionThrow").withThreshold(Duration.ofMillis(20));

            recording.start();

            ExcelParser parser = new ExcelParser();

            // Verifica que no se lance ninguna excepción al parsear
            assertDoesNotThrow(
                    () -> parser.parseExcel(CLASSPATH_TEST_EXCEL),
                    "El parser debería procesar el archivo sin lanzar excepciones");

            recording.stop();
            recording.dump(jfrFile);
        }

        // Verificar eventos capturados
        List<RecordedEvent> events = RecordingFile.readAllEvents(jfrFile);
        System.out.println("Eventos capturados: " + events.size());
        events.stream()
                .limit(15)
                .forEach(
                        e ->
                                System.out.println(
                                        e.getEventType().getName() + " @ " + e.getStartTime()));

        System.out.println("Grabado JFR en: " + jfrFile.toAbsolutePath());
    }
}
