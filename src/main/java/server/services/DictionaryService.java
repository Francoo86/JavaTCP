package server.services;

import com.j256.ormlite.support.ConnectionSource;
import dao.Word;
import dao.WordDAO;

public class DictionaryService {
    private final WordDAO wordDao;

    //inject this connection because it looks better.
    public DictionaryService(ConnectionSource connSource) {
        wordDao = new WordDAO(connSource);
    }

    public void addWord(String word) {
        Word newWord = new Word();
        newWord.setWordName(word);
        wordDao.add(newWord);
    }

    public boolean addDefinition(String word, String def) {

        return wordDao.addMeaning(word, def);
    }

    public Word lookupWord(String wordName) {
        return wordDao.findUnique(wordName);
    }
}
