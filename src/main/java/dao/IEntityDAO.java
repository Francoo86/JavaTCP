package dao;

import java.util.Collection;

//Due to requirements we only need add and findUnique.
public interface IEntityDAO <T, U> {
    void add(T entity);
    //void update(T entity);
    //T findByWordName(String wordName);
    //void delete(T entityId);
    Collection<T> findAll();
    T findUnique(U criteria);
}
