# Laboratorium 6 - CSV Reader

CSV to popularny tekstowy format zapisu danych.

- Poszczególne pola oddzielone są separatorami, np. przecinkami, średnikami lub znakami tabulacji.
- Kolumny powinny zawierać dane tego samego typu (liczby całkowite, double, teksty, daty)
- Często na początku pliku umieszczany jest wiersz nagłówkowy z nazwami (etykietami) kolumn
- W niektórych rekordach może brakować wartości
- Teksty mogą być ujęte w cudzysłowy

Przykład `titanic-part.csv` - informacje o pasażerach Titanica.

```csv
PassengerId,Survived,Pclass,Name,Sex,Age,SibSp,Parch,Ticket,Fare,Cabin,Embarked
1,0,3,"Braund, Mr. Owen Harris",male,22,1,0,A/5 21171,7.25,,S
2,1,1,"Cumings, Mrs. John Bradley (Florence Briggs Thayer)",female,38,1,0,PC 17599,71.2833,C85,C
3,1,3,"Heikkinen, Miss. Laina",female,26,0,0,STON/O2. 3101282,7.925,,S
4,1,1,"Futrelle, Mrs. Jacques Heath (Lily May Peel)",female,35,1,0,113803,53.1,C123,S
5,0,3,"Allen, Mr. William Henry",male,35,0,0,373450,8.05,,S
6,0,3,"Moran, Mr. James",male,,0,0,330877,8.4583,,Q
7,0,1,"McCarthy, Mr. Timothy J",male,54,0,0,17463,51.8625,E46,S
8,0,3,"Palsson, Master. Gosta Leonard",male,2,3,1,349909,21.075,,S
9,1,3,"Johnson, Mrs. Oscar W (Elisabeth Vilhelmina Berg)",female,27,0,2,347742,11.1333,,S
10,1,2,"Nasser, Mrs. Nicholas (Adele Achem)",female,14,1,0,237736,30.0708,,C
11,1,3,"Sandstrom, Miss. Marguerite Rut",female,4,1,1,PP 9549,16.7,G6,S
12,1,1,"Bonnell, Miss. Elizabeth",female,58,0,0,113783,26.55,C103,S
13,0,3,"Saundercock, Mr. William Henry",male,20,0,0,A/5. 2151,8.05,,S
14,0,3,"Andersson, Mr. Anders Johan",male,39,1,5,347082,31.275,,S
15,0,3,"Vestrom, Miss. Hulda Amanda Adolfina",female,14,0,0,350406,7.8542,,S
16,1,2,"Hewlett, Mrs. (Mary D Kingcome) ",female,55,0,0,248706,16,,S
17,0,3,"Rice, Master. Eugene",male,2,4,1,382652,29.125,,Q
18,1,2,"Williams, Mr. Charles Eugene",male,,0,0,244373,13,,S
```

Naszym zadaniem jest napisanie klasy konfigurowalnej `CSVReader` pozwalającej na odczyt danych z plików CSV.

## Przykładowe pliki do wykorzystania

- [with-header.csv](examples/with-header.csv)
- [no-header.csv](examples/no-header.csv)
- [accelerator.csv](examples/accelerator.csv)
- [missing-values.csv](examples/missing-values.csv)
- [elec.csv](examples/elec.csv)

## Zadeklaruj klasę CSVReader

```java
public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;
 
    /**
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */
    public CSVReader(String filename, String delimiter, boolean hasHeader) {
        reader = new BufferedReader(new FileReader(filename));
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if (hasHeader) {
            parseHeader();
        }
    }
    //...
}
```

- `Reader` to klasa pozwalająca na odczyt bajtów i konwersję ich na **znaki** unicode. Czyli np. dwa bajty w pliku w formacie UTF-8 rerezentujące polskie znaki zostaną zamienione na ę, ą, ł, itd.
- `FileReader` czyta znaki z pliku
- `BufferedReader` dodaje możliwość buforowanego odczytu, czyli czytania całych linii

## Nazwy kolumn

Nazwy kolumn będą przechowywane w dwóch miejscach: na liście oraz w mapie. Mapa ma przyspieszyć wyszukiwanie indeksu w tablicy. Można po prostu szukać tekstu na liście, ale w ten sposób będzie prościej.

```java
// nazwy kolumn w takiej kolejności, jak w pliku
List<String> columnLabels = new ArrayList<>();
// odwzorowanie: nazwa kolumny -> numer kolumny
Map<String,Integer> columnLabelsToInt = new HashMap<>();
```

Funkcja `parseHeader()` pokazuje typowe przetwarzanie wiersza pliku (w tym przypadku nagłówka)

```java
void parseHeader() {
    // wczytaj wiersz
    String line = reader.readLine();
    if (line == null){
        return;
    }
    // podziel na pola
    String[] header = line.split(delimiter);
    // przetwarzaj dane w wierszu
    for (int i = 0; i < header.length; i++) {
        // dodaj nazwy kolumn do columnLabels i numery do columnLabelsToInt
    }
}
```

## Odczyt danych

Przyjmijmy następującą strategię:

- aby wczytać kolejny rekord należy wywołać funkcję `next()`. Jeśli nie udało się wczytać wiersza - ma ona zwrócić `false`
- przed rozpoczęciem dostępu do danych nic nie jest wczytane, czyli na początku należy wywołać `next()`
- funkcja `next()` działa podobnie, jak `parseHeader()` rozpakowuje zawartość wiersza i przypisuje do `current`
- klasa zapewnia funkcje odczytu zawartości elementów tablicy `current` (i ewentualnej konwersji).

```java
String[]current;
boolean next() {
    // czyta następny wiersz, dzieli na elementy i przypisuje do current
    return false;
}
```

Czyli standardowy sposób dostępu do danych powinien być następujący:

```java
CSVReader reader = new CSVReader("titanic-part.csv", ",", true);
while (reader.next()) {
    int id = reader.getInt("PassengerId");
    String name = reader.get("Name");
    double fare = reader.getDouble("Fare");

    System.out.printf(Locale.US, "%d %s %d", id, name, fare);
}
```

Ponieważ plik CSV może nie zawierać nagłówka, klasa `CSVReader` powininna zapewniać interfejs dostępu do pól poprzez numer kolumny

```java
CSVReader reader = new CSVReader("titanic-part.csv", ",", true);
while (reader.next()) {
    int id = reader.getInt(0);
    String name = reader.get(3);
    double fare = reader.getDouble(9);
    System.out.printf(Locale.US, "%d %s %d", id, name, fare);
}
```

**Uwaga, nie zaczynaj od pliku `titanic-part.csv`, ponieważ będzie sprawiał problemy. Potraktuj powyższy kod, jako przykład i dostosuj do konkretnego pliku. O tym na samym końcu...**

## Do zaimplementowania

- Wymienione wcześniej funkcje
- Konstruktory przyjmujące standardowe wartości `CSVReader(String filename, String delimiter)` oraz `CSVReader(String filename)`
- Dodaj jeszcze konstruktor `public CSVReader(Reader reader, String delimiter, boolean hasHeader)` pozwalający na odczyt z dowolnego źródła
- Wywołaj konstruktor najbardziej ogólny z konstruktorów przyjmujących standardowe wartości argumentów wywołania tak, aby nie duplikować kodu. O wywołaniu konstruktora z innego - patrz treść wykładu [Wykład 4-5 Klasy, pola, metody, konstruktory, klonowanie](http://pszwed.kis.agh.edu.pl/wyklady_java/w4-5-java-klasy.pdf)
- `List<String> getColumnLabels()` - zwraca etykiety kolumn
- `int getRecordLength()` - zwraca długość bieżącego wczytanego rekordu
- `boolean isMissing(int columnIndex)` – czy wartość istnieje w bieżącym rekordzie
- `boolean isMissing(String columnLabel)` – analogiczny dostęp przez etykietę kolumny
- `String get(int columnIndex)` oraz `String get(String columnLabel)` zwraca wartość jako `String`, raczej pusty tekst, a nie `null`.
- `int getInt(int columnIndex)` oraz `int getInt(String columnLabel)` - funkcja konwertuje wartość do `int`. Użyj `Integer.parseInt()`. Funkcja wygeneruje wyjątek, jeśli pole było puste.
- `long getLong(int columnIndex)` oraz `long getLong(String columnLabel)`
- `double getDouble(int columnIndex)` oraz `double getDouble(String columnLabel)`

### Uwagi do `isMissing()`

```java
for(String s:"ala ma kota,12,,,,4,,,,".split(",")){
    System.out.println("<"+s+">");
}
```

Wynik

```
<ala ma kota>
<12>
<>
<>
<>
<4>
```

Brakuje atrybutu jeśli:

- numer kolumny jest poza zakresem `current.length`
- tekst jest pusty

## Napisz testy

Nie będą to formalnie testy jednostkowe poszczególnych funkcji ale raczej testy scenariuszy odczytu z pliku

1. Napisz testy całych sekwencji odczytu przykładowych plików. W pętli wykonuj `next()` i odczytaj zawartość wszystkich pól jako `String` oraz wypisz
2. Przetestuj poprawność funkcji zwracających wartości poszczególnych typów.
3. Pokaż, że jesteś w stanie przetworzyć plik z brakującymi wartościami pól. Możesz zastosować dwie strategie - albo przechwycisz wyjątek, albo sprawdzisz, czy nie brakuje wartości.
4. Napisz testy odwołań się do nieistniejących kolumn, zarówno podanych jako indeksy, jak i nazwy...
5. Przetestuj także odczyt z innych źródeł niż plik, np.
    ```java
    String text = "a,b,c\n123.4,567.8,91011.12";
    reader = new CSVReader(new StringReader(text), ",", true);
    ```

## Teksty w cudzysłowach

Zastanów się, co można zrobić z tekstami w cudzysłowach. Przy eksporcie CSV pola umieszcza się w cudzysłowach, jeśli zwierają separatory pól. Tak właśnie jest w pliku titanic-part.csv. Jest to problem, ponieważ przy standardowej metodzie przetwarzania nastąpi wydzielenie pól wewnątrz tekstu w cudzysłowach, czego oczywiście chcemy uniknąć.

Odpowiedź jest tu: [https://stackoverflow.com/questions/15738918/splitting-a-csv-file-with-quotes-as-text-delimiter-using-string-split](https://stackoverflow.com/questions/15738918/splitting-a-csv-file-with-quotes-as-text-delimiter-using-string-split), ale trzeba dostosować kod do zdefiniowanego ogranicznika pól...

Możesz użyć `String.format()` do przygotowania wyrażenia regularnego dla funkcji `split()` lub przekazać wyrażenie regularne, jako ogranicznik. Wybór nie jest jednoznaczny - oba rozwiązania mają wady i zalety. Prawdopodobnie bardziej elastyczne jest przekazanie wyrażenia regularnego.

## Funkcje `getTime` i `getDate`

Napisz funkcje CSVReader zwracające czas i datę. Ich dodatkowym parametrem powinien być format zapisu, czyli np. `LocalTime getTime(int columnIndes, String format)`.

Możesz wykorzystać poniższe fragmenty kodu

```java
LocalTime time = LocalTime.parse("12:55", DateTimeFormatter.ofPattern("HH:mm"));
System.out.println(time);
time = LocalTime.parse("12:55:23", DateTimeFormatter.ofPattern("HH:mm:ss"));
System.out.println(time);

LocalDate date = LocalDate.parse("2017-11-30", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
System.out.println(date);
date = LocalDate.parse("23.11.2017", DateTimeFormatter.ofPattern("dd.MM.yyyy"));
System.out.println(date);
```

Możesz także napisać funkcje zwracające `LocalDateTime` (połaczenie daty i czasu).

## Zestaw znaków

Poniższy kod pokazuje, jak odczytać/zapisać plik wskazując zestaw znaków

```java
try (BufferedReader input = new BufferedReader(new InputStreamReader( new FileInputStream(inname), Charset.forName("Cp1250")))) {
    try (PrintWriter output = new PrintWriter( new OutputStreamWriter(new FileOutputStream(outname), "Cp1250"))) {
        for(;;) {
            String line = input.readLine();
            if (line == null) {
                break;
            }
            output.println(line);
        }
    }
}
```