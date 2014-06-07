package com.mkyong.common.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.matlux.NreplServer;
import net.matlux.testobjects.Department;
import net.matlux.testobjects.Employee;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/city")
public class HelloController {

	@RequestMapping(value = "{name}", method = RequestMethod.GET)
	public String printWelcome(@PathVariable String name, ModelMap model) {
		//String name = "NY";
		Department dep = (Department)NreplServer.instance.get("department");
		List<Employee> emps = dep.getEmployees();
		Collections.sort(emps,new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Employee e1 = (Employee)o1;
				Employee e2 = (Employee)o2;
				return e1.getAddress().getCity().compareTo(e2.getAddress().getCity());
			}
			
		});
		List<Employee> empsRes = new ArrayList<Employee>();
		for(Employee e:emps) {
			if(name.equals("all")) {
				empsRes.add(e);
			} else if(name.equalsIgnoreCase(e.getAddress().getCity()) ) {
				empsRes.add(e);
			}
			
		}
		
		model.put("empsRes", empsRes);
		model.put("city", name);
		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "hello";

	}
	@RequestMapping(method = RequestMethod.GET)
	public String getAllEmployees(ModelMap model) {
		String name = "all";
		Department dep = (Department)NreplServer.instance.get("department");
		List<Employee> emps = dep.getEmployees();
		Collections.sort(emps,new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				Employee e1 = (Employee)o1;
				Employee e2 = (Employee)o2;
				return e1.getAddress().getCity().compareTo(e2.getAddress().getCity());
			}
			
		});
		List<Employee> empsRes = new ArrayList<Employee>();
		for(Employee e:emps) {
			if(name.equals("all")) {
				empsRes.add(e);
			} else if(name.equalsIgnoreCase(e.getAddress().getCity()) ) {
				empsRes.add(e);
			}
			
		}
		
		model.put("empsRes", empsRes);
		model.put("city", name);
		return "displayAllEmployees";

	}
	
}