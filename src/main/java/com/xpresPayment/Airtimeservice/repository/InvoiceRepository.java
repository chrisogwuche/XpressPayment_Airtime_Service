package com.xpresPayment.Airtimeservice.repository;


import com.xpresPayment.Airtimeservice.model.Invoice;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
    Optional<Invoice> findInvoiceByUniqueId(String uniqueId);
}
