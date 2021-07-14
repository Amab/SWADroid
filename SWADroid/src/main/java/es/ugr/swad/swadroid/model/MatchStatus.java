package es.ugr.swad.swadroid.model;

import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;

/**
 * Class for store a matchStatus
 *
 * @author Juan Miguel Boyero Corral <juanmi1982@gmail.com>
 * @author Sergio Díaz Rueda <sergiodiazrueda8@gmail.com>
 */
public class MatchStatus extends Model{
    private int questionIndex;
    private int numQuestions;
    private int answerIndex;
    private static final PropertyInfo PI_id = new PropertyInfo();
    private static final PropertyInfo PI_questionIndex = new PropertyInfo();
    private static final PropertyInfo PI_numQuestions = new PropertyInfo();
    private static final PropertyInfo PI_answerIndex = new PropertyInfo();
    private static final PropertyInfo[] PI_PROP_ARRAY =
            {
                    PI_id,
                    PI_questionIndex,
                    PI_numQuestions,
                    PI_answerIndex
            };

    /**
     * Constructor
     */
    public MatchStatus(long matchCode, int questionIndex, int numQuestions,
                 int answerIndex) {
        super(matchCode);
        this.questionIndex = questionIndex;
        this.numQuestions = numQuestions;
        this.answerIndex = answerIndex;

    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    public void setQuestionIndex(int questionIndex) {
        this.questionIndex = questionIndex;
    }

    public int getNumQuestion() {
        return numQuestions;
    }

    public void setNumQuestions(int numQuestions) {
        this.numQuestions = numQuestions;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int selected) {
        this.answerIndex = answerIndex;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getProperty(int)
     */
    public Object getProperty(int param) {
        Object object = null;
        switch (param) {
            case 0:
                object = this.getId();
                break;
            case 1:
                object = questionIndex;
                break;
            case 2:
                object = numQuestions;
                break;
            case 3:
                object = answerIndex;
                break;
        }

        return object;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyCount()
     */
    public int getPropertyCount() {
        return PI_PROP_ARRAY.length;
    }

    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#getPropertyInfo(int, java.util.Hashtable, org.ksoap2.serialization.PropertyInfo)
     */
    public void getPropertyInfo(int param, @SuppressWarnings("rawtypes")
            Hashtable arg1, PropertyInfo propertyInfo) {
        switch (param) {
            case 0:
                propertyInfo.type = PropertyInfo.LONG_CLASS;
                propertyInfo.name = "matchCode";
                break;
            case 1:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "questionIndex";
                break;
            case 2:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "numQuestions";
                break;
            case 3:
                propertyInfo.type = PropertyInfo.INTEGER_CLASS;
                propertyInfo.name = "answerIndex";
                break;
        }
    }
    /* (non-Javadoc)
     * @see org.ksoap2.serialization.KvmSerializable#setProperty(int, java.lang.Object)
     */
    public void setProperty(int param, Object obj) {
        switch (param) {
            case 0:
                this.setId((Long) obj);
                break;
            case 1:
                questionIndex = (int) obj;
                break;
            case 2:
                numQuestions = (int) obj;
                break;
            case 3:
                answerIndex = (int) obj;
                break;
        }
    }

    @Override
    public String toString() {
        return "MatchStatus{" +
                ", questionIndex='" + questionIndex + '\'' +
                ", numQuestions='" + numQuestions + '\'' +
                ", selected='" + answerIndex + '\'' +
                "} " + super.toString();
    }
}
