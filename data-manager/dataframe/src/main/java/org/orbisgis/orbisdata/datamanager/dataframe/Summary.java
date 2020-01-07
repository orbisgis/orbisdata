package org.orbisgis.orbisdata.datamanager.dataframe;

import org.orbisgis.orbisdata.datamanager.api.dataset.ISummary;

/**
 * {@link ISummary} implementation for the {@link DataFrame} object.
 *
 * @author Sylvain PALOMINOS (Lab-STICC UBS 2019)
 */
public class Summary extends DataFrame implements ISummary {

    public Summary(smile.data.DataFrame dataFrame) {
        setInternalDataFrame(dataFrame);
    }
}
