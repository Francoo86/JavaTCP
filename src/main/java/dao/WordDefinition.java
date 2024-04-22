package dao;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class WordDefinition {
    @DatabaseField(generatedId = true)
    private int id;
    //Word and associates.
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Word word;
    @DatabaseField
    private String def;

    public WordDefinition() {}

    public String getDef() {
        return def;
    }

    public Word getWord() {
        return word;
    }

    public int getId() {
        return id;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public void setWord(Word word) {
        this.word = word;
    }
}
