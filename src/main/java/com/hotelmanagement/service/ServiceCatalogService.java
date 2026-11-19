package com.hotelmanagement.service;

import com.hotelmanagement.dao.ServiceDAO;
import com.hotelmanagement.entity.HotelService;
import com.hotelmanagement.util.HibernateUtil;
import com.hotelmanagement.util.ServiceException;
import com.hotelmanagement.util.Validator;
import java.math.BigDecimal;
import java.util.List;

/** The list of hotel services (breakfast, laundry...) with their prices. */
public class ServiceCatalogService {

    private final ServiceDAO serviceDAO = new ServiceDAO();

    public List<HotelService> list() {
        return HibernateUtil.read(s -> serviceDAO.findAllByName(s));
    }

    public HotelService get(Integer id) {
        HotelService h = HibernateUtil.read(s -> serviceDAO.findById(s, id));
        if (h == null) throw new ServiceException("Service not found.");
        return h;
    }

    public void add(String name, BigDecimal price) {
        String n = Validator.required(name, "Service name", 100);
        BigDecimal p = Validator.money(price, "Price", true);
        HibernateUtil.txRun(s -> {
            HotelService h = new HotelService();
            h.setServiceName(n);
            h.setPrice(p);
            serviceDAO.save(s, h);
        });
    }

    public void update(Integer id, String name, BigDecimal price) {
        String n = Validator.required(name, "Service name", 100);
        BigDecimal p = Validator.money(price, "Price", true);
        HibernateUtil.txRun(s -> {
            HotelService h = serviceDAO.findById(s, id);
            if (h == null) throw new ServiceException("Service not found.");
            h.setServiceName(n);
            h.setPrice(p);
        });
    }

    public void delete(Integer id) {
        HibernateUtil.txRun(s -> {
            HotelService h = serviceDAO.findById(s, id);
            if (h == null) throw new ServiceException("Service not found.");
            serviceDAO.delete(s, h);
        });
    }
}
