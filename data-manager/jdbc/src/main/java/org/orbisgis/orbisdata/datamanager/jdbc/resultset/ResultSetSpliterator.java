/*
 * Bundle DataManager is part of the OrbisGIS platform
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
 * DataManager is distributed under LGPL 3 license.
 *
 * Copyright (C) 2018 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * DataManager is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * DataManager is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * DataManager. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.orbisdata.datamanager.jdbc.resultset;

import org.orbisgis.commons.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * {@link Spliterator} implementation dedicated to {@link ResultSet}.
 *
 * @author Erwan Bocher (CNRS)
 * @author Sylvain PALOMINOS (UBS Lab-STICC 2020)
 */
public class ResultSetSpliterator implements Spliterator<StreamResultSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResultSetSpliterator.class);

    private final long size;
    private StreamResultSet rs;

    public ResultSetSpliterator(long size, ResultSet rs){
        this.size = size;
        this.rs = new StreamResultSet(rs);
        this.rs.first();
    }

    @Override
    public boolean tryAdvance(Consumer<? super StreamResultSet> consumer) {
        if(size == 0){
            return false;
        }
        consumer.accept(rs);
        return rs.next();
    }

    @Override
    @Nullable
    public Spliterator<StreamResultSet> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return size;
    }

    @Override
    public int characteristics() {
        return ORDERED;
    }
}
