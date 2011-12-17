package com.blogspot.rrusin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.codehaus.jackson.map.ObjectMapper;

@SuppressWarnings("serial")
public class DataTablesJEEServlet extends HttpServlet {
	private List<List<Object>> myList;
	
    public DataTablesJEEServlet() {
        super();
        
        Random random = new Random();
        
        myList = new ArrayList<List<Object>>();
        for (int i=0; i < 20000; i++) {
        	List<Object> l = new ArrayList<Object>();
        	for (int j=0; j < 5; j++) {
        		l.add((random.nextInt() & 0xffff) + "("+j+","+i+")");
        	}
        	myList.add(l);
        }
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ObjectMapper mapper = new ObjectMapper();
//		System.out.println(mapper.writeValueAsString(request.getParameterMap()));
		int iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
		int iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
		String sSearch = request.getParameter("sSearch");
		
		final int iSortCol_0 = Integer.parseInt(request.getParameter("iSortCol_0"));
		final boolean sSortDir_0 = request.getParameter("sSortDir_0").equals("asc");
		
        Map<Object, Object> result = new HashMap<Object, Object>();
        result.put("sEcho", request.getParameter("sEcho"));

        List<List<Object>> filteredList = new ArrayList<List<Object>>();
        
        for (int i = 0; i < myList.size(); i++) {
    		if (filter(sSearch, myList.get(i))) {
    			filteredList.add(myList.get(i));
    		}
        }
        
        Collections.sort(filteredList, new Comparator<List<Object>>() {
			@Override
			public int compare(List<Object> o1, List<Object> o2) {
				if (sSortDir_0) {
					return o1.get(iSortCol_0).toString().compareTo(o2.get(iSortCol_0).toString());
				} else {
					return o2.get(iSortCol_0).toString().compareTo(o1.get(iSortCol_0).toString());
				}
			}
        });

        List<List<Object>> selectedList = new ArrayList<List<Object>>();
        
        for (int i = iDisplayStart; i < iDisplayStart + iDisplayLength; i++) {
        	if (i < filteredList.size()) {
        		if (filter(sSearch, filteredList.get(i))) {
        			selectedList.add(filteredList.get(i));
        		}
        	}
        }
        
        result.put("iTotalRecords", myList.size());
        result.put("iTotalDisplayRecords", filteredList.size());
        
        result.put("aaData", selectedList);

//        System.out.println(mapper.writeValueAsString(result));
        mapper.writeValue(response.getOutputStream(), result);
	}

	private boolean filter(String sSearch, List<Object> list) {
		if (sSearch.equals("")) {
			return true;
		}
		return list.toString().contains(sSearch);
	}
}
