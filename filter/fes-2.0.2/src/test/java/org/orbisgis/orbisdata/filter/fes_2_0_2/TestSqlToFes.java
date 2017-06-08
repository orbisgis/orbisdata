/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 *
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.filter.fes_2_0_2;


import net.opengis.fes._2_0_2.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.util.HashMap;

import static org.orbisgis.orbisdata.filter.fes_2_0_2.SqlToFes.sqlToXml;

/**
 * Test of the classFesToSql
 * @author Vincent QUILLIEN
 */
public class TestSqlToFes {

    /**
     * Test of the static method for SortBy
     */
    @Test
    public void testXmlToSqlSortBy() {
        //Branch SortBy
        String request = "SELECT Column1 FROM table1 ORDER BY column1 ASC, column2 DESC";
        HashMap<String,JAXBElement> list = sqlToXml(request);
        JAXBElement<SortByType> sortByElement = list.get("ORDER BY");
        SortByType sortBy = sortByElement.getValue();

        Assert.assertTrue(sortByElement instanceof JAXBElement);
        Assert.assertEquals(sortByElement.getName().getLocalPart(),"SortBy");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getValueReference(),"column1");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getSortOrder().value(),"ASC");
        Assert.assertEquals(sortBy.getSortProperty().get(1).getValueReference(),"column2");
        Assert.assertEquals(sortBy.getSortProperty().get(1).getSortOrder().value(),"DESC");

        request = "SELECT Column1 FROM table1 ORDER BY column1 ASC, column2";
        list = sqlToXml(request);
        sortByElement = list.get("ORDER BY");
        sortBy = sortByElement.getValue();

        Assert.assertTrue(sortByElement instanceof JAXBElement);
        Assert.assertEquals(sortByElement.getName().getLocalPart(),"SortBy");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getValueReference(),"column1");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getSortOrder().value(),"ASC");
        Assert.assertEquals(sortBy.getSortProperty().get(1).getValueReference(),"column2");

        request = "SELECT Column1 FROM table1 ORDER BY column1, column2";
        list = sqlToXml(request);
        sortByElement = list.get("ORDER BY");
        sortBy = sortByElement.getValue();

        Assert.assertTrue(sortByElement instanceof JAXBElement);
        Assert.assertEquals(sortByElement.getName().getLocalPart(),"SortBy");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getValueReference(),"column1");
        Assert.assertEquals(sortBy.getSortProperty().get(1).getValueReference(),"column2");

        request = "SELECT Column1 FROM table1 ORDER BY find(element1,element2)";
        list = sqlToXml(request);
        sortByElement = list.get("ORDER BY");
        sortBy = sortByElement.getValue();

        Assert.assertTrue(sortByElement instanceof JAXBElement);
        Assert.assertEquals(sortByElement.getName().getLocalPart(),"SortBy");
        Assert.assertEquals(sortBy.getSortProperty().get(0).getValueReference(),"find(element1,element2)");

    }


    /**
     * Test of the static method for the comparison operator
     */
    @Test
    public void testXmlToSqlFilterComparison() {

        //Branch PropertyIsEqualTo
        String request = "SELECT Column1 FROM table1 WHERE DEPTH = 100";
        HashMap<String,JAXBElement> list = sqlToXml(request);
        JAXBElement<FilterType> filterElement = list.get("WHERE");
        FilterType filter = filterElement.getValue();
        BinaryComparisonOpType comparison = (BinaryComparisonOpType) filter.getComparisonOps().getValue();
        LiteralType literal = (LiteralType) comparison.getExpression().get(1).getValue();


        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsEqualTo");
        Assert.assertEquals(comparison.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

        //Branch PropertyIsBetween
        request = "SELECT Column1 FROM table1 WHERE DEPTH BETWEEN 100 200";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        PropertyIsBetweenType between = (PropertyIsBetweenType) filter.getComparisonOps().getValue();
        LiteralType upper = (LiteralType) between.getUpperBoundary().getExpression().getValue();
        LiteralType lower = (LiteralType) between.getLowerBoundary().getExpression().getValue();


        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsBetween");
        Assert.assertEquals(between.getExpression().getValue().toString(),"DEPTH");
        Assert.assertEquals(lower.getContent().get(0).toString(),"100");
        Assert.assertEquals(upper.getContent().get(0).toString(),"200");

        //Branch PropertyIsNull
        request = "SELECT Column1 FROM table1 WHERE DEPTH IS NULL";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        PropertyIsNullType nullType = (PropertyIsNullType) filter.getComparisonOps().getValue();


        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsNull");
        Assert.assertEquals(nullType.getExpression().getValue().toString(),"DEPTH");

        //Branch PropertyIsLike
        request = "SELECT Column1 FROM table1 WHERE name LIKE 'smi' ";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        PropertyIsLikeType propertyIsLike = (PropertyIsLikeType) filter.getComparisonOps().getValue();


        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsLike");
        Assert.assertEquals(propertyIsLike.getExpression().get(0).getValue().toString(),"name");

        //Branch PropertyIsGreaterThan
        request = "SELECT Column1 FROM table1 WHERE DEPTH > 100";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinaryComparisonOpType propertyIsGreaterThan = (BinaryComparisonOpType) filter.getComparisonOps().getValue();
        literal = (LiteralType) propertyIsGreaterThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsGreaterThan");
        Assert.assertEquals(propertyIsGreaterThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

        //Branch PropertyIsGreaterThanOrEqualTo
        request = "SELECT Column1 FROM table1 WHERE DEPTH >= 100";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinaryComparisonOpType propertyIsGreaterThanOrEqualTo = (BinaryComparisonOpType) filter.getComparisonOps().getValue();
        literal = (LiteralType) propertyIsGreaterThanOrEqualTo.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsGreaterThanOrEqualTo");
        Assert.assertEquals(propertyIsGreaterThanOrEqualTo.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

        //Branch PropertyIsLessThan
        request = "SELECT Column1 FROM table1 WHERE DEPTH < 100";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinaryComparisonOpType propertyIsLessThan = (BinaryComparisonOpType) filter.getComparisonOps().getValue();
        literal = (LiteralType) propertyIsLessThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsLessThan");
        Assert.assertEquals(propertyIsLessThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

        //Branch PropertyIsLessThanOrEqualTo
        request = "SELECT Column1 FROM table1 WHERE DEPTH <= 100";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinaryComparisonOpType propertyIsLessThanOrEqualTo = (BinaryComparisonOpType) filter.getComparisonOps().getValue();
        literal = (LiteralType) propertyIsLessThanOrEqualTo.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getComparisonOps().getName().getLocalPart(),"PropertyIsLessThanOrEqualTo");
        Assert.assertEquals(propertyIsLessThanOrEqualTo.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

    }

    /**
     * Test of the static method for the spatial operator
     *
     * @throws JAXBException
     */
    @Test
    public void testXmlToSqlFilterSpatial() throws JAXBException {
        //not implemented Yet
        //Branch Equals

        //Branch Dwithin

        //Branch BBOX

    }

    /**
     * Test of the static method for the type Function
     *
     * @throws JAXBException
     */
    @Test
    public void testXmlToSqFilterFunction() throws JAXBException {
        //not implemented Yet
    }

    /**
     * Test of the static method for the type logical
     *
     * @throws JAXBException
     */
    @Test
    public void testXmlToSqlFilterLogical() throws JAXBException {
        //not implemented Yet
        //Branch AndOr

        //Branch Not

    }

}
