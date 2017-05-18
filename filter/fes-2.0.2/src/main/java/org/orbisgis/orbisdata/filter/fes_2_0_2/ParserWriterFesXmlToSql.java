package org.orbisgis.orbisdata.filter.fes_2_0_2;

import net.opengis.fes._2_0_2.*;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.orbisgis.orbisdata.filter.fes_2_0_2.JaxbContainer.JAXBCONTEXT;

/**
 * @Author Vincent QUILLIEN
 */
public class ParserWriterFesXmlToSql {


    /**Object for the process of deserializing XML data into newly created Java content trees.*/
    private Unmarshaller unmarshaller;

    private JAXBElement objectFromFilterXml;

    private Object query;

    private HashMap<String, ArrayList<String>> listTag;

    private ArrayList<String> subListTag;

    public ParserWriterFesXmlToSql(InputStream filterXML) throws JAXBException {
        listTag = new HashMap<String, ArrayList<String>>();
        subListTag = new ArrayList<String>();
        unmarshaller = JAXBCONTEXT.createUnmarshaller() ;
        objectFromFilterXml = (JAXBElement) unmarshaller.unmarshal(filterXML);
        // first node : One branch for Filter and one for SortBy
        switch (objectFromFilterXml.getName().getLocalPart()){
            case "Filter":
                listTag.put("Filter", subListTag );
                queryToFilter();
                break;
            case "SortBy":
                listTag.put("SortBy", subListTag );
                queryToSortBy();
                break;
        }
    }

    private void queryToSortBy(){
        subListTag.clear();
        SortByType sortByType = (SortByType) objectFromFilterXml.getValue();
        if(sortByType.isSetSortProperty()){
            List<SortPropertyType> listProperty = sortByType.getSortProperty();
            for (SortPropertyType property : listProperty) {
                if (property.isSetValueReference()){
                    subListTag.add(property.getValueReference());
                }
                if (property.isSetSortOrder()){
                    subListTag.add(property.getSortOrder().value());
                }
            }
        }
    }

    private void queryToFilter(){
        subListTag.clear();
        FilterType filterType = (FilterType) objectFromFilterXml.getValue();
        if(filterType.isSetComparisonOps()){
            JAXBElement<ComparisonOpsType> comparisonElement = (JAXBElement<ComparisonOpsType>) filterType.getComparisonOps();
            listTag.put(comparisonElement.getName().getLocalPart(), subListTag);
            ComparisonOpsType comparisonOpsType = comparisonElement.getValue();
            operatorComparison(comparisonOpsType, comparisonElement);
        }
        else if(filterType.isSetLogicOps()){
            operatorLogical();
        }
        else if(filterType.isSetSpatialOps()){
            operatorSpatial();
        }
        else if(filterType.isSetTemporalOps()){
            operatorTemporal();
        }
        else if(filterType.isSetFunction()){
            operatorFunction();
        }
        else if(filterType.isSetId()){
            operatorId();
        }
        else if(filterType.isSetExtensionOps()){
            operatorExtension();
        }
    }

    private void operatorComparison(ComparisonOpsType comparisonOpsType, JAXBElement<ComparisonOpsType> comparisonElement){
        switch (comparisonElement.getName().getLocalPart()){
            case "PropertyIsLike" :
                ((PropertyIsLikeType) comparisonOpsType).getEscapeChar();
                ((PropertyIsLikeType) comparisonOpsType).getSingleChar();
                ((PropertyIsLikeType) comparisonOpsType).getWildCard();
                ((PropertyIsLikeType) comparisonOpsType).getExpres;
                break;
            case "PropertyIsGreaterThan":
                break;
            case "PropertyIsNil":
                break;
        }

    }

    private void operatorLogical(){

    }

    private void operatorSpatial(){

    }

    private void operatorTemporal(){

    }

    private void operatorFunction(){

    }


    private void operatorId(){

    }

    private void operatorExtension(){

    }

    private void getExpression(){}

    public void displayListTag(){
        for (Map.Entry<String,ArrayList<String>> element : listTag.entrySet()) {
            System.out.println(element.getKey()+"\n");
            for(String s : element.getValue()){
                System.out.println(s);
            }
            System.out.println("\n");
        }
    }
}
