/*
 * Bundle DatafFame is part of the OrbisGIS platform
 *
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
 * DataManager API  is distributed under LGPL 3 license.
 *
 * Copyright (C) 2019 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager API  is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager API  is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager API. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.dataframe;

import groovy.lang.GString;
import org.h2gis.utilities.TableLocation;
import org.locationtech.jts.geom.Geometry;
import org.orbisgis.commons.annotations.NotNull;
import org.orbisgis.commons.annotations.Nullable;
import org.orbisgis.commons.printer.Ascii;
import org.orbisgis.commons.printer.Html;
import org.orbisgis.commons.printer.ICustomPrinter;
import org.orbisgis.orbisdata.datamanager.api.dataset.DataBaseType;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ISpatialTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.orbisgis.orbisdata.datamanager.api.datasource.IJdbcDataSource;
import org.orbisgis.orbisdata.datamanager.api.dsl.IBuilderResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.data.Tuple;
import smile.data.formula.Formula;
import smile.data.type.DataType;
import smile.data.type.DataTypes;
import smile.data.type.StructField;
import smile.data.type.StructType;
import smile.data.vector.Vector;
import smile.data.vector.*;
import smile.math.matrix.DenseMatrix;
import smile.math.matrix.Matrix;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.CENTER;
import static org.orbisgis.commons.printer.ICustomPrinter.CellPosition.RIGHT;

/**
 * Wrap the {@link smile.data.DataFrame} into a new class implementing the {@link ITable} interface. This new
 * DataFrame is compatible with the OrbisData API and can be generated from an {@link ITable instance}.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019-2020)
 */
public class DataFrame implements smile.data.DataFrame, ITable<BaseVector, Tuple> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFrame.class);

    /**
     * Wrapped {@link smile.data.DataFrame}
     */
    private smile.data.DataFrame internalDataFrame;

    /**
     * Row index.
     */
    private int row = -1;

    /**
     * Return the internal {@link DataFrame}.
     *
     * @return The internal {@link DataFrame}.
     */
    @NotNull
    private smile.data.DataFrame getInternalDataFrame() {
        return internalDataFrame;
    }

    /**
     * Set the internal {@link DataFrame}.
     *
     * @param dataFrame The internal {@link DataFrame}.
     */
    protected void setInternalDataFrame(@NotNull smile.data.DataFrame dataFrame) {
        internalDataFrame = dataFrame;
    }

    /**
     * Apply the given {@link Formula} to the internal {@link smile.data.DataFrame}.
     *
     * @param formula {@link Formula} to apply.
     */
    public DataFrame apply(@Nullable Formula formula) {
        if(formula != null) {
            return DataFrame.of(formula.apply(getInternalDataFrame()));
        }
        else {
            return this;
        }
    }

    @Override
    @NotNull
    public StructType schema() {
        return getInternalDataFrame().schema();
    }

    @Override
    public int ncols() {
        return getInternalDataFrame().ncols();
    }

    @Override
    public int columnIndex(String s) {
        return getInternalDataFrame().columnIndex(s);
    }

    @Override
    @NotNull
    public BaseVector<?, ?, ?> column(int i) {
        return getInternalDataFrame().column(i);
    }

    @Override
    @NotNull
    public <T> Vector<T> vector(int i) {
        return getInternalDataFrame().vector(i);
    }

    @Override
    @NotNull
    public BooleanVector booleanVector(int i) {
        return getInternalDataFrame().booleanVector(i);
    }

    @Override
    @NotNull
    public CharVector charVector(int i) {
        return getInternalDataFrame().charVector(i);
    }

    @Override
    @NotNull
    public ByteVector byteVector(int i) {
        return getInternalDataFrame().byteVector(i);
    }

    @Override
    @NotNull
    public ShortVector shortVector(int i) {
        return getInternalDataFrame().shortVector(i);
    }

    @Override
    @NotNull
    public IntVector intVector(int i) {
        return getInternalDataFrame().intVector(i);
    }

    @Override
    @NotNull
    public LongVector longVector(int i) {
        return getInternalDataFrame().longVector(i);
    }

    @Override
    @NotNull
    public FloatVector floatVector(int i) {
        return getInternalDataFrame().floatVector(i);
    }

    @Override
    @NotNull
    public DoubleVector doubleVector(int i) {
        return getInternalDataFrame().doubleVector(i);
    }

    @Override
    @NotNull
    public StringVector stringVector(int i) {
        return getInternalDataFrame().stringVector(i);
    }

    @Override
    @NotNull
    public DataFrame select(int... ints) {
        return of(getInternalDataFrame().select(ints));
    }

    @Override
    @NotNull
    public DataFrame select(String... cols) {
        return of(getInternalDataFrame().select(cols));
    }

    @Override
    @NotNull
    public DataFrame drop(int... ints) {
        return of(getInternalDataFrame().drop(ints));
    }

    @Override
    @NotNull
    public DataFrame merge(smile.data.DataFrame... dataFrames) {
        return of(getInternalDataFrame().merge(dataFrames));
    }

    @Override
    @NotNull
    public DataFrame merge(BaseVector... baseVectors) {
        return of(getInternalDataFrame().merge(baseVectors));
    }

    @Override
    @NotNull
    public DataFrame union(smile.data.DataFrame... dataFrames) {
        return of(getInternalDataFrame().union(dataFrames));
    }

    @Override
    @NotNull
    public double[][] toArray() {
        return getInternalDataFrame().toArray();
    }

    @Override
    @NotNull
    public DenseMatrix toMatrix() {
        return getInternalDataFrame().toMatrix();
    }

    @Override
    @NotNull
    public Iterator<BaseVector> iterator() {
        return getInternalDataFrame().iterator();
    }

    @Override
    public int size() {
        return getInternalDataFrame().size();
    }

    @Override
    public boolean isEmpty() {
        return getInternalDataFrame().isEmpty();
    }

    @Override
    @NotNull
    public Summary getSummary() {
        return summary();
    }

    @Override
    public boolean reload() {
        return false;
    }

    @Override
    @NotNull
    public Summary summary() {
        return new Summary(getInternalDataFrame().summary());
    }

    @Override
    public Tuple get(int i) {
        return getInternalDataFrame().get(i);
    }

    @Override
    public String getString(int column) {
        return getInternalDataFrame().getString(getRow(), column);
    }

    @Override
    public boolean getBoolean(int column) {
        return getInternalDataFrame().getBoolean(getRow(), column);
    }

    @Override
    public byte getByte(int column) {
        return getInternalDataFrame().getByte(getRow(), column);
    }

    @Override
    public short getShort(int column) {
        return getInternalDataFrame().getShort(getRow(), column);
    }

    @Override
    public int getInt(int column) {
        return getInternalDataFrame().getInt(getRow(), column);
    }

    @Override
    public long getLong(int column) {
        return getInternalDataFrame().getLong(getRow(), column);
    }

    @Override
    public float getFloat(int column) {
        return getInternalDataFrame().getFloat(getRow(), column);
    }

    @Override
    public double getDouble(int column) {
        return getInternalDataFrame().getDouble(getRow(), column);
    }

    @Override
    public byte[] getBytes(int column) {
        String str = getString(column);
        return str != null ? str.getBytes() : new byte[]{};
    }

    @Override
    public Date getDate(int column) {
        LocalDate date = getInternalDataFrame().getDate(getRow(), column);
        if(date == null){
            return null;
        }
        return Date.valueOf(date);
    }

    @Override
    public Time getTime(int column) {
        LocalTime time = getInternalDataFrame().getTime(getRow(), column);
        if(time == null){
            return null;
        }
        return Time.valueOf(time);
    }

    @Override
    public Timestamp getTimestamp(int column) {
        Object obj = getObject(column);
        if(obj instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime)obj);
        }
        return null;
    }

    @Override
    public Object getObject(int column) {
        return getInternalDataFrame().get(getRow(), column);
    }

    @Override
    public BigDecimal getBigDecimal(int column) {
        return getInternalDataFrame().getDecimal(getRow(), getColumns().get(column));
    }

    @Override
    public String getString(@NotNull String column) {
        return getInternalDataFrame().getString(getRow(), column);
    }

    @Override
    public boolean getBoolean(@NotNull String column) {
        return getInternalDataFrame().getBoolean(getRow(), column);
    }

    @Override
    public byte getByte(@NotNull String column) {
        return getInternalDataFrame().getByte(getRow(), column);
    }

    @Override
    public short getShort(@NotNull String column) {
        return getInternalDataFrame().getShort(getRow(), column);
    }

    @Override
    public int getInt(@NotNull String column) {
        return getInternalDataFrame().getInt(getRow(), column);
    }

    @Override
    public long getLong(@NotNull String column) {
        return getInternalDataFrame().getLong(getRow(), column);
    }

    @Override
    public float getFloat(@NotNull String column) {
        return getInternalDataFrame().getFloat(getRow(), column);
    }

    @Override
    public double getDouble(@NotNull String column) {
        return getInternalDataFrame().getDouble(getRow(), column);
    }

    @Override
    public byte[] getBytes(@NotNull String column) {
        String str = getString(column);
        return str != null ? str.getBytes() : new byte[]{};
    }

    @Override
    public Date getDate(@NotNull String column) {
        LocalDate date = getInternalDataFrame().getDate(getRow(), column);
        if(date == null){
            return null;
        }
        return Date.valueOf(date);
    }

    @Override
    public Time getTime(@NotNull String column) {
        LocalTime time = getInternalDataFrame().getTime(getRow(), column);
        if(time == null){
            return null;
        }
        return Time.valueOf(time);
    }

    @Override
    public Timestamp getTimestamp(@NotNull String column) {
        Object obj = getObject(column);
        if(obj instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime)obj);
        }
        return null;
    }

    @Override
    public Object getObject(@NotNull String column) {
        return getInternalDataFrame().get(getRow(), column);
    }

    @Override
    public BigDecimal getBigDecimal(@NotNull String column) {
        return getInternalDataFrame().getDecimal(getRow(), column);
    }

    @Override
    public Object getObject(int column, @NotNull Class clazz){
        if (clazz == BigDecimal.class) {
            return this.getBigDecimal(column);
        } else if (clazz == BigInteger.class) {
            BigDecimal bd = this.getBigDecimal(column);
            if(bd != null) {
                return bd.toBigInteger();
            }
            return null;
        } else if (clazz == String.class) {
            return this.getString(column);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return this.getObject(column);
        } else if (clazz == Byte.class || clazz == byte.class) {
            return this.getByte(column);
        } else if (clazz == Short.class || clazz == short.class) {
            return this.getShort(column);
        } else if (clazz == Integer.class || clazz == int.class) {
            return this.getInt(column);
        } else if (clazz == Long.class || clazz == long.class) {
            return this.getLong(column);
        } else if (clazz == Float.class || clazz == float.class) {
            return this.getFloat(column);
        } else if (clazz == Double.class || clazz == double.class) {
            return this.getDouble(column);
        } else if (clazz == Date.class) {
            return this.getDate(column);
        } else if (clazz == Time.class) {
            return this.getTime(column);
        } else if (clazz == Timestamp.class) {
            return this.getTimestamp(column);
        } else if (clazz == UUID.class) {
            throw new UnsupportedOperationException();
            //return this.getObject(column);
        } else if (clazz == byte[].class) {
            return this.getBytes(column);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object getObject(@NotNull String column, @NotNull Class clazz){
        if (clazz == BigDecimal.class) {
            return this.getBigDecimal(column);
        } else if (clazz == BigInteger.class) {
            BigDecimal bd = this.getBigDecimal(column);
            if(bd != null) {
                return bd.toBigInteger();
            }
            return null;
        } else if (clazz == String.class) {
            return this.getString(column);
        } else if (clazz == Boolean.class || clazz == boolean.class) {
            return this.getBoolean(column);
        } else if (clazz == Byte.class || clazz == byte.class) {
            return this.getByte(column);
        } else if (clazz == Short.class || clazz == short.class) {
            return this.getShort(column);
        } else if (clazz == Integer.class || clazz == int.class) {
            return this.getInt(column);
        } else if (clazz == Long.class || clazz == long.class) {
            return this.getLong(column);
        } else if (clazz == Float.class || clazz == float.class) {
            return this.getFloat(column);
        } else if (clazz == Double.class || clazz == double.class) {
            return this.getDouble(column);
        } else if (clazz == Date.class) {
            return this.getDate(column);
        } else if (clazz == Time.class) {
            return this.getTime(column);
        } else if (clazz == Timestamp.class) {
            return this.getTimestamp(column);
        } else if (clazz == UUID.class) {
            throw new UnsupportedOperationException();
            //return this.getObject(column);
        } else if (clazz == byte[].class) {
            return this.getBytes(column);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Stream<Tuple> stream() {
        return getInternalDataFrame().stream();
    }

    @Override
    @NotNull
    public List<String> getColumns() {
        return Arrays.asList(names());
    }

    @Override
    @NotNull
    public Map<String, String> getColumnsTypes() {
        DataType[] dataTypes = types();
        String[] names = names();
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < dataTypes.length; i++) {
            map.put(names[i], dataTypes[i].name());
        }
        return map;
    }

    @Override
    public String getColumnType(@NotNull String columnName) {
        String[] names = names();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (name.equalsIgnoreCase(columnName)) {
                return types()[i].unboxed().name();
            }
        }
        return null;
    }

    @Override
    public int getColumnCount() {
        return ncols();
    }

    @Override
    public boolean hasColumn(@NotNull String columnName, @NotNull Class<?> clazz) {
        String[] names = names();
        DataType[] dataTypes = types();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            if (name.equalsIgnoreCase(columnName) ) {
                return dataTypes[i].unboxed().name().equalsIgnoreCase(clazz.getCanonicalName());
            }
        }
        return false;
    }

    @Override
    public int getRowCount() {
        return nrows();
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public boolean next(){
        if(row < this.getRowCount() - 1){
            row++;
            return true;
        }
        return false;
    }

    @Override
    public boolean previous() {
        if(row > 0){
            row--;
            return true;
        }
        return false;
    }

    @Override
    public boolean first() {
        row = 0;
        return true;
    }

    @Override
    public boolean last() {
        row = getRowCount()-1;
        return true;
    }

    @Override
    public boolean isFirst() {
        return row == 0;
    }

    @Override
    public boolean isLast() {
        return row == getRowCount()-1;
    }

    @Override
    public Collection<String> getUniqueValues(@NotNull String column) {
        int colIndex = columnIndex(column);
        List<String> values = new ArrayList<>();

        for (int i = 0; i < nrows(); i++) {
            values.add(get(i, colIndex).toString());
        }
        return values.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public String save(@NotNull String filePath, String encoding) {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    LOGGER.error("Unable to create the file '" + f.getAbsolutePath() + "'.");
                    return null;
                }
            } catch (IOException e) {
                LOGGER.error("Unable to create the file '" + f.getAbsolutePath() + "'.", e);
                return null;
            }
        }
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            LOGGER.error("Unable to create the FileWriter.", e);
            return null;
        }
        try {
            writer.write(String.join(",", names()) + "\n");
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write in the FileWriter.", e);
            return null;
        }
        for (int i = 0; i < nrows(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < ncols(); j++) {
                Object obj = get(i, j);
                row.add(obj == null ? "null" : obj.toString());
            }
            try {
                writer.write(String.join(",", row) + "\n");
                writer.flush();
            } catch (IOException e) {
                LOGGER.error("Unable to write in the FileWriter.", e);
                return null;
            }
        }
        return filePath;
    }

    @Override
    public String save(IJdbcDataSource dataSource, int batchSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String save(IJdbcDataSource dataSource, boolean deleteTable, int batchSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String save(IJdbcDataSource dataSource, String outputTableName, boolean deleteTable) {
        return  save(dataSource,  outputTableName,  deleteTable, 1000);
    }

    @Override
    public String save(@NotNull  IJdbcDataSource dataSource, @NotNull  String outputTableName, boolean deleteTable, int batchSize) {
        if (isEmpty()) {
            return null;
        }
        String tableName = TableLocation.parse(outputTableName, dataSource.getDataBaseType() == DataBaseType.H2GIS).toString(dataSource.getDataBaseType() == DataBaseType.H2GIS);
        try {
            PreparedStatement preparedStatement = null;
            Connection outputconnection = dataSource.getConnection();
            try {
                Statement outputconnectionStatement = outputconnection.createStatement();
                if (deleteTable) {
                    outputconnectionStatement.execute("DROP TABLE IF EXISTS " + outputTableName);
                }
                StringBuilder create_table_ = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
                StringBuilder insertTable = new StringBuilder("INSERT INTO ");
                insertTable.append(outputTableName).append(" VALUES(");
                int k = 0;
                DataType[] dataTypes = types();
                String[] names = names();
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < dataTypes.length; i++) {
                    String columnName = names[i];
                    DataType dataType = dataTypes[i];
                    if (k == 0) {
                        insertTable.append("?");
                        create_table_.append(columnName).append(" ").append(getSQLType(dataType));
                    } else {
                        insertTable.append(",?");
                        create_table_.append(",").append(columnName).append(" ").append(getSQLType(dataType));
                    }
                    k++;
                }
                create_table_.append(")");
                insertTable.append(")");
                outputconnection.setAutoCommit(false);
                outputconnectionStatement.execute(create_table_.toString());
                preparedStatement = outputconnection.prepareStatement(insertTable.toString());
                //Check the first row in order to limit the batch size if the query doesn't work
                this.next();
                for (int i = 0; i < getColumnCount(); i++) {
                    preparedStatement.setObject(i +1,getObject(i) );
                }
                preparedStatement.execute();
                outputconnection.commit();
                long batch_size = 0;
                while (this.next()) {
                    for (int i = 0; i < getColumnCount(); i++) {
                        preparedStatement.setObject(i + 1, getObject(i));
                    }
                    preparedStatement.addBatch();
                    batch_size++;
                    if (batch_size >= batchSize) {
                        preparedStatement.executeBatch();
                        preparedStatement.clearBatch();
                        batchSize = 0;
                    }
                }
                if (batch_size > 0) {
                    preparedStatement.executeBatch();
                }
            } catch (SQLException e) {
                LOGGER.error("Cannot save the dataframe.\n", e);
                return null;
            } finally {
                outputconnection.setAutoCommit(true);
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Cannot save the dataframe.\n", e);
            return null;
        }
        return tableName;
    }

    @Override
    @NotNull
    public List<Object> getFirstRow() {
        List<Object> firstRow = new ArrayList<>();
        for (int i = 0; i < ncols(); i++) {
            firstRow.add(get(0, i));
        }
        return firstRow;
    }

    @Override
    @NotNull
    public DataFrame columns(@NotNull String... columns) {
        List<String> col = new ArrayList<>(getColumns());
        col.removeAll(Arrays.asList(columns));
        return of(drop(col.toArray(new String[0])));
    }

    @Override
    @Nullable
    public DataFrame filter(String filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBuilderResult filter(GString filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IBuilderResult filter(String filter, List<Object> params) {
        throw new UnsupportedOperationException();
    }

    @Override
    @NotNull
    public Map<String, Object> firstRow() {
        Map<String, Object> map = new HashMap<>();
        if(first()){
            for(String column : getColumns()){
                map.put(column, getObject(column));
            }
        }
        else{
            LOGGER.error("Unable to go to the first row.");
        }
        return map;
    }

    @Override
    public boolean isSpatial() {
        return false;
    }

    @Override
    public String getLocation() {
        return "smile.data.DataFrame";
    }

    @Override
    @NotNull
    public String getName() {
        return "DataFrame";
    }

    @Override
    @NotNull
    public Object getMetaData() {
        return summary();
    }

    @Override
    public Object asType(@NotNull Class<?> clazz) {
        if (String.class.isAssignableFrom(clazz)) {
            return toString();
        } else if (Matrix.class.isAssignableFrom(clazz)) {
            return toString();
        } else if (DataFrame.class.isAssignableFrom(clazz)) {
            return this;
        } else if (Ascii.class.equals(clazz) || Html.class.equals(clazz)) {
            ICustomPrinter printer;
            if (Ascii.class.equals(clazz)) {
                printer = new Ascii();
            } else {
                printer = new Html();
            }
            int maxWidth = this.getName().length();
            for (String name : this.names()) {
                maxWidth = Math.max(maxWidth, name.length());
            }
            printer.startTable(maxWidth, this.getColumnCount());
            printer.appendTableTitle(this.getName());
            printer.appendTableLineSeparator();
            String[] titles = this.names();
            for (String title : titles) {
                printer.appendTableHeaderValue(title, CENTER);
            }
            printer.appendTableLineSeparator();
            for (int i = 0; i < this.size(); i++) {
                for (int j = 0; j < this.getColumnCount(); j++) {
                    Object obj = this.get(i, j);
                    printer.appendTableValue(obj == null ? "null" : obj.toString(), RIGHT);
                }
                printer.appendTableLineSeparator();
            }
            printer.endTable();
            return printer;
        }
        return null;
    }

    @Nullable
    @Override
    public ITable<?, ?> getTable() {
        return this;
    }

    @Nullable
    @Override
    public ISpatialTable<?, ?> getSpatialTable() {
        throw new UnsupportedOperationException();
    }

    /**
     * Convert a smile {@link smile.data.DataFrame} into an OrbisData {@link DataFrame}.
     *
     * @param dataFrame Smile {@link smile.data.DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    @NotNull
    public static DataFrame of(@NotNull smile.data.DataFrame dataFrame) {
        DataFrame df = new DataFrame();
        df.internalDataFrame = dataFrame;
        return df;
    }

    /**
     * Convert a {@link ResultSet} into an OrbisData {@link DataFrame}.
     *
     * @param rs {@link ResultSet}.
     * @return OrbisData {@link DataFrame}.
     * @throws SQLException Exception thrown in case or error while manipulation SQL base {@link ResultSet}.
     */
    @Nullable
    public static DataFrame of(@NotNull ResultSet rs) throws SQLException {
        if (rs instanceof IJdbcTable) {
            IJdbcTable jdbcTable = (IJdbcTable) rs;
            StructType schema = getStructure(jdbcTable);
            if(schema==null){
                return null;
            }
            ArrayList<Tuple> rows = new ArrayList<>();
            while (jdbcTable.next()) {
                rows.add(toTuple(jdbcTable, schema));
            }
            return of(smile.data.DataFrame.of(rows));
        } else {
            return of(smile.data.DataFrame.of(rs));
        }
    }

    @Nullable
    private static StructType getStructure(@NotNull IJdbcTable<?> table) {
        ResultSetMetaData metadata = table.getMetaData();
        try {
            int columnCount = metadata.getColumnCount();
            StructField[] fields = new StructField[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String type = metadata.getColumnTypeName(i);
                if (type.equalsIgnoreCase("geometry")) {
                    type="VARCHAR";
                }
                DataType dataType = DataType.of(JDBCType.valueOf(type), metadata.isNullable(i) != 0,(table).getDbType().toString());
                fields[i-1] = new StructField(metadata.getColumnName(i), dataType);
            }
            return new StructType(fields);
        } catch (SQLException e) {
            LOGGER.error("Unable to create the structure of the dataframe", e);
            return null;
        }
    }

    /**
     * Create a {@link DataFrame} from a file.
     *
     * @param path Path to the file to load into the {@link DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    @Nullable
    public static DataFrame of(@NotNull String path) throws IOException {
        return of(new File(path));
    }

    /**
     * Create a {@link DataFrame} from a file.
     *
     * @param file {@link File} to load into the {@link DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    @Nullable
    public static DataFrame of(@NotNull File file) throws IOException {
        if (!file.exists()) {
            LOGGER.error("The file '" + file.getAbsolutePath() + "' does not exists.");
            return null;
        }
        int dotIndex = file.getName().lastIndexOf(".");
        if (!file.getName().substring(dotIndex + 1).equalsIgnoreCase("csv")) {
            LOGGER.error("Only CSV file are supported.");
            return null;
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        List<Tuple> tuples = new ArrayList<>();
        String line = reader.readLine();
        String[] split = line.split(",");

        List<StructField> fields = new ArrayList<>();
        for (String s : split) {
            fields.add(new StructField(s, DataType.of(String.class)));
        }
        StructType schema = new StructType(fields);
        while ((line = reader.readLine()) != null) {
            String[] sp = line.split(",");
            tuples.add(Tuple.of(sp, schema));
        }
        return of(smile.data.DataFrame.of(tuples, schema));
    }

    /**
     * Creates a {@link Tuple} from a {@link ResultSet} which can contains spatial daa like {@link Geometry}.
     *
     * @param rs     {@link ResultSet} to read.
     * @param schema Smile {@link smile.data.DataFrame} schema.
     * @return {@link Tuple} containing the data from the {@link ResultSet}.
     * @throws SQLException Exception get on fetching data from the given {@link ResultSet}.
     */
    @NotNull
    private static Tuple toTuple(@NotNull ResultSet rs, @NotNull StructType schema) throws SQLException {
        Object[] row = new Object[rs.getMetaData().getColumnCount()];
        for (int i = 0; i < row.length; ++i) {
            row[i] = rs.getObject(i + 1);
            if (row[i] instanceof Date) {
                row[i] = ((Date) row[i]).toLocalDate();
            } else if (row[i] instanceof Time) {
                row[i] = ((Time) row[i]).toLocalTime();
            } else if (row[i] instanceof Timestamp) {
                row[i] = ((Timestamp) row[i]).toLocalDateTime();
            } else if (row[i] instanceof Geometry) {
                row[i] = row[i].toString();
            }
        }
        return Tuple.of(row, schema);
    }

    @NotNull
    @Override
    public String toString() {
        return getInternalDataFrame().toString();
    }

    @Override
    public List<Object> getParams() {
        throw new UnsupportedOperationException();
    }

    /* Override methods from SMILE API */

    @Override
    @NotNull
    public DataFrame structure() {
        return of(getInternalDataFrame().structure());
    }

    @Override
    @NotNull
    public DataFrame omitNullRows() {
        return of(getInternalDataFrame().omitNullRows());
    }

    @Override
    @NotNull
    public DataFrame of(int... index) {
        return of(getInternalDataFrame().of(index));
    }

    @Override
    @NotNull
    public DataFrame of(boolean... index) {
        return of(getInternalDataFrame().of(index));
    }

    @Override
    @NotNull
    public DataFrame slice(int from, int to) {
        return of(getInternalDataFrame().slice(from, to));
    }

    @Override
    @NotNull
    public DataFrame drop(String... cols) {
        return of(getInternalDataFrame().drop(cols));
    }

    @Override
    @NotNull
    public DataFrame factorize(String... cols) {
        return of(getInternalDataFrame().factorize(cols));
    }

    @NotNull
    public static DataFrame of(BaseVector<?, ?, ?>... vectors) {
        return of(smile.data.DataFrame.of(vectors));
    }

    @NotNull
    public static DataFrame of(double[][] data, @NotNull String... names) {
        return of(smile.data.DataFrame.of(data, names));
    }

    @NotNull
    public static DataFrame of(int[][] data, @NotNull String... names) {
        return of(smile.data.DataFrame.of(data, names));
    }

    @NotNull
    public static <T> DataFrame of(@NotNull List<T> data, @NotNull Class<T> clazz) {
        return of(smile.data.DataFrame.of(data, clazz));
    }

    @NotNull
    public static DataFrame of(@NotNull Stream<Tuple> data) {
        return of(smile.data.DataFrame.of(data));
    }

    @NotNull
    public static DataFrame of(@NotNull Stream<Tuple> data, @NotNull StructType schema) {
        return of(smile.data.DataFrame.of(data, schema));
    }

    @NotNull
    public static DataFrame of(@NotNull List<Tuple> data) {
        return of(smile.data.DataFrame.of(data));
    }

    @NotNull
    public static DataFrame of(@NotNull List<Tuple> data, @NotNull StructType schema) {
        return of(smile.data.DataFrame.of(data, schema));
    }

    @NotNull
    public static <T> DataFrame of(@NotNull Collection<Map<String, T>> data, @NotNull StructType schema) {
        return of(smile.data.DataFrame.of(data, schema));
    }

    /**
     * Returns the SQL type.
     * @param dataType from the dataframe
     */
    public String getSQLType(DataType dataType) {
        if (DataTypes.BooleanObjectType.equals(dataType) || DataTypes.BooleanType.equals(dataType)) {
            return "BOOLEAN";
        } else if (DataTypes.BooleanObjectType.equals(dataType) || DataTypes.BooleanType.equals(dataType)) {
            return "TINYINT";
        } else if (DataTypes.ShortObjectType.equals(dataType) || DataTypes.ShortType.equals(dataType)) {
            return "SMALLINT";
        } else if (DataTypes.IntegerType.equals(dataType) || DataTypes.IntegerObjectType.equals(dataType)) {
            return "INTEGER";
        } else if (DataTypes.LongObjectType.equals(dataType) || DataTypes.LongType.equals(dataType)) {
            return "BIGINT";
        } else if (DataTypes.DoubleType.equals(dataType) || DataTypes.DoubleObjectType.equals(dataType)) {
            return "DOUBLE PRECISION";
        } else if (DataTypes.DecimalType.equals(dataType)) {
            return "DECIMAL";
        } else if (DataTypes.FloatObjectType.equals(dataType) || DataTypes.FloatObjectType.equals(dataType)) {
            return "FLOAT";
        } else if (DataTypes.StringType.equals(dataType)) {
            return "VARCHAR";
        } else if (DataTypes.DateType.equals(dataType)) {
            return "DATE";
        } else if (DataTypes.TimeType.equals(dataType)) {
            return "TIME";
        } else if (DataTypes.DateTimeType.equals(dataType)) {
            return "TIMESTAMP";
        } else if (DataTypes.ByteObjectType.equals(dataType)) {
            return "BINARY";
        }
        throw new UnsupportedOperationException(String.format("Unsupported dataframe type: %s", dataType));
    }
}
