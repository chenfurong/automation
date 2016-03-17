package com.ibm.automation.core.util;


import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 格式化类
 * 
 * @author wanjuan.li
 *
 */
public class FormatUtil {
	//格式化时间 
	public static String dateFormatBymm(String mmStr){
		int mm = Integer.parseInt(mmStr);
		String str = "";
		//天
		int day = (int)mm/(24*60*60);
		if(day>0){
			str +=day+"日";
		}
		//小时
		int hTotal = (int)mm%(24*60*60);
		int hour = hTotal/(60*60);
		if(hour>0){
			str +=hour;
		}
		//分钟
		int mTotal = hTotal%(60*60);
		int minute = (int)mTotal/60;
		if(minute>0){
			str +=minute+"分钟";
		}
		//秒
		int second = (int)mTotal%60;
		if(second>0){
			str +=second+"秒";
		}
		return str;
	}
	public static String msFormat(long ms){
		double hour;
		double min;
		double second;
		
		if(ms>=60*60*1000){
			hour = ms/(60*60*1000);
			String strmb = String.format("%.2f", hour);
			return strmb + "h";
		}else if(ms>=60*1000){
			min = ms/(60*1000);
			String strkb = String.format("%.2f", min);
			return strkb + "m";
		}else if(ms>=1000){
			second = ms/(1000);
			String strkb = String.format("%.2f", second);
			return strkb + "s";
		}else{
			return ms + "ms";
		}
	}
	//截取日期
	public static String dateFormatByTime(String timeStr){
		String[] str=timeStr.split("T");		
		return str[0];
	}
	
	//格式化容量B-->MB
	public static String bFormatMb(long numb){
		
		double mb = numb/(1024.0*1024.0);
		String strmb = String.format("%.2f", mb);
		
		return strmb + "MB";
	}
	
	//格式化容量B-->GB
	public static String bFormatGb(long numb){
		
		double gb = numb/(1024.0*1024.0*1024.0);
		String strgb = String.format("%.2f", gb);
		
		return strgb + "GB";
	}
	
	//格式化容量KB-->MB
	public static String kbFormatMb(long numkb){
		
		double mb = numkb/1024.0;
		String strmb = String.format("%.2f", mb);
		
		return strmb + "MB";
	}
	
	//格式化容量MB-->GB
	public static String mbFormatGb(long nummb){
		
		double gb = nummb/1024.0;
		String strgb = String.format("%.2f", gb);
		
		return strgb + "GB";
	}
	
	//格式化容量KB-->GB
	public static String kbFormatGb(long numkb){
		
		double gb = numkb/(1024.0*1024.0);
		String strgb = String.format("%.2f", gb);
		
		return strgb + "GB";
	}
	
	//格式化容量KB-->GBMBKB
	public static String kbFormatGbMbKb(long numkb){
		
		long  gb = numkb/(1024*1024);
		
		long  mbTotal = numkb%(1024*1024);
		long  mb = mbTotal/1024;
		
		long  kb = mbTotal%1024;
		
		return gb + "GB " + mb + "MB " + kb + "KB";
	}
	
	//格式化容量B-->GB或MB或KB或B
	public static String bFormatGbMbKbB(long numb){
		
		double gb;
		double mb;
		double kb;
		
		if(numb>=1024.0*1024.0*1024.0){
			gb = numb/(1024.0*1024.0*1024.0);
			String strgb = String.format("%.2f", gb);
			return strgb + "GB";
		}else if(numb>=1024.0*1024.0){
			mb = numb/(1024.0*1024.0);
			String strmb = String.format("%.2f", mb);
			return strmb + "MB";
		}else if(numb>=1024.0){
			kb = numb/(1024.0);
			String strkb = String.format("%.2f", kb);
			return strkb + "KB";
		}else{
			return numb + "B";
		}
	}
	
	//格式化错误json信息
	public static String jsonErrorToErrorMessage(String error) {
		String message = "";
		if(error.indexOf("403 Forbidden")>-1){
			message = "403 Forbidden. "+error.substring(error.indexOf("\n\n")+2, error.lastIndexOf("\n\n"));
		}else{
			message = "\"message\": \"";
			
			int start = error.indexOf(message);
			message = error.substring(start+message.length());
			String[] arr = message.split("\"");
			message = arr[0];
		}
		return message;
	}
	
	public static String db2TimeToNormalTime(String timeStamp) {
		
		String ts = "20"+timeStamp.substring(1, 13);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		String date = "";
		try {
			Date d = sdf.parse(ts);
//			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
			date = new SimpleDateFormat("MM-dd HH:mm:ss").format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String normalTimeToDB2Time(Date date) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String time = sdf.format(date);
		String db2Time = "1"+time.substring(2);
		return db2Time;
	}
	
	//格林尼治标准时间 +8 = 北京时间
	public static String dateTimeZone(String strDate) {  
		TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT+8"); //格林威治时间+8小时 
		//TimeZone estTimeZone = TimeZone.getTimeZone("EST");//东部标准时间 
		if(strDate != null && strDate != "") {
			Long date = Long.parseLong(strDate);       
			Long targetTime = date + gmtTimeZone.getRawOffset();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			return formatter.format(new Date(targetTime));
		} else {
			return null;
		}
    }  

	public static String dateTime(String strDate) {  
		if(strDate != null && strDate != "") {
			Long date = Long.parseLong(strDate);       
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			return formatter.format(new Date(date));
		} else {
			return null;
		}
    }  
	
	//把resource bean 的数据 copy 到 target bean 中
	/*public static void deepCopyPropertiesIgnoreNull(Object target, Object resource) {
        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        PropertyDescriptor[] srcDescriptors = propertyUtils.getPropertyDescriptors(resource);
        for (PropertyDescriptor origDescriptor : srcDescriptors) {
            String name = origDescriptor.getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (propertyUtils.isReadable(resource, name) && propertyUtils.isWriteable(target, name)) {
                try {
                    Object value = propertyUtils.getSimpleProperty(resource, name);
                    if(null == value)
                        continue;     //空值不复制
                    if(deepClone(value) == null){
                        continue;
                    }
                    propertyUtils.setProperty(target, name, deepClone(value));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }*/
	
	

	/**
	 * 获得两个数的商的百分数
	 * @param molecular 分子
	 * @param denominator 分母
	 * @return  精确到小数点后2位的String
	 */
	public static String getPercent(int molecular , int denominator){
		
        // 创建一个数值格式化对象  
		  
        NumberFormat numberFormat = NumberFormat.getInstance();  
  
        // 设置精确到小数点后2位  
  
        numberFormat.setMaximumFractionDigits(2);  
  
        String result = numberFormat.format((float) molecular / (float) denominator * 100);
  
		return result + "%";
		
	}
 
 
}
