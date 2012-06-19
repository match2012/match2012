package util;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class WebConfig  extends HttpServlet {
	/**
	 * serial id
	 */
	private static final long serialVersionUID = -2819896512667530335L;
	
	/**
	 * Constructor of the object.
	 */
	public WebConfig() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		try {
			Properties sys = System.getProperties();
			String sep = sys.getProperty("file.separator");
			if (sep == null || sep.length() < 1) {
				sep = "\\";
			}
			GlobalParams.LoginAccount = getServletContext().getInitParameter("account");
			GlobalParams.DB_URL = "jdbc:mysql://"+getServletContext().getInitParameter("db_ip")+":"+getServletContext().getInitParameter("db_port")
					+"/"+getServletContext().getInitParameter("db_name")+"?user="+getServletContext().getInitParameter("db_user")+"&password="+getServletContext().getInitParameter("db_pwd");

//			System.out.println("GlobalParams.RealProjectPath-->"+GlobalParams.RealProjectPath);
			DBHelper.initConnections();
			DataHelper.initMapData();
			System.out.println("website start------------->");
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
