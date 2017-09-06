package com.ge.dao;


import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;



@Repository
public class ProdictiveDaoImpl {
	
	@Autowired
	JdbcTemplate jdbctemplate;
	
	
	public Boolean insertWorkflowTimeRetrieval(Map<String, ArrayList<String>> workflowStep) {
	     ArrayList<String> steparry = new  ArrayList<String>();
		 for (Map.Entry<String, ArrayList<String>> entry : workflowStep.entrySet()){
				
            if (entry.getKey().equals("step")){

           	 steparry =entry.getValue();

            }
            

        }
		 String 	sql = "insert into predictive_workflow values (nextval('workflow_id_seq')";
		// String 	sql = "insert into dbo.workflow_steps_time_retrieval values (nextval('dbo.workflow_id_seq')";
		 String 	sql1 ="";
		 for(int i=0;i<steparry.size();i++){
			 sql1 = sql1 + ",'" +steparry.get(i) +"'";
		 }
	String 	sql2 = sql+sql1+")";
	jdbctemplate.update(sql2);
	return true;
}


	

}
