/*

Benutzerverwaltung

Funktionen 		Umgesetzt
- benutzer 		ja
- change		nein
- admls			nein
- delete 		nein
- activate 		nein


*/

import java.io.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Benutzer extends HttpServlet{

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		String sqlUsr  = "dw54";
    	String sqlPswd = "";
	
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession();

		String funktion  = req.getParameter("f");
		String userID    = (String) session.getAttribute("uid");
		String userRolle = (String) session.getAttribute("urolle");



		String logCheck = (String) session.getAttribute("iEingeloggt");


		//out.println("system message: funktion: ("+funktion+") logCheck: ("+logCheck+") userID: ("+userID+")");


		//DB-Treiber einbinden
		try { Class.forName("org.gjt.mm.mysql.Driver"); }
		catch (ClassNotFoundException e) { out.println("DB-Treiber nicht da!"); }
		try
		{
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+sqlUsr, sqlUsr, sqlPswd);
			Statement st = con.createStatement();

			if(logCheck.equals("true")){
				if(funktion.equals("benutzer")){ // ------------------------------------------------------------------------------------------------- Benutzer Daten ausgeben

					ResultSet rs = st.executeQuery("SELECT * FROM benutzer WHERE id LIKE '"+userID+"'");

					while(rs.next())
					{
						String uid    = rs.getString("id");
						String uname  = rs.getString("name");
						String umail  = rs.getString("email");
						String urolle = rs.getString("rolle");

						String zeile1 = "<form method='POST' action='http://praxi.mt.haw-hamburg.de/servlet/Benutzer'><p>Deine Kundennummer: "+uid+" und deine Rolle: "+urolle +"</p>";
						String zeile2 = "<p>Dein Name: <input type='text' name='uname' value='"+uname+"'></p>";
						String zeile3 = "<p>Deine E-Mail: <input type='email' name='uname' value='"+umail+"'></p>";
						String zeile4 = "<p>Dein Passwort&auml;ndern: Aktuell: <input type='password' name='pswda'> Neu: <input type='password' name='pswdb'> Nochmal: <input type='password' name='pswdc'></p>";
						String zeile5 = "<input id='update' type='submit' value='&Auml;ndern' name='absenden' disabled></form>";

						out.println( zeile1+zeile2+zeile3+zeile4+zeile5);

					}

					st.close();
					con.close();


				}
				else if (funktion.equals("change")){ // --------------------------------------------------------------------------------------------- Seine Daten aendern

				}
				else if ((funktion.equals("delete"))&&(userRolle.equals("1"))) {
					
				}
				else if ((funktion.equals("aktiv"))&&(userRolle.equals("1"))) {
					
				}
				else if ((funktion.equals("admchange"))&&(userRolle.equals("1"))) { // -------------------------------------------------------------- der Admin aendert einen Benutzer
					String user = req.getParameter("usr");

					ResultSet rs = st.executeQuery("SELECT * FROM benutzer WHERE id LIKE '"+user+"'");

					while(rs.next())
					{
						String uid        = rs.getString("id");
						String uname      = rs.getString("name");
						String umail      = rs.getString("email");
						String urolle     = rs.getString("rolle");
						String ukommentar = rs.getString("bemerkung");
						String upass      = rs.getString("passwort");

						String zeile1 = "<form method='POST' action='http://praxi.mt.haw-hamburg.de/servlet/Benutzer'><p>Deine Kundennummer: "+uid+" und deine Rolle: "+urolle +" ";//<input id='adminUpdate' type='submit' value='Aktivieren' name='usrAktiv'> <input id='mAdmin' type='submit' value='Make Admin' name='mAdmin'></p>";
						String zeile2 = "<p>Name: <input type='text' name='uname' value='"+uname+"'></p>";
						String zeile3 = "<p>E-Mail: <input type='email' name='uname' value='"+umail+"'></p>";
						String zeile4 = "<p>Passwort: <input type='text' name='pswd' value='"+upass+"'> </p>";
						String zeile5 = "Bemerkung: <input type='text' name='ukommentar' value='"+ukommentar+"'></p>";
						String zeile6 = "<input id='usrUpd' type='submit' value='&Auml;ndern' name='usrUpd'><input id='usrDel' type='submit' value='L&ouml;schen' name='usrDel'></form>";

						out.println( zeile1+zeile2+zeile3+zeile4+zeile5+zeile6);

					}

					st.close();
					con.close();

				}
				else if ((funktion.equals("admls"))&&(userRolle.equals("1"))) { // ------------------------------------------------------------------ Benutzer auflisten

					ResultSet rs = st.executeQuery("SELECT * FROM benutzer");

					while(rs.next())
					{
						String uid    = rs.getString("id");
						String uname  = rs.getString("name");
						String umail  = rs.getString("email");
						String urolle = rs.getString("rolle");
						String ukomme = rs.getString("bemerkung");
						String upass  = rs.getString("passwort");

						out.println( "<p>"+uid+" ("+urolle +") "+uname+" ("+umail+") <a href='index.jsp?p=admin&adm=5&uid="+uid+"'>Bearbeiten</a></p>");

					}

					st.close();
					con.close();
				}
				else out.println("Keine Daten<br>"+userRolle+"<br>"+funktion);
			}
			else out.println("kein logcheck");
		}
		catch (Exception e)
		{
			out.println(" MySQL Exception: " + e.getMessage());
		}

		
	}
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
		doPost(req,res);
	}
}
