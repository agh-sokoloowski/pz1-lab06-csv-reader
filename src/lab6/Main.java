package lab6;

import java.io.IOException;
import java.util.Locale;

public class Main {

    public static void main(String[] args) throws Exception {
        CSVReader reader = null;
        String path = "/path/to/lab6/examples/";
        try {
            reader = new CSVReader(path + "titanic-part.csv", ",", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (reader != null && reader.next()) {
            int id = reader.getInt("PassengerId");
            String name = reader.get("Name");
            double fare = reader.getDouble("Fare");

            System.out.printf(Locale.US, "%d %s %f\n", id, name, fare);
        }
    }
}
