import com.github.sol239.javafi.backend.utils.database.DBHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.util.List;

public class DBHandlerTest {

    public static final String DB_PATH = "jdbc:h2:file:./data/mydb";

    @Test
    public void isConnectedTest1() {
        DBHandler db = new DBHandler(DB_PATH);
        assert db.isConnected();
    }

    @Test
    public void getAllTablesTest() {
        DBHandler db = new DBHandler(DB_PATH);
        List<String> tables1 = db.getAllTables();
        db.executeQuery("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(50))");
        List<String> tables2 = db.getAllTables();
        assert tables1.size() + 1 == tables2.size();
    }

    @Test
    public void cleanTest() {
        DBHandler db = new DBHandler(DB_PATH);
        try {
            InputStream inputStream = new FileInputStream("assets/csv/BTCUSD_1D.csv");
            db.insertCsvData("test_table", inputStream);
            // TODO: Programmatic interface must be created before testing this method so instruments can be added programmatically.
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            db.deleteTable("test_table");
        }
    }

    @Test
    public void deleteTableTest() {
        DBHandler db = new DBHandler(DB_PATH);
        List<String> tables1 = db.getAllTables();
        db.executeQuery("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(50))");
        List<String> tables2 = db.getAllTables();
        db.deleteTable("test_table");
        List<String> tables3 = db.getAllTables();
        assert tables1.size() == tables3.size();
    }

    @Disabled
    @Test
    public void getResultSetTest() {
        // adding mock data into database would be mandatory to test this method
        DBHandler db = new DBHandler(DB_PATH);
        try {
            InputStream inputStream = new FileInputStream("assets/csv/BTCUSD_1D.csv");
            db.insertCsvData("test_table", inputStream);
            ResultSet rs = db.getResultSet("SELECT * FROM eth");
            if (rs == null) {
                assert false;
            } else {
                int i = 0;
                while (rs.next()) {
                    i++;
                    if (i > 10) {
                        break;
                    }
                }
                assert true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            db.deleteTable("test_table");
        }
    }

    @Test
    public void executeQueryTest() {
        DBHandler db = new DBHandler(DB_PATH);
        try {
            List<String> tables1 = db.getAllTables();
            db.executeQuery("CREATE TABLE test_table (id SERIAL PRIMARY KEY, name VARCHAR(50))");
            List<String> tables2 = db.getAllTables();
            assert tables1.size() + 1 == tables2.size();
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            db.deleteTable("test_table");
        }
    }

    @Test
    public void insertCsvDataTest() {
        DBHandler db = new DBHandler(DB_PATH);
        try {
            InputStream inputStream = new FileInputStream("assets/csv/BTCUSD_1D.csv");
            db.insertCsvData("test_table", inputStream);
            ResultSet rs = db.getResultSet("SELECT * FROM test_table");
            while (rs.next()) {
                double close = rs.getDouble("close");
                if (close == 158.61) {
                    assert true;
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            db.deleteTable("test_table");
        }

    }






}
