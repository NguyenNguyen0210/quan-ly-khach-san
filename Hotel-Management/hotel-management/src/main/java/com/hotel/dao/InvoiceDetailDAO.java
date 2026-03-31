package com.hotel.dao;

import com.hotel.dao.base.GenericDAOImpl;
import com.hotel.entity.InvoiceDetail;
import com.hotel.util.HibernateUtil;
import org.hibernate.Session;

import java.util.List;

public class InvoiceDetailDAO extends GenericDAOImpl<InvoiceDetail, Long> {

    public InvoiceDetailDAO() {
        super(InvoiceDetail.class);
    }

    public List<InvoiceDetail> findByInvoiceId(Long invoiceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                            "select d from InvoiceDetail d left join fetch d.service where d.invoice.id = :invoiceId",
                            InvoiceDetail.class)
                    .setParameter("invoiceId", invoiceId)
                    .getResultList();
        }
    }

    public List<InvoiceDetail> findByServiceId(Long serviceId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from InvoiceDetail where service.id = :serviceId", InvoiceDetail.class)
                    .setParameter("serviceId", serviceId)
                    .getResultList();
        }
    }
}
