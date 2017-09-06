package com.ge.service;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.dao.ProdictiveDaoImpl;

@Service
public class ProdictiveServiceImpl implements ProdictiveService{
	
	@Autowired
	ProdictiveDaoImpl loginDaoImpl;
	
	@Override
	public Boolean insertWorkflowTimeRetrieval(Map<String, ArrayList<String>> workflowStep) {
		return loginDaoImpl.insertWorkflowTimeRetrieval(workflowStep);
	}

}
