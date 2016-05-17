package org.avaliabrasil.avaliabrasil.avb.javabeans.survey.service;

import android.content.ContentValues;
import android.content.Context;

import org.avaliabrasil.avaliabrasil.avb.data.AvBContract;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.dao.QuestionDAO;
import org.avaliabrasil.avaliabrasil.avb.javabeans.survey.object.Question;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public class QuestionDAOImpl implements QuestionDAO{

    private Context context;

    public QuestionDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean bulkInsertQuestion(String groupId, List<Question> questions) throws SQLException {

        ContentValues[] values = new ContentValues[questions.size()];
        ContentValues value = null;

        for (int i = 0; i < questions.size(); i++) {
            value = new ContentValues();
            value.put(AvBContract.QuestionEntry.GROUP_ID, groupId);
            value.put(AvBContract.QuestionEntry.QUESTION, questions.get(i).getTitle());
            value.put(AvBContract.QuestionEntry.QUESTION_ID, questions.get(i).getId());
            value.put(AvBContract.QuestionEntry.QUESTION_TYPE, questions.get(i).getQuestionType());
            values[i] = value;
        }

        context.getContentResolver().bulkInsert(
                AvBContract.QuestionEntry.QUESTION_URI, values);

        return true;
    }
}
