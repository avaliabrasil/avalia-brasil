package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Question;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public interface QuestionDAO {

    public boolean bulkInsertQuestion(String groupId, List<Question> questions) throws SQLException;
}
