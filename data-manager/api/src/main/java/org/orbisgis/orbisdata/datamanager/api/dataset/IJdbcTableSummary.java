package org.orbisgis.orbisdata.datamanager.api.dataset;

/**
 * Extension of the {@link ISummary} interface dedicated to the {@link IJdbcTable}.
 *
 * @author Sylvain Palominos (Lab-STICC UBS 2019)
 */
public interface IJdbcTableSummary extends ISummary {
    /**
     * Returns the {@link ITableLocation} of the summarized {@link IJdbcTable}.
     *
     * @return The {@link ITableLocation} of the summarized {@link IJdbcTable}.
     */
    ITableLocation getLocation();

    /**
     * Returns the row count of the summarized {@link IJdbcTable}.
     *
     * @return The row count of the summarized {@link IJdbcTable}.
     */
    int getRowCount();

    /**
     * Returns the column count of the summarized {@link IJdbcTable}.
     *
     * @return The column count of the summarized {@link IJdbcTable}.
     */
    int getColumnCount();
}