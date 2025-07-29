package com.github.sol239.javafi.backend.utils.instrument;

import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * This class executes instruments on a table.
 */
@Component
public class InstrumentExecutor {

    private static DBHandler staticDbHandler;

    /**
     * The available instruments.
     */
    JavaInstrument[] availableInstruments;

    /**
     * Constructor for InstrumentExecutor.
     * It uses ServiceLoader to load all available JavaInstrument implementations.
     * @param dbHandler the database handler
     */
    @Autowired
    public InstrumentExecutor(DBHandler dbHandler) {
        ServiceLoader<JavaInstrument> loader = ServiceLoader.load(JavaInstrument.class);
        this.availableInstruments = loader.stream().map(ServiceLoader.Provider::get).toArray(JavaInstrument[]::new);
        InstrumentExecutor.staticDbHandler = dbHandler;
    }

    /**
     * Get the number of available instruments.
     * @return the number of available instruments
     */
    public int getInstrumentCount() {
        return availableInstruments.length;
    }

    /**
     * Get the available instruments.
     * @return the available instruments
     */
    public JavaInstrument[] getAvailableInstruments() {
        return availableInstruments;
    }

    /**
     * Get an instrument by name.
     * @param name the name of the instrument
     * @return the instrument
     */
    public JavaInstrument getInstrumentByName(String name) {
        for (JavaInstrument instrument : this.getAvailableInstruments()) {
            if (name.equals(instrument.getName())) {
                return instrument;
            }
        }
        return null;
    }

    /**
     * Run an instrument on a table - this method is deprecated. It used different approach to run instruments.
     * @param instrumentName the name of the instrument
     * @param tableName the name of the table
     * @param params the parameters for the instrument
     * @return the values of the column
     */
    @Deprecated
    public List<Double> getColumnValues(String instrumentName, String tableName, Double... params) {

        // GENERAL
        int stashSize = params[0].intValue();
        List<Double[]> stash = new ArrayList<>();

        // GENERAL
        ResultSet rs = staticDbHandler.getResultSet("SELECT * FROM " + tableName + " ORDER BY id ASC");

        // GENERAL
        // Chceme naplnit List columnValues:

        JavaInstrument instrument = null;
        Double[] values;
        List<Double> columnValues = new ArrayList<>();


        for (JavaInstrument _instrument : availableInstruments) {
            if (instrumentName.equals(_instrument.getName())) {
                instrument = _instrument;
            } else {
                return null;
            }}

        values = new Double[instrument.getColumnNames().length];

        // iterate over db
        int id = 0;
        int x = 0;
        try {
            while (rs.next()) {
                id += 1;
                for (int i = 0; i < instrument.getColumnNames().length; i++) {
                    values[i] = rs.getDouble(instrument.getColumnNames()[i]);
                }

                stash.add(values.clone());

                if (stash.size() == stashSize) {
                    stash.remove(0);
                    continue;
                }

                x++;

            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
        return columnValues;
    }

    /**
     * Run instruments on a list of tables.
     * @param tableNames the names of the tables
     * @param instruments the instruments to run
     */
    public static void runInstruments(List<String> tableNames, Map<String, Double[]> instruments ) {
        for (String tableName : tableNames) {
            runInstrument(tableName, instruments);
        }
    }

    /**
     * Get the database column name for an instrument.
     * @param instruments the instruments map
     * @param instrumentName the name of the instrument
     * @return the database column name for the instrument
     */
    public static String getInstrumentDBColumnName(Map<String, Double[]> instruments, String instrumentName) {
        StringBuilder params = new StringBuilder();
        for (Double param : instruments.get(instrumentName)) {
            params.append(param.intValue()).append("_");
        }
        params.delete(params.length() - 1, params.length());
        return instrumentName + "_" + params.toString() + "_ins_";
    }

    /**
     * Run instruments on a table.
     * @param tableName the name of the table
     * @param instruments the instruments to run
     */
    public static void runInstrument(String tableName, Map<String, Double[]> instruments) {

        Map<String, Double[]> instrumentsRemapped = new LinkedHashMap<>();
        for (Map.Entry<String, Double[]> entry : instruments.entrySet()) {
            String key = entry.getKey();
            Double[] value = entry.getValue();
            key = getInstrumentDBColumnName(instruments, key);
            instrumentsRemapped.put(key, value);
        }

        long t1 = System.currentTimeMillis();
        long rows = 0;

        // All needed columns
        Set<String> columnNames = new HashSet<>();
        columnNames.add("id");
        List<JavaInstrument> _instruments = new ArrayList<>();

        // Získání dostupných instrumentů přes ServiceLoader (staticky)
        JavaInstrument[] availableInstruments = ServiceLoader.load(JavaInstrument.class)
                .stream().map(ServiceLoader.Provider::get).toArray(JavaInstrument[]::new);

        for (Map.Entry<String, Double[]> entry : instruments.entrySet()) {
            Double[] params = entry.getValue();
            String instrumentName = entry.getKey();

            for (JavaInstrument instrument : availableInstruments) {
                if (instrumentName.equals(instrument.getName())) {
                    columnNames.addAll(Arrays.asList(instrument.getColumnNames()));
                    _instruments.add(instrument);
                }
            }
        }

        String names = String.join(", ", columnNames);
        ResultSet rs = staticDbHandler.getResultSet("SELECT " + names + " FROM " + tableName + " ORDER BY id ASC");


        // instrument name (rsi) : InstrumentHelper ( Map : string column name, List<Double> values)
        LinkedHashMap<String, InstrumentHelper> columns = new LinkedHashMap<>();
        for (String instrumentName : instruments.keySet()) {
            String name = getInstrumentDBColumnName(instruments, instrumentName);
            columns.put(name, new InstrumentHelper(instruments.get(instrumentName)[0].intValue()));
        }

        // map column name : Double representing row
        HashMap<String, Double> row = new LinkedHashMap<>();

        HashMap<String, List<IdValueRecord>> results = new LinkedHashMap<>();

        long id = 0;
        try {
            while(rs.next()) {

                for (String columnName : columnNames) {
                    row.put(columnName, rs.getDouble(columnName));
                }

                id += 1;

                for (JavaInstrument instrument : _instruments) {
                    String name = instrument.getName();

                    name = getInstrumentDBColumnName(instruments, name);

                    InstrumentHelper helper = columns.get(name);

                    for (String param : instrument.getColumnNames()) {
                        helper.add(param, row.get(param));
                    }
                    columns.put(name, helper);
                    double val = instrument.updateRow(columns.get(name).stash, instrumentsRemapped.get(name));
                    results.putIfAbsent(name, new ArrayList<>());
                    results.get(name).add(new IdValueRecord(id, val));
                    rows += 1;

                }
                row.clear();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        long t2 = System.currentTimeMillis();

        // print results columns
        results.forEach((name, records) -> {System.out.println(name);});

        // FAST DB UPDATE
        staticDbHandler.insertColumnsBatch(tableName, results);

        long t3 = System.currentTimeMillis();

        /*
        System.out.println("Columns[" + columnNames.size() + "]: " + columnNames);
        System.out.println("Instruments[" + _instruments.size() + "]: " + _instruments.stream().map(JavaInstrument::getName).toList());
        System.out.println("INSTRUMENT EXECUTION TIME: " + Double.parseDouble(String.valueOf(t2 - t1)) / 1000 + " s");
        System.out.println("DB INSERTION TIME: " + Double.parseDouble(String.valueOf(t3 - t2)) / 1000 + " s");
        System.out.println("Total Rows: " + rows);
         */
    }
}
