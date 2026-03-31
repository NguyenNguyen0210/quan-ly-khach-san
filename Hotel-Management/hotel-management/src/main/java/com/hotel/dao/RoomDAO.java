package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.Room;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class RoomDAO extends GenericDAOImpl<Room, Long> {

    public RoomDAO() {
        super(Room.class);
    }

    public List<Room> findByStatus(String status) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select r from Room r join fetch r.roomType where r.status = :status", Room.class)
                    .setParameter("status", status)
                    .getResultList();
        }
    }

    public Room findByRoomNumber(String roomNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Room> results = session.createQuery("from Room where roomNumber = :roomNumber", Room.class)
                    .setParameter("roomNumber", roomNumber)
                    .getResultList();
            return results.stream().findFirst().orElse(null);
        }
    }

    public List<Room> findByRoomTypeId(Long roomTypeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select r from Room r join fetch r.roomType where r.roomType.id = :roomTypeId", Room.class)
                    .setParameter("roomTypeId", roomTypeId)
                    .getResultList();
        }
    }

    public List<Room> findAllWithRoomType() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select r from Room r join fetch r.roomType", Room.class)
                    .getResultList();
        }
    }

    public Room findByIdWithRoomType(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("select r from Room r join fetch r.roomType where r.id = :id", Room.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }
}
