package golden.controller;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.ibatis.javassist.bytecode.stackmap.Tracer;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/*import com.mysql.jdbc.exceptions.MySQLSyntaxErrorException;*/

import golden.model.Collection;
import golden.model.HasReadWords;
import golden.model.HasReadWordsWithBLOBs;
import golden.model.WordListWithBLOBs;
import golden.service.userservice;
import golden.tempmodel.collection_1;
import golden.time_handler.get_last_week_interval;
import javassist.compiler.TypeChecker;





@Controller
@RequestMapping({"/api"})
public class usercontroller {
    
	@Autowired
	userservice user_service;
	
	@RequestMapping({"/queryWord"})
	@ResponseBody
	public JSONObject queryword(Integer word_id)throws NumberFormatException{
		String code=null;//��ѯjson�ֶγ�����������Ҫ�ǵø������°汾��mysql-connector-java
	    String msg="";
	    JSONObject json=new JSONObject();
		if(word_id==null) {
			code="-1";
		    msg="word_id missing";
		    json.put("code",code);
		    json.put("msg",msg);
		    return json;
		}		
		 	 
	    WordListWithBLOBs word_info=user_service.selectbyword_id(word_id);
		

	    if (word_info!=null) {
	    	code="200";
	        msg="success";
	    }
	    else { 
	    	code="-2";
	    	msg="faild";
	    }

	    json.put("data",word_info);//�Զ����ø���get������ֵ��json��Ӧ��key
        json.put("code",code);
        json.put("msg",msg);
	    return json;
 }


	
		
	  @RequestMapping({"/queryWordlist"})	  
	  @ResponseBody
	  public JSONObject queryword_list(String word){
		  String code=null;
	      String msg=null;
	      JSONObject json=new JSONObject();
		  if(word==""||word==null) {
			  code="-1";
			  msg="word missing";
			  json.put("code",code); 
		      json.put("msg",msg); 
		      return json;
		  }		  
	      List<Object> word_list=user_service.selectbyword(word); 
	      if(word_list==null) {
			  code="-2";
			  msg="fail";
			  json.put("code",code); 
		      json.put("msg",msg); 
		      return json;
	      }
	      code="200";
		  msg="sucess";
	      json.put("data", word_list);  
	      json.put("code",code); 
	      json.put("msg",msg); 
	      System.out.print("\n"+word_list.toString()+"\n");
	      return json; 
	  }
	  
	  
	  @SuppressWarnings("deprecation")
	  @RequestMapping({"/statistics"})
	  @ResponseBody
	  public JSONObject getStatistics(Integer student_id) throws ParseException {
		  String code=null,msg=null; 
		  JSONObject result=new JSONObject();
		  if(student_id==null) {
			  code="-1";
			  msg="student_id missing";
			  result.put("code",code);
			  result.put("msg",msg);
			  return result;
		  }
		  HasReadWordsWithBLOBs hasreadwords = user_service.selectby_studentid(student_id);
		  if(hasreadwords==null) {
			  code="-2";
			  msg="weekly data doesn't exit";
			  result.put("code",code);
			  result.put("msg",msg);
			  return result;
		  }
		  JSONArray statistics = (JSONArray) hasreadwords.getWords();
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		  get_last_week_interval getinterval=new get_last_week_interval();
		  Date[] dates=getinterval.getLastWeekInterval();
		  int num[]= {0,0,0,0,0,0,0};
		  for(int i=statistics.size()-1,flag=7;i>=0&&flag>0;i--) {
			  JSONObject temp= (JSONObject) statistics.get(i);
			  Date date=sdf.parse((String) temp.get("date"));//���ַ���ת��Ϊ��������
			  if(date.after(dates[0])&&date.before(dates[1])){
		          JSONArray data=  (JSONArray)temp.get("data");
		          @SuppressWarnings("deprecation")
				  int day=date.getDay();//0�������գ�1������1....�Դ����ƣ�ע���calendar�Ļ�ȡ�������ֿ�
		          num[day]+=data.size();
		          flag--;
			  }			  
		  }	
//		  num[7]=num[0];

		  result.put("weekly",num);
//		  System.out.println(dates[0]);
//		  System.out.println(date.after(dates[0])&&date.before(dates[1]));
//		  System.out.println(sdf.format(date));
//		  System.out.println(temp.get("date").getClass().toString());
//		  System.out.println("++++++++"+statistics.get(0)+"+++++++++++++++");
		  golden.model.Calendar calendarcol = user_service.select_calendar_by_studentid(student_id); 
		  if(calendarcol==null) {
			  code="-3";
			  msg="yearly data doesn't exit";
			  result.put("code",code);
			  result.put("msg",msg);
			  return result;
		  }
		  JSONArray calendar = (JSONArray) calendarcol.getCalendarcol();
		  SimpleDateFormat sdf_1 = new SimpleDateFormat("yyyy-MM");
		  Date date=new Date();//��ȡ���ݿ��е�����
		  Date calendar_1 = new Date();//���ڻ�ȡ��ǰ���
		  System.out.println(calendar_1.getYear());//���ص�ǰ�����1900��Ĳ�
		  int sum[]= {0,0,0,0,0,0,0,0,0,0,0,0};
		  for(int i=calendar.size()-1,flag=12;i>=0&&flag>0;i--) {//����JSONArray���飬��flag������Ч��ѯ
			  JSONObject temp =(JSONObject)calendar.get(i);
			  date = sdf_1.parse((String)temp.get("date"));			  
			  if(date.getYear()==calendar_1.getYear()) {
				  int mon = date.getMonth();//������ֻ�ǲ��Ƽ�ʹ�ö��ѣ���Ӱ��
//				  System.out.println(calendar_1.getMonth());//�����ǰ�·ݼ�1
				  System.out.println(mon);
				  JSONArray data=(JSONArray)temp.get("data");
				  for(int j=0;j<data.size();j++) {
					  if((int)data.get(j)==1) {
						  sum[mon]++;
					  }
				  }		
				  flag--;
				  }
		  }
		  result.put("yearly",sum);
		  code="200";
		  msg="success";
		  result.put("code",code);
		  result.put("msg",msg);
		  return result;
	  }
	  	  
	@RequestMapping({"/collection"})
	@ResponseBody
	public JSONObject collection(@RequestBody String data) {
		JSONObject json = JSON.parseObject(data);
		Collection collec=new Collection();
		String msg=null,code=null;
		JSONObject result=new JSONObject();
		if(json.get("student_id")==null) {
			msg="student_id missing";
			code="-2";
			result.put("msg",msg);
			result.put("code", code);
			return result;
		}
		if(json.get("words")==null&&json.get("article")==null&&json.get("vedio")==null) {
			msg="parameter missing";
			code="-1";
			result.put("msg",msg);
			result.put("code", code);
			return result;
		}
//		JSONObject temp=new JSONObject();		
//		try {
//		    temp=json.getJSONObject(0);
//            JSONObject temp_2=json.getJSONObject(1);
//		}
//		catch (java.lang.IndexOutOfBoundsException e){
//			code="-1";
//			msg="parameter missing";
//			result.put("msg",msg);
//			result.put("code", code);
//			return result;
//		}
//		if(temp.get("student_id")==null||temp.get("student_id")=="") {
//			msg="student_id missing";
//			code="-2";
//			result.put("msg",msg);
//			result.put("code", code);
//			return result;
//		}
		collec.setStudentId((Integer)json.get("student_id"));
		if(json.get("words")!=null&&json.get("words")!="")
		    collec.setWords((String)json.get("words"));
		if(json.get("article")!=null&&json.get("article")!="")
			collec.setArticle((String)json.get("article"));
		if(json.get("vedio")!=null&&json.get("vedio")!="")
			collec.setVedio((String)json.get("vedio"));
//		for(int i=1;i<=json.size()-1;i++) {
//			try{
//				JSONObject temp_1=new JSONObject();
//				temp_1=json.getJSONObject(1);
//				if(temp_1.get("words")==null&&temp_1.get("article")==null&&temp_1.get("vedio")==null) {
//					code="-1";
//					msg="parameter missing";
//					result.put("msg",msg);
//					result.put("code", code);
//					return result;
//				}
//				if(temp_1.get("words")!=null&&temp_1.get("words")!="")
//				    collec.setWords(JSON.toJSONString(json.get(1)));
//				if(temp_1.get("article")!=null&&temp_1.get("article")!="")
//					collec.setArticle(JSON.toJSONString(json.get(1)));
//				if(temp_1.get("vedio")!=null&&temp_1.get("vedio")!="")
//					collec.setVedio(JSON.toJSONString(json.get(1)));
//			}
//			catch (java.lang.IndexOutOfBoundsException e){
//				System.out.print("indexout");
//			}

		
		int num=user_service.collection(collec);
		if(num==0) {
			code="-3";
			msg="insert error";
			result.put("msg",msg);
			result.put("code", code);
			return result;
		}
		code="200";
		msg="success";
		result.put("msg",msg);
		result.put("code", code);
		return result;
	}	
	
	
	@RequestMapping({"/querycollection"})
	@ResponseBody
	public JSONObject select_collection(Integer student_id) {

		JSONObject result=new JSONObject();
		String msg=null;
		String code=null;
		if(student_id==null) {
			msg="student_id missing";
			code="-1";
			result.put("msg",msg);
			result.put("code",code);
			return result;
		}
		collection_1 data =user_service.select_collection(student_id);
        if(data==null) {
        	msg="collection null";
        	code="0";
        	result.put("msg",msg);
			result.put("code",code);
			return result;
        }
        msg="success";
    	code="200";
    	result.put("msg",msg);
		result.put("code",code);
		result.put("data",data);
		return result;
	}
	}


