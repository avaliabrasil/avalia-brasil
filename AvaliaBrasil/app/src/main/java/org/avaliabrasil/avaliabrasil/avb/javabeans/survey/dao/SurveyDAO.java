package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao;

import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Survey;

import java.sql.SQLException;

/**
 * Created by Developer on 09/05/2016.
 */
public interface SurveyDAO {

    public boolean boundPlaceAndInstrument(String placeId, Survey survey) throws SQLException;

    public boolean bulkAddInstrument(InstrumentDAO instrumentDAO,Survey survey) throws SQLException;

    public boolean bulkAddQuestionGroup(GroupQuestionDAO groupQuestionDAO, Survey survey) throws SQLException;

    public boolean bulkAddQuestion(QuestionDAO questionDAO, Survey survey) throws SQLException;

    public Survey findSurveyByPlaceId(InstrumentDAO instrumentDAO, GroupQuestionDAO groupQuestionDAO,String placeId) throws SQLException;

    public boolean checkIfThereIsPendingSurvey();
}
