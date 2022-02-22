package lab6;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;

    // nazwy kolumn w takiej kolejności, jak w pliku
    List<String> columnLabels = new ArrayList<>();
    // odwzorowanie: nazwa kolumny -> numer kolumny
    Map<String, Integer> columnLabelsToInt = new HashMap<>();

    String[] current;

    /**
     * @param filename  - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */
    public CSVReader(String filename, String delimiter, boolean hasHeader) throws IOException {
        this.reader = new BufferedReader(new FileReader(filename));
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if (hasHeader) {
            this.parseHeader();
        }
    }

    public CSVReader(String filename, String delimiter) {

    }

    public CSVReader(String filename) {

    }

    void parseHeader() throws IOException {
        // wczytaj wiersz
        String line = reader.readLine();
        if (line == null) {
            return;
        }
        // podziel na pola
        String[] header = line.split(delimiter);
        // przetwarzaj dane w wierszu
        for (int i = 0; i < header.length; i++) {
            // dodaj nazwy kolumn do columnLabels i numery do columnLabelsToInt
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i], i);
        }
    }

    boolean next() {
        // czyta następny wiersz, dzieli na elementy i przypisuje do current
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) { /* e.printStackTrace(); */ }
        if (line == null) {
            return false;
        }

        // Zamień przecinki w stringach
        line = line.replaceAll("\"(.+),(.+)\"", "\"$1#comma#$2\"");

        // podziel na pola
        this.current = line.split(delimiter);

        // przywróć przecinki i usuń cudzysłowy
        for (int i = 0; i < this.current.length; i++) {
            this.current[i] = this.current[i].replaceAll("#comma#", ",");
            this.current[i] = this.current[i].replaceAll("\"", "");
        }

        return true;
    }

    List<String> getColumnLabels() {
        return this.columnLabels;
    }

    int getRecordLength() {
        return this.current.length;
    }

    boolean isMissing(int columnIndex) {
        if (columnIndex < this.current.length) {
            return this.get(columnIndex).isEmpty();
        }

        return true;
    }

    boolean isMissing(String columnLabel) {
        if (this.columnLabelsToInt.get(columnLabel) < this.current.length) {
            return this.get(columnLabel).isEmpty();
        }

        return true;
    }

    public String get(String columnName) {
        return this.current[this.columnLabelsToInt.get(columnName)];
    }

    public String get(int columnIndex) {
        return this.current[columnIndex];
    }

    public int getInt(String columnName) throws Exception {
        if (this.get(columnName).equals("")) {
            throw new Exception("Empty value!");
        }
        return Integer.parseInt(this.get(columnName));
    }

    public int getInt(int columnIndex) throws Exception {
        if (this.get(columnIndex).equals("")) {
            throw new Exception("Empty value!");
        }
        return Integer.parseInt(this.current[columnIndex]);
    }

    public long getLong(String columnName) throws Exception {
        if (this.get(columnName).equals("")) {
            throw new Exception("Empty value!");
        }
        return Long.parseLong(this.current[this.columnLabelsToInt.get(columnName)]);
    }

    public long getLong(int columnIndex) throws Exception {
        if (this.get(columnIndex).equals("")) {
            throw new Exception("Empty value!");
        }
        return Long.parseLong(this.current[columnIndex]);
    }

    public double getDouble(String columnName) throws Exception {
        if (this.get(columnName).equals("")) {
            throw new Exception("Empty value!");
        }
        int idx = this.columnLabelsToInt.get(columnName);
        return Double.parseDouble(this.current[idx]);
    }

    public double getDouble(int columnIndex) throws Exception {
        if (this.get(columnIndex).equals("")) {
            throw new Exception("Empty value!");
        }
        return Double.parseDouble(this.current[columnIndex]);
    }
}
