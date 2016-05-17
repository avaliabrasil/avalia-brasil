package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Instrument;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 09/05/2016.
 */
public interface InstrumentDAO {

    public boolean bulkAddInstrument(List<Instrument> instruments) throws SQLException;
    public boolean updateInstrument(Instrument instrument) throws SQLException;
    public List<String> getInstrumentIdListByPlace(String placeId) throws SQLException;

}
