package org.orbisgis.orbisdata.datamanager.api.dataset;

/**
 * Raster data.
 *
 * @author Sylvain PALOMINOS (UBS LAB-STICC 2019)
 */
public interface IRaster {

    /**
     * Returns the {@link IRasterMetadata} of a raster.
     *
     * @return The {@link IRasterMetadata} of a raster.
     */
    IRasterMetadata getMetadata();
}
