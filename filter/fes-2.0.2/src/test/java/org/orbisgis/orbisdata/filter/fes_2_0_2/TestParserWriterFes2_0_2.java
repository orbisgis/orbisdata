package org.orbisgis.orbisdata.filter.fes_2_0_2;

import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.InputStream;

/**
 * Created by quillien on 16/05/2017.
 */
public class TestParserWriterFes2_0_2 {


    @Test
    public void testXmlToSql() throws JAXBException {
        //Branch SortBy
        InputStream xml = TestParserWriterFes2_0_2.class.getResourceAsStream("filter_Sorting.xml");
        ParserWriterFesXmlToSql parser = new ParserWriterFesXmlToSql(xml);
        parser.displayListTag();

        //Branch Filter
        xml = TestParserWriterFes2_0_2.class.getResourceAsStream("filter_PropertyIsBetween.xml");
        parser = new ParserWriterFesXmlToSql(xml);

        //Branch Filter
        xml = TestParserWriterFes2_0_2.class.getResourceAsStream("filter_PropertyIsLike.xml");
        parser = new ParserWriterFesXmlToSql(xml);

        //Branch Filter
        xml = TestParserWriterFes2_0_2.class.getResourceAsStream("filter_PropertyIsGreaterThan.xml");
        parser = new ParserWriterFesXmlToSql(xml);

    }
}
