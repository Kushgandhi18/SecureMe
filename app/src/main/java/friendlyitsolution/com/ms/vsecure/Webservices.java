package friendlyitsolution.com.ms.vsecure;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Webservices {

	String url;
	List<String> val=new ArrayList<String>();
	List<String> pam=new ArrayList<String>();
	String query="";
	int j=0;
	String data="";
	int urll=0;
	int conn=0;
	InputStream is=null;
	boolean bol=false;
	public void setUrl(String Url)
	{
		url=Url;
		urll=1;
	}
	
	public void addParam(String value, String param)
	{
		val.add(value);
		pam.add(param);
	}
	
	public String getPrepare()
	{
		for(int i=0;i<val.size();i++)
		{
			if(i==0)
			{
			query=pam.get(i)+"="+removeSpace(val.get(i));
			}
			else
			{
			query=query+"&"+pam.get(i)+"="+removeSpace(val.get(i));
			}
			
		}
		
		
		return query;
	}
	
	public String removeSpace(String st)
	{
		
		st=st.replace(' ', '+');
		
		
		return st;
	}
	
	
	public String getData()
	{
	
	if(conn==1)
	{
		if(bol)
		{
			try
			{
		
			int ch;
			String data1="";
			while((ch=is.read())!=-1)
			{
				data1=data1+(char)ch;
			}
			
			return data1;
			}
			catch(Exception e)
			{
			return "Error.......!!!! Not getting any value from "+url;	
			}

		}
		else
		{
			if(url==null)
			{
			return "Error.......!!!!First You have to set URL";	
			}
			else
			{
			return "nc";
			}
		}
		
	}
	else
	{
		return "You have to first set url & then call connect";
	}
	
		
		
		
	}
	
	
	public boolean connect()
	{
		
	if(urll==1)
	{
		try{
	 
			conn=1;
			
			String charset = "UTF-8";
			HttpURLConnection connection ;
			if(val.isEmpty())
	        {
				
				connection = (HttpURLConnection) new URL(url).openConnection();
				
	        }
			else
			{
				getPrepare();
				connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
				
			}
			connection.setRequestProperty("Accept-Charset", charset);
			connection.connect();
			is = connection.getInputStream();
			
	
		if(connection.getResponseCode()==200)
		{
			bol=true;
        return true;
		}
		else
		{
	    return false;
		}
		}
		catch(Exception e)
		{
		//e.printStackTrace();
		//System.out.println("Error:"+e.getMessage());

		return false;
		
		}
	}
	else
	{
		return false;
	}
}

	
	
	public static String getAuthorDetails()
	{
		String info="Author name: Meet Patel\nSkills     : Java , Advance Java , JavaFx , Android , Asp.Net , PHP , OpenCMS "
				+ "\nContact    :...........\n            Email    : patelmeet341993@gmail.com  \n            Mobile   : +91-8758849273  \n            Facebook : www.facebook.com/patel.meet.687";
		
		
		return info;
	}
	
	public static String getApiDetails()
	{
		String info="\nsteps how to use this api... \n"
				+ "step 1 : first create object of 'webservice' class\n"
				+ "step 2 : then call setUrl() method.. and pass the url as argunment\n"
				+ "step 3 : (optional) set all parameters using setParam() method.. pass param name and value in this method\n"
				+ "step 4 : call connect() method\n"
				+ "strp 5 : call getData()";
		
		
		return getAuthorDetails()+info;
	}
	public static String getAndroidDetails()
	{
		String st1="\nIf in android you getting NULL then add to this befor using api.\n";
		String st="if (android.os.Build.VERSION.SDK_INT > 9)\n"+
            "{\n"+
               "     StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();\n"+
               "     StrictMode.setThreadPolicy(policy);\n"+
            "}\n";
		return st1+st;
	}

}
