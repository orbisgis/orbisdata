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
     */
    @Test
    public void testXmlToSqlFilterSpatial() {
        //Branch DWithin
        String request = "SELECT Column1 FROM table1 WHERE ST_DWithin(Paris, London, 344)";
        HashMap<String,JAXBElement> list = sqlToXml(request);
        JAXBElement<FilterType> filterElement = list.get("WHERE");
        FilterType filter = filterElement.getValue();
        DistanceBufferType distanceBuffer = (DistanceBufferType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"DWithin");
        Assert.assertEquals(((JAXBElement)distanceBuffer.getExpressionOrAny().get(0)).getValue().toString(),"Paris");
        Assert.assertEquals(((JAXBElement)distanceBuffer.getExpressionOrAny().get(1)).getValue().toString(),"London");
        Assert.assertEquals(distanceBuffer.getDistance().getValue(),new Double(344.0), new Double(0));


        //Branch BBOX
        request = "SELECT Column1 FROM table1 WHERE NOT ST_Disjoint(element, 100, 200)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BBOXType bbox = (BBOXType) filter.getSpatialOps().getValue();
        JAXBElement expression = (JAXBElement) bbox.getExpressionOrAny().get(1);
        JAXBElement expression2 = (JAXBElement) bbox.getExpressionOrAny().get(2);

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"BBOX");
        Assert.assertEquals(((JAXBElement)bbox.getExpressionOrAny().get(0)).getValue().toString(),"element");
        Assert.assertEquals(((LiteralType)expression.getValue()).getContent().get(0).toString(),"100");
        Assert.assertEquals(((LiteralType)expression2.getValue()).getContent().get(0).toString(),"200");

        //Branch Equals
        request = "SELECT Column1 FROM table1 WHERE ST_Equals(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType equals = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Equals");
        Assert.assertEquals(((JAXBElement)equals.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)equals.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Disjoint
        request = "SELECT Column1 FROM table1 WHERE ST_Disjoint(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType disjoint = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Disjoint");
        Assert.assertEquals(((JAXBElement)disjoint.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)disjoint.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Touches
        request = "SELECT Column1 FROM table1 WHERE ST_Touches(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType touches = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Touches");
        Assert.assertEquals(((JAXBElement)touches.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)touches.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Overlaps
        request = "SELECT Column1 FROM table1 WHERE ST_Overlaps(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType overlaps = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Overlaps");
        Assert.assertEquals(((JAXBElement)overlaps.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)overlaps.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Crosses
        request = "SELECT Column1 FROM table1 WHERE ST_Crosses(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType crosses = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Crosses");
        Assert.assertEquals(((JAXBElement)crosses.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)crosses.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Intersects
        request = "SELECT Column1 FROM table1 WHERE ST_Intersects(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType intersects = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Intersects");
        Assert.assertEquals(((JAXBElement)intersects.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)intersects.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Contains
        request = "SELECT Column1 FROM table1 WHERE ST_Contains(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType contains = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Contains");
        Assert.assertEquals(((JAXBElement)contains.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)contains.getExpressionOrAny().get(1)).getValue().toString(),"element2");

        //Branch Within
        request = "SELECT Column1 FROM table1 WHERE ST_Within(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinarySpatialOpType within = (BinarySpatialOpType) filter.getSpatialOps().getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getSpatialOps().getName().getLocalPart(),"Within");
        Assert.assertEquals(((JAXBElement)within.getExpressionOrAny().get(0)).getValue().toString(),"element1");
        Assert.assertEquals(((JAXBElement)within.getExpressionOrAny().get(1)).getValue().toString(),"element2");
    }

    /**
     * Test of the static method for the type logical
     */
    @Test
    public void testXmlToSqlFilterLogical() {

        //Branch Not
        String request = "SELECT Column1 FROM table1 WHERE NOT DEPTH < 100";
        HashMap<String,JAXBElement> list = sqlToXml(request);
        JAXBElement<FilterType> filterElement = list.get("WHERE");
        FilterType filter = filterElement.getValue();
        UnaryLogicOpType unaryLogic= (UnaryLogicOpType) filter.getLogicOps().getValue();
        BinaryComparisonOpType propertyIsLessThan = (BinaryComparisonOpType) unaryLogic.getComparisonOps().getValue();
        LiteralType literal = (LiteralType) propertyIsLessThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getLogicOps().getName().getLocalPart(),"logicOps");
        Assert.assertEquals(propertyIsLessThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");

        //Branch Not Not
        request = "SELECT Column1 FROM table1 WHERE NOT NOT DEPTH < 100";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        unaryLogic= (UnaryLogicOpType) filter.getLogicOps().getValue();
        JAXBElement<UnaryLogicOpType> unaryLogicElement = (JAXBElement<UnaryLogicOpType>) unaryLogic.getLogicOps();
        propertyIsLessThan = (BinaryComparisonOpType) unaryLogicElement.getValue().getComparisonOps().getValue();
        literal = (LiteralType) propertyIsLessThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getLogicOps().getName().getLocalPart(),"logicOps");
        Assert.assertEquals(unaryLogicElement.getName().getLocalPart(),"logicOps");
        Assert.assertEquals(propertyIsLessThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literal.getContent().get(0).toString(),"100");


        //Branch AND
        request = "SELECT Column1 FROM table1 WHERE DEPTH < 100 AND DEPTH > 50";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        BinaryLogicOpType binaryLogic = (BinaryLogicOpType) filter.getLogicOps().getValue();
        JAXBElement element1 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(0);
        JAXBElement element2 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(1);
        BinaryComparisonOpType lessThan = (BinaryComparisonOpType) element1.getValue();
        BinaryComparisonOpType greaterThan = (BinaryComparisonOpType) element2.getValue();
        LiteralType literalLess = (LiteralType) lessThan.getExpression().get(1).getValue();
        LiteralType literalGreater = (LiteralType) greaterThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getLogicOps().getName().getLocalPart(),"And");
        Assert.assertEquals(element1.getName().getLocalPart(),"PropertyIsLessThan");
        Assert.assertEquals(element2.getName().getLocalPart(),"PropertyIsGreaterThan");
        Assert.assertEquals(lessThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(greaterThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literalLess.getContent().get(0).toString(),"100");
        Assert.assertEquals(literalGreater.getContent().get(0).toString(),"50");


        //Branch OR
        request = "SELECT Column1 FROM table1 WHERE DEPTH < 100 OR DEPTH > 50";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        binaryLogic = (BinaryLogicOpType) filter.getLogicOps().getValue();
        element1 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(0);
        element2 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(1);
        lessThan = (BinaryComparisonOpType) element1.getValue();
        greaterThan = (BinaryComparisonOpType) element2.getValue();
        literalLess = (LiteralType) lessThan.getExpression().get(1).getValue();
        literalGreater = (LiteralType) greaterThan.getExpression().get(1).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getLogicOps().getName().getLocalPart(),"Or");
        Assert.assertEquals(element1.getName().getLocalPart(),"PropertyIsLessThan");
        Assert.assertEquals(element2.getName().getLocalPart(),"PropertyIsGreaterThan");
        Assert.assertEquals(lessThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(greaterThan.getExpression().get(0).getValue().toString(),"DEPTH");
        Assert.assertEquals(literalLess.getContent().get(0).toString(),"100");
        Assert.assertEquals(literalGreater.getContent().get(0).toString(),"50");

        //Branch OR AND
        request = "SELECT Column1 FROM table1 WHERE DEPTH < 100 OR DEPTH > 50 AND DEPTH <= 1000";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        binaryLogic = (BinaryLogicOpType) filter.getLogicOps().getValue();
        element1 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(0);
        element2 = binaryLogic.getComparisonOpsOrSpatialOpsOrTemporalOps().get(1);

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getLogicOps().getName().getLocalPart(),"And");

    }


    /**
     * Test of the static method for the type Function
     */
    @Test
    public void testXmlToSqFilterFunction() {

        //Branch function
        String request = "SELECT Column1 FROM table1 WHERE function(element1, element2)";
        HashMap<String,JAXBElement> list = sqlToXml(request);
        JAXBElement<FilterType> filterElement = list.get("WHERE");
        FilterType filter = filterElement.getValue();
        FunctionType function= filter.getFunction();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getFunction().getName(),"function");
        Assert.assertEquals(function.getExpression().get(0).getValue().toString(),"element1");
        Assert.assertEquals(function.getExpression().get(1).getValue().toString(),"element2");

        //Branch function
        request = "SELECT Column1 FROM table1 WHERE function( function(element1, element2)";
        list = sqlToXml(request);
        filterElement = list.get("WHERE");
        filter = filterElement.getValue();
        FunctionType function1 = filter.getFunction();
        FunctionType function2 = (FunctionType) function1.getExpression().get(0).getValue();

        Assert.assertTrue(filterElement instanceof JAXBElement);
        Assert.assertEquals(filterElement.getName().getLocalPart(),"Filter");
        Assert.assertEquals(filter.getFunction().getName(),"function");
        Assert.assertEquals(function1.getExpression().get(0).getName().getLocalPart(), "Function");
        Assert.assertEquals(function2.getExpression().get(0).getValue().toString(),"element1");
        Assert.assertEquals(function2.getExpression().get(1).getValue().toString(),"element2");

    }

}
