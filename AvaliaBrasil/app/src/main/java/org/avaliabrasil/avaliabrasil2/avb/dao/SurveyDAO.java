package org.avaliabrasil.avaliabrasil2.avb.dao;

import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Survey;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 09/05/2016.
 */
public interface SurveyDAO {

    public boolean boundPlaceAndInstrument(String placeId, Survey survey) throws SQLException;

    public boolean bulkAddInstrument(Survey survey) throws SQLException;

    public boolean bulkAddQuestionGroup(Survey survey) throws SQLException;

    public boolean bulkAddQuestion(Survey survey) throws SQLException;

    public void addSurvey(Survey survey);

    public Survey findSurveyByPlaceId(String placeId) throws SQLException;

    public boolean checkIfThereIsPendingSurvey();

    public void removeSurvey(Survey survey);

    public Survey findPendingSurvey();

    public List<Survey> getAllUnsendedSurvey();
}
