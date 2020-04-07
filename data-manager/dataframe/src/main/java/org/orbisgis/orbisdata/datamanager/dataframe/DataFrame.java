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

import org.locationtech.jts.geom.Geometry;
import org.orbisgis.orbisdata.datamanager.api.dataset.IJdbcTable;
import org.orbisgis.orbisdata.datamanager.api.dataset.ITable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import smile.data.Tuple;
import smile.data.type.DataType;
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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Wrap the {@link smile.data.DataFrame} into a new class implementing the {@link ITable} interface. This new
 * DataFrame is compatible with the OrbisData API and can be generated from an {@link ITable instance}.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
public class DataFrame implements smile.data.DataFrame, ITable<BaseVector> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFrame.class);

    /**
     * Wrapped {@link smile.data.DataFrame}
     */
    private smile.data.DataFrame internalDataFrame;

    /**
     * Return the internal {@link DataFrame}.
     *
     * @return The internal {@link DataFrame}.
     */
    private smile.data.DataFrame getInternalDataFrame() {
        return internalDataFrame;
    }

    /**
     * Set the internal {@link DataFrame}.
     *
     * @param dataFrame The internal {@link DataFrame}.
     */
    void setInternalDataFrame(smile.data.DataFrame dataFrame) {
        internalDataFrame = dataFrame;
    }

    @Override
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
    public BaseVector column(int i) {
        return getInternalDataFrame().column(i);
    }

    @Override
    public <T> Vector<T> vector(int i) {
        return getInternalDataFrame().vector(i);
    }

    @Override
    public BooleanVector booleanVector(int i) {
        return getInternalDataFrame().booleanVector(i);
    }

    @Override
    public CharVector charVector(int i) {
        return getInternalDataFrame().charVector(i);
    }

    @Override
    public ByteVector byteVector(int i) {
        return getInternalDataFrame().byteVector(i);
    }

    @Override
    public ShortVector shortVector(int i) {
        return getInternalDataFrame().shortVector(i);
    }

    @Override
    public IntVector intVector(int i) {
        return getInternalDataFrame().intVector(i);
    }

    @Override
    public LongVector longVector(int i) {
        return getInternalDataFrame().longVector(i);
    }

    @Override
    public FloatVector floatVector(int i) {
        return getInternalDataFrame().floatVector(i);
    }

    @Override
    public DoubleVector doubleVector(int i) {
        return getInternalDataFrame().doubleVector(i);
    }

    @Override
    public StringVector stringVector(int i) {
        return getInternalDataFrame().stringVector(i);
    }

    @Override
    public DataFrame select(int... ints) {
        return of(getInternalDataFrame().select(ints));
    }

    @Override
    public DataFrame select(String... cols) {
        return of(getInternalDataFrame().select(cols));
    }

    @Override
    public DataFrame drop(int... ints) {
        return of(getInternalDataFrame().drop(ints));
    }

    @Override
    public DataFrame merge(smile.data.DataFrame... dataFrames) {
        return of(getInternalDataFrame().merge(dataFrames));
    }

    @Override
    public DataFrame merge(BaseVector... baseVectors) {
        return of(getInternalDataFrame().merge(baseVectors));
    }

    @Override
    public DataFrame union(smile.data.DataFrame... dataFrames) {
        return of(getInternalDataFrame().union(dataFrames));
    }

    @Override
    public double[][] toArray() {
        return getInternalDataFrame().toArray();
    }

    @Override
    public DenseMatrix toMatrix() {
        return getInternalDataFrame().toMatrix();
    }

    @Override
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
    public Summary getSummary() {
        return summary();
    }

    @Override
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
        throw new UnsupportedOperationException("The methods 'getBytes(int)' is not supported.");
    }

    @Override
    public Date getDate(int column) {
        return Date.valueOf(getInternalDataFrame().getDate(getRow(), column));
    }

    @Override
    public Time getTime(int column) {
        return Time.valueOf(getInternalDataFrame().getTime(getRow(), column));
    }

    @Override
    public Timestamp getTimestamp(int column) {
        throw new UnsupportedOperationException("The methods 'getTimestamp(int)' is not supported.");
    }

    @Override
    public Object getObject(int column) {
        return getInternalDataFrame().get(getRow(), column);
    }

    @Override
    public BigDecimal getBigDecimal(int column) {
        throw new UnsupportedOperationException("The methods 'getBigDecimal(int)' is not supported.");
    }

    @Override
    public String getString(String column) {
        return getInternalDataFrame().getString(getRow(), column);
    }

    @Override
    public boolean getBoolean(String column) {
        return getInternalDataFrame().getBoolean(getRow(), column);
    }

    @Override
    public byte getByte(String column) {
        return getInternalDataFrame().getByte(getRow(), column);
    }

    @Override
    public short getShort(String column) {
        return getInternalDataFrame().getShort(getRow(), column);
    }

    @Override
    public int getInt(String column) {
        return getInternalDataFrame().getInt(getRow(), column);
    }

    @Override
    public long getLong(String column) {
        return getInternalDataFrame().getLong(getRow(), column);
    }

    @Override
    public float getFloat(String column) {
        return getInternalDataFrame().getFloat(getRow(), column);
    }

    @Override
    public double getDouble(String column) {
        return getInternalDataFrame().getDouble(getRow(), column);
    }

    @Override
    public byte[] getBytes(String column) {
        throw new UnsupportedOperationException("The methods 'getBytes(String)' is not supported.");
    }

    @Override
    public Date getDate(String column) {
        return Date.valueOf(getInternalDataFrame().getDate(getRow(), column));
    }

    @Override
    public Time getTime(String column) {
        return Time.valueOf(getInternalDataFrame().getTime(getRow(), column));
    }

    @Override
    public Timestamp getTimestamp(String column) {
        throw new UnsupportedOperationException("The methods 'getTimestamp(String)' is not supported.");
    }

    @Override
    public Object getObject(String column) {
        return getInternalDataFrame().getBoolean(getRow(), column);
    }

    @Override
    public BigDecimal getBigDecimal(String column) {
        throw new UnsupportedOperationException("The methods 'getBigDecimal(String)' is not supported.");
    }

    @Override
    public Stream<Tuple> stream() {
        return getInternalDataFrame().stream();
    }

    @Override
    public Collection<String> getColumns() {
        return Arrays.asList(names());
    }

    @Override
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
    public String getColumnType(String columnName) {
        int index = -1;
        for (String name : names()) {
            index++;
            if (name.equalsIgnoreCase(columnName))
                break;
        }
        return types()[index].name();
    }

    @Override
    public boolean hasColumn(String columnName, Class clazz) {
        return getColumnsTypes().get(columnName).equalsIgnoreCase(clazz.getCanonicalName());
    }

    @Override
    public int getRowCount() {
        return nrows();
    }

    @Override
    public int getRow() {
        return -1;
    }

    @Override
    public Collection<String> getUniqueValues(String column) {
        int colIndex = columnIndex(column);
        List<String> values = new ArrayList<>();

        for (int i = 0; i < nrows(); i++) {
            values.add(get(i, colIndex).toString());
        }
        return values.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public boolean save(String filePath, String encoding) {
        File f = new File(filePath);
        if (!f.exists()) {
            try {
                if (!f.createNewFile()) {
                    LOGGER.error("Unable to create the file '" + f.getAbsolutePath() + "'.");
                    return false;
                }
            } catch (IOException e) {
                LOGGER.error("Unable to create the file '" + f.getAbsolutePath() + "'.", e);
                return false;
            }
        }
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(f));
        } catch (IOException e) {
            LOGGER.error("Unable to create the FileWriter.", e);
            return false;
        }
        try {
            writer.write(String.join(",", names()) + "\n");
            writer.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to write in the FileWriter.", e);
            return false;
        }
        for (int i = 0; i < nrows(); i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < ncols(); j++) {
                row.add(get(i, j).toString());
            }
            try {
                writer.write(String.join(",", row) + "\n");
                writer.flush();
            } catch (IOException e) {
                LOGGER.error("Unable to write in the FileWriter.", e);
                return false;
            }
        }
        return f.exists();
    }

    @Override
    public List<Object> getFirstRow() {
        List<Object> firstRow = new ArrayList<>();
        for (int i = 0; i < ncols(); i++) {
            firstRow.add(get(0, i));
        }
        return firstRow;
    }

    @Override
    public DataFrame columns(String... columns) {
        List<String> col = new ArrayList<>(getColumns());
        col.removeAll(Arrays.asList(columns));
        return of(drop(col.toArray(new String[0])));
    }

    @Override
    public DataFrame columns(List<String> columns) {
        List<String> col = new ArrayList<>(getColumns());
        col.removeAll(columns);
        return of(drop(col.toArray(new String[0])));
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
    public String getName() {
        return "DataFrame";
    }

    @Override
    public Object getMetaData() {
        return summary();
    }

    @Override
    public Object asType(Class clazz) {
        if (String.class.isAssignableFrom(clazz)) {
            return toString();
        } else if (Matrix.class.isAssignableFrom(clazz)) {
            return toString();
        } else if (DataFrame.class.isAssignableFrom(clazz)) {
            return this;
        }
        return null;
    }

    /**
     * Convert a smile {@link smile.data.DataFrame} into an OrbisData {@link DataFrame}.
     *
     * @param dataFrame Smile {@link smile.data.DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    public static DataFrame of(smile.data.DataFrame dataFrame) {
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
    public static DataFrame of(ResultSet rs) throws SQLException {
        if (rs instanceof IJdbcTable) {
            IJdbcTable jdbcTable = (IJdbcTable) rs;
            StructType schema = getStructure(jdbcTable);
            ArrayList<Tuple> rows = new ArrayList<>();
            while (jdbcTable.next()) {
                rows.add(toTuple(jdbcTable, schema));
            }
            return of(smile.data.DataFrame.of(rows));
        } else {
            return of(smile.data.DataFrame.of(rs));
        }
    }

    private static StructType getStructure(IJdbcTable table) {
        StructField[] fields = new StructField[table.getColumnCount()];
        int i = -1;
        for (Map.Entry<String, String> entry : table.getColumnsTypes().entrySet()) {
            i++;
            String type = entry.getValue();
            switch (entry.getValue().toUpperCase()) {
                case "GEOMETRY":
                case "GEOMETRYCOLLECTION":
                case "GEOMCOLLECTION":
                case "MULTIPOLYGON":
                case "POLYGON":
                case "MULTILINESTRING":
                case "LINESTRING":
                case "MULTIPOINT":
                case "POINT":
                    type = "VARCHAR";
                    break;
                default:
                    break;
            }
            DataType dataType = DataType.of(JDBCType.valueOf(type), false, (table).getDbType().toString());
            fields[i] = new StructField(entry.getKey(), dataType);
        }

        return new StructType(fields);
    }

    /**
     * Create a {@link DataFrame} from a file.
     *
     * @param path Path to the file to load into the {@link DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    public static DataFrame of(String path) throws IOException {
        return of(new File(path));
    }

    /**
     * Create a {@link DataFrame} from a file.
     *
     * @param file {@link File} to load into the {@link DataFrame}.
     * @return OrbisData {@link DataFrame}.
     */
    public static DataFrame of(File file) throws IOException {
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
     * @throws SQLException
     */
    private static Tuple toTuple(ResultSet rs, StructType schema) throws SQLException {
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

    @Override
    public String toString() {
        return getInternalDataFrame().toString();
    }


    /* Override methods from SMILE API */

    @Override
    public DataFrame structure() {
        return of(getInternalDataFrame().structure());
    }

    @Override
    public DataFrame omitNullRows() {
        return of(getInternalDataFrame().omitNullRows());
    }

    @Override
    public DataFrame of(int... index) {
        return of(getInternalDataFrame().of(index));
    }

    @Override
    public DataFrame of(boolean... index) {
        return of(getInternalDataFrame().of(index));
    }

    @Override
    public DataFrame slice(int from, int to) {
        return of(getInternalDataFrame().slice(from, to));
    }

    @Override
    public DataFrame drop(String... cols) {
        return of(getInternalDataFrame().drop(cols));
    }

    @Override
    public DataFrame factorize(String... cols) {
        return of(getInternalDataFrame().factorize(cols));
    }

    public static DataFrame of(BaseVector... vectors) {
        smile.data.DataFrame df = smile.data.DataFrame.of(vectors);
        return of(df);
    }

    public static DataFrame of(double[][] data, String... names) {
        smile.data.DataFrame df = smile.data.DataFrame.of(data, names);
        return of(df);
    }

    public static DataFrame of(int[][] data, String... names) {
        smile.data.DataFrame df = smile.data.DataFrame.of(data, names);
        return of(df);
    }

    public static <T> DataFrame of(List<T> data, Class<T> clazz) {
        smile.data.DataFrame df = smile.data.DataFrame.of(data, clazz);
        return of(df);
    }

    public static DataFrame of(Stream<Tuple> data) {

        smile.data.DataFrame df = smile.data.DataFrame.of(data);
        return of(df);
    }

    public static DataFrame of(Stream<Tuple> data, StructType schema) {

        smile.data.DataFrame df = smile.data.DataFrame.of(data, schema);
        return of(df);
    }

    public static DataFrame of(List<Tuple> data) {

        smile.data.DataFrame df = smile.data.DataFrame.of(data);
        return of(df);
    }

    public static DataFrame of(List<Tuple> data, StructType schema) {

        smile.data.DataFrame df = smile.data.DataFrame.of(data, schema);
        return of(df);
    }

    public static <T> DataFrame of(Collection<Map<String, T>> data, StructType schema) {
        smile.data.DataFrame df = smile.data.DataFrame.of(data, schema);
        return of(df);
    }

    @Override
    public Object getObject(int column, Class clazz){
        if (clazz == BigDecimal.class) {
            return this.getBigDecimal(column);
        } else if (clazz == BigInteger.class) {
            return this.getBigDecimal(column).toBigInteger();
        } else if (clazz == String.class) {
            return this.getString(column);
        } else if (clazz == Boolean.class) {
            return this.getBoolean(column);
        } else if (clazz == Byte.class) {
            return this.getByte(column);
        } else if (clazz == Short.class) {
            return this.getShort(column);
        } else if (clazz == Integer.class) {
            return this.getInt(column);
        } else if (clazz == Long.class) {
            return this.getLong(column);
        } else if (clazz == Float.class) {
            return this.getFloat(column);
        } else if (clazz == Double.class) {
            return this.getDouble(column);
        } else if (clazz == Date.class) {
            return this.getDate(column);
        } else if (clazz == Time.class) {
            return this.getTime(column);
        } else if (clazz == Timestamp.class) {
            return this.getTimestamp(column);
        } else if (clazz == UUID.class) {
            return this.getObject(column);
        } else if (clazz == byte[].class) {
            return this.getBytes(column);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Object getObject(String column, Class clazz){
        if (clazz == BigDecimal.class) {
            return this.getBigDecimal(column);
        } else if (clazz == BigInteger.class) {
            return this.getBigDecimal(column).toBigInteger();
        } else if (clazz == String.class) {
            return this.getString(column);
        } else if (clazz == Boolean.class) {
            return this.getBoolean(column);
        } else if (clazz == Byte.class) {
            return this.getByte(column);
        } else if (clazz == Short.class) {
            return this.getShort(column);
        } else if (clazz == Integer.class) {
            return this.getInt(column);
        } else if (clazz == Long.class) {
            return this.getLong(column);
        } else if (clazz == Float.class) {
            return this.getFloat(column);
        } else if (clazz == Double.class) {
            return this.getDouble(column);
        } else if (clazz == Date.class) {
            return this.getDate(column);
        } else if (clazz == Time.class) {
            return this.getTime(column);
        } else if (clazz == Timestamp.class) {
            return this.getTimestamp(column);
        } else if (clazz == UUID.class) {
            return this.getObject(column);
        } else if (clazz == byte[].class) {
            return this.getBytes(column);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
