package org.avaliabrasil.avaliabrasil2.avb.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.avaliabrasil.avaliabrasil2.avb.dao.AvBContract;
import org.avaliabrasil.avaliabrasil2.avb.dao.GroupQuestionDAO;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.GroupQuestion;
import org.avaliabrasil.avaliabrasil2.avb.javabeans.survey.Question;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Developer on 10/05/2016.
 */
public class GroupQuestionDAOImpl implements GroupQuestionDAO{

    private Context context;

    public GroupQuestionDAOImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean bulkQuestionGroup(String instrumentId, List<GroupQuestion> groupQuestion) throws SQLException {
        ContentValues[] values = new ContentValues[groupQuestion.size()];
        ContentValues value = null;

        for (int i = 0; i < groupQuestion.size(); i++) {
            value = new ContentValues();
            value.put(AvBContract.GroupQuestionEntry.INSTRUMENT_ID, instrumentId);
            value.put(AvBContract.GroupQuestionEntry.GROUP_ID, groupQuestion.get(i).getId());
            value.put(AvBContract.GroupQuestionEntry.ORDER_QUESTION, groupQuestion.get(i).getOrder());
            values[i] = value;
        }

        context.getContentResolver().bulkInsert(
                AvBContract.GroupQuestionEntry.GROUP_URI, values);

        return true;
    }

    @Override
    public List<GroupQuestion> findGroupByInstrumentId(String instrumentId) throws SQLException {
        Cursor c = context.getContentResolver().query(AvBContract.GroupQuestionEntry.buildGroupQuestionsUri(instrumentId), null, null, null, null);

        ArrayList<GroupQuestion> groupQuestions = new ArrayList<GroupQuestion>();

        boolean foundGroup = false;

        while (c.moveToNext()) {
            for (GroupQuestion g : groupQuestions) {
                if (g.getId().contentEquals(c.getString(c.getColumnIndex(AvBContract.GroupQuestionEntry.GROUP_ID)))) {
                    g.addQuestion(new Question(c));
                    foundGroup = true;
                }
            }
            if (foundGroup) {
                foundGroup = false;
                continue;
            }
            groupQuestions.add(new GroupQuestion(c));
        }

        c.close();

        return groupQuestions;
    }
}
