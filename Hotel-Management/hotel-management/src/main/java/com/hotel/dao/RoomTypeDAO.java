package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.RoomType;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class RoomTypeDAO extends GenericDAOImpl<RoomType, Long> {

    public RoomTypeDAO() {
        super(RoomType.class);
    }

    public RoomType findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<RoomType> results = session.createQuery("from RoomType where name = :name", RoomType.class)
                    .setParameter("name", name)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }
}
