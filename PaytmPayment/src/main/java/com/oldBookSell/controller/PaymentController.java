package com.oldBookSell.controller;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.oldBookSell.model.PaymentDetails;
import com.paytm.pg.merchant.CheckSumServiceHelper;

@CrossOrigin
@RestController
public class PaymentController {
	@Autowired(required = true)
	private PaymentDetails paytmDetails;
	
		//	@Autowired(required = true)
		//	private Environment env;
	
	 	@GetMapping(value = "/pgredirect")
	    public ModelAndView getRedirect1() throws Exception {
		 	
		 	Random rand= new Random();
		 	int val1=rand.nextInt(999)*10;
		 	int val2=rand.nextInt(9988)*100;
//		 	System.out.println(val1+".............."+val2);
		 	
		 	ModelAndView modelAndView = new ModelAndView("redirect:"+paytmDetails.getPaytmUrl());
	        TreeMap<String, String> parameters = new TreeMap<>();
	        paytmDetails.getDetails().forEach((k, v) -> parameters.put(k, v));
	        parameters.put("MOBILE_NO", "7352844178");
	        parameters.put("EMAIL", "pk145119@gmail.com");
	        parameters.put("ORDER_ID", val1+"");
	        parameters.put("TXN_AMOUNT","1");
	        parameters.put("CUST_ID", val2+"");
	        String checkSum = getCheckSum(parameters);
	        parameters.put("CHECKSUMHASH", checkSum);
	        modelAndView.addAllObjects(parameters);
	        System.out.println("working complete........");
	        return modelAndView;
	    }
	 	@GetMapping(value = "/pgredirect1")
	    public TreeMap<String, String> getRedirect() throws Exception {
	 		Random rand= new Random();
		 	int val1=rand.nextInt(999)*10;
		 	int val2=rand.nextInt(9988)*100;
//		 	ModelAndView modelAndView = new ModelAndView(paytmDetails.getPaytmUrl());
	        TreeMap<String, String> parameters = new TreeMap<>();
	        paytmDetails.getDetails().forEach((k, v) -> parameters.put(k, v));
	        parameters.put("urls", paytmDetails.getPaytmUrl());
	        parameters.put("MOBILE_NO", "7352844178");
	        parameters.put("EMAIL", "pk145119@gmail.com");
	        parameters.put("ORDER_ID", val1+"");
	        parameters.put("TXN_AMOUNT","1");
	        parameters.put("CUST_ID", val2+"");
	        String checkSum = getCheckSum(parameters);
	        parameters.put("CHECKSUMHASH", checkSum);
//	        modelAndView.addAllObjects(parameters);
	        System.out.println("working complete........");
	        return parameters;
	    }
	 
	 @PostMapping(value = "/pgresponse")
	    public String getResponseRedirect(HttpServletRequest request, Model model) {

	        Map<String, String[]> mapData = request.getParameterMap();
	        TreeMap<String, String> parameters = new TreeMap<String, String>();
	        mapData.forEach((key, val) -> parameters.put(key, val[0]));
	        String paytmChecksum = "";
	        if (mapData.containsKey("CHECKSUMHASH")) {
	            paytmChecksum = mapData.get("CHECKSUMHASH")[0];
	        }
	        String result;

	        boolean isValideChecksum = false;
	        System.out.println("RESULT : "+parameters.toString());
	        try {
	            isValideChecksum = validateCheckSum(parameters, paytmChecksum);
	            System.out.println(isValideChecksum+".............");
	            if (isValideChecksum && parameters.containsKey("RESPCODE")) {
	                if (parameters.get("RESPCODE").equals("01")) {
	                    result = "Payment Successful";
	                } else {
	                    result = "Payment Failed";
	                }
	            } else {
	                result = "Checksum mismatched";
	            }
	        } catch (Exception e) {
	            result = e.toString();
	        }
	        model.addAttribute("result",result);
	        parameters.remove("CHECKSUMHASH");
	        model.addAttribute("parameters",parameters);
	        return "report";
	    }

	    private boolean validateCheckSum(TreeMap<String, String> parameters, String paytmChecksum) throws Exception {
	    	System.out.println("method valid sucess........");
	        return CheckSumServiceHelper.getCheckSumServiceHelper().verifycheckSum(paytmDetails.getMerchantKey(),
	                parameters, paytmChecksum);
	    }


	private String getCheckSum(TreeMap<String, String> parameters) throws Exception {
		System.out.println("method check sum........");
		return CheckSumServiceHelper.getCheckSumServiceHelper().genrateCheckSum(paytmDetails.getMerchantKey(), parameters);
	}
}
