package it.univaq.mwt.fastmarket.business.impl.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.univaq.mwt.fastmarket.business.BusinessException;
import it.univaq.mwt.fastmarket.business.RequestGrid;
import it.univaq.mwt.fastmarket.business.ResponseGrid;
import it.univaq.mwt.fastmarket.business.SystemInformationService;
import it.univaq.mwt.fastmarket.business.model.SystemInformation;
import it.univaq.mwt.fastmarket.common.utility.ConversionUtility;

@Service
public class SystemInformationServiceHibernate implements SystemInformationService {

	@Autowired
	private SessionFactory sessionFactory;

	@Transactional
	public ResponseGrid<SystemInformation> findAllSystemInformationsPaginated(RequestGrid requestGrid) throws BusinessException {
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(SystemInformation.class,"si");
		
		if ("id".equals(requestGrid.getSortCol())) {
			requestGrid.setSortCol("si.id");
		} else {
			requestGrid.setSortCol("si." + requestGrid.getSortCol());
		}
	
		if(!"".equals(requestGrid.getSortCol()) && !"".equals(requestGrid.getSortDir())) {
			if("asc".equals(requestGrid.getSortDir())){
				criteria.addOrder(Order.asc(requestGrid.getSortCol()));	
			}else{
				criteria.addOrder(Order.desc(requestGrid.getSortCol()));	
			}
		}
		
		if(!"".equals(requestGrid.getsSearch())){
			criteria.add(Restrictions.like("si.name", ConversionUtility.addPercentSuffix(requestGrid.getsSearch())).ignoreCase());
		}
		
		criteria.setFirstResult((int) requestGrid.getiDisplayStart())
			.setMaxResults((int) requestGrid.getiDisplayLength());
		
		Long records = (Long) session.createCriteria(SystemInformation.class)
				.setProjection(Projections.rowCount()).uniqueResult();
		
		List<SystemInformation> systemInformations = criteria.list();
		
		return new ResponseGrid<SystemInformation>(requestGrid.getsEcho(), records, records, systemInformations);
	}
	
	@Transactional
	public void create(SystemInformation systemInformation) throws BusinessException {
		Session session = sessionFactory.getCurrentSession();
		session.save(systemInformation);
		
	}

	@Transactional
	public SystemInformation findSystemInformationByID(Long id) throws BusinessException {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("from SystemInformation where id=:id");
		query.setParameter("id", id);
		SystemInformation systemInformation = (SystemInformation) query.uniqueResult();
		if(systemInformation == null) {
			throw new BusinessException("SystemInformation not found");
		} else {
			return systemInformation;
		}
	}

	@Transactional
	public void update(SystemInformation systemInformation) throws BusinessException {
		Session session = sessionFactory.getCurrentSession();
		session.merge(systemInformation);
		
	}

	@Transactional
	public void delete(SystemInformation systemInformation) throws BusinessException {
		Session session = sessionFactory.getCurrentSession();
		SystemInformation systemInformation2 = (SystemInformation) session.load(SystemInformation.class, systemInformation.getId());
		session.delete(systemInformation2);
		
	}

}
