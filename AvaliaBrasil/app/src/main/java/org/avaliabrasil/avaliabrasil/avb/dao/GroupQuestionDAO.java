package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.GroupQuestion;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public interface GroupQuestionDAO {

    public boolean bulkQuestionGroup(String instrumentId,List<GroupQuestion> groupQuestion) throws SQLException;
    public List<GroupQuestion> findGroupByInstrumentId(String instrumentId) throws SQLException;
}
