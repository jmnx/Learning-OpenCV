import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Delete extends HttpServlet{

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		String sqlUsr  = "dw54";
    	String sqlPswd = "";
	
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();

		String funktion  = req.getParameter("f");
		String userRolle = (String) session.getAttribute("urolle");
		String logCheck  = (String) session.getAttribute("iEingeloggt");

		//DB-Treiber einbinden
		try { Class.forName("org.gjt.mm.mysql.Driver"); }
		catch (ClassNotFoundException e) { out.println("DB-Treiber nicht da!"); }

		try
		{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+sqlUsr, sqlUsr, sqlPswd);
			Statement st = con.createStatement();

			if(logCheck.equals("true")){