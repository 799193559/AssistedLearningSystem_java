package golden.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;


public class WordList {
    private Integer wordId;

    private Object sentence;

    public Integer getWordId() {
        return wordId;
    }

    public void setWordId(Integer wordId) {
        this.wordId = wordId;
    }

    public Object getSentence() {//�Ƚ�����ת��Ϊstring����תΪjsonarray��ʵ�ַ���ǰ�˵�json�д˱�������Ϊ����
    	JSONArray object = JSON.parseArray((String) sentence);
        return object;
    }

    public void setSentence(Object sentence) {
        this.sentence = sentence;
    }
}