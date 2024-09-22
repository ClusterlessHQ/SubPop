package io.clusterless.subpop.algorithm;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.RFC4180Parser;
import com.opencsv.RFC4180ParserBuilder;
import com.opencsv.exceptions.CsvValidationException;
import io.clusterless.subpop.algorithm.cptree.CPTree;
import io.clusterless.subpop.algorithm.items.ItemStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class JEPTest {

    private RFC4180Parser parser = new RFC4180ParserBuilder()
            .withSeparator(',')
            .build();

    @Test
    void mushroom() throws IOException, CsvValidationException {
        CPTree cpTree = handle("data/mushrooms.csv", true, true);

        Assertions.assertEquals(8416, cpTree.itemStore().size());
    }

    @Test
    void twoClass() throws IOException, CsvValidationException {
        CPTree cpTree = handle("data/two-class-example.csv", true, false);
        Assertions.assertEquals(8, cpTree.itemStore().size());
        System.out.println(cpTree.print());
    }

    private CPTree handle(String filename, boolean hasHeader, boolean retainCol) throws IOException, CsvValidationException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename)) {
            assert inputStream != null;
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                 CSVReader reader = createReader(inputStreamReader)) {

                int classIndex = 0;
                String[] headers = hasHeader ? reader.readNext() : new String[0];
                ItemStore itemStore = new ItemStore(classIndex, headers, retainCol);

                reader.iterator()
                        .forEachRemaining(itemStore::insert);

                return new CPTree(itemStore);
            }
        }
    }

    private CSVReader createReader(InputStreamReader inputStreamReader) {
        return new CSVReaderBuilder(inputStreamReader)
                .withCSVParser(parser)
                .withSkipLines(0)
                .build();
    }
}
