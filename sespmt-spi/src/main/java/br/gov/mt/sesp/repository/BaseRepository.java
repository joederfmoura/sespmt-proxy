package br.gov.mt.sesp.repository;

import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public class BaseRepository<T> {

    protected EntityManager em;
    private Class<T> clazz;
    private Session session;
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<T> criteriaQuery;
    private Root<T> root;

    public BaseRepository(EntityManager em) {
        this.em = em;
        Class clazz = getClass();
        Type genericSuperclass = null;
        while (true) {
            genericSuperclass = clazz.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                break;
            }
            clazz = clazz.getSuperclass();
        }
        ParameterizedType genericSuperclass_ = (ParameterizedType) genericSuperclass;
        this.clazz = (Class<T>) genericSuperclass_.getActualTypeArguments()[0];
    }

    protected Session getSession() {
        if (session == null) {
            session = (Session) em.getDelegate();
        }

        return session;
    }

    protected CriteriaBuilder getCriteriaBuilder() {
        if (criteriaBuilder == null) {
            criteriaBuilder = em.getCriteriaBuilder();
        }

        return criteriaBuilder;
    }

    protected Root<T> getRoot() {
        return root;
    }

    private CriteriaQuery<T> getCriteria() {
        criteriaQuery = getCriteriaBuilder().createQuery(clazz);
        root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);

        return criteriaQuery;
    }

    public List<T> findAll() {
        TypedQuery<T> query = em.createQuery(getCriteria());
        return Optional.ofNullable(query.getResultList()).orElse(new ArrayList());
    }

    public List<T> findAll(int firstResult, int maxResults) {
        TypedQuery<T> query = em.createQuery(getCriteria());

        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);

        return Optional.ofNullable(query.getResultList()).orElse(new ArrayList());
    }

    public T find(Serializable id) {
        return em.find(clazz, id);
    }
}
