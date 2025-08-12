package poliplanner.excel.exception;

public class LayoutMatchException extends ExcelParserException {
    private final String expected;
    private final String actual;

    public LayoutMatchException(String expected, String actual, String sheetName) {
        super(String.format("Layout mismatch in sheet %s: expected '%s', found '%s'",
                sheetName, expected, actual), null);
        this.expected = expected;
        this.actual = actual;
    }
}
