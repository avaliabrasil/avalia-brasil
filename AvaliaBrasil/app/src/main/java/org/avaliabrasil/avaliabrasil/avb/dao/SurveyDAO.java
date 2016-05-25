package org.avaliabrasil.avaliabrasil.avb.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.Survey;

import java.sql.SQLException;

/**
 * Created by Developer on 09/05/2016.
 */
public interface SurveyDAO {

    public boolean boundPlaceAndInstrument(String placeId, Survey survey) throws SQLException;

    public boolean bulkAddInstrument(Survey survey) throws SQLException;

    public boolean bulkAddQuestionGroup(Survey survey) throws SQLException;

    public boolean bulkAddQuestion(Survey survey) throws SQLException;

    public Survey findSurveyByPlaceId(String placeId) throws SQLException;

    public boolean checkIfThereIsPendingSurvey();

    public void removePendingSurvey();

    public Survey findPendingSurvey();
}
