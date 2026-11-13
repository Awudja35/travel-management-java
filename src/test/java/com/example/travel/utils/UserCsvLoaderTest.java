package com.example.travel.utils;

import com.example.travel.exception.DataLoad;
import com.example.travel.model.Agent;
import com.example.travel.model.Customer;
import com.example.travel.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserCsvLoaderTest {

    private static final String HEADER = "id,type,name,balance\n";

    @TempDir
    Path tempDir;

    private final UserCsvLoader loader = new UserCsvLoader();

    // writes a small CSV file into the temporary folder and returns its path
    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("users.csv");
        Files.writeString(file, content);
        return file;
    }

    // ---------- Normal cases ----------

    @Test
    void shouldLoadUsersFromFile() throws IOException {
        Path file = writeFile(HEADER
                + "1,CUSTOMER,Alice,1000.0\n"
                + "2,AGENT,Bob,500.0\n");

        List<User> users = loader.load(file);

        assertEquals(2, users.size());
        assertEquals("Alice", users.get(0).getName());
        assertEquals(1000.0, users.get(0).getBalance(), 0.001);
        assertEquals("Bob", users.get(1).getName());
    }

    @Test
    void shouldCreateCustomersAndAgentsFromTheTypeColumn() throws IOException {
        Path file = writeFile(HEADER
                + "1,CUSTOMER,Alice,1000.0\n"
                + "2,AGENT,Bob,500.0\n");

        List<User> users = loader.load(file);

        assertInstanceOf(Customer.class, users.get(0));
        assertInstanceOf(Agent.class, users.get(1));
        assertEquals(0.10, users.get(1).getDiscountRate(), 0.001);
    }

    // ---------- Edge cases ----------

    @Test
    void shouldReturnEmptyListWhenFileHasOnlyHeader() throws IOException {
        Path file = writeFile(HEADER);

        List<User> users = loader.load(file);

        assertEquals(0, users.size());
    }

    @Test
    void shouldIgnoreBlankLines() throws IOException {
        Path file = writeFile(HEADER
                + "1,CUSTOMER,Alice,1000.0\n"
                + "\n"
                + "2,AGENT,Bob,500.0\n");

        List<User> users = loader.load(file);

        assertEquals(2, users.size());
    }

    // ---------- Invalid cases ----------

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        Path missing = tempDir.resolve("does-not-exist.csv");

        assertThrows(DataLoad.class, () -> loader.load(missing));
    }

    @Test
    void shouldThrowWhenUserTypeIsUnknown() throws IOException {
        Path file = writeFile(HEADER + "1,MANAGER,Alice,1000.0\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenBalanceIsNotANumber() throws IOException {
        Path file = writeFile(HEADER + "1,CUSTOMER,Alice,abc\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenBalanceIsNegative() throws IOException {
        Path file = writeFile(HEADER + "1,CUSTOMER,Alice,-5.0\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenALineHasMissingValues() throws IOException {
        Path file = writeFile(HEADER + "1,CUSTOMER,Alice\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldNameTheLineNumberInTheErrorMessage() throws IOException {
        // the bad row is the third line of the file (header, Alice, then Bob)
        Path file = writeFile(HEADER
                + "1,CUSTOMER,Alice,1000.0\n"
                + "2,MANAGER,Bob,500.0\n");

        DataLoad error = assertThrows(DataLoad.class, () -> loader.load(file));

        assertTrue(error.getMessage().contains("line 3"));
    }
    @Test
    void shouldThrowWhenUserIdIsZero() throws IOException {
        Path file = writeFile(HEADER + "0,CUSTOMER,Alice,1000.0\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenUserNameIsEmpty() throws IOException {
        // two commas in a row give an empty name
        Path file = writeFile(HEADER + "1,CUSTOMER,,1000.0\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }
}